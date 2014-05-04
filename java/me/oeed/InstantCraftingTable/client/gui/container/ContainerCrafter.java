package me.oeed.InstantCraftingTable.client.gui.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.oeed.InstantCraftingTable.CrafterSlot;
import me.oeed.InstantCraftingTable.helper.CraftingHelper;
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
import net.minecraft.item.crafting.IRecipe;
import cpw.mods.fml.client.FMLClientHandler;

public class ContainerCrafter extends Container {
	
	public InventoryCrafting craftingInventory;
	public InventoryPlayer invPlayer;
	public ArrayList<IRecipe> availableRecipes;
	public ArrayList<IRecipe> displayedRecipes;
	public boolean isClientSideOnly;
	public String searchFilter;
	public float currentScroll;

	//used to 'mimic' a crafting table
	public ContainerWorkbench craftingTable;
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
	
	private static InventoryBasic inventory = new InventoryBasic("tmp", true, 46);
	
	public ContainerCrafter(InventoryPlayer _invPlayer, boolean clientSide, ContainerWorkbench craftingTable){
		isClientSideOnly = clientSide;
		this.craftingTable = craftingTable;
		invPlayer = _invPlayer;
		availableRecipes = new ArrayList<IRecipe>();
		displayedRecipes = new ArrayList<IRecipe>();
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
		
		update("");
	}
	
	public void update(String filter){
		searchFilter = filter;
		currentScroll = 0;
		update();
	}
	
	public void update(){
		availableRecipes = CraftingHelper.getAvailableRecipes(invPlayer);
		displayedRecipes = (ArrayList<IRecipe>) availableRecipes.clone();
		if(searchFilter != ""){
	        Iterator iterator = displayedRecipes.iterator();
			while (iterator.hasNext()){
	            ItemStack itemstack = ((IRecipe) iterator.next()).getRecipeOutput();
	            boolean flag = false;
	            Iterator iterator1 = itemstack.getTooltip(invPlayer.player, FMLClientHandler.instance().getClient().gameSettings.advancedItemTooltips).iterator();

	            while (true){
	                if (iterator1.hasNext()){
	                    String s1 = (String)iterator1.next();
	                    if (!s1.toLowerCase().contains(searchFilter)){
	                        continue;
	                    }
	                    flag = true;
	                }

	                if (!flag){
	                    iterator.remove();
	                }

	                break;
	            }
	        }
		}
		
		updateScroll();		
	}
	
	public void scrollTo(float par1){
		currentScroll = par1;
		updateScroll();
	}
	
	public void updateScroll(){
		int rows = displayedRecipes.size() / 9 - 5 + 1;
		if(rows == 0){
			LogHelper.log("Row 0");
			currentScroll = 0.0F;
		}
		
		int row = (int)(currentScroll * rows + 0.5D);
		
		if(row < 0){
			row = 0;
		}

		for(int y = 0; y < 5; y ++){
			for(int x = 0; x < 9; x ++){
				int i = x + (9*(y + row));
			    if (i >= 0 && i < displayedRecipes.size()){			    	
			    	inventory.setInventorySlotContents(x + (9 * y), displayedRecipes.get(i).getRecipeOutput());
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
