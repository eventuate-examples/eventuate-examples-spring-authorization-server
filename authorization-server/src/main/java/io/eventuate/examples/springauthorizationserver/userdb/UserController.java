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
    
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResponseStatusException e) {
        return Map.of("error", e.getReason());
    }
}