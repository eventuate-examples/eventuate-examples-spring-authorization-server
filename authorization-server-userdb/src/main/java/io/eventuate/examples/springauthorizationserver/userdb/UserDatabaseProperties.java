package io.eventuate.examples.springauthorizationserver.userdb;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "users")
public class UserDatabaseProperties {
    
    private List<InitialUser> initial;

    public List<InitialUser> getInitial() {
        return initial;
    }

    public void setInitial(List<InitialUser> initial) {
        this.initial = initial;
    }

    public static class InitialUser {
        private String username;
        private String password;
        private List<String> roles;
        private boolean enabled = true;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}