Feature: Inventory can add and check ingredients.
  The default inventory contains 15 amount of each ingredient.

  Scenario: Add ingredients into recipe
    Given a default inventory 
    When I add 3 amount of each ingredients
    Then I should have 18 amount of each ingredient
