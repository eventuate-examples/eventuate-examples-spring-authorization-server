package io.eventuate.examples.springauthorizationserver.userdb;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("UserDatabase")
@EnableConfigurationProperties(UserDatabaseProperties.class)
public class UserDatabaseConfig {
    
    private final UserDatabaseProperties properties;
    private final UserService userService;
    
    public UserDatabaseConfig(UserDatabaseProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }
    
    @PostConstruct
    public void initializeUsers() {
        if (properties.getInitial() != null) {
            for (UserDatabaseProperties.InitialUser initialUser : properties.getInitial()) {
                User user = new User(
                    initialUser.getUsername(),
                    initialUser.getPassword(),
                    initialUser.getRoles(),
                    initialUser.isEnabled()
                );
                userService.createUser(user);
            }
        }
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}