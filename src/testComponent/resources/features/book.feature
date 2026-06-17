Feature: book management

  Scenario: the user creates two books and retrieves both
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    And the user creates the book with title "The Pragmatic Programmer" and author "David Thomas"
    When the user gets all books
    Then the list should contains the following books
      | title                    | author        |
      | Clean Code               | Robert Martin |
      | The Pragmatic Programmer | David Thomas  |
