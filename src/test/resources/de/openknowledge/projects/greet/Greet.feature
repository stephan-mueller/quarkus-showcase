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

  Scenario Outline: Get response to salutation
    When a user wants to get the response to "<salutation>"
    Then the response is "<response>"

    Examples:
      | salutation | response |
      | Marco      | Polo     |
      | Ping       | Pong     |
      | Moin       | Moin     |

  Scenario: Get response to unknown salutation
    When a user wants to get the response to "Polo"
    Then the response is not found