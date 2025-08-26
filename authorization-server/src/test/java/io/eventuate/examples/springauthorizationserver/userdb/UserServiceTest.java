package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService();
    }
    
    @Test
    void testCreatingUserStoresItInMemory() {
        String username = "testuser";
        String password = "testpassword";
        
        User user = new User(username, password);
        userService.createUser(user);
        
        User retrievedUser = userService.findByUsername(username);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo(username);
        assertThat(retrievedUser.getPassword()).isEqualTo(password);
    }
}