package io.eventuate.examples.springauthorizationserver.application;

import com.github.dockerjava.api.command.CreateContainerCmd;
import io.eventuate.examples.springauthorizationserver.testcontainers.AuthorizationServerContainerForServiceContainers;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;
import java.util.function.Consumer;

@SpringBootApplication
public class AuthorizationServerApplicationMain {

  public static final AuthorizationServerContainerForServiceContainers authorizationServer = AuthorizationServerContainerForServiceContainers.makeFromDockerfile()
          .withCreateContainerCmdModifier(addUniqueSuffix("authorization-server"))
          .withReuse(true);


  private static @NotNull Consumer<CreateContainerCmd> addUniqueSuffix(String containerName) {
    return cmd -> cmd.withName(containerName + "-" + UUID.randomUUID());
  }


  public static void main(String[] args) {
    authorizationServer.start();

    SpringApplication.run(AuthorizationServerApplicationMain.class, args);
  }
}
