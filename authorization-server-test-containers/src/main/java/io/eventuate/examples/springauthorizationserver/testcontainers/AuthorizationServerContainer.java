package io.eventuate.examples.springauthorizationserver.testcontainers;

import io.eventuate.common.testcontainers.ContainerUtil;
import io.eventuate.common.testcontainers.EventuateGenericContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import io.restassured.RestAssured;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;

public abstract class AuthorizationServerContainer extends EventuateGenericContainer<AuthorizationServerContainer> implements PropertyProvidingContainer {

  public static final int PORT = 9000;
  public static final String TOKEN_ENDPOINT_PATH = "/oauth2/token";



  @Override
  protected int getPort() {
    return PORT;
  }


  public AuthorizationServerContainer() {
    super(ContainerUtil.findImage("eventuateio/eventuate-examples-spring-authorization-server", "eventuate.examples.spring-authorization-server.version.properties"));
    withConfiguration();
  }

  public AuthorizationServerContainer(Path dockerfile) {
    super(new ImageFromDockerfile().withDockerfile(dockerfile));
    withConfiguration();
  }

  private void withConfiguration() {
    waitingFor(Wait.forHealthcheck());
    withExposedPorts(PORT);
  }


  public String getJwt() {
    String baseUrl = String.format("http://localhost:%s", getFirstMappedPort());

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

}
