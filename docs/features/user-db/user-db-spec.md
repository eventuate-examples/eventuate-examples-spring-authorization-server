# UserDatabase Feature Specification

## Overview
Enhance the Spring Authorization Server with a dynamic, in-memory user database that replaces the hardcoded single user configuration. This feature provides a REST API for user management while maintaining compatibility with the existing OAuth2 authorization server configuration.

## Activation
- **Profile Name**: `UserDatabase`
- **Activation Command**: `--spring.profiles.active=UserDatabase`
- **Configuration File**: `application-UserDatabase.yaml`

## Architecture

### Package Structure
All classes will be contained in a single package: `com.example.authserver.userdb`

### Core Components

#### 1. UserDatabaseConfig
```java
@Configuration
@Profile("UserDatabase")
@EnableConfigurationProperties(UserDatabaseProperties.class)
public class UserDatabaseConfig {
    // Bean definitions
}
```
- Defines all beans required for the UserDatabase profile
- Replaces Spring Security's default user configuration
- Configures password encoder and UserDetailsService

#### 2. User (Domain Model)
```java
public class User {
    private String username;      // Unique identifier
    private String password;       // Encoded password
    private List<String> roles;    // User roles
    private boolean enabled;       // Account status
}
```

#### 3. UserService
```java
@Service
@Profile("UserDatabase")
public class UserService implements UserDetailsService {
    private final ConcurrentHashMap<String, User> users;
    private final PasswordEncoder passwordEncoder;
    
    // UserDetailsService implementation
    // CRUD operations
}
```
- Implements Spring Security's `UserDetailsService`
- Manages all user CRUD operations
- Thread-safe using ConcurrentHashMap
- Handles password encoding/validation

#### 4. UserController
```java
@RestController
@RequestMapping("/api/users")
@Profile("UserDatabase")
public class UserController {
    private final UserService userService;
    
    // REST endpoint implementations
}
```

#### 5. UserDatabaseProperties
```java
@ConfigurationProperties(prefix = "users")
public class UserDatabaseProperties {
    private List<InitialUser> initial;
    
    public static class InitialUser {
        private String username;
        private String password;
        private List<String> roles;
        private boolean enabled = true;
    }
}
```

## REST API Specification

### Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/users` | List all users | - | Array of UserDTO |
| GET | `/api/users/{username}` | Get specific user | - | UserDTO |
| POST | `/api/users` | Create new user | CreateUserRequest | UserDTO |
| PUT | `/api/users/{username}` | Update user | UpdateUserRequest | UserDTO |
| DELETE | `/api/users/{username}` | Delete user | - | 204 No Content |
| PUT | `/api/users/{username}/roles` | Replace all roles | String[] | UserDTO |
| POST | `/api/users/{username}/roles/{role}` | Add single role | - | UserDTO |
| DELETE | `/api/users/{username}/roles/{role}` | Remove single role | - | UserDTO |

### Data Transfer Objects

#### UserDTO (Response)
```json
{
  "username": "user1",
  "roles": ["USER", "ADMIN"],
  "enabled": true
}
```
**Note**: Password is never included in responses

#### CreateUserRequest
```json
{
  "username": "newuser",
  "password": "plaintext-password",
  "roles": ["USER"],
  "enabled": true
}
```

#### UpdateUserRequest
```json
{
  "password": "new-password",  // Optional
  "roles": ["USER", "ADMIN"],  // Optional
  "enabled": false             // Optional
}
```

### Security
- All endpoints secured with HTTP Basic Authentication
- Uses OAuth2 client credentials (e.g., `realguardio-client:secret-rg`)
- Authentication handled by Spring Security filter chain

### Error Responses
```json
{
  "error": "User not found"
}
```
- 400 Bad Request - Validation errors
- 404 Not Found - User doesn't exist
- 409 Conflict - Username already exists

## Configuration

### application-UserDatabase.yaml
```yaml
# Copy all settings from application.yaml except spring.security.user
server:
  port: 9000

logging:
  level:
    org.springframework.security: trace

spring:
  security:
    oauth2:
      authorizationserver:
        client:
          realguardio-client:
            token:
              access-token-time-to-live: 2m 
              refresh-token-time-to-live: 60m
            registration:
              client-id: "realguardio-client"
              client-secret: "{noop}secret-rg"
              client-authentication-methods:
                - "client_secret_basic"
              authorization-grant-types:
                - "authorization_code"
                - "refresh_token"
                - "client_credentials"
                - "password"
              redirect-uris:
                - "http://127.0.0.1:8080/login/oauth2/code/realguardio-client-oidc"
                - "http://127.0.0.1:8080/authorized"
                - "http://localhost:3000/api/auth/callback/oauth2-pkce"
              post-logout-redirect-uris:
                - "http://127.0.0.1:8080/logged-out"
              scopes:
                - "openid"
                - "profile"
                - "email"
                - "message.read"
                - "message.write"
            require-authorization-consent: true
            require-proof-key: true

# Initial users configuration
users:
  initial:
    - username: user1
      password: password
      roles:
        - USER
        - ADMIN
        - REALGUARDIO_ADMIN
      enabled: true
```

## Implementation Details

### Password Encoding
- Use Spring's `DelegatingPasswordEncoder` as primary encoder
- Default encoding: BCrypt
- Support for multiple formats via prefix (e.g., `{bcrypt}`, `{noop}`)
- Automatic encoding of plain text passwords on user creation/update

### Data Storage
- `ConcurrentHashMap<String, User>` for thread-safe operations
- Username as key for O(1) lookups
- No persistence - all data lost on restart
- Suitable for development and testing environments

### Spring Security Integration
```java
@Bean
public UserDetailsService userDetailsService() {
    return userService;
}

@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

### Validation Rules
- Username: Required, must be unique
- Password: Required (no length/complexity requirements)
- Roles: Optional, can be empty list
- Enabled: Defaults to true if not specified

## Testing Plan

### Unit Tests
1. **UserServiceTest**
   - Test user CRUD operations
   - Test UserDetailsService implementation
   - Test password encoding/validation
   - Test concurrent access scenarios

2. **UserControllerTest**
   - Test all REST endpoints
   - Test error scenarios (404, 409)
   - Test authentication/authorization

### Integration Tests
1. **Profile Activation Test**
   - Verify beans are created only when profile is active
   - Verify default configuration is replaced

2. **OAuth2 Flow Test**
   - Test password grant with created users
   - Test role-based authorization
   - Verify token generation with user claims

3. **API Security Test**
   - Test endpoints require authentication
   - Test client credentials authentication

### Manual Testing Checklist
- [ ] Start server with `--spring.profiles.active=UserDatabase`
- [ ] Verify initial user (user1) can authenticate
- [ ] Create new user via API
- [ ] Authenticate with new user credentials
- [ ] Update user roles and verify changes
- [ ] Delete user and verify authentication fails
- [ ] Test OAuth2 password grant with multiple users

## Development Steps

1. Create package structure: `com.example.authserver.userdb`
2. Implement domain model (`User`)
3. Create `UserDatabaseProperties` for configuration
4. Implement `UserService` with `UserDetailsService`
5. Create `UserController` with REST endpoints
6. Implement `UserDatabaseConfig` with bean definitions
7. Create `application-UserDatabase.yaml`
8. Write unit tests
9. Write integration tests
10. Manual testing and validation

## Security Considerations

- Passwords are never returned in API responses
- All passwords stored encoded (BCrypt by default)
- API requires authentication via client credentials
- In-memory storage means no persistent security risks
- Suitable for development/testing, not production

## Limitations

- No persistence - data lost on restart
- No pagination for user list endpoint
- No search/filter capabilities
- No password history or policies
- No audit logging
- Single-node only (no clustering support)

## Future Enhancements (Out of Scope)

- Add H2 file persistence option
- Implement user search/filtering
- Add pagination for large user lists
- Password policy enforcement
- Audit logging
- Rate limiting on API endpoints
- User account locking after failed attempts

## Success Criteria

- Server starts successfully with UserDatabase profile
- Initial user (user1) can authenticate with configured roles
- All REST endpoints function as specified
- OAuth2 flows work with dynamically created users
- No impact on default configuration when profile not active
- Thread-safe concurrent user operations
- All tests pass