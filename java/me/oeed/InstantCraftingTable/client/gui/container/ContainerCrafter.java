package me.oeed.InstantCraftingTable.client.gui.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import me.oeed.InstantCraftingTable.CrafterSlot;
import me.oeed.InstantCraftingTable.InventoryRecipes;
import me.oeed.InstantCraftingTable.helper.CraftingHelper;
import me.oeed.InstantCraftingTable.helper.GuiHelper;
import me.oeed.InstantCraftingTable.helper.InventoryHelper;
import me.oeed.InstantCraftingTable.lib.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

public class ContainerCrafter extends Container {
	
	public InventoryCrafting craftingInventory;
	public InventoryPlayer invPlayer;
	public InventoryRecipes availableRecipes;
	public boolean isClientSideOnly;

	//used to 'mimic' a crafting table
	public ContainerWorkbench craftingTable;
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
	
	private static InventoryBasic inventory = new InventoryBasic("tmp", true, 46);
	
	public ContainerCrafter(InventoryPlayer _invPlayer, boolean clientSide, ContainerWorkbench craftingTable){
		isClientSideOnly = clientSide;
		this.craftingTable = craftingTable;
		invPlayer = _invPlayer;
		availableRecipes = new InventoryRecipes(500 * 9); //500 rows is plenty
		craftingInventory = new InventoryCrafting(this, 3, 3);
		
		for(int x = 0; x < 9; x++){
			this.addSlotToContainer(new Slot(invPlayer, x, 9 + x * 18, 180));
		}

		for(int y = 0; y < 3; y++) {
		  for(int x = 0; x < 9; x++) {
		        this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 9 + x * 18, 122 + y * 18));
		  }
		}
		
		
		for(int y = 0; y < 5; y++) {
		  for(int x = 0; x < 9; x++) {
		        addSlotToContainer(new CrafterSlot(invPlayer.player, inventory, x + y * 9, 9 + x * 18, 18 + y * 18));
		  }
		}
		
		//calculateAvailableItems();
		update();
	}
	
	public void update(){
		availableRecipes = CraftingHelper.getAvailableRecipes(invPlayer);
        this.scrollTo(0.0f);		
	}
	
	public void scrollTo(float par1){
		int rows = availableRecipes.getCount() / 9 - 5 + 1;
		int row = 0;//(int)((double)(par1 * (float)i) + 0.5D);
		
		if(row < 0){
			row = 0;
		}

		System.out.println(availableRecipes.getCount());
		for(int y = 0; y < 5; y ++){
			for(int x = 0; x < 9; x ++){
				int i = x + (9*(y + row));
			    if (i >= 0 && i < availableRecipes.getCount()){			    	
			    	inventory.setInventorySlotContents(i, (ItemStack)availableRecipes.getRecipeOutput(i));
			    }
			    else{
			    	inventory.setInventorySlotContents(i, (ItemStack)null);
			    }
			}
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
		LogHelper.log("CAlleddd!!");
        Slot slot = (Slot)this.inventorySlots.get(par2);
        return slot != null ? slot.getStack() : null;
    }

	
	@Override
	public ItemStack slotClick(int slotIndex, int mouseButton, int modifierKey, EntityPlayer entityPlayer){
		// -999 is thrown outside
		if(slotIndex != -999 && inventorySlots.get(slotIndex) != null && inventorySlots.get(slotIndex) instanceof CrafterSlot) {
			CrafterSlot slot = (CrafterSlot) inventorySlots.get(slotIndex);
			ItemStack itemStack = entityPlayer.inventory.getItemStack();
			if(itemStack == null || InventoryHelper.isStackEqualTo(slot.getStack(), itemStack) && itemStack.stackSize < itemStack.getMaxStackSize()){
		        List<ItemStack> usedIngredients = new ArrayList();
		        List<IRecipe> usedRecipes = new ArrayList();
				ItemStack crafted = CraftingHelper.craftItem(slot.getStack(), entityPlayer.inventory, usedIngredients, usedRecipes, craftingTable, craftMatrix, craftResult);
				update();
				return crafted;
			}
			return null;
		}
		else{
			return super.slotClick(slotIndex, mouseButton, modifierKey, entityPlayer);
		}
	}
	
	private void calculateAvailableItems(){
		availableRecipes = CraftingHelper.getAvailableRecipes(invPlayer);
		
		// Clear the list of craftable recipes.
		availableRecipes.clearRecipes();
		// add favourites and recipes to recipe list.
		List recipeList = new ArrayList();
		for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
			recipeList.add(CraftingManager.getInstance().getRecipeList().get(i));
		}
		recipeList = Collections.unmodifiableList(recipeList);
		
		// Loop through each recipe, getting the ingredient and checking the player
		// has the necessary ingredient.
		for(int i = 0; i < recipeList.size(); i++) {
			IRecipe irecipe = (IRecipe)recipeList.get(i);
			// Copy the recipe ingredients into an ItemStack array.
			ItemStack[] recipeIngredients = getRecipeIngredients(irecipe);
			if(recipeIngredients == null)
				continue;
			// Check if the player has the required ingredients.
			// 1. Copy the players inventory to a temporary inventory.
			InventoryPlayer tempPlayerInventory = new InventoryPlayer( invPlayer.player );
			tempPlayerInventory.copyInventory( invPlayer );
			// 2. Loop through the temp inventory checking for the ingredients.
			boolean playerHasAllIngredients = true;
			for(int i1 = 0; i1 < recipeIngredients.length; i1++) {
				if(recipeIngredients[i1] == null)
					continue;
				
				ItemStack itemstack = recipeIngredients[i1];
				itemstack.stackSize = 1;
				int slotindex = getFirstInventoryPlayerSlotWithItemStack(tempPlayerInventory, itemstack);
				if(slotindex != -1) {
					tempPlayerInventory.decrStackSize(slotindex, itemstack.stackSize);
				} else {
					playerHasAllIngredients = false;
					break;
				}
			}
			// 3. Add recipe to list of craftable recipes if player has all the ingredients.
			if(playerHasAllIngredients) {
				availableRecipes.addRecipe(irecipe);
			}
		}
	}
	

	private int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, ItemStack itemstack)
	{
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack itemstack1 = inventory.getStackInSlot(i);
			if(itemstack1 != null
					&& itemstack1.itemID == itemstack.itemID 
					&& (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Get a list of ingredient required to craft the recipe item.
	@SuppressWarnings("unchecked")
	private ItemStack[] getRecipeIngredients(IRecipe irecipe)
	{
//		try {
			if(irecipe instanceof ShapedRecipes) {
				return ((ShapedRecipes) irecipe).recipeItems;
			} else if(irecipe instanceof ShapelessRecipes) {
				ArrayList recipeItems = new ArrayList(((ShapelessRecipes) irecipe).recipeItems);
				return (ItemStack[])recipeItems.toArray(new ItemStack[recipeItems.size()]);
			}
			return null;
//			} else {
//				String className = irecipe.getClass().getName();
//				if(className.equals("ic2.common.AdvRecipe")) {
//					return (ItemStack[]) ModLoader.getPrivateValue(irecipe.getClass(), irecipe, "input");
//				} else if(className.equals("ic2.common.AdvShapelessRecipe")) {
//					return (ItemStack[]) ModLoader.getPrivateValue(irecipe.getClass(), irecipe, "input");
//				} else {
//					return null;
//				}
//			}
//		} catch(NoSuchFieldException e) {
//			e.printStackTrace();
//			return null;
//		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
	
}
