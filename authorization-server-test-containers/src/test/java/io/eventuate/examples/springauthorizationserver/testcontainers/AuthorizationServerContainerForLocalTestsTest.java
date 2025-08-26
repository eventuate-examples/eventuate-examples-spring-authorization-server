package io.eventuate.examples.springauthorizationserver.testcontainers;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

  @Test
  public void shouldGetClientCredentialsJwtWithUserDb() {
    try (AuthorizationServerContainerForLocalTests authorizationServerContainer = AuthorizationServerContainerForLocalTests.makeFromDockerfile()
            .withUserDb()
            .withReuse(false)) {
      authorizationServerContainer.start();
      
      String jwt = authorizationServerContainer.getClientCredentialsJwt();
      
      assertThat(jwt).isNotNull().isNotEmpty();
      logger.info("Client credentials JWT for UserDb: {}", jwt);
    }
  }

  @Test
  public void shouldListUsersWithUserDb() {
    try (AuthorizationServerContainerForLocalTests authorizationServerContainer = AuthorizationServerContainerForLocalTests.makeFromDockerfile()
            .withUserDb()
            .withReuse(false)) {
      authorizationServerContainer.start();
      
      List<String> users = authorizationServerContainer.listUsers();
      List<String> expectedUsers = Arrays.asList("user1");
      
      logger.info("Users returned: {}", users);
      assertThat(users).isEqualTo(expectedUsers);
      logger.info("Users: {}", users);
    }
  }

}