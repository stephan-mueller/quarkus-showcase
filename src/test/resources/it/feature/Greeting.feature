Feature: Greeting

  Scenario: Greet the world
    Given a greeting "Hello"
    When a user wants to greet
    Then the message is "Hello World!"

  Scenario Outline: Greet someone
    Given a greeting "<greeting>"
    When a user wants to greet "<name>"
    Then the message is "<greeting> <name>!"

    Examples:
      | greeting | name      |
      | Hola     | Christian |
      | Hey      | Max       |
      | Moin     | Stephan   |
