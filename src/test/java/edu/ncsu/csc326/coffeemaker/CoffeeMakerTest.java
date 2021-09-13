/*
 * Copyright (c) 2009,  Sarah Heckman, Laurie Williams, Dright Ho
 * All Rights Reserved.
 * 
 * Permission has been explicitly granted to the University of Minnesota 
 * Software Engineering Center to use and distribute this source for 
 * educational purposes, including delivering online education through
 * Coursera or other entities.  
 * 
 * No warranty is given regarding this software, including warranties as
 * to the correctness or completeness of this software, including 
 * fitness for purpose.
 * 
 * 
 * Modifications 
 * 20171114 - Ian De Silva - Updated to comply with JUnit 4 and to adhere to 
 * 							 coding standards.  Added test documentation.
 */
package edu.ncsu.csc326.coffeemaker;

// import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.*;
import java.io.*;
import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;
import jdk.jfr.Timestamp;

/**
 * Unit tests for CoffeeMaker class.
 * 
 * @author Chanathip Thumkanon
 */
public class CoffeeMakerTest {
	
	/**
	 * The object under test.
	 */
	private CoffeeMaker coffeeMaker;
	
	// Sample recipes to use in testing.
	private Recipe recipe1;
	private Recipe recipe2;
	private Recipe recipe3;
	private Recipe recipe4;

	/**
	 * Initializes some recipes to test with and the {@link CoffeeMaker} 
	 * object we wish to test.
	 * 
	 * @throws RecipeException  if there was an error parsing the ingredient 
	 * 		amount when setting up the recipe.
	 */
	@Before
	public void setUp() throws RecipeException {
		coffeeMaker = new CoffeeMaker();

		//Set up for r1
		recipe1 = new Recipe();
		recipe1.setName("Coffee");
		recipe1.setAmtChocolate("0");
		recipe1.setAmtCoffee("3");
		recipe1.setAmtMilk("1");
		recipe1.setAmtSugar("1");
		recipe1.setPrice("50");
		
		//Set up for r2
		recipe2 = new Recipe();
		recipe2.setName("Mocha");
		recipe2.setAmtChocolate("20");
		recipe2.setAmtCoffee("3");
		recipe2.setAmtMilk("1");
		recipe2.setAmtSugar("1");
		recipe2.setPrice("75");
		
		//Set up for r3
		recipe3 = new Recipe();
		recipe3.setName("Latte");
		recipe3.setAmtChocolate("0");
		recipe3.setAmtCoffee("3");
		recipe3.setAmtMilk("3");
		recipe3.setAmtSugar("1");
		recipe3.setPrice("100");
		
		//Set up for r4
		recipe4 = new Recipe();
		recipe4.setName("Hot Chocolate");
		recipe4.setAmtChocolate("4");
		recipe4.setAmtCoffee("0");
		recipe4.setAmtMilk("1");
		recipe4.setAmtSugar("1");
		recipe4.setPrice("65");
	}
	
	
	/**
	 * Test that the amount of ingredients are added correctly into inventory.
	 * 
	 * @throws InventoryException  if there was an error parsing the quanity
	 * 		to a positive integer.
	 */
	@Test
	public void testAddInventory() throws InventoryException {
		coffeeMaker.addInventory("4","7","3","9");
		String expectedInventory = ingredientString(19, 22, 18, 24);
		assertEquals(expectedInventory, coffeeMaker.checkInventory());
	}
	
	/**
	 * Test that an Exception is thrown when improper arguments are being parsed.
	 * Illegal arguments are such as negative or non-numeric string.
	 * 
	 * @throws InventoryException  if there was an error parsing the quanity
	 * 		to a positive integer.
	 */
	@Test(expected = InventoryException.class)
	public void testAddInventoryException() throws InventoryException {
		coffeeMaker.addInventory("4", "-1", "asdf", "3");
	}
	
	/**
	 * Test that a coffee is successfully made and ingredients used correctly.
	 * This assumes that the recipe is valid.
	 */
	@Test
	public void testMakeCoffee() {
		coffeeMaker.addRecipe(recipe1);
		assertEquals(25, coffeeMaker.makeCoffee(0, 75));
	}

	/**
	 * Test making a coffee without having enough inventory.
	 * The ingredients in should not be modified.
	 * The change should be the same as the payment.
	 */
	@Test
	public void testMakeCoffeeWithoutEnoughIngredients() throws RecipeException 
	{
		int payment = 100; 
		Recipe tooMuchCoffee = createRecipe("too much Coffee", "50", "30", "0", "0", "0");
		Recipe tooMuchMilk = createRecipe("too much Milk", "50", "0", "0", "0", "30");
		Recipe tooMuchSugar = createRecipe("too much Sugar", "50", "0", "0", "30", "0");
		Recipe tooMuchChocolate = createRecipe("too much Chocolate", "50", "0", "30", "0", "0");
		
		coffeeMaker.addRecipe(tooMuchCoffee);
		coffeeMaker.addRecipe(tooMuchMilk);
		coffeeMaker.addRecipe(tooMuchSugar);
		String expectedInventory = ingredientString(15, 15, 15, 15);
		

		assertEquals(payment, coffeeMaker.makeCoffee(0, payment));
		assertEquals(expectedInventory, coffeeMaker.checkInventory());

		assertEquals(payment, coffeeMaker.makeCoffee(1, payment));
		assertEquals(expectedInventory, coffeeMaker.checkInventory());

		assertEquals(payment, coffeeMaker.makeCoffee(2, payment));
		assertEquals(expectedInventory, coffeeMaker.checkInventory());

		// coffeeMaker.deleteRecipe(2);
		// coffeeMaker.addRecipe(tooMuchChocolate);
		coffeeMaker.editRecipe(2, tooMuchChocolate);
		assertEquals(payment, coffeeMaker.makeCoffee(2, payment));
		assertEquals(expectedInventory, coffeeMaker.checkInventory());
	}

	/**
	 * Test making a coffee without having enough funds.
	 * The ingredients in should not be modified.
	 * The change should be the same as the payment.
	 */
	@Test
	public void testMakeCoffeeWithoutEnoughPayment() throws RecipeException 
	{
		int payment = 20; 
		coffeeMaker.addRecipe(recipe1);
		assertEquals(payment, coffeeMaker.makeCoffee(0, payment));

		// should not modify ingredients
		String expectedInventory = ingredientString(15, 15, 15, 15);
		assertEquals(expectedInventory, coffeeMaker.checkInventory());

	}


	/**
	 * Test whether the inventory is correctly checked.
	 * Also test if the ingredients are used correctly.
	 * 
	 * @throws RecipeException  if there was an error in setting invalid values
	 *      to the recipe.
	 */
	@Test
	public void testUseIngredients() throws RecipeException
	{
		Recipe myRecipe = createRecipe("mycoffee", "30", "3", "3", "3", "3");
		coffeeMaker.addRecipe(myRecipe);
		coffeeMaker.makeCoffee(0, 50);

		String expectedInventory = ingredientString(12, 12, 12, 12);
		assertEquals(expectedInventory, coffeeMaker.checkInventory());

	}

	/**
	 * Test that there can be only up to 3 recipes in the recipe book.
	 */
	@Test
	public void testNoMoreThan3Recipes()
	{
		coffeeMaker.addRecipe(recipe1);
		coffeeMaker.addRecipe(recipe2);
		coffeeMaker.addRecipe(recipe3);
		assertFalse("There can only be up to 3 recipes.", coffeeMaker.addRecipe(recipe4));
	}

	/**
	 * Test that a new recipe with the same name cannot be added.
	 * 
	 * @throws RecipeException  if there was an error in setting invalid values
	 *      to the recipe.
	 */
	@Test
	public void testNoRecipeWithSameName() throws RecipeException
	{
		Recipe dupe1 = createRecipe("Dupe", "10", "1", "2", "2", "1");
		Recipe dupe2 = createRecipe("Dupe", "20", "1", "0", "0", "0");

		coffeeMaker.addRecipe(dupe1);
		assertFalse("Cannot add recipe with the same name.", coffeeMaker.addRecipe(dupe2));
	}

	/**
	 * Test that the price of the recipe cannot be set to zero.
	 * 
	 * @throws RecipeException  if there was an error in setting invalid values
	 *      to the recipe.
	 */
	@Test(expected = RecipeException.class)
	public void testNoRecipeWithZeroPrice() throws RecipeException
	{
		Recipe freeCoffee = new Recipe();
		freeCoffee.setPrice("0");
	}

	/**
	 * Test that the price of the recipe cannot be set to negative value.
	 * 
	 * @throws RecipeException  if there was an error in setting invalid values
	 *      to the recipe.
	 */
	@Test(expected = RecipeException.class)
	public void testNoRecipeWithNegativePrice() throws RecipeException
	{
		Recipe embezzlement = new Recipe();
		embezzlement.setPrice("-99999");
	}

	/**
	 * Test that the recipe is deleted correctly.
	 * 
	 */
	@Test
	public void testDeleteRecipe()
	{
		coffeeMaker.addRecipe(recipe1);
		coffeeMaker.deleteRecipe(0);
		assertEquals(null, coffeeMaker.getRecipes()[0]);
	}

	/**
	 * Test that recipes cannot be deleted if it does not exist.
	 * 
	 * @throws RecipeException  if there was an error in setting invalid values
	 *      to the recipe.
	 */
	@Test
	public void testDeleteNonExistentRecipe() throws RecipeException
	{
		assertEquals(null, coffeeMaker.deleteRecipe(0));
	}


	/**
	 * Test that the recipe is edited correctly.
	 * All values are modified except the recipe name.
	 * 
	 * @throws RecipeException  if there was an error in setting invalid values
	 *      to the recipe.
	 */
	@Test
	public void testEditRecipe() throws RecipeException
	{
		coffeeMaker.addRecipe(recipe1);
		String name = recipe1.getName();

		Recipe coldStyle = createRecipe("Does not matter", "50", "5", "0", "0", "0");
		coffeeMaker.editRecipe(0, coldStyle);
		assertEquals(name, coffeeMaker.getRecipes()[0].getName());

	}

	/**
	 * Test that the inventory checking displays the ingredients correctly.
	 * @see ingredientString() method
	 * 
	 * @throws RecipeException
	 */
	@Test
	public void testCheckingInventory() throws RecipeException 
	{
		String expectedInventory = ingredientString(15, 15, 15, 15);
		assertEquals(expectedInventory, coffeeMaker.checkInventory());

	}


	// @Test
	// public void testMainMenu()
	// {

		// String input = "";
		// ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        // System.setIn(in);

		// Main app = new Main();

		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// System.setOut(new PrintStream(out));


		// String args[] = {};
		// Main.main(args);
		
		// String consoleOutput = "1. Add a recipe" + System.getProperty("line.separator");
		// consoleOutput += "2. Delete a recipe" + System.getProperty("line.separator");
		// consoleOutput += "3. Edit a recipe" + System.getProperty("line.separator");
		// consoleOutput += "4. Add inventory" + System.getProperty("line.separator");
		// consoleOutput += "5. Check inventory" + System.getProperty("line.separator");
		// consoleOutput += "6. Make coffee" + System.getProperty("line.separator");
		// consoleOutput += "0. Exit\n" + System.getProperty("line.separator");

		// consoleOutput += "Please press the number that corresponds to what you would like the coffee maker to do." + System.getProperty("line.separator");
		
		// assertEquals(consoleOutput, out.toString());
		
		// System.setIn(System.in);
		// System.setOut(System.out);

	// }

	/**
	 * Test that the coffee is added into inventory correctly.
	 * 
	 * @throws InventoryException - when values that are not positive
	 *     integer are given
	 */
	@Test
	public void testAddCoffee() throws InventoryException
	{
		inventoryAddTestHelper(coffeeMaker, 10, 0, 0, 0);

		inventoryExceptionTestHelper(coffeeMaker, "abcd", "0", "0", "0",
				"Non-integer coffee value added to coffee maker's inventory");

		inventoryExceptionTestHelper(coffeeMaker, "-1", "0", "0", "0",
				"Negative integer coffee value added to coffee maker's inventory");
	}

	/**
	 * Test that the milk is added into inventory correctly.
	 * 
	 * @throws InventoryException - when values that are not positive
	 *     integer are given
	 */
	@Test
	public void testAddMilk() throws InventoryException
	{
		inventoryAddTestHelper(coffeeMaker, 0, 10, 0, 0);

		inventoryExceptionTestHelper(coffeeMaker, "0", "abcd", "0", "0",
				"Non-integer milk value added to coffee maker's inventory");

		inventoryExceptionTestHelper(coffeeMaker, "0", "-1", "0", "0",
				"Negative integer milk value added to coffee maker's inventory");
	}

	/**
	 * Test that the sugar is added into inventory correctly.
	 * 
	 * @throws InventoryException - when values that are not positive
	 *     integer are given
	 */
	@Test
	public void testAddSugar() throws InventoryException
	{
		inventoryExceptionTestHelper(coffeeMaker, "0", "0", "abcd", "0",
				"Non-integer sugar value added to coffee maker's inventory");

		inventoryExceptionTestHelper(coffeeMaker, "0", "0", "-1", "0",
				"Negative integer sugar value added to coffee maker's inventory");

		inventoryAddTestHelper(coffeeMaker, 0, 0, 10, 0);
	}

	/**
	 * Test that the chocolate is added into inventory correctly.
	 * 
	 * @throws InventoryException - when values that are not positive
	 *     integer are given
	 */
	@Test
	public void testAddChocolate() throws InventoryException
	{
		inventoryAddTestHelper(coffeeMaker, 0, 0, 0, 10);

		inventoryExceptionTestHelper(coffeeMaker, "0", "0", "0", "abcd",
				"Non-integer chocolate value added to coffee maker's inventory");

		inventoryExceptionTestHelper(coffeeMaker, "0", "0", "0", "-1",
				"Negative integer chocolate value added to coffee maker's inventory");
	}

	/**
	 * Test that the inventory will not set invalid ingredients value.
	 * The amount of ingredients in the inventory must still be the same.
	 * 
	 */
	@Test
    public void testSetInvalidIngredients()
    {
        Inventory inventory = new Inventory();
        inventory.setCoffee(-1);
        inventory.setMilk(-1);
        inventory.setChocolate(-1);
        inventory.setSugar(-1);

        String expectedInventory = ingredientString(15, 15, 15, 15);
		assertEquals(expectedInventory, inventory.toString());

    }

	/**
	 * Test that a coffee will not be created without a valid recipe.
	 */
	@Test
	public void testMakeNonExistentCoffee()
	{
		int payment = 100;
		assertEquals(100, coffeeMaker.makeCoffee(0, payment));
	}

	/**
	 * Test that a non-existent recipe cannot be edited.
	 * No new recipe will be created or added by editing a non-existent recipe.
	 */
	@Test
	public void testEditNonExistentRecipe() throws RecipeException
	{
		assertNull("Non-existent recipe must be non-modifiable.", coffeeMaker.editRecipe(0, recipe1));
	}

	/**
	 * Test that multiple coffee makers works independently and correctly.
	 * The variables must not be shared among coffee maker objects.
	 * 
	 * @throws RecipeException
	 */
	@Test
    public void testMultipleCoffeeMaker() throws RecipeException
    {
		CoffeeMaker nescafe = new CoffeeMaker();
		CoffeeMaker starbucks = new CoffeeMaker();

		Recipe instantCoffee = createRecipe("instant coffee", "10", "2", "0", "1", "0");
		Recipe caffeMocha = createRecipe("caffe mocha", "140", "5", "2", "2", "4");

		nescafe.addRecipe(instantCoffee);
		starbucks.addRecipe(caffeMocha);

		assertEquals(10, nescafe.makeCoffee(0, 20));
		assertEquals(60, starbucks.makeCoffee(0, 200));

		String expectedNescafeInventory = ingredientString(13, 15, 14, 15);
		String expectedStarbucksInventory = ingredientString(10, 11, 13, 13);

		assertEquals(expectedNescafeInventory, nescafe.checkInventory());
		assertEquals(expectedStarbucksInventory, starbucks.checkInventory());
    }

	// test subroutine helper

	/**
	 * This is a subroutine to help test adding ingredient into the inventory.
	 * It does not support adding invalid ingredients.
	 * 
	 * @throws InventoryException
	 */
	private static void inventoryAddTestHelper(CoffeeMaker coffeeMaker,
			int coffee, int milk, int sugar, int chocolate) 
			throws InventoryException
	{
		coffeeMaker.addInventory(String.valueOf(coffee),
				String.valueOf(milk),
				String.valueOf(sugar),
				String.valueOf(chocolate)); // coffee, milk, sugar, chocolate

		String expectedInventory = ingredientString(15+coffee,
			15+milk, 15+sugar, 15+chocolate);
		assertEquals(expectedInventory, coffeeMaker.checkInventory());
	}

	/**
	 * This is a subroutine to help test adding invalid ingredients.
	 * It expects at least 1 ingredient value to be invalid.
	 * 
	 * @param errMsg the desired error message to be given when this helper fails.
	 */
	private static void inventoryExceptionTestHelper(CoffeeMaker coffeeMaker,
			String coffee, String milk, String sugar, String chocolate,
			String errMsg)
	{
		try 
		{
			coffeeMaker.addInventory(coffee, milk, sugar, chocolate);
			fail(errMsg);
		}
		catch (InventoryException e) {} // expected result
	}

	// misc helper functions

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

	/**
	 * This function is a helper to create recipe with desired values quickly.
	 * 
	 */
	private static Recipe createRecipe(String name, String price,
			String coffee, String chocolate, String sugar, String milk)
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
}
