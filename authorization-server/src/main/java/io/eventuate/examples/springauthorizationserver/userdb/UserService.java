package io.eventuate.examples.springauthorizationserver.userdb;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    
    public void createUser(User user) {
        users.put(user.getUsername(), user);
    }
    
    public User findByUsername(String username) {
        return users.get(username);
    }
}