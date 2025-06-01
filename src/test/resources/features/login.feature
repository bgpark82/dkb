Feature: API Login
  As an API consumer
  I want to authenticate via the login endpoint
  So that I can access protected resources

  Scenario: Successful API login with valid credentials
    Given the application is running
    When a POST request is sent to "/api/login" with username "john.doe@example.com" and password "password123"
    Then the response status code should be 200
#    And the response body should contain "success": true
    And the response body should contain "message": "Login successful!"
    And the response body should contain "user.email": "john.doe@example.com"

#  Scenario: Failed API login with invalid credentials
#    Given the application is running
#    When a POST request is sent to "/api/login" with username "invalid@example.com" and password "wrongpass"
#    Then the response status code should be 200
#    And the response body should contain "success": false
#    And the response body should contain "message": "Invalid credentials."
