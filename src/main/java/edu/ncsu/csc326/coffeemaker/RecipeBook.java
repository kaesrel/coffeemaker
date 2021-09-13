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
 */
package edu.ncsu.csc326.coffeemaker;

public interface RecipeBook {
	
	/** Array of recipes in coffee maker*/
	/** Number of recipes in coffee maker */
	public final int NUM_RECIPES = 4; 
	public Recipe [] recipeArray = new Recipe[NUM_RECIPES];
	
	/**
	 * Returns the recipe array.
	 * @param r
	 * @return Recipe[]
	 */
	public Recipe[] getRecipes();
	
	public boolean addRecipe(Recipe r);

	/**
	 * Returns the name of the recipe deleted at the position specified
	 * and null if the recipe does not exist.
	 * @param recipeToDelete
	 * @return String
	 */
	public String deleteRecipe(int recipeToDelete);
	
	/**
	 * Returns the name of the recipe edited at the position specified
	 * and null if the recipe does not exist.
	 * @param recipeToEdit
	 * @param newRecipe
	 * @return String
	 */
	public String editRecipe(int recipeToEdit, Recipe newRecipe);

}

