package io.eventuate.examples.springauthorizationserver;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorizationServerMainTest {

    @Configuration
    @Import(AuthorizationServerMain.class)
    public static class Config  {

    }

    public static final String TOKEN_ENDPOINT_PATH = "/oauth2/token";

    @LocalServerPort
    private int port;

    public String getJwt() {
        String baseUrl = String.format("http://localhost:%s", port);

        // Set the client ID and secret
        String clientId = "messaging-client";
        String clientSecret = "secret";

        // Set the request parameters
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
        Assertions.assertThat(getJwt()).isNotNull();
    }
}