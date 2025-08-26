package io.eventuate.examples.springauthorizationserver.userdb;

import io.eventuate.examples.springauthorizationserver.AuthorizationServerMain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static io.restassured.RestAssured.given;

@SpringBootTest(
    classes = AuthorizationServerMain.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("UserDatabase")
@TestPropertySource(properties = {
    "users.initial[0].username=testuser1",
    "users.initial[0].password=testpass1",
    "users.initial[0].roles[0]=USER",
    "users.initial[0].roles[1]=ADMIN",
    "users.initial[0].enabled=true",
    "users.initial[1].username=testuser2",
    "users.initial[1].password=testpass2",
    "users.initial[1].roles[0]=USER",
    "users.initial[1].enabled=false"
})
class UserDatabaseIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Test
    void testApplicationStartsWithUserDatabaseProfile() {
        assertThat(context).isNotNull();
        assertThat(context.containsBean("userService")).isTrue();
        assertThat(context.containsBean("userDetailsService")).isTrue();
        assertThat(context.containsBean("passwordEncoder")).isTrue();
    }
    
    @Test
    void testInitialUsersAreLoadedFromConfiguration() {
        UserService userService = context.getBean("userService", UserService.class);
        
        User user1 = userService.findByUsername("testuser1");
        assertThat(user1).isNotNull();
        assertThat(user1.getRoles()).containsExactly("USER", "ADMIN");
        assertThat(user1.isEnabled()).isTrue();
        
        User user2 = userService.findByUsername("testuser2");
        assertThat(user2).isNotNull();
        assertThat(user2.getRoles()).containsExactly("USER");
        assertThat(user2.isEnabled()).isFalse();
    }
    
    @Test
    void testDefaultSpringSecurityUserIsNotCreated() {
        // The default Spring Security user should not exist
        UserService userService = context.getBean("userService", UserService.class);
        User defaultUser = userService.findByUsername("user");
        assertThat(defaultUser).isNull();
    }
    
    @Test
    void testAuthenticationWithInitialUsers() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser1");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser1");
        assertThat(userDetails.getPassword()).isEqualTo("{noop}testpass1");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting(auth -> auth.getAuthority())
                .containsExactlyInAnyOrder("USER", "ADMIN");
    }
}