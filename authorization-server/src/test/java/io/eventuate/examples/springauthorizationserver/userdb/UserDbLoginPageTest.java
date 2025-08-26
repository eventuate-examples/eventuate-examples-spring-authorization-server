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
class UserDbLoginPageTest extends AbstractLoginPageTest {

    @Configuration
    @Import(AuthorizationServerMain.class)
    public static class Config {
    }

    @Override
    protected String getUsername() {
        return "user1";
    }
}