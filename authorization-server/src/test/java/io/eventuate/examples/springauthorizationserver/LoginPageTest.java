package io.eventuate.examples.springauthorizationserver;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "built-in")
class LoginPageTest extends AbstractLoginPageTest {

    @Configuration
    @Import(AuthorizationServerMain.class)
    public static class Config {
    }
}