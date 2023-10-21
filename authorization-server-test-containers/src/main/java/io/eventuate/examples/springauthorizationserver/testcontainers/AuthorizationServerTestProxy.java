package io.eventuate.examples.springauthorizationserver.testcontainers;

import io.eventuate.common.testcontainers.EventuateGenericContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AuthorizationServerTestProxy extends EventuateGenericContainer<AuthorizationServerTestProxy> implements PropertyProvidingContainer {

  int PORT = 80;

  @NotNull
  public static AuthorizationServerTestProxy makeFromDockerfile() {
    return new AuthorizationServerTestProxy(DockerfileUtil.findDockerfileInProject("authorization-server/authorization-server-test-proxy/Dockerfile"));
  }

  @Override
  protected int getPort() {
    return PORT;
  }

  public AuthorizationServerTestProxy(Path path) {
    super(new ImageFromDockerfile().withDockerfile(path));
    withConfiguration();
  }

  private void withConfiguration() {
    withExposedPorts(PORT);
  }

  @Override
  public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {

  }
}
