package io.eventuate.examples.springauthorizationserver.userdb;

import java.util.List;

public class UserDTO {
    private String username;
    private List<String> roles;
    private boolean enabled;
    
    public UserDTO() {
    }
    
    public UserDTO(String username, List<String> roles, boolean enabled) {
        this.username = username;
        this.roles = roles;
        this.enabled = enabled;
    }
    
    public static UserDTO from(User user) {
        return new UserDTO(user.getUsername(), user.getRoles(), user.isEnabled());
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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