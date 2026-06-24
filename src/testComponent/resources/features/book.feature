Feature: book management

  Scenario: the user reserves a book and sees it as reserved
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    When the user reserves the book with title "Clean Code"
    And the user gets all books
    Then the list should contains the following books
      | title      | author        | reserved |
      | Clean Code | Robert Martin | true     |

  Scenario: the user creates two books and retrieves both
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    And the user creates the book with title "The Pragmatic Programmer" and author "David Thomas"
    When the user gets all books
    Then the list should contains the following books
      | title                    | author        | reserved |
      | Clean Code               | Robert Martin | false    |
      | The Pragmatic Programmer | David Thomas  | false    |
