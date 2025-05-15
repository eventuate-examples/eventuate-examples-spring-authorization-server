package io.eventuate.examples.springauthorizationserver;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "built-in")
class AuthorizationServerMainTest {

    @Configuration
    @Import(AuthorizationServerMain.class)
    public static class Config  {

    }

    public static final String TOKEN_ENDPOINT_PATH = "/oauth2/token";
    public static final String AUTHORIZATION_ENDPOINT_PATH = "/oauth2/authorize";

    @LocalServerPort
    private int port;

    public String getJwt() {
        String baseUrl = String.format("http://localhost:%s", port);

        String clientId = "messaging-client";
        String clientSecret = "secret";

        String grantType = "password";
        String username = "user";
        String password = "password";

        // Send the POST request
        String jwt = RestAssured.given()
                .auth().preemptive().basic(clientId, clientSecret)
                .formParam("grant_type", grantType)
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post(baseUrl + TOKEN_ENDPOINT_PATH)
                .then()
                .statusCode(200)
                .extract().path("access_token");
        System.out.printf("Got JWT %s\n", jwt);
        return jwt;

    }

    @Test
     void testGetJwt() {
        assertThat(getJwt()).isNotNull();
    }



//    @Test
//    void testAuthorizationCodeGrant() {
//
//        String baseUrl = String.format("http://localhost:%s", port);
//
//        String clientId = "messaging-client";
//        String redirectUri = "http://127.0.0.1:8080/authorized";
//        String responseType = "code";
//        String scope = "openid";
//
//        String redirectLocation = RestAssured.given()
//                .param("client_id", clientId)
//                .param("redirect_uri", redirectUri)
//                .param("response_type", responseType)
//                .param("scope", scope)
//                .redirects().follow(false)
//                .when()
//                .get(baseUrl + AUTHORIZATION_ENDPOINT_PATH)
//                .then()
//                .statusCode(302)
//                .extract().header("location");
//
//        URI uri = URI.create(redirectLocation);
//        URIBuilder uriBuilder = new URIBuilder(uri);
//
//        // This should be a redirect to the login page, not the redirectUrl1
//
//        assertThat(uriBuilder.getPath()).isEqualTo("/login");
//
//        Map<String, String> queryParams = new HashMap<>();
//        uriBuilder.getQueryParams().forEach(param ->
//            queryParams.put(param.getName(), param.getValue()));
//
//        System.out.printf("Query params: %s\n", queryParams);
//
//        assertThat(queryParams).doesNotContainKey("error");
//
//        var loginResponse = RestAssured.given().
//            get(redirectLocation)
//            .then()
//            .statusCode(200)
//            .extract().response();
//    }


    public String getAuthorizationCode() {
        String baseUrl = String.format("http://localhost:%s", port);

        String clientId = "messaging-client";
        String redirectUri = "http://127.0.0.1:8080/authorized";
        String responseType = "code";
        String scope = "openid";

        // First, get the login page
        String authorizationEndpointUrl = baseUrl + AUTHORIZATION_ENDPOINT_PATH;

        Response response = RestAssured.given()
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", responseType)
            .queryParam("scope", scope)
            .redirects().follow(false)
            .when()
            .get(authorizationEndpointUrl)
            .then()
            .statusCode(302)
            .extract().response();

        String location = response.getHeader("Location");
        String jsessionid = response.getCookie("JSESSIONID");

        response = RestAssured.given()
            .cookie("JSESSIONID", jsessionid)
            .when()
            .get(location)
            .then()
            .statusCode(200)
            .extract().response();

        // Extract the CSRF token from the login page
        String loginPage = response.getBody().asString();
        String csrfToken = extractCsrfToken(loginPage);

        // Submit the login form with username and password

        response = RestAssured.given()
            .formParam("username", "user")
            .formParam("password", "password")
            .formParam("_csrf", csrfToken)
            .cookie("JSESSIONID", jsessionid)
            .when()
            .post(baseUrl + "/login")
            .then()
            .statusCode(302)
            .extract().response();

        // If consent is required, submit the consent form
        location = response.getHeader("Location");
        if (location.contains("/oauth2/consent")) {
            String cookie2 = response.getCookie("JSESSIONID");
            response = RestAssured.given()
                .cookie("JSESSIONID", cookie2)
                .when()
                .get(location)
                .then()
                .statusCode(200)
                .extract().response();

            String consentPage = response.getBody().asString();
            String consentCsrfToken = extractCsrfToken(consentPage);

            response = RestAssured.given()
                .formParam("scope", scope)
                .formParam("_csrf", consentCsrfToken)
                .cookie("JSESSIONID", response.getCookie("JSESSIONID"))
                .when()
                .post(baseUrl + "/oauth2/consent")
                .then()
                .statusCode(302)
                .extract().response();
        }

        assertThat(location).startsWith(authorizationEndpointUrl);

        jsessionid = response.getCookie("JSESSIONID");

        response = RestAssured.given()
            .redirects().follow(false)
            .cookie("JSESSIONID", jsessionid)
            .log().all()
            .urlEncodingEnabled(false)
            .when()
            .get(location)
            .then()
            .statusCode(302)
            .extract().response();

        // Extract the authorization code from the redirect URL
        location = response.getHeader("Location");
        assertThat(location).startsWith(redirectUri);
        String code = extractAuthorizationCode(location);
        System.out.printf("Got authorization code: %s\n", code);
        return code;
    }

    public String getJwtWithAuthorizationCode() {
        String baseUrl = String.format("http://localhost:%s", port);

        String clientId = "messaging-client";
        String clientSecret = "secret";
        String redirectUri = "http://127.0.0.1:8080/authorized";

        // Get the authorization code
        String code = getAuthorizationCode();

        // Exchange the code for a token
        String jwt = RestAssured.given()
            .auth().preemptive().basic(clientId, clientSecret)
            .formParam("grant_type", "authorization_code")
            .formParam("code", code)
            .formParam("redirect_uri", redirectUri)
            .when()
            .post(baseUrl + TOKEN_ENDPOINT_PATH)
            .then()
            .statusCode(200)
            .extract().path("access_token");

        System.out.printf("Got JWT with authorization code: %s\n", jwt);
        return jwt;
    }

    private String extractCsrfToken(String html) {
        // Simple extraction of CSRF token from HTML
        String fieldMarker = "name=\"_csrf\" type=\"hidden\" value=\"";
        int start = html.indexOf(fieldMarker) + fieldMarker.length();
        int end = html.indexOf("\"", start);
        return html.substring(start, end);
    }

    private String extractAuthorizationCode(String redirectUrl) {
        // Extract the code parameter from the redirect URL
        int start = redirectUrl.indexOf("code=") + "code=".length();
        int end = redirectUrl.indexOf("&", start);
        if (end == -1) {
            end = redirectUrl.length();
        }
        return redirectUrl.substring(start, end);
    }

    @Test
    void testAuthorizationCodeGrant() {
        Assertions.assertThat(getJwtWithAuthorizationCode()).isNotNull();
    }
}
