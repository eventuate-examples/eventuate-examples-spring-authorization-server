package io.eventuate.examples.springauthorizationserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
public class AuthorizationServerMain {

//  @Bean
//  public UserDetailsService userDetailsService() {
//    UserDetails userDetails = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build();
//
//    return new InMemoryUserDetailsManager(userDetails);
//  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings(@Value("${authorizationserver.issuer.uri:#{null}}") String issuerUri) {
    AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();
    if (issuerUri != null)
      builder.issuer(issuerUri);
    return builder.build();
  }

  public static void main(String[] args) {
    SpringApplication.run(AuthorizationServerMain.class, args);
  }
}
