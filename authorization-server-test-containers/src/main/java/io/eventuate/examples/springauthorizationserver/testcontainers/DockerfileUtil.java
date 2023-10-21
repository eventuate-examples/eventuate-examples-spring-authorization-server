package io.eventuate.examples.springauthorizationserver.testcontainers;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class DockerfileUtil {
  @NotNull
  public static Path findDockerfileInProject(String dockerfilePath) {

    for (int i = 0; i < 3; i++) {
      Path path = FileSystems.getDefault().getPath(dockerfilePath);
      if (path.toFile().exists()) {
        return path;
      }
      dockerfilePath = "../" + dockerfilePath;
    }
    throw new RuntimeException("Can't find " + dockerfilePath);
  }
}
