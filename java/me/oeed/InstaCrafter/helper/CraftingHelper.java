package me.oeed.InstaCrafter.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.oeed.InstaCrafter.InventoryRecipes;
import me.oeed.InstaCrafter.lib.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class CraftingHelper {

    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;
    
    private static final int mouseLeftClick = 0;
    private static final int mouseRightClick = 1;
    private static final int shiftNotHeld = 0;
    private static final int shiftHeld = 1;
    
	private static final int maxRecipes = 500 * 9;
	private static final int maxRecursion = 10;
	private static final List<IRecipe> allRecipes = getAllRecipes();
	
	
	public static InventoryRecipes getAvailableRecipes(InventoryPlayer invPlayer){
		InventoryRecipes availableRecipes = new InventoryRecipes(500 * 9);
		//List<IRecipe> allRecipes = getAllRecipes();


		for(int i = 0; i < allRecipes.size(); i++) {
			IRecipe recipe = (IRecipe) allRecipes.get(i);
			InventoryPlayer temp = new InventoryPlayer(invPlayer.player);
	        temp.copyInventory(invPlayer);
	        //&& recipe instanceof ShapedOreRecipe && recipe.getRecipeOutput().itemID == Item.stick.itemID
	        List<ItemStack> usedIngredients = new ArrayList();
	        List<IRecipe> usedRecipes = new ArrayList();
			if(recipe != null && canCraftRecipe(temp, recipe, usedIngredients, usedRecipes)){// && (recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes)){
				//System.out.println("Adding "+i);
				availableRecipes.addRecipe(recipe);
			}
			else{

				//System.out.println("Cannot craft: "+recipe.getRecipeOutput());
				//System.out.println("Cannot craft: "+recipe);
			}
		}
        
        //create a list of the ingredients used
        
        //loop through all the ingredients
        //check if the item is in the inventory, if so consume
        //if it's not, get all recipes that create the required item and try to craft it
		
		return availableRecipes;
	}
	
	
	

	
	private static ItemStack craftingWindowClick(ContainerWorkbench craftingTable, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
        short short1 = craftingTable.getNextTransactionID(par5EntityPlayer.inventory);
        ItemStack itemstack = craftingTable.slotClick(par2, par3, par4, par5EntityPlayer);
        try {   
	        Field field = PlayerControllerMP.class.getDeclaredField("netClientHandler");
	        field.setAccessible(true);
	        NetClientHandler netClientHandler = (NetClientHandler) field.get(Minecraft.getMinecraft().playerController);
	        netClientHandler.addToSendQueue(new Packet102WindowClick(craftingTable.windowId, par2, par3, par4, itemstack, short1));
	        return itemstack;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static boolean doCraft(IRecipe recipe, InventoryPlayer invPlayer, ContainerWorkbench craftingTable, InventoryCrafting craftMatrix, IInventory craftResult){
		Minecraft client = FMLClientHandler.instance().getClient();
        if(recipe instanceof ShapedRecipes){
        	LogHelper.log("Crafting: "+recipe.getRecipeOutput());
        	int itemsSlot = 0;
    		for(int y = 0; y < ((ShapedRecipes)recipe).recipeHeight; y++) {
            	for(int x = 0; x < ((ShapedRecipes)recipe).recipeWidth; x++) {
            		int matrixSlot = (((ShapedRecipes)recipe).recipeWidth - x - 1 + y * ((ShapedRecipes)recipe).recipeWidth);
        			ItemStack itemStack = ((ShapedRecipes)recipe).recipeItems[(((ShapedRecipes)recipe).recipeWidth - x - 1 + y * ((ShapedRecipes)recipe).recipeWidth)];
        			if(itemStack == null)
        				continue;
        			else
        				itemsSlot ++;
        			int invSlot = InventoryHelper.findItemInInventory(invPlayer, itemStack);

        			
            		//TODO: use a less hard coded way
            		//converting the player invSlot to the crafting table slot, this is pretty hacky and should use a better method
            		if(invSlot >= 0 && invSlot <= 8){
            			//hotbar
            			invSlot += 37;
            		}
            		else if(invSlot >= 9 && invSlot <= 45){
            			invSlot += 1;            			
            		}
        			
        			craftingWindowClick(craftingTable, invSlot, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the players item slot that contains the needed item
        			craftingWindowClick(craftingTable, matrixSlot + 1, mouseRightClick, shiftNotHeld, invPlayer.player); //'fake' click the needed item in to the crafting table
        			craftingWindowClick(craftingTable, invSlot, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the players item back to the slot it was in
        			
        		}
        	}
    		
			LogHelper.log("Output: "+ craftingTable.getSlot(0).getStack());
			//TODO: handle if the item isn't there and there's no inv space
			craftingWindowClick(craftingTable, 0, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the recipe output, (hopefully) sending the item to the inventory
			LogHelper.log("Final: "+ craftingTable.getSlot(0).getStack());
			return true; 
        }
        else{
        	LogHelper.log("Not shaped");
        }
		
		return false;
	}

	public static ItemStack craftItem(ItemStack itemStack, InventoryPlayer invPlayer, List<ItemStack> usedIngredients, List<IRecipe> usedRecipes, ContainerWorkbench craftingTable, InventoryCrafting craftMatrix, IInventory craftResult){
		if(itemStack == null)
			return null;
		
		ArrayList<IRecipe> recipes = getRecipesForItemStack(itemStack);
		for(int i = 0; i < recipes.size(); i++) {
			IRecipe recipe = (IRecipe) recipes.get(i);
			InventoryPlayer temp = new InventoryPlayer(invPlayer.player);
	        temp.copyInventory(invPlayer);
			if(recipe != null && canCraftRecipe(temp, recipe, usedIngredients, usedRecipes)){
				for(int r = usedRecipes.size() - 1; r >= 0 ; r--)
					doCraft(usedRecipes.get(r), invPlayer, craftingTable, craftMatrix, craftResult);
				return null;
			}
		}
        return null;
	}
	
	public static ArrayList<IRecipe> getRecipesForItemStack(ItemStack itemStack){
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		for(int i = 0; i < allRecipes.size(); i++) {
			IRecipe recipe = (IRecipe) allRecipes.get(i);
			if(recipe.getRecipeOutput() != null && InventoryHelper.isStackEqualTo(recipe.getRecipeOutput(), itemStack)){
				recipes.add(recipe);
			}
		}
		return recipes;
	}
	
	public static ItemStack[] getRecipeIngredients(IRecipe recipe){
		ArrayList recipeItems = null;
		if(recipe instanceof ShapedRecipes) {
			recipeItems = new ArrayList(Arrays.asList(((ShapedRecipes) recipe).recipeItems));
		}
		else if(recipe instanceof ShapedOreRecipe){
			recipeItems = new ArrayList(Arrays.asList(ReflectionHelper.<Object[], ShapedOreRecipe> getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe) recipe, 3)));
		}
		else if(recipe instanceof ShapelessOreRecipe) {
            recipeItems = new ArrayList(ReflectionHelper.<List, ShapelessOreRecipe> getPrivateValue(ShapelessOreRecipe.class, (ShapelessOreRecipe) recipe, 1));
		}
		else if(recipe instanceof ShapelessRecipes) {
			recipeItems = new ArrayList(((ShapelessRecipes) recipe).recipeItems);
		}
		else{
			//TODO: mod support
			return null;
		}
		
		for(int i = 0; i < recipeItems.size(); i++) {
			if(recipeItems.get(i) instanceof ArrayList){
				if(((ArrayList)recipeItems.get(i)).size() > 0)
					recipeItems.set(i, ((ArrayList)recipeItems.get(i)).get(0));
				else{
					recipeItems.set(i, null);
				}
					
			}
		}

		recipeItems.removeAll(Collections.singleton(null));
		
		return (ItemStack[])recipeItems.toArray(new ItemStack[recipeItems.size()]);
	}
	
	public static boolean canCraftRecipe(InventoryPlayer invPlayer, IRecipe recipe, List<ItemStack> usedIngredients, List<IRecipe> usedRecipes){
		return canCraftRecipe(invPlayer, recipe, usedIngredients, usedRecipes, 0);
	}
	
	public static boolean canCraftRecipe(InventoryPlayer invPlayer, IRecipe recipe, List<ItemStack> usedIngredients, List<IRecipe> usedRecipes,  int recursion){
		return canCraftRecipe(invPlayer, recipe, usedIngredients, usedRecipes, recursion, new ArrayList<ItemStack>());
	}
	
	public static boolean canCraftRecipe(InventoryPlayer invPlayer, IRecipe recipe, List<ItemStack> usedIngredients, List<IRecipe> usedRecipes, int recursion, ArrayList<ItemStack> blacklist){
		if(recursion >= maxRecursion)
			return false;
		ItemStack[] ingredients = getRecipeIngredients(recipe);
		if(ingredients == null){
			return false;
		}
		
		usedRecipes.add(recipe);

//		System.out.println("i: "+ingredients.length);

		boolean canCraft = false;
		ingredients: for(int i = 0; i < ingredients.length; i++) {
			for(int i2 = 0; i2 < blacklist.size(); i2++) {
				if(InventoryHelper.isStackEqualTo(ingredients[i], blacklist.get(i2))){
//					System.out.println("blacklisted: "+i+" "+ingredients[i]);
					break ingredients;
				}
				
			}
			int slot = InventoryHelper.findItemInInventory(invPlayer, ingredients[i]);

//			System.out.println("r: "+recursion);
//			System.out.println("Ingredient: "+i+" "+ingredients[i]+" "+ingredients[i].getDisplayName()+" "+slot);
			if(slot != -1 && InventoryHelper.consumeItem(invPlayer, slot, usedIngredients)){

//				System.out.println("yep");
				canCraft = true;
				//the item is in the inventory
				//return true;
				//continue;
			}
			else if(recursion < maxRecursion){
				canCraft = false;
//				System.out.println("nope "+ingredients[i]);
				ArrayList<IRecipe> recipes = getRecipesForItemStack(ingredients[i]);
				//inventory doesn't have the required item, try to craft it using the available recipes
				ArrayList<ItemStack> _blacklist = new ArrayList(2);
				_blacklist.add(recipe.getRecipeOutput());
				_blacklist.add(ingredients[i]);
				
				for(int i2 = 0; i2 < recipes.size(); i2++) {
//					try {
//					    Thread.sleep(250);
//					} catch(InterruptedException ex) {
//					    Thread.currentThread().interrupt();
//					}

					
					
					if(canCraftRecipe(invPlayer, recipes.get(i2), usedIngredients, usedRecipes, recursion + 1, _blacklist)){
						InventoryHelper.addItemToInventory(invPlayer, recipes.get(i2).getRecipeOutput());
						int _slot = InventoryHelper.findItemInInventory(invPlayer, ingredients[i]);
						InventoryHelper.consumeItem(invPlayer, _slot, usedIngredients);
						canCraft = true;
//						System.out.println("subcrafted "+ingredients[i].getDisplayName());
						break;
					}
				}
				if(!canCraft)
					return false;
				//the item isn't in the inventory, try to craft it
			}
		}

		return canCraft;
	}
	
	//TODO: Mod compatibility (IC2, EE3)
	public static List<IRecipe> getAllRecipes(){
		List recipes = new ArrayList<IRecipe>();
		for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
			recipes.add(CraftingManager.getInstance().getRecipeList().get(i));
		}		
		return Collections.unmodifiableList(recipes);
	}
	
}
