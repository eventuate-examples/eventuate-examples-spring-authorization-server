package io.eventuate.examples.springauthorizationserver.userdb;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Profile("UserDatabase")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(UserDTO::from)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{username}")
    public UserDTO getUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return UserDTO.from(user);
    }
    
    @PostMapping
    public UserDTO createUser(@RequestBody CreateUserRequest request) {
        if (userService.findByUsername(request.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = new User(request.getUsername(), request.getPassword(), 
                            request.getRoles(), request.isEnabled());
        userService.createUser(user);
        return UserDTO.from(user);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public org.springframework.http.ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        return org.springframework.http.ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", e.getReason()));
    }
}