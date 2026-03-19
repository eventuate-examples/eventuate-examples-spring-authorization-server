package io.eventuate.examples.springauthorizationserver;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
    classes = AuthorizationServerMain.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class OAuth2PasswordGrantDefaultTest {

    @LocalServerPort
    private int port;

    private String authHeader;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        authHeader = "Basic " + Base64.getEncoder().encodeToString("messaging-client:secret".getBytes());
    }

    @Test
    void testPasswordGrantWithDefaultUser() throws Exception {
        String response = given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "user")
            .formParam("password", "password")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("token_type", equalTo("Bearer"))
            .body("scope", containsString("openid"))
            .body("scope", containsString("profile"))
            .extract()
            .asString();

        String accessToken = io.restassured.path.json.JsonPath.from(response).getString("access_token");
        assertThat(accessToken).isNotNull();

        String[] parts = accessToken.split("\\.");
        assertThat(parts).hasSize(3);

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        Map<String, Object> claims = new ObjectMapper().readValue(payload, Map.class);
        assertThat(claims.get("sub")).as("sub claim in JWT payload: %s", payload).isEqualTo("user");
        assertThat((List<?>) claims.get("authorities")).as("authorities claim in JWT payload: %s", payload).isNotEmpty();
    }

    @Test
    void testPasswordGrantWithInvalidPassword() {
        given()
            .redirects().follow(false)
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "user")
            .formParam("password", "wrongpassword")
            .formParam("scope", "openid")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(anyOf(equalTo(302), equalTo(400)));
    }

    @Test
    void testPasswordGrantWithInvalidUsername() {
        given()
            .redirects().follow(false)
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "nonexistent")
            .formParam("password", "password")
            .formParam("scope", "openid")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(anyOf(equalTo(302), equalTo(400)));
    }

    @Test
    void testAccessTokenIsValidJwt() {
        String response = given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "user")
            .formParam("password", "password")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .extract()
            .asString();

        String accessToken = io.restassured.path.json.JsonPath.from(response).getString("access_token");
        assertThat(accessToken).isNotNull();

        String[] parts = accessToken.split("\\.");
        assertThat(parts).hasSize(3);
    }
}
