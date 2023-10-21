package io.eventuate.examples.springauthorizationserver.testcontainers;

import io.eventuate.common.testcontainers.ContainerUtil;
import io.eventuate.common.testcontainers.EventuateGenericContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import io.restassured.RestAssured;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AuthorizationServerContainer extends EventuateGenericContainer<AuthorizationServerContainer> implements PropertyProvidingContainer {

  public static final int PORT = 9000;
  public static final String TOKEN_ENDPOINT_PATH = "/oauth2/token";
  private final Optional<AuthorizationServerTestProxy> authorizationServerTestProxy;
  private String issuerUri;

  public static AuthorizationServerContainer makeForIntegrationTest() {
    return new AuthorizationServerContainer(DockerfileUtil.findDockerfileInProject("authorization-server/Dockerfile"));
  }

  public static AuthorizationServerContainer makeFromDockerfile() {
    return new AuthorizationServerContainer(DockerfileUtil.findDockerfileInProject("authorization-server/Dockerfile"));
  }

  public static AuthorizationServerContainer makeForComponentTest(Network network, AuthorizationServerTestProxy authorizationServerTestProxy) {
    AuthorizationServerContainer authorizationServerContainer = new AuthorizationServerContainer(DockerfileUtil.findDockerfileInProject("authorization-server/Dockerfile"), authorizationServerTestProxy)
            .withNetworkAliases("authorization-server")
            .withNetwork(network);
    authorizationServerTestProxy.dependsOn(authorizationServerContainer);
    return authorizationServerContainer;
  }

  @Override
  protected int getPort() {
    return PORT;
  }

  public AuthorizationServerContainer() {
    this(Optional.empty());
  }

  public AuthorizationServerContainer(AuthorizationServerTestProxy authorizationServerTestProxy) {
    this(Optional.of(authorizationServerTestProxy));
  }

  public AuthorizationServerContainer(Optional<AuthorizationServerTestProxy> authorizationServerTestProxy) {
    super(ContainerUtil.findImage("eventuateio/eventuate-examples-spring-authorization-server", "eventuate.examples.spring-authorization-server.version.properties"));
    this.authorizationServerTestProxy = authorizationServerTestProxy;
  }

  public AuthorizationServerContainer(Path path, AuthorizationServerTestProxy authorizationServerTestProxy) {
    super(new ImageFromDockerfile().withDockerfile(path));
    this.authorizationServerTestProxy = Optional.of(authorizationServerTestProxy);
    withConfiguration();
  }
  public AuthorizationServerContainer(Path path) {
    super(new ImageFromDockerfile().withDockerfile(path));
    this.authorizationServerTestProxy = Optional.empty();
    withConfiguration();
  }

  private void withConfiguration() {
    waitingFor(Wait.forHealthcheck());
    withExposedPorts(PORT);
  }

  @Override
  public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
    Integer port = getFirstMappedPort();
    /// Hmmm... these are not used by if there is a proxy
    registry.accept("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> String.format("http://localhost:%s/oauth2/jwks", port));
    registry.accept("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> authorizationServerTestProxy.map(proxy -> String.format("http://authorization-server:%s", PORT)).orElseGet(() -> String.format("http://localhost:%s", port)));
  }

  public String getJwt() {
    String baseUrl = String.format("http://localhost:%s", authorizationServerTestProxy.map(ContainerState::getFirstMappedPort).orElseGet(this::getFirstMappedPort));

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

  public Map<String, String> resourceServerEnv() {
    return Map.of("SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI", String.format("http://authorization-server:%s/oauth2/jwks", PORT),
                   "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI", String.format("http://authorization-server:%s", PORT));
  }

  public Map<String, String> clientEnv() {
    return Map.of("API_GATEWAY_TOKEN_ENDPOINT", "http://authorization-server:9000/" + TOKEN_ENDPOINT_PATH);
  }

  public AuthorizationServerContainer withIssuerUri(String issuerUri) {
    withEnv("AUTHORIZATIONSERVER_ISSUER_URI", issuerUri);
    this.issuerUri = issuerUri;
    return this;
  }
}
