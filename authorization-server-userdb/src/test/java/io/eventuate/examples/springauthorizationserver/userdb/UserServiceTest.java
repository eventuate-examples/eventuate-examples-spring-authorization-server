package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    
    @Test
    void testFindAllReturnsAllUsers() {
        User user1 = new User("user1", "password1");
        User user2 = new User("user2", "password2");
        User user3 = new User("user3", "password3");
        
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        
        var allUsers = userService.findAll();
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2", "user3");
    }
    
    @Test
    void testLoadUserByUsernameReturnsSpringSecurityUserDetails() {
        User user = new User("testuser", "password", List.of("ROLE_USER", "ROLE_ADMIN"));
        userService.createUser(user);
        
        UserDetails userDetails = userService.loadUserByUsername("testuser");
        
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(2);
        assertThat(userDetails.getAuthorities())
                .extracting(auth -> auth.getAuthority())
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
    
    @Test
    void testLoadUserByUsernameThrowsExceptionForNonExistentUser() {
        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }
}