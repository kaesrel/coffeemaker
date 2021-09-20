Feature: Purchasing the coffee from the coffee maker
  The inventory in the coffee maker contains 15 amount of each ingredient

  Scenario: Buy coffee normally
    Given a default coffee maker
    And add the recipes
    | name | price | coffee | milk | sugar | chocolate |
    | creamy | 60 | 2 | 4 | 2 | 0 |
    | gold star | 500 | 4 | 2 | 1 | 1 |
    | fibonacci | 233 | 21 | 13 | 5 | 8 |
    When I spend 800 to buy the coffee #1
    Then I have 300 as change.
    And the ingredients left are 
    | coffee | milk | sugar | chocolate |
    | 11 | 13 | 14 | 14 |
    
  Scenario: Buy coffee without enough ingredients
    Given a default coffee maker
    And add the recipes
    | name | price | coffee | milk | sugar | chocolate |
    | fibonacci | 233 | 34 | 21 | 8 | 13 |
    When I spend 800 to buy the coffee #0
    Then I have 800 as change.
    And the ingredients left are 
    | coffee | milk | sugar | chocolate |
    | 15 | 15 | 15 | 15 |

  Scenario: Buy coffee without enough payment
    Given a default coffee maker
    And add the recipes
    | name | price | coffee | milk | sugar | chocolate |
    | What a waste of money | 500000 | 1 | 1 | 1 | 1 |
    When I spend 800 to buy the coffee #0
    Then I have 800 as change.
    And the ingredients left are 
    | coffee | milk | sugar | chocolate |
    | 15 | 15 | 15 | 15 |

