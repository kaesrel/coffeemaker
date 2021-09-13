package edu.ncsu.csc326.coffeemaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.ncsu.csc326.coffeemaker.CoffeeMaker;
import edu.ncsu.csc326.coffeemaker.Inventory;
import edu.ncsu.csc326.coffeemaker.Recipe;
import edu.ncsu.csc326.coffeemaker.RecipeBook;
import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;


/**
 * A test class for testing the CoffeeMaker and Inventory class.
 * It assumes that the RecipeBook class is incomplete so test doubles are made.
 * Based on mocking RecipeBook and spying Recipe objects.
 * 
 */
public class MockCoffeeTest
{
    @Mock
    RecipeBook recipeBook;

    private Inventory inventory;
    // private Recipe recipe1;
    private static String addErrMsg = "Coffee maker should be able to add a valid recipe.";

    /**
	 * Initialize attributes annotated with \@Mock.
 	 * This ensures a new mock is created for each test.
	 */
	@Before
	public void setUp() throws RecipeException
    {
		MockitoAnnotations.initMocks(this);
        // recipe1 = createRecipe("recipe 1", 3, 10, 1, 1, 2);

        inventory = new Inventory();
	}


    /**
     * Test that a coffee is made correctly and ingredients are used correctly.
     * The amount of ingredients remain must be correct.
     * 
     * @throws RecipeException
     */
    @Test
    public void testMockMakeCoffee() throws RecipeException
    {
        Recipe cappuccino = createRecipe("espresso", 80, 4, 5, 0, 0);
        Recipe mocha = createRecipe("mocha", 90, 3, 3, 1, 2);
        Recipe recipeArray1[] = {cappuccino};
        Recipe recipeArray2[] = {cappuccino, mocha};

        CoffeeMaker coffeeMaker = new CoffeeMaker(recipeBook, inventory);

        when(recipeBook.addRecipe(cappuccino)).thenReturn(true);
        when(recipeBook.addRecipe(mocha)).thenReturn(true);
        when(recipeBook.getRecipes()).thenReturn(recipeArray1);

        assertTrue(addErrMsg, coffeeMaker.addRecipe(cappuccino));
        assertEquals(20, coffeeMaker.makeCoffee(0, 100));
        
        when(recipeBook.getRecipes()).thenReturn(recipeArray2);

        assertTrue(addErrMsg, coffeeMaker.addRecipe(mocha));
        assertEquals(10, coffeeMaker.makeCoffee(1, 100));

        verify(recipeBook, times(1)).addRecipe(cappuccino);
        verify(recipeBook, times(1)).addRecipe(mocha);
        verify(recipeBook, atLeast(2)).getRecipes();

        String expectedIngredients = ingredientString(8, 7, 14, 13);
        assertEquals(expectedIngredients, coffeeMaker.checkInventory());
    }

    /**
     * Test that the coffee will not be made without enough ingredients.
     * The change (payment) returned must remain the same.
     * 
     * @throws RecipeException
     */
    @Test
    public void testMockMakeCoffeeWithoutEnoughIngredients() throws RecipeException
    {
        Recipe bulkCoffee = createRecipe("bulk of coffee", 500, 100, 80, 10, 20);
        Recipe recipeArray[] = {bulkCoffee};
        CoffeeMaker coffeeMaker = new CoffeeMaker(recipeBook, inventory);

        when(recipeBook.addRecipe(bulkCoffee)).thenReturn(true);
        when(recipeBook.getRecipes()).thenReturn(recipeArray);

        assertTrue(addErrMsg, coffeeMaker.addRecipe(bulkCoffee));
        assertEquals(1000, coffeeMaker.makeCoffee(0, 1000));

        verify(recipeBook, times(1)).addRecipe(bulkCoffee);
        verify(recipeBook, atLeastOnce()).getRecipes();

    }

    /**
     * Test that the coffee will not be made without enough payment.
     * The change (payment) returned must remain the same.
     * 
     * @throws RecipeException
     */
    @Test
    public void testMockMakeCoffeeWithoutEnoughPayment() throws RecipeException
    {
        Recipe goldCoffee = createRecipe("gold coffee", 1_000_000, 13, 8, 1, 5);
        Recipe recipeArray[] = {goldCoffee};
        CoffeeMaker coffeeMaker = new CoffeeMaker(recipeBook, inventory);

        when(recipeBook.addRecipe(goldCoffee)).thenReturn(true);
        when(recipeBook.getRecipes()).thenReturn(recipeArray);

        assertTrue(addErrMsg, coffeeMaker.addRecipe(goldCoffee));
        assertEquals(200, coffeeMaker.makeCoffee(0, 200));

        verify(recipeBook, times(1)).addRecipe(goldCoffee);
        verify(recipeBook, atLeastOnce()).getRecipes();
    }

    /**
     * Test that the CoffeeMaker handles creating non-existent coffee correctly.
     */
    @Test
    public void testMockMakeCoffeeWithNonExistentRecipe()
    {
        CoffeeMaker coffeeMaker = new CoffeeMaker(recipeBook, inventory);
        Recipe recipeArray[] = new Recipe[3];
        when(recipeBook.getRecipes()).thenReturn(recipeArray);

        assertEquals(100, coffeeMaker.makeCoffee(1, 100));
        verify(recipeBook, atLeastOnce()).getRecipes();
    }

    /**
     * Test that the appropriate calls/invocations are made by Inventory to Recipe.
     * It spies how the inventory invokes getXxx() for each recipe.
     * 
     * @throws RecipeException
     */
    @Test
    public void testSpyRecipeCallCorrectly() throws RecipeException
    {
        CoffeeMaker coffeeMaker = new CoffeeMaker(recipeBook, inventory);
        Recipe cheapRecipe = Mockito.spy(createRecipe("cheap coffee", 30, 5, 2, 1, 2));
        Recipe pricyRecipe = Mockito.spy(createRecipe("pricy coffee", 200, 0, 2, 1, 4));
        Recipe recipeArray[] = {cheapRecipe, pricyRecipe};

        when(recipeBook.addRecipe(cheapRecipe)).thenReturn(true);
        when(recipeBook.addRecipe(pricyRecipe)).thenReturn(true);
        when(recipeBook.getRecipes()).thenReturn(recipeArray);

        assertTrue(addErrMsg, coffeeMaker.addRecipe(cheapRecipe));
        assertTrue(addErrMsg, coffeeMaker.addRecipe(pricyRecipe));

        assertEquals(70, coffeeMaker.makeCoffee(0, 100));
        assertEquals(100, coffeeMaker.makeCoffee(1, 100)); // not enough payment

        verify(cheapRecipe, atLeastOnce()).getPrice();
        verify(cheapRecipe, atLeastOnce()).getAmtCoffee();
        verify(cheapRecipe, atLeastOnce()).getAmtMilk();
        verify(cheapRecipe, atLeastOnce()).getAmtSugar();
        verify(cheapRecipe, atLeastOnce()).getAmtChocolate();

        verify(pricyRecipe, times(1)).getPrice();
        verify(pricyRecipe, never()).getAmtCoffee();
        verify(pricyRecipe, never()).getAmtMilk();
        verify(pricyRecipe, never()).getAmtSugar();
        verify(pricyRecipe, never()).getAmtChocolate();
    }

    // helper functions

    /**
	 * This function is a helper to create recipe with desired values quickly.
	 * 
	 */
	private static Recipe createRecipe(String name, int price,
            int coffee, int milk, int sugar, int chocolate)
            throws RecipeException
    {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setPrice(String.valueOf(price));
        recipe.setAmtCoffee(String.valueOf(coffee));
        recipe.setAmtChocolate(String.valueOf(chocolate));
        recipe.setAmtSugar(String.valueOf(sugar));
        recipe.setAmtMilk(String.valueOf(milk));

        return recipe;
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
