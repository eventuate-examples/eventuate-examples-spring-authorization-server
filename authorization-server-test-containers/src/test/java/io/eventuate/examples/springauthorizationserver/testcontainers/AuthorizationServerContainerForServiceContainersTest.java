package io.eventuate.examples.springauthorizationserver.testcontainers;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class AuthorizationServerContainerForServiceContainersTest {

  private final Logger logger = getLogger(getClass());

  @Test
  public void shouldStart() {
    try (AuthorizationServerContainerForServiceContainers authorizationServerContainer = AuthorizationServerContainerForServiceContainers.makeFromDockerfile()
            .withNetworkAliases("authorization-server")) {
      authorizationServerContainer.start();
      String jwt = authorizationServerContainer.getJwt();
      logger.info("JWT: {}", jwt);
    }
  }

  @Test
  public void shouldListUsersWithUserDb() {
    try (AuthorizationServerContainerForServiceContainers authorizationServerContainer = AuthorizationServerContainerForServiceContainers.makeFromDockerfile()
            .withUserDb()
            .withNetworkAliases("authorization-server")) {
      authorizationServerContainer.start();
      
      List<String> users = authorizationServerContainer.listUsers();
      List<String> expectedUsers = Arrays.asList("user1");
              
      assertThat(users).isEqualTo(expectedUsers);
      logger.info("Users: {}", users);
    }
  }

}