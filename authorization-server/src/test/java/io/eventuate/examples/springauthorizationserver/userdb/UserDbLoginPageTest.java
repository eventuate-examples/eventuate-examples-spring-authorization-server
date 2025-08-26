package io.eventuate.examples.springauthorizationserver.userdb;

import io.eventuate.examples.springauthorizationserver.AbstractLoginPageTest;
import io.eventuate.examples.springauthorizationserver.AuthorizationServerMain;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("UserDatabase")
@TestPropertySource(properties = {
    "users.initial[0].username=user",
    "users.initial[0].password={noop}password",
    "users.initial[0].roles[0]=USER",
    "users.initial[0].enabled=true"
})
class UserDbLoginPageTest extends AbstractLoginPageTest {

    @Configuration
    @Import(AuthorizationServerMain.class)
    public static class Config {
    }
}