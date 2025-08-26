# User Database Feature Discussion

## Original Idea
Enhance the authorization server with:
- Define a UserDatabase Spring profile with same configuration as application.yaml except:
  - In-memory database (hashmap or h2) for users instead of hardwired single user/password
  - Prepopulated with user/password
  - REST API for user CRUD

## Current Configuration Context
The existing authorization server uses:
- Spring Security's simple in-memory user configuration (spring.security.user.name/password)
- Single hardcoded user with roles: user1/password with roles [USER, ADMIN, REALGUARDIO_ADMIN]
- OAuth2 authorization server with client "realguardio-client"
- Users need to support multiple roles per user

## Q&A Session

### Question 1: User Data Model and Storage

**Answer: A - Minimal (Just essentials)**
- username (unique)
- password (encoded)
- roles (list)
- enabled (boolean)

### Question 2: Password Encoding Strategy

**Answer: C - DelegatingPasswordEncoder (Spring's flexible approach)**
- Supports multiple encoders simultaneously
- Can migrate from one encoder to another
- Prefixes passwords with encoder id
- Default uses BCrypt for new passwords

### Question 3: REST API Design

**Answer: B - CRUD plus role management**
- GET /api/users (list all)
- GET /api/users/{username} (get one)
- POST /api/users (create)
- PUT /api/users/{username} (update)
- DELETE /api/users/{username} (delete)
- PUT /api/users/{username}/roles (replace all roles)
- POST /api/users/{username}/roles/{role} (add role)
- DELETE /api/users/{username}/roles/{role} (remove role)

### Question 4: API Security and Authentication

**Answer: Basic Auth using client credentials**
- HTTP Basic authentication using OAuth2 client credentials
- Example: messaging-client/secret or realguardio-client/secret-rg
- Leverages existing OAuth2 client configuration

### Question 5: Initial Data Population

**Answer: C - Configurable via properties with generated application-UserDatabase.yaml**
- Define users in application-UserDatabase.yaml
- Pre-generated with current user: user1/password with [USER, ADMIN, REALGUARDIO_ADMIN]
- Format: `users.initial[0].username=user1`
- Can be overridden with environment variables

### Question 6: Database Implementation Choice

**Answer: A - ConcurrentHashMap-based**
- Simple, thread-safe Java collection
- No dependencies, minimal overhead
- Custom UserDetailsService implementation
- Good for <1000 users

### Question 7: Error Handling and Validation

**Answer: D - Minimal validation**
- Username: required, unique
- Password: required (no length check)
- Generic error messages

### Question 8: Package Structure and Class Organization

**Answer: A - Single package approach**
- `com.example.authserver.userdb`
- All classes in one package
- UserController, UserService, User, UserDatabaseConfig

### Question 9: Response Format and DTOs

**Answer: B - DTO with password hidden**
```json
{
  "username": "user1",
  "roles": ["USER", "ADMIN"],
  "enabled": true
}
```
Never return password in responses

### Question 10: Profile Activation and Configuration

**Answer: A - Profile-only activation**
- Run with: `--spring.profiles.active=UserDatabase`
- application-UserDatabase.yaml for all config
- Completely replaces default configuration

## Final Specification Summary

### Core Requirements
1. **Profile Name**: UserDatabase
2. **Activation**: `--spring.profiles.active=UserDatabase`
3. **Configuration File**: `application-UserDatabase.yaml`

### User Data Model
- username (String, unique identifier)
- password (String, encoded)
- roles (List<String>)
- enabled (boolean)

### Technical Implementation
- **Storage**: ConcurrentHashMap-based in-memory store
- **Password Encoding**: DelegatingPasswordEncoder (BCrypt default)
- **Package**: `com.example.authserver.userdb`
- **Main Classes**:
  - `UserDatabaseConfig` - @Configuration class with @Profile("UserDatabase")
  - `UserController` - REST API endpoints
  - `UserService` - Business logic and UserDetailsService implementation
  - `User` - Domain model
  - `UserDatabaseProperties` - @ConfigurationProperties for initial users

### REST API Endpoints
- `GET /api/users` - List all users
- `GET /api/users/{username}` - Get specific user
- `POST /api/users` - Create new user
- `PUT /api/users/{username}` - Update user
- `DELETE /api/users/{username}` - Delete user
- `PUT /api/users/{username}/roles` - Replace all roles
- `POST /api/users/{username}/roles/{role}` - Add single role
- `DELETE /api/users/{username}/roles/{role}` - Remove single role

### Security
- API secured with HTTP Basic Auth using OAuth2 client credentials
- Example: realguardio-client/secret-rg

### Response Format
```json
{
  "username": "user1",
  "roles": ["USER", "ADMIN"],
  "enabled": true
}
```
Passwords never returned in responses

### Initial Data
- Configured in `application-UserDatabase.yaml`
- Pre-populated with: user1/password, roles: [USER, ADMIN, REALGUARDIO_ADMIN]
- Format: `users.initial[0].username=user1`

### Validation Rules
- Username: required, must be unique
- Password: required (no length restrictions)
- Generic error messages for simplicity

### Implementation Notes
- Custom UserDetailsService implementation for Spring Security integration
- Thread-safe operations using ConcurrentHashMap
- No external database dependencies
- All user data lost on application restart (true in-memory)

### Spring Configuration Details
- **Profile Activation**: Classes annotated with `@Profile("UserDatabase")`
- **Key Beans**:
  - `UserDetailsService` bean (replaces default Spring Security user)
  - `PasswordEncoder` bean (DelegatingPasswordEncoder)
  - `UserService` bean (manages user CRUD operations)
  - `UserController` (auto-registered by component scan)
- **Property Binding**: `@ConfigurationProperties(prefix = "users")`
- **Security Config**: May need `@Order` to ensure proper security chain priority