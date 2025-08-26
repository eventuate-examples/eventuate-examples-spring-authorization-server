# UserDatabase Feature Implementation Plan

## Overview
This plan implements the UserDatabase feature for the Spring Authorization Server using the Steel Thread methodology. Each thread builds incrementally, delivering value at each step while following TDD principles.

## Implementation Instructions for Coding Agent
- Mark each checkbox `[x]` when the task/step is completed
- Follow TDD principles: Write test first, make it pass, refactor if needed
- Each thread delivers a working increment of functionality
- Commit after each successful thread completion

## Steel Thread 1: Project Setup and CI/CD Pipeline

[x] **Setup project structure and automated deployment pipeline**

```text
Create the UserDatabase package structure and setup CI/CD pipeline for the authorization server.

Steps:
[x] 1. Create package structure `io.eventuate.examples.springauthorizationserver.userdb` in authorization-server/src/main/java
[x] 2. Create corresponding test package structure in authorization-server/src/test/java
[x] 3. Review existing GitHub Actions workflow (if present) or create new workflow file
[x] 4. Ensure the workflow builds and tests the authorization server module
[x] 5. Run tests to verify the build pipeline works
[x] 6. Commit changes with message "Setup UserDatabase package structure and CI/CD pipeline"
```

## Steel Thread 2: User Domain Model - Basic Creation

[x] **Create User class with basic fields**

```text
TDD: Write one test for User creation, implement, then commit.

Steps:
[x] 1. Write single test in UserTest:
    - Test User creation with username and password
[x] 2. Create User class with username and password fields
[x] 3. Run test to ensure it passes
[x] 4. Commit changes with message "Create basic User domain model"
```

## Steel Thread 3: User Domain Model - Add Roles

[x] **Add roles field to User**

```text
TDD: Write one test for roles, implement, then commit.

Steps:
[x] 1. Write single test:
    - Test User has roles list
[x] 2. Add roles field (List<String>) to User class
[x] 3. Run test to ensure it passes
[x] 4. Commit changes with message "Add roles to User domain model"
```

## Steel Thread 4: User Domain Model - Add Enabled Status

[ ] **Add enabled status to User**

```text
TDD: Write one test for enabled status, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test User enabled status defaults to true
[ ] 2. Add enabled field with default value true
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add enabled status to User domain model"
```

## Steel Thread 5: Configuration Properties

[ ] **Create configuration properties for initial users**

```text
Implement UserDatabaseProperties to load initial users from configuration.

Steps:
[ ] 1. Write UserDatabasePropertiesTest with tests for:
    - Loading properties from test configuration
    - Handling empty initial users list
    - Loading multiple initial users
[ ] 2. Create UserDatabaseProperties class with:
    - @ConfigurationProperties(prefix = "users")
    - Inner class InitialUser with username, password, roles, enabled
    - List<InitialUser> initial field
[ ] 3. Create test resources file application-test.yaml with sample configuration
[ ] 4. Run tests to ensure properties load correctly
[ ] 5. Commit changes with message "Add UserDatabaseProperties for initial user configuration"
```

## Steel Thread 6: UserService - Create User

[ ] **Implement user creation in UserService**

```text
TDD: Write one test for creating a user, implement, then commit.

Steps:
[ ] 1. Write single test in UserServiceTest:
    - Test creating a user stores it in memory
[ ] 2. Create UserService with ConcurrentHashMap storage
[ ] 3. Implement createUser method
[ ] 4. Run test to ensure it passes
[ ] 5. Commit changes with message "Implement user creation in UserService"
```

## Steel Thread 7: UserService - Find User

[ ] **Implement finding user by username**

```text
TDD: Write one test for finding user, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test findByUsername returns correct user
[ ] 2. Implement findByUsername method
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add findByUsername to UserService"
```

## Steel Thread 8: UserService - List All Users

[ ] **Implement listing all users**

```text
TDD: Write one test for listing users, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test findAll returns all users
[ ] 2. Implement findAll method
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add findAll to UserService"
```

## Steel Thread 9: UserService - UserDetailsService

[ ] **Implement UserDetailsService interface**

```text
TDD: Write one test for loadUserByUsername, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test loadUserByUsername returns Spring Security UserDetails
[ ] 2. Implement UserDetailsService interface
[ ] 3. Convert User to UserDetails in loadUserByUsername
[ ] 4. Run test to ensure it passes
[ ] 5. Commit changes with message "Implement UserDetailsService in UserService"
```

## Steel Thread 10: REST API Controller - List Users

[ ] **Implement GET /api/users endpoint**

```text
TDD: Write one test for listing users, implement, then commit.

Steps:
[ ] 1. Write single test in UserControllerTest:
    - Test GET /api/users returns all users
[ ] 2. Create UserDTO class (no password field)
[ ] 3. Create UserController with GET /api/users endpoint
[ ] 4. Run test to ensure it passes
[ ] 5. Commit changes with message "Add GET /api/users endpoint"
```

## Steel Thread 11: REST API Controller - Get Single User

[ ] **Implement GET /api/users/{username} endpoint**

```text
TDD: Write one test for getting single user, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test GET /api/users/{username} returns specific user
[ ] 2. Implement GET /api/users/{username} endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add GET /api/users/{username} endpoint"
```

## Steel Thread 12: REST API Controller - Handle User Not Found

[ ] **Add 404 handling for non-existent users**

```text
TDD: Write one test for 404 response, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test GET /api/users/{username} returns 404 for non-existent user
[ ] 2. Add error handling to GET endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add 404 handling for GET user endpoint"
```

## Steel Thread 13: REST API Controller - Create User

[ ] **Add POST endpoint for user creation**

```text
TDD: Write one test for user creation, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test POST /api/users creates user successfully
[ ] 2. Create CreateUserRequest DTO
[ ] 3. Implement POST /api/users endpoint
[ ] 4. Run test to ensure it passes
[ ] 5. Commit changes with message "Add POST endpoint for user creation"
```

## Steel Thread 14: REST API Controller - Handle Duplicate Username

[ ] **Add 409 conflict handling for duplicate usernames**

```text
TDD: Write one test for duplicate username, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test POST /api/users returns 409 for duplicate username
[ ] 2. Add duplicate checking to POST endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add 409 handling for duplicate usernames"
```

## Steel Thread 15: Spring Security Configuration

[ ] **Create UserDatabaseConfig for profile activation**

```text
Implement configuration class that wires everything together when UserDatabase profile is active.

Steps:
[ ] 1. Write UserDatabaseConfigTest:
    - Test beans are created when profile is active
    - Test beans are not created when profile is inactive
    - Test UserDetailsService bean is properly configured
    - Test PasswordEncoder bean is created
[ ] 2. Create UserDatabaseConfig class:
    - @Configuration, @Profile("UserDatabase")
    - @EnableConfigurationProperties(UserDatabaseProperties.class)
    - Create UserService bean
    - Create UserDetailsService bean (returning userService)
    - Create PasswordEncoder bean (DelegatingPasswordEncoder)
    - Initialize users from properties on startup
[ ] 3. Create application-UserDatabase.yaml with full configuration
[ ] 4. Run tests with profile active and inactive
[ ] 5. Commit changes with message "Add UserDatabaseConfig for Spring Security integration"
```

## Steel Thread 16: API Security Configuration

[ ] **Secure REST endpoints with authentication**

```text
Configure security for the REST API endpoints to require client credentials.

Steps:
[ ] 1. Write security tests:
    - Test endpoints return 401 without authentication
    - Test endpoints work with valid client credentials
    - Test Basic authentication with OAuth2 client credentials
[ ] 2. Update UserDatabaseConfig with security configuration:
    - Configure HttpSecurity for /api/users/** endpoints
    - Require authentication for all API endpoints
    - Support HTTP Basic authentication
[ ] 3. Test with actual OAuth2 client credentials (realguardio-client:secret-rg)
[ ] 4. Run all security tests
[ ] 5. Commit changes with message "Secure REST endpoints with client authentication"
```

## Steel Thread 17: Integration Testing - Profile Activation

[ ] **Create integration tests for profile activation**

```text
Write comprehensive integration tests to verify the UserDatabase profile works correctly.

Steps:
[ ] 1. Create UserDatabaseIntegrationTest class:
    - Test application starts with UserDatabase profile
    - Test initial users are loaded from configuration
    - Test default Spring Security user is NOT created
    - Test authentication with initial users
[ ] 2. Create test configuration with multiple initial users
[ ] 3. Run integration tests
[ ] 4. Commit changes with message "Add integration tests for UserDatabase profile activation"
```

## Steel Thread 18: Integration Testing - OAuth2 Password Grant

[ ] **Test OAuth2 password grant with dynamic users**

```text
Verify that OAuth2 password grant works with users from the UserDatabase.

Steps:
[ ] 1. Write OAuth2PasswordGrantTest:
    - Test password grant with initial user
    - Create new user via API
    - Test password grant with newly created user
    - Test grant fails with deleted user
    - Test role-based authorization in tokens
[ ] 2. Use REST-assured or Spring's TestRestTemplate for OAuth2 flows
[ ] 3. Verify JWT tokens contain correct user claims and roles
[ ] 4. Run OAuth2 integration tests
[ ] 5. Commit changes with message "Add OAuth2 password grant integration tests"
```

## Steel Thread 19: End-to-End Testing (Happy Path)

[ ] **Complete end-to-end testing with basic happy path**

```text
Perform comprehensive testing with focus on the simple happy path (read and create operations only).

Steps:
[ ] 1. Start server with --spring.profiles.active=UserDatabase
[ ] 2. Verify initial user (user1) can authenticate
[ ] 3. Test basic REST API operations:
    - List users
    - Get specific user
    - Create new user
    - Authenticate with new user
[ ] 4. Test OAuth2 flows with multiple users
[ ] 5. Run all tests to ensure nothing is broken
[ ] 6. Update documentation if needed
[ ] 7. Commit changes with message "Complete end-to-end testing for UserDatabase happy path"
```

## Steel Thread 20: Update User Password

[ ] **Add PUT endpoint to update user password**

```text
TDD: Write one test for password update, implement, then commit.

Steps:
[ ] 1. Write single test in UserControllerTest:
    - Test PUT /api/users/{username} updates password successfully
[ ] 2. Create UpdateUserRequest DTO with optional password field
[ ] 3. Implement PUT /api/users/{username} endpoint (password update only)
[ ] 4. Run test to ensure it passes
[ ] 5. Commit changes with message "Add PUT endpoint for password update"
```

## Steel Thread 21: Update User Enabled Status

[ ] **Extend PUT endpoint to update enabled status**

```text
TDD: Write one test for enabling/disabling users, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test PUT /api/users/{username} updates enabled status
[ ] 2. Add enabled field to UpdateUserRequest DTO
[ ] 3. Extend PUT endpoint implementation to handle enabled status
[ ] 4. Run test to ensure it passes
[ ] 5. Commit changes with message "Add enabled status update to PUT endpoint"
```

## Steel Thread 22: Replace User Roles

[ ] **Add PUT endpoint to replace all user roles**

```text
TDD: Write one test for replacing roles, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test PUT /api/users/{username}/roles replaces all roles
[ ] 2. Implement PUT /api/users/{username}/roles endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add PUT endpoint to replace user roles"
```

## Steel Thread 23: Add Single Role

[ ] **Add POST endpoint to add a single role**

```text
TDD: Write one test for adding a role, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test POST /api/users/{username}/roles/{role} adds role
[ ] 2. Implement POST /api/users/{username}/roles/{role} endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add POST endpoint to add single role"
```

## Steel Thread 24: Remove Single Role

[ ] **Add DELETE endpoint to remove a single role**

```text
TDD: Write one test for removing a role, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test DELETE /api/users/{username}/roles/{role} removes role
[ ] 2. Implement DELETE /api/users/{username}/roles/{role} endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add DELETE endpoint to remove single role"
```

## Steel Thread 25: Handle Update Non-Existent User

[ ] **Add 404 handling for update operations**

```text
TDD: Write one test for 404 on non-existent user, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test PUT /api/users/{username} returns 404 for non-existent user
[ ] 2. Add error handling to PUT endpoints
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add 404 handling for update operations"
```

## Steel Thread 26: Delete User

[ ] **Add DELETE endpoint for user removal**

```text
TDD: Write one test for user deletion, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test DELETE /api/users/{username} returns 204 on success
[ ] 2. Implement DELETE /api/users/{username} endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add DELETE endpoint for user removal"
```

## Steel Thread 27: Handle Delete Non-Existent User

[ ] **Add 404 handling for delete operation**

```text
TDD: Write one test for 404 on delete, implement, then commit.

Steps:
[ ] 1. Write single test:
    - Test DELETE /api/users/{username} returns 404 for non-existent user
[ ] 2. Add error handling to DELETE endpoint
[ ] 3. Run test to ensure it passes
[ ] 4. Commit changes with message "Add 404 handling for delete operation"
```

## Steel Thread 28: Verify Deleted User Cannot Authenticate

[ ] **Test authentication fails after user deletion**

```text
TDD: Write one integration test for authentication after deletion.

Steps:
[ ] 1. Write single integration test:
    - Create user, delete user, verify authentication fails
[ ] 2. Run test to ensure it passes (should already work)
[ ] 3. Commit changes with message "Verify deleted user cannot authenticate"
```

## Steel Thread 29: Error Handling and Validation

[ ] **Enhance error handling and validation**

```text
Improve error responses and input validation.

Steps:
[ ] 1. Write validation tests:
    - Test empty username validation
    - Test empty password validation
    - Test invalid role names
    - Test malformed requests
[ ] 2. Create ErrorResponse DTO for consistent error messages
[ ] 3. Add @ExceptionHandler methods in UserController
[ ] 4. Implement input validation with Bean Validation annotations
[ ] 5. Run all tests to ensure error handling works
[ ] 6. Commit changes with message "Enhance error handling and validation"
```

## Change History

- 2025-08-26: Removed Steel Thread 14 (Performance and Concurrency Testing) - These tests are already covered in Steel Thread 4 where thread-safe concurrent operations are tested as part of UserService implementation
- 2025-08-26: Reorganized steel threads to focus on happy path first - moved Update (PUT) and Delete operations to after end-to-end testing (threads 12-13), simplified end-to-end testing to focus on basic operations
- 2025-08-26: Refactored threads 12-20 to follow strict TDD methodology - each thread now implements a single test followed by implementation, ensuring true test-driven development