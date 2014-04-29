package me.oeed.InstantCraftingTable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class InventoryRecipes {
	
	private int maxRecipes;
	private IRecipe[] recipes;
	
	public InventoryRecipes(int max){
		maxRecipes = max;
		clearRecipes();
	}
	
	public int getCount(){
		for(int i = 0; i < recipes.length; i++) {
			if(recipes[i] == null)
				return i;
		}
		
		return 0;
	}
	
	public boolean addRecipe(IRecipe irecipe){
		int size = getCount();
		if(size >= maxRecipes || irecipe == null)
			return false;
		
		//avoid recipes with duplicate outputs
//		for(int i = 0; i < recipes.length; i++) {
//			if(recipes[i].getOutput())
//		}
		
		recipes[size] = irecipe;
		return true;
	}
	
	public IRecipe getIRecipe(int i)
	{
		return recipes[i];
	}
	
	public ItemStack getRecipeOutput(int i)
	{
		if(recipes[i] != null)
			return recipes[i].getRecipeOutput().copy();
		else
			return null;
	}
	
	public void clearRecipes(){
		recipes = null;
		recipes = new IRecipe[maxRecipes];
		
	}
	
}
