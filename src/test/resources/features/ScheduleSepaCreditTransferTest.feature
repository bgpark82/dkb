Feature: scheduled sepa credit transfer test
  Background: Precondition: The user is logged in
    Given an access user token

  Scenario Outline: Creating a scheduled SEPA credit transfer is successful
    When create a scheduled SEPA credit transfer with
      | debtorIban | DE519 |
      | creditorIban | DE299 |
      | creditorName | <creditorName> |
      | executionOn | now  2 days |
#    Then the status code equals to 201
    Examples:
      | creditorName |
      | Ceyman Project |