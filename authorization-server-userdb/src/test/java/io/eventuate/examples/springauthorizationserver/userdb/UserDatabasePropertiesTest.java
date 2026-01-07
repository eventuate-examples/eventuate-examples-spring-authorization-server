package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = UserDatabasePropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
    "users.initial[0].username=user1",
    "users.initial[0].password=password1",
    "users.initial[0].roles[0]=USER",
    "users.initial[0].roles[1]=ADMIN",
    "users.initial[0].enabled=true",
    "users.initial[1].username=user2",
    "users.initial[1].password=password2",
    "users.initial[1].roles[0]=USER",
    "users.initial[1].enabled=false"
})
class UserDatabasePropertiesTest {

    @EnableConfigurationProperties(UserDatabaseProperties.class)
    static class TestConfig {
    }

    @Autowired
    private UserDatabaseProperties properties;

    @Test
    void testLoadingPropertiesFromTestConfiguration() {
        assertThat(properties).isNotNull();
        assertThat(properties.getInitial()).isNotNull();
        assertThat(properties.getInitial()).hasSize(2);
    }

    @Test
    void testLoadingMultipleInitialUsers() {
        List<UserDatabaseProperties.InitialUser> users = properties.getInitial();
        
        UserDatabaseProperties.InitialUser user1 = users.get(0);
        assertThat(user1.getUsername()).isEqualTo("user1");
        assertThat(user1.getPassword()).isEqualTo("password1");
        assertThat(user1.getRoles()).containsExactly("USER", "ADMIN");
        assertThat(user1.isEnabled()).isTrue();
        
        UserDatabaseProperties.InitialUser user2 = users.get(1);
        assertThat(user2.getUsername()).isEqualTo("user2");
        assertThat(user2.getPassword()).isEqualTo("password2");
        assertThat(user2.getRoles()).containsExactly("USER");
        assertThat(user2.isEnabled()).isFalse();
    }

    @Test
    void testHandlingEmptyInitialUsersList() {
        UserDatabaseProperties emptyProperties = new UserDatabaseProperties();
        assertThat(emptyProperties).isNotNull();
        assertThat(emptyProperties.getInitial()).isNull();
    }
}