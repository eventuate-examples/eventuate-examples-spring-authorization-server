package io.eventuate.examples.springauthorizationserver.userdb;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<String> roles;
    private boolean enabled = true;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>();
    }
    
    public User(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>(roles);
    }
    
    public User(String username, String password, List<String> roles, boolean enabled) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>(roles);
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}