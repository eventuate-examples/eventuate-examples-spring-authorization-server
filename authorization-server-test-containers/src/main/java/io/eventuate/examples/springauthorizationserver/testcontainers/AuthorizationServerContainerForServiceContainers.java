package io.eventuate.examples.springauthorizationserver.testcontainers;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AuthorizationServerContainerForServiceContainers extends AuthorizationServerContainer {

  public AuthorizationServerContainerForServiceContainers() {
  }

  public AuthorizationServerContainerForServiceContainers(Path dockerfile) {
    super(dockerfile);
  }

  public static AuthorizationServerContainer makeFromDockerfile() {
    return new AuthorizationServerContainerForServiceContainers(DockerfileUtil.findDockerfileInProject("authorization-server/Dockerfile"));
  }

  @Override
  public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
    throw new UnsupportedOperationException();
  }

  public Map<String, String> resourceServerEnv() {
    return Map.of("SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI", String.format("%s/oauth2/jwks", getBaseUrl()),
            "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI", getBaseUrl());
  }

  public String getBaseUrl() {
    String firstAlias = getFirstNetworkAlias();
    return firstAlias != null ? String.format("http://%s:%s", firstAlias, PORT) : String.format("http://authorization-server:%s", PORT);
  }

  public Map<String, String> clientEnv() {
    return Map.of("API_GATEWAY_TOKEN_ENDPOINT", getBaseUrl() + TOKEN_ENDPOINT_PATH);
  }

  public AuthorizationServerContainer withIssuerUri(String issuerUri) {
    return this;
  }

  @Override
  public AuthorizationServerContainer withNetworkAliases(String... aliases) {
    super.withNetworkAliases(aliases);
    withEnv("AUTHORIZATIONSERVER_ISSUER_URI", getBaseUrl());
    return this;
  }

}
