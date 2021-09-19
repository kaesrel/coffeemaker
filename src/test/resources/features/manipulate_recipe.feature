Feature: Manipulating recipes to a coffee maker.
  A customer can add, remove, and edit recipes in the coffee maker.

  # value types for recipe 
  # | name | price | coffee | milk | sugar | chocolate |

  Scenario: add recipes to the coffee maker
    Given a default coffee maker
    And the recipes
    | name | price | coffee | milk | sugar | chocolate |
    | capuccino | 100 | 3 | 4 | 0 | 0 |
    | espresso | 80 | 5 | 0 | 0 | 0 |
    When I add the recipes into a coffee maker
    Then the coffee maker should contain those recipes

  Scenario: edit a recipe in a coffee maker
    Given a default coffee maker
    And add the recipes
    | name | price | coffee | milk | sugar | chocolate |
    | chocpuccino | 100 | 3 | 4 | 1 | 5 |
    | espresso | 80 | 5 | 0 | 0 | 0 |
    When I edit recipe number 0 to be
    | name | price | coffee | milk | sugar | chocolate |
    | somecoffee | 150 | 3 | 2 | 1 | 3 |
    Then the recipe number 0 of the coffee maker should be
    | name | price | coffee | milk | sugar | chocolate |
    | chocpuccino | 150 | 3 | 2 | 1 | 3 |

  Scenario: delete a recipe in a coffee maker
    Given a default coffee maker
    And add the recipes
    | name | price | coffee | milk | sugar | chocolate |
    | latte | 120 | 3 | 2 | 4 | 1 |
    | lungo | 140 | 8 | 0 | 0 | 0 |
    When I remove the recipe number 1
    Then recipe number 1 of the coffee maker should be null
