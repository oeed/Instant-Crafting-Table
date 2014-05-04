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
import me.oeed.InstantCraftingTable.helper.LogHelper;
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
		
		update();
	}
	
	public void update(){
		availableRecipes = CraftingHelper.getAvailableRecipes(invPlayer);
        this.scrollTo(0.0f);		
	}
	
	public void scrollTo(float par1){
		int rows = availableRecipes.getCount() / 9 - 5 + 1;
		int row = (int)((double)(par1 * rows) + 0.5D);
		
		if(row < 0){
			row = 0;
		}

		for(int y = 0; y < 5; y ++){
			for(int x = 0; x < 9; x ++){
				int i = x + (9*(y + row));
			    if (i >= 0 && i < availableRecipes.getCount()){			    	
			    	inventory.setInventorySlotContents(x + (9 * y), (ItemStack)availableRecipes.getRecipeOutput(i));
			    }
			    else{
			    	inventory.setInventorySlotContents(x + (9 * y), (ItemStack)null);
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
	        List<ItemStack> usedIngredients = new ArrayList();
	        List<IRecipe> usedRecipes = new ArrayList();
			ItemStack crafted = CraftingHelper.craftItem(slot.getStack(), entityPlayer.inventory, usedIngredients, usedRecipes, craftingTable, craftMatrix, craftResult);
			update();
			return crafted;
		}
		else{
			return super.slotClick(slotIndex, mouseButton, modifierKey, entityPlayer);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
	
}
