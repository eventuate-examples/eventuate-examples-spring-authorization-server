package io.eventuate.examples.springauthorizationserver.testcontainers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class AuthorizationServerContainerForLocalTestsTest {

  private final Logger logger = getLogger(getClass());

  @Test
  public void shouldStart() {
    try (AuthorizationServerContainerForLocalTests authorizationServerContainer = AuthorizationServerContainerForLocalTests.makeFromDockerfile()
            .withReuse(false)) {
      authorizationServerContainer.start();
      String jwt = authorizationServerContainer.getJwt();
      logger.info("JWT: {}", jwt);
    }
  }

}