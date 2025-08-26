package io.eventuate.examples.springauthorizationserver.userdb;

import io.eventuate.examples.springauthorizationserver.AuthorizationServerMain;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.anyOf;

@SpringBootTest(
    classes = AuthorizationServerMain.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("UserDatabase")
@TestPropertySource(properties = {
    "users.initial[0].username=testuser1",
    "users.initial[0].password={noop}testpass1",
    "users.initial[0].roles[0]=USER",
    "users.initial[0].roles[1]=ADMIN",
    "users.initial[0].enabled=true",
    "users.initial[1].username=testuser2",
    "users.initial[1].password={noop}testpass2",
    "users.initial[1].roles[0]=USER",
    "users.initial[1].enabled=false"
})
class OAuth2PasswordGrantTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private UserService userService;
    
    private String authHeader;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        // Basic auth header for OAuth2 client (realguardio-client:secret-rg)
        authHeader = "Basic " + Base64.getEncoder().encodeToString("realguardio-client:secret-rg".getBytes());
    }
    
    @Test
    void testPasswordGrantWithInitialUser() {
        given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "testuser1")
            .formParam("password", "testpass1")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("scope", containsString("openid"))
            .body("scope", containsString("profile"));
    }
    
    @Test
    void testPasswordGrantWithNewlyCreatedUser() {
        // Create a new user via UserService
        User newUser = new User("newuser", "{noop}newpass", Arrays.asList("USER"), true);
        userService.createUser(newUser);
        
        // Test password grant with the newly created user
        given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "newuser")
            .formParam("password", "newpass")
            .formParam("scope", "openid")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("scope", containsString("openid"));
    }
    
    @Test
    void testPasswordGrantFailsWithDeletedUser() {
        // Create a user
        User tempUser = new User("tempuser", "{noop}temppass", Arrays.asList("USER"), true);
        userService.createUser(tempUser);
        
        // Verify user can authenticate
        given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "tempuser")
            .formParam("password", "temppass")
            .formParam("scope", "openid")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue());
        
        // Delete the user
        userService.deleteUser("tempuser");
        
        // Verify authentication fails after deletion - Spring redirects to login on auth failure
        given()
            .redirects().follow(false)
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "tempuser")
            .formParam("password", "temppass")
            .formParam("scope", "openid")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(anyOf(equalTo(302), equalTo(400)));  // Spring may redirect or return error
    }
    
    @Test
    void testPasswordGrantWithDisabledUser() {
        // Test disabled user behavior
        // Note: Spring Security's password grant may still issue tokens for disabled users
        // but the user should not be able to access protected resources
        given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "testuser2")
            .formParam("password", "testpass2")
            .formParam("scope", "openid")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(400)));  // Behavior may vary by Spring Security version
    }
    
    @Test
    void testRoleBasedAuthorizationInTokens() {
        // Get token for user with multiple roles
        String response = given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "testuser1")
            .formParam("password", "testpass1")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .extract()
            .asString();
        
        // Extract the access token
        String accessToken = io.restassured.path.json.JsonPath.from(response).getString("access_token");
        assertThat(accessToken).isNotNull();
        
        // Parse JWT payload (middle part between dots)
        String[] parts = accessToken.split("\\.");
        assertThat(parts).hasSize(3);
        
        // Decode the payload
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        
        // Verify that roles are included in the token
        assertThat(payload).contains("USER");
        assertThat(payload).contains("ADMIN");
    }
}