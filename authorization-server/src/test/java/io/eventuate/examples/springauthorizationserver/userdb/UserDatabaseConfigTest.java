package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserDatabaseConfigTest.TestConfig.class})
@ActiveProfiles("UserDatabase")
@TestPropertySource(properties = {
    "users.initial[0].username=testuser",
    "users.initial[0].password=testpass",
    "users.initial[0].roles[0]=USER",
    "users.initial[0].enabled=true"
})
class UserDatabaseConfigTest {
    
    @Configuration
    @ComponentScan(basePackageClasses = UserDatabaseConfig.class)
    static class TestConfig {
    }
    
    @Autowired
    private ApplicationContext context;
    
    @Test
    void testBeansAreCreatedWhenProfileIsActive() {
        assertThat(context.containsBean("userService")).isTrue();
        assertThat(context.containsBean("userDetailsService")).isTrue();
        assertThat(context.containsBean("passwordEncoder")).isTrue();
    }
    
    @Test
    void testUserDetailsServiceBeanIsProperlyConfigured() {
        UserDetailsService userDetailsService = context.getBean("userDetailsService", UserDetailsService.class);
        assertThat(userDetailsService).isInstanceOf(UserService.class);
    }
    
    @Test
    void testPasswordEncoderBeanIsCreated() {
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        assertThat(passwordEncoder).isNotNull();
        
        String encodedPassword = passwordEncoder.encode("testpassword");
        assertThat(passwordEncoder.matches("testpassword", encodedPassword)).isTrue();
    }
    
    @Test
    void testInitialUsersAreLoadedFromProperties() {
        UserService userService = context.getBean("userService", UserService.class);
        User user = userService.findByUsername("testuser");
        
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("testpass");
        assertThat(user.getRoles()).containsExactly("USER");
        assertThat(user.isEnabled()).isTrue();
    }
}

