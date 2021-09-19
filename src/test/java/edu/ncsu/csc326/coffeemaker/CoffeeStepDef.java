package edu.ncsu.csc326.coffeemaker;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;

import static org.junit.Assert.*;
import io.cucumber.java.en.*;
import jdk.nashorn.internal.runtime.Debug;
import io.cucumber.datatable.*;
import java.util.*;


public class CoffeeStepDef
{
    CoffeeMaker coffeemaker;
    Recipe recipes[];
    Inventory inventory;
    int change;
    private static String addErrMsg = "Coffee maker should be able to add a valid recipe.";

    @Given("a default coffee maker")
    public void aDefaultCoffeeMaker() 
    {
        coffeemaker = new CoffeeMaker(new RecipeBookImpl(), new Inventory());
    }

    @Given("the recipes")
    public void theRecipes(DataTable dataTable) throws RecipeException
    {
        List<Map<String,String>> rawRecipes = dataTable.asMaps();

        recipes = new Recipe[rawRecipes.size()];
        int i = 0;

        for(Map<String,String> map : rawRecipes)
        {
            recipes[i] = createRecipe(map.get("name"), map.get("price"),
                    map.get("coffee"), map.get("milk"),
                    map.get("sugar"), map.get("chocolate"));
            i++;
        }
    }

    @Given("add the recipes")
    public void addTheRecipes(DataTable dataTable) throws RecipeException
    {
        List<Map<String,String>> rawRecipes = dataTable.asMaps();

        recipes = new Recipe[rawRecipes.size()];
        int i = 0;

        for(Map<String,String> map : rawRecipes)
        {
            recipes[i] = createRecipe(map.get("name"), map.get("price"),
                    map.get("coffee"), map.get("milk"),
                    map.get("sugar"), map.get("chocolate"));
            coffeemaker.addRecipe(recipes[i]);
            i++;
        }
    }

    @When("I add the recipes into a coffee maker")
    public void iAddTheRecipesIntoACoffeeMaker()
    {
        for (int i=0; i<recipes.length; i++)
        {
            assertTrue(addErrMsg, coffeemaker.addRecipe(recipes[i]));
        }
    }

    @Then("the coffee maker should contain those recipes")
    public void theCoffeeMakerShouldContainThoseRecipes()
    {
        assertEquals(recipes, flattenArray(coffeemaker.getRecipes()));
    }

    @When("I edit recipe number {int} to be")
    public void iEditRecipeNumberToBe(int index, DataTable dataTable)
            throws RecipeException
    {
        List<Map<String,String>> rawRecipes = dataTable.asMaps();
        Map<String,String> map = rawRecipes.get(0);
        Recipe newRecipe = createRecipe(map.get("name"), map.get("price"),
                map.get("coffee"), map.get("milk"),
                map.get("sugar"), map.get("chocolate"));

        coffeemaker.editRecipe(index, newRecipe);
    }

    @Then("the recipe number {int} of the coffee maker should be")
    public void theRecipeNumberOfTheCoffeeMakerShouldBe(int index, DataTable dataTable)
            throws RecipeException
    {
        List<Map<String,String>> rawRecipes = dataTable.asMaps();
        Map<String,String> map = rawRecipes.get(0);
        Recipe newRecipe = createRecipe(map.get("name"), map.get("price"),
                map.get("coffee"), map.get("milk"),
                map.get("sugar"), map.get("chocolate"));

        assertNotEquals(recipes[index], coffeemaker.getRecipes()[index]);
        assertEquals(newRecipe, coffeemaker.getRecipes()[index]);
    }

    @When("I remove the recipe number {int}")
    public void iRemoveTheRecipeNumber(int index) throws RecipeException
    {
        coffeemaker.deleteRecipe(index);
    }

    @Then("recipe number {int} of the coffee maker should be null")
    public void recipeNumberOfTheCoffeeMakerShouldBeNull(int index)
            throws RecipeException
    {
        assertNull("Recipe should be null", coffeemaker.getRecipes()[index]);
    }

    @Given("a default inventory")
    public void aDefaultInventory()
    {
        inventory = new Inventory();
    }

    @When("I add {int} amount of each ingredients")
    public void iAddAmountOfEachIngredients(int amount) throws InventoryException
    {
        String amtString = String.valueOf(amount);
        inventory.addCoffee(amtString);
        inventory.addMilk(amtString);
        inventory.addSugar(amtString);
        inventory.addChocolate(amtString);
    }

    @Then("I should have {int} amount of each ingredient")
    public void iShouldHaveAmountOfEachIngredient(int amount)
    {
        int val = 15+amount;
        String expectedInventory = ingredientString(val, val, val, val);
        assertEquals(expectedInventory, inventory.toString());
    }

    @When("I spend {int} to buy the coffee #{int}")
    public void iSpendToBuyTheCoffee(int payment, int index)
    {
        change = coffeemaker.makeCoffee(index, payment);
    }

    @Then("I have {int} as change.")
    public void iHaveAsAChange(int spareMoney)
    {
        assertEquals(spareMoney, change);
    }

    @Then("the ingredients left are")
    public void theIngredientsLeftAre(DataTable dataTable)
    {
        List<Map<String,String>> rawIngredients = dataTable.asMaps();
        Map<String,String> map = rawIngredients.get(0);
        String expectedInventory = ingredientString(
                Integer.parseInt(map.get("coffee")),
                Integer.parseInt(map.get("milk")),
                Integer.parseInt(map.get("sugar")),
                Integer.parseInt(map.get("chocolate"))
                );

        assertEquals(expectedInventory, coffeemaker.checkInventory());
    }


    /**
	 * This function is a helper to create recipe with desired values quickly.
	 * 
	 */
	private static Recipe createRecipe(String name, String price,
            String coffee, String milk, String sugar, String chocolate)
            throws RecipeException
    {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setPrice(price);
        recipe.setAmtCoffee(coffee);
        recipe.setAmtChocolate(chocolate);
        recipe.setAmtSugar(sugar);
        recipe.setAmtMilk(milk);

        return recipe;
    }

    /**
     * Creates a new array that removes the null element.
     */
    private static Recipe[] flattenArray(Recipe recipes[])
    {
        List<Recipe> list = new ArrayList<Recipe>();
        for (int i=0; i<recipes.length; i++)
        {
            if (recipes[i] != null)
            {
                list.add(recipes[i]);
            }
        }
        return list.toArray(new Recipe[0]);
    }

        /**
	 * Formats the given ingredients into the Inventory.toString() format.
	 * 
	 * @return the ingredients represented as a String with same format as Inventory.toString()
	 */
	private static String ingredientString(int coffee, int milk, int sugar, int chocolate)
	{
		StringBuffer buf = new StringBuffer();
    	buf.append("Coffee: ");
    	buf.append(coffee);
    	buf.append("\n");
    	buf.append("Milk: ");
    	buf.append(milk);
    	buf.append("\n");
    	buf.append("Sugar: ");
    	buf.append(sugar);
    	buf.append("\n");
    	buf.append("Chocolate: ");
    	buf.append(chocolate);
    	buf.append("\n");
		return buf.toString();
	}
}


// Recipe r = coffeemaker.getRecipes()[index];
// System.out.printf("%s, %d, %d, %d, %d, %d\n", r.getName(), r.getPrice(),
//         r.getAmtCoffee(), r.getAmtMilk(), r.getAmtSugar(),
//         r.getAmtChocolate());
// throws RecipeException
// throws InventoryException;

