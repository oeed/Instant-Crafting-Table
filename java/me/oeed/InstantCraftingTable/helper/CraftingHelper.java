package me.oeed.InstantCraftingTable.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import me.oeed.InstantCraftingTable.InventoryRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
import cpw.mods.fml.common.ObfuscationReflectionHelper;
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
		for(int i = 0; i < allRecipes.size(); i++) {
			IRecipe recipe = (IRecipe) allRecipes.get(i);
			InventoryPlayer temp = new InventoryPlayer(invPlayer.player);
	        temp.copyInventory(invPlayer);
	        List<ItemStack> usedIngredients = new ArrayList();
	        List<IRecipe> usedRecipes = new ArrayList();
			if(recipe != null && canCraftRecipe(temp, recipe, usedIngredients, usedRecipes)){
				availableRecipes.addRecipe(recipe);
			}
		}
		return availableRecipes;
	}
	
	
	// converts the slot number of the crafting gui to the crafting table one
	public static int convertToCraftingSlot(ContainerWorkbench craftingTable, int invSlot){
		//TODO: use a less hard coded way
		//converting the player invSlot to the crafting table slot, this is pretty hacky and should use a better method
		if(invSlot >= 0 && invSlot <= 8){
			//hotbar
			invSlot += 37;
		}
		else if(invSlot >= 9 && invSlot <= 45){
			invSlot += 1;            			
		}
		return invSlot;
	}
	
	public static ItemStack craftingWindowClick(ContainerWorkbench craftingTable, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
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

	public static void emptyCraftingTable(InventoryPlayer invPlayer, ContainerWorkbench craftingTable, InventoryCrafting craftMatrix){
		craftingWindowClick(craftingTable, -999, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the item in to slot -999, dropping it
		for(int y = 0; y < MAX_CRAFT_GRID_WIDTH; y++) {
        	for(int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
        		int matrixSlot = 1 + x + y * 3;
        		craftingWindowClick(craftingTable, matrixSlot + 1, mouseLeftClick, shiftHeld, invPlayer.player); //'fake' shift click the item, hopefully sending it in to the players inventory
        		craftingWindowClick(craftingTable, -999, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the item in to slot -999, dropping it (if there wasn't space)
        	}
		}
	}
	
	public static boolean doCraft(IRecipe recipe, InventoryPlayer invPlayer, ContainerWorkbench craftingTable, InventoryCrafting craftMatrix, IInventory craftResult, boolean subComponent){
		Minecraft client = FMLClientHandler.instance().getClient();
		ItemStack[] recipeItems = getRecipeIngredients(recipe, true);
		int recipeHeight = 3;
		int recipeWidth = 3;
        if(recipe instanceof ShapedRecipes){
        	recipeWidth = ((ShapedRecipes) recipe).recipeWidth;
        	recipeHeight = ((ShapedRecipes) recipe).recipeHeight;
        }
        else if(recipe instanceof ShapedOreRecipe){
            recipeWidth = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe)recipe, "width");
            recipeHeight = ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, (ShapedOreRecipe)recipe, "height");
        }

    	int itemsSlot = 0;
		y: for(int y = 0; y < recipeHeight; y++) {
        	x: for(int x = 0; x < recipeWidth; x++) {
        		int matrixSlot = x + y * MAX_CRAFT_GRID_HEIGHT;
        		if(recipeItems.length - 1 < itemsSlot){
        			LogHelper.log(recipeItems.length - 1 + "<" + matrixSlot+ " FIX THIS!");
        			break y;
        		}
    			ItemStack itemStack = recipeItems[itemsSlot];
    			itemsSlot ++;
    			if(itemStack == null)
    				continue x;

    			int invSlot = convertToCraftingSlot(craftingTable, InventoryHelper.findItemInInventory(invPlayer, itemStack));
    			if(invSlot == -1){
        			LogHelper.log("Missing item: "+itemStack, Level.WARNING);
    				break y;
    			}
    			craftingWindowClick(craftingTable, invSlot, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the players item slot that contains the needed item
    			craftingWindowClick(craftingTable, matrixSlot + 1, mouseRightClick, shiftNotHeld, invPlayer.player); //'fake' click the needed item in to the crafting table
    			craftingWindowClick(craftingTable, invSlot, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the players item back to the slot it was in

    			if(craftingTable.getSlot(0).getStack() != null && InventoryHelper.isStackEqualTo(craftingTable.getSlot(0).getStack(), recipe.getRecipeOutput()))
    				break y;
    		}
    	}
		
		if(craftingTable.getSlot(0).getStack()==null || !InventoryHelper.isStackEqualTo(craftingTable.getSlot(0).getStack(), recipe.getRecipeOutput())){
			LogHelper.log("Something went wrong! Couldn't craft item using recipe: "+recipe+" (Creates: "+recipe.getRecipeOutput()+"). Please report this to oeed.", Level.WARNING);
			LogHelper.log("Output: "+craftingTable.getSlot(0).getStack(), Level.WARNING);

			for(int i = 0; i < 9; i++) {
				LogHelper.log("Slot "+i+": "+craftingTable.getSlot(1+i).getStack(), Level.WARNING);
			}
			emptyCraftingTable(invPlayer, craftingTable, craftMatrix);
			return false;
		}
		else if(subComponent)
			craftingWindowClick(craftingTable, 0, mouseLeftClick, shiftHeld, invPlayer.player); //'fake' shift click the recipe output, (hopefully) sending the item to the inventory
		else
			craftingWindowClick(craftingTable, 0, mouseLeftClick, shiftNotHeld, invPlayer.player); //'fake' click the recipe output, (hopefully) sending the item to the inventory
		//TODO: handle if the item isn't there and there's no inv space
		return true;
	}

	public static ItemStack craftItem(ItemStack itemStack, InventoryPlayer invPlayer, List<ItemStack> usedIngredients, List<IRecipe> usedRecipes, ContainerWorkbench craftingTable, InventoryCrafting craftMatrix, IInventory craftResult){
		ItemStack playerStack = invPlayer.getItemStack();
		if(itemStack == null || (playerStack != null && !InventoryHelper.isStackEqualTo(playerStack, itemStack))){
			return null;
		}
		
		ArrayList<IRecipe> recipes = getRecipesForItemStack(itemStack);
		for(int i = 0; i < recipes.size(); i++) {
			IRecipe recipe = (IRecipe) recipes.get(i);
			if(playerStack != null && playerStack.stackSize + recipe.getRecipeOutput().stackSize > playerStack.getMaxStackSize()){
				return null;
			}
			InventoryPlayer temp = new InventoryPlayer(invPlayer.player);
	        temp.copyInventory(invPlayer);
			if(recipe != null && canCraftRecipe(temp, recipe, usedIngredients, usedRecipes)){
				for(int r = 0; r < usedRecipes.size(); r++)
					LogHelper.log("R: "+usedRecipes.get(r).getRecipeOutput().getDisplayName());
				for(int r = 0; r < usedRecipes.size(); r++)
					doCraft(usedRecipes.get(r), invPlayer, craftingTable, craftMatrix, craftResult, r != usedRecipes.size() - 1);
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
		return getRecipeIngredients(recipe, false);
	}
	
	public static ItemStack[] getRecipeIngredients(IRecipe recipe, boolean allowNull){
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
		
		if(!allowNull)
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
		if(ingredients == null)
			return false;

		boolean canCraft = false;
		ingredients: for(int i = 0; i < ingredients.length; i++) {
			for(int i2 = 0; i2 < blacklist.size(); i2++)
				if(InventoryHelper.isStackEqualTo(ingredients[i], blacklist.get(i2)))
					break ingredients;				
			
			int slot = InventoryHelper.findItemInInventory(invPlayer, ingredients[i]);
			if(slot != -1 && InventoryHelper.consumeItem(invPlayer, slot, usedIngredients)){
				canCraft = true;
			}
			else if(recursion < maxRecursion){
				//inventory doesn't have the required item, try to craft it using the available recipes
				canCraft = false;
				ArrayList<IRecipe> recipes = getRecipesForItemStack(ingredients[i]);
				ArrayList<ItemStack> _blacklist = new ArrayList(2);
				_blacklist.add(recipe.getRecipeOutput());
				_blacklist.add(ingredients[i]);
				
				for(int i2 = 0; i2 < recipes.size(); i2++) {
					if(canCraftRecipe(invPlayer, recipes.get(i2), usedIngredients, usedRecipes, recursion + 1, _blacklist)){
						InventoryHelper.addItemToInventory(invPlayer, recipes.get(i2).getRecipeOutput());
						int _slot = InventoryHelper.findItemInInventory(invPlayer, ingredients[i]);
						InventoryHelper.consumeItem(invPlayer, _slot, usedIngredients);
						canCraft = true;
						break;
					}
				}
				if(!canCraft)
					return false;
			}
		}
		if(canCraft)
			usedRecipes.add(recipe);
		
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
