package io.eventuate.examples.springauthorizationserver;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "built-in")
class LoginPageTest {

    @Configuration
    @Import(AuthorizationServerMain.class)
    public static class Config {
    }

    @LocalServerPort
    private int port;

    @Test
    void testLoginPageFlow() {
        String baseUrl = String.format("http://localhost:%s", port);

        // Step 1: Get the login page
        Response loginPageResponse = RestAssured.given()
                .redirects().follow(false)
                .when()
                .get(baseUrl + "/login")
                .then()
                .statusCode(200)
                .extract().response();

        // Verify login page contains expected elements
        String loginPageHtml = loginPageResponse.getBody().asString();
        assertThat(loginPageHtml).contains("<form");
        assertThat(loginPageHtml).contains("name=\"username\"");
        assertThat(loginPageHtml).contains("name=\"password\"");
        assertThat(loginPageHtml).contains("name=\"_csrf\"");

        // Extract CSRF token and session cookie
        String csrfToken = extractCsrfToken(loginPageHtml);
        String sessionId = loginPageResponse.getCookie("JSESSIONID");
        assertThat(csrfToken).isNotNull();
        assertThat(sessionId).isNotNull();

        // Step 2: Submit login credentials
        Response loginResponse = RestAssured.given()
                .formParam("username", "user")
                .formParam("password", "password")
                .formParam("_csrf", csrfToken)
                .cookie("JSESSIONID", sessionId)
                .redirects().follow(false)
                .when()
                .post(baseUrl + "/login")
                .then()
                .statusCode(302)
                .extract().response();

        // Step 3: Verify successful login
        String redirectLocation = loginResponse.getHeader("Location");
        
        // After successful login, Spring Security redirects to "/" or the originally requested URL
        // The important thing is that it's not redirecting back to /login with an error
        assertThat(redirectLocation).isNotNull();
        assertThat(redirectLocation).doesNotContain("/login?error");
        
        // Get new session ID if present
        String newSessionId = loginResponse.getCookie("JSESSIONID");
        if (newSessionId != null) {
            sessionId = newSessionId;
        }

        // Follow the redirect to verify we're authenticated
        String targetUrl = redirectLocation.startsWith("http") ? redirectLocation : baseUrl + redirectLocation;
        Response authenticatedResponse = RestAssured.given()
                .cookie("JSESSIONID", sessionId)
                .redirects().follow(false)
                .when()
                .get(targetUrl)
                .then()
                .extract().response();

        // Verify we're authenticated 
        // The response should be 200 (content), 302 (further redirect), or 404 (no default page)
        // but NOT a redirect back to the login page
        int statusCode = authenticatedResponse.getStatusCode();
        assertThat(statusCode)
                .as("Expected successful response after login")
                .isIn(200, 302, 404);
        
        if (statusCode == 302) {
            String nextLocation = authenticatedResponse.getHeader("Location");
            assertThat(nextLocation).doesNotContain("/login");
        }
    }

    private String extractCsrfToken(String html) {
        String fieldMarker = "name=\"_csrf\" type=\"hidden\" value=\"";
        int start = html.indexOf(fieldMarker);
        if (start == -1) {
            fieldMarker = "name=\"_csrf\" value=\"";
            start = html.indexOf(fieldMarker);
            if (start == -1) {
                throw new RuntimeException("CSRF token not found in HTML");
            }
        }
        start += fieldMarker.length();
        int end = html.indexOf("\"", start);
        return html.substring(start, end);
    }
}