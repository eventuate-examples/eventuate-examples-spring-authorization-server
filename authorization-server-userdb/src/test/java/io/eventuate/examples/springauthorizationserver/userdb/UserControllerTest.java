package io.eventuate.examples.springauthorizationserver.userdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.http.MediaType;

@WebMvcTest(UserController.class)
@ActiveProfiles("UserDatabase")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void testGetUsersReturnsAllUsers() throws Exception {
        User user1 = new User("user1", "password1", List.of("USER"));
        User user2 = new User("user2", "password2", List.of("ADMIN"));
        
        when(userService.findAll()).thenReturn(List.of(user1, user2));
        
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[0].roles[0]", is("USER")))
                .andExpect(jsonPath("$[0].enabled", is(true)))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].username", is("user2")))
                .andExpect(jsonPath("$[1].roles[0]", is("ADMIN")))
                .andExpect(jsonPath("$[1].enabled", is(true)))
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }
    
    @Test
    void testGetUserByUsernameReturnsSpecificUser() throws Exception {
        User user = new User("testuser", "password", List.of("USER", "ADMIN"));
        
        when(userService.findByUsername("testuser")).thenReturn(user);
        
        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[0]", is("USER")))
                .andExpect(jsonPath("$.roles[1]", is("ADMIN")))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
    
    @Test
    void testGetUserReturns404ForNonExistentUser() throws Exception {
        when(userService.findByUsername("nonexistent")).thenReturn(null);
        
        mockMvc.perform(get("/api/users/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));
    }
    
    @Test
    void testPostUsersCreatesUserSuccessfully() throws Exception {
        String requestBody = """
            {
                "username": "newuser",
                "password": "newpassword",
                "roles": ["USER", "ADMIN"],
                "enabled": true
            }
            """;
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[0]", is("USER")))
                .andExpect(jsonPath("$.roles[1]", is("ADMIN")))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
    
    @Test
    void testPostUsersReturns409ForDuplicateUsername() throws Exception {
        when(userService.findByUsername("existinguser")).thenReturn(new User("existinguser", "password"));
        
        String requestBody = """
            {
                "username": "existinguser",
                "password": "newpassword",
                "roles": ["USER"],
                "enabled": true
            }
            """;
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Username already exists")));
    }
}