package io.eventuate.examples.springauthorizationserver.testcontainers;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AuthorizationServerContainerForLocalTests extends AuthorizationServerContainer<AuthorizationServerContainerForLocalTests> {

  public AuthorizationServerContainerForLocalTests() {
  }

  public AuthorizationServerContainerForLocalTests(Path dockerfile) {
    super(dockerfile);
  }

  @Override
  public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
    Integer port = getFirstMappedPort();
    registry.accept("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> String.format("http://localhost:%s/oauth2/jwks", port));
    registry.accept("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> String.format("http://localhost:%s", port));
  }

  public static AuthorizationServerContainerForLocalTests makeFromDockerfile() {
    return new AuthorizationServerContainerForLocalTests(DockerfileUtil.findDockerfileInProject("authorization-server/Dockerfile"));
  }

}
