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
    
    @Test
    void testFindByUsernameReturnsCorrectUser() {
        User user1 = new User("user1", "password1");
        User user2 = new User("user2", "password2");
        
        userService.createUser(user1);
        userService.createUser(user2);
        
        User foundUser = userService.findByUsername("user2");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("user2");
        assertThat(foundUser.getPassword()).isEqualTo("password2");
    }
}