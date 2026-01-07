package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.Test;

import java.util.List;

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
    
    @Test
    void testUserHasRolesList() {
        String username = "testuser";
        String password = "testpassword";
        List<String> roles = List.of("USER", "ADMIN");
        
        User user = new User(username, password, roles);
        
        assertThat(user.getRoles()).isEqualTo(roles);
    }
    
    @Test
    void testUserEnabledStatusDefaultsToTrue() {
        String username = "testuser";
        String password = "testpassword";
        
        User user = new User(username, password);
        
        assertThat(user.isEnabled()).isTrue();
    }
}