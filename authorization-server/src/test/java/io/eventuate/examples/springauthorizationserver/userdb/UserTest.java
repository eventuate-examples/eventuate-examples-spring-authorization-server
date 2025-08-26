package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testUserCreationWithUsernameAndPassword() {
        String username = "testuser";
        String password = "testpassword";
        
        User user = new User(username, password);
        
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
    }
}