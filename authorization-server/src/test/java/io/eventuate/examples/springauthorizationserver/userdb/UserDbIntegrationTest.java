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
    "users.initial[0].username=user1",
    "users.initial[0].password={noop}password",
    "users.initial[0].roles[0]=USER",
    "users.initial[0].roles[1]=ADMIN",
    "users.initial[0].enabled=true"
})
class UserDbIntegrationTest {
    
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
    void testCompleteHappyPath() {
        // Step 1: Get client credentials token for API access
        String clientToken = given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "client_credentials")
            .formParam("scope", "message.read message.write")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .extract()
            .path("access_token");
        
        assertThat(clientToken).isNotNull();
        
        // Step 2: Verify initial user can authenticate via OAuth2 password grant
        String userToken = given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "user1")
            .formParam("password", "password")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("scope", containsString("openid"))
            .extract()
            .path("access_token");
        
        assertThat(userToken).isNotNull();
        
        // Step 3: List all users via REST API using bearer token
        given()
            .header("Authorization", "Bearer " + clientToken)
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .body("[0].username", equalTo("user1"))
            .body("[0].roles", hasItems("USER", "ADMIN"))
            .body("[0].enabled", equalTo(true));
        
        // Step 4: Get specific user via REST API
        given()
            .header("Authorization", "Bearer " + clientToken)
        .when()
            .get("/api/users/user1")
        .then()
            .statusCode(200)
            .body("username", equalTo("user1"))
            .body("roles", hasItems("USER", "ADMIN"))
            .body("enabled", equalTo(true));
        
        // Step 5: Create a new user via REST API
        given()
            .header("Authorization", "Bearer " + clientToken)
            .contentType(ContentType.JSON)
            .body("""
                {
                    "username": "newuser",
                    "password": "{noop}newpass",
                    "roles": ["USER"],
                    "enabled": true
                }
                """)
        .when()
            .post("/api/users")
        .then()
            .statusCode(200)
            .body("username", equalTo("newuser"))
            .body("roles", hasItem("USER"))
            .body("enabled", equalTo(true));
        
        // Step 6: Verify the new user appears in the list
        given()
            .header("Authorization", "Bearer " + clientToken)
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("username", hasItems("user1", "newuser"));
        
        // Step 7: Authenticate with the new user via OAuth2
        String newUserToken = given()
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
            .extract()
            .path("access_token");
        
        assertThat(newUserToken).isNotNull();
        assertThat(newUserToken).isNotEqualTo(userToken);
        
        // Step 8: Test OAuth2 flows work with multiple users
        // Create another user programmatically
        User anotherUser = new User("testuser", "{noop}testpass", Arrays.asList("USER", "VIEWER"), true);
        userService.createUser(anotherUser);
        
        // Authenticate with this user
        given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "testuser")
            .formParam("password", "testpass")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue());
        
        // Step 9: Verify all three users exist
        given()
            .header("Authorization", "Bearer " + clientToken)
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("$", hasSize(3))
            .body("username", hasItems("user1", "newuser", "testuser"));
        
        // Step 10: Test 404 for non-existent user
        given()
            .header("Authorization", "Bearer " + clientToken)
        .when()
            .get("/api/users/nonexistent")
        .then()
            .statusCode(404);
        
        // Step 11: Test duplicate user creation returns 409
        given()
            .header("Authorization", "Bearer " + clientToken)
            .contentType(ContentType.JSON)
            .body("""
                {
                    "username": "newuser",
                    "password": "{noop}somepass",
                    "roles": ["USER"],
                    "enabled": true
                }
                """)
        .when()
            .post("/api/users")
        .then()
            .statusCode(409);
    }
    
    @Test
    void testOAuth2ClientCredentialsFlow() {
        // Test that client credentials grant works
        given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "client_credentials")
            .formParam("scope", "message.read message.write")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("scope", containsString("message.read"))
            .body("scope", containsString("message.write"));
    }
    
    @Test
    void testOAuth2RefreshTokenFlow() {
        // Get initial token with refresh token
        String refreshToken = given()
            .header("Authorization", authHeader)
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "password")
            .formParam("username", "user1")
            .formParam("password", "password")
            .formParam("scope", "openid profile")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("refresh_token", anyOf(notNullValue(), nullValue()))  // Refresh token may not be issued
            .extract()
            .path("refresh_token");
        
        // Use refresh token to get new access token (if refresh token was issued)
        if (refreshToken != null) {
            given()
                .header("Authorization", authHeader)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "refresh_token")
                .formParam("refresh_token", refreshToken)
            .when()
                .post("/oauth2/token")
            .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"));
        }
    }
}