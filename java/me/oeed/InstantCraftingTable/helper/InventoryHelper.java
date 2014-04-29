package me.oeed.InstantCraftingTable.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class InventoryHelper {

	public static int getInventorySize(IInventory inventory){
		return inventory.getSizeInventory();
	}
	
	public static boolean isStackEqualTo(ItemStack itemStack1, ItemStack itemStack2){	
		if(itemStack1 == null || itemStack2 == null)
			return false;
		
		if(itemStack1.getClass() != itemStack2.getClass())
			return false;
		
		if(itemStack1.itemID != itemStack2.itemID)
			return false;
		
		if(itemStack1.getItemDamage() != itemStack2.getItemDamage() && itemStack1.getItemDamage() != OreDictionary.WILDCARD_VALUE && itemStack2.getItemDamage() != OreDictionary.WILDCARD_VALUE)
			return false;
		
		return true;
	}
	
	public static int findItemInInventory(IInventory inventory, ItemStack itemStack){
		int inventorySize = getInventorySize(inventory);
		for(int i = 0; i < inventorySize; i ++){
			if(isStackEqualTo(itemStack, inventory.getStackInSlot(i))){
				return i;
			}
		}
		return -1;
	}

    public static int getEmptySlot(IInventory inventory) {
        int invSize = getInventorySize(inventory);
        for (int i = 0; i < invSize; i++) {
            if (inventory.getStackInSlot(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public static List<ItemStack> storeContents(IInventory inventory) {
        List<ItemStack> copy = new ArrayList<ItemStack>(inventory.getSizeInventory());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            copy.add(i, ItemStack.copyItemStack(inventory.getStackInSlot(i)));
        }
        return copy;
    }

    public static void setContents(IInventory inventory, List<ItemStack> list) {
        if (inventory.getSizeInventory() != list.size()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            inventory.setInventorySlotContents(i, ItemStack.copyItemStack(list.get(i)));
        }
    }


    public static void setContents(IInventory to, IInventory from) {
        int invSize = Math.min(to.getSizeInventory(), from.getSizeInventory());
        for (int i = 0; i < invSize; i++) {
            to.setInventorySlotContents(i, ItemStack.copyItemStack(from.getStackInSlot(i)));
        }
    }
    
	 public static boolean addItemToInventory(IInventory inventory, ItemStack itemstack) {
	    List<ItemStack> contents = storeContents(inventory);
	    int invSize = getInventorySize(inventory);
	    int maxStack = Math.min(inventory.getInventoryStackLimit(), itemstack.getMaxStackSize());
	    for (int i = 0; i < invSize; i++) {
	        if (ItemStack.areItemStacksEqual(itemstack, inventory.getStackInSlot(i))) {
	            ItemStack is = inventory.getStackInSlot(i);
	            if (is.stackSize >= maxStack) {
	                continue;
	            }
	            if (is.stackSize + itemstack.stackSize <= maxStack) {
	                is.stackSize += itemstack.stackSize;
	                return true;
	            } else {
	                itemstack.stackSize -= maxStack - is.stackSize;
	                is.stackSize = maxStack;
	            }
	        }
	    }
	    while (true) {
	        int slot = getEmptySlot(inventory);
	        if (slot != -1) {
	            if (itemstack.stackSize <= maxStack) {
	                inventory.setInventorySlotContents(slot, itemstack.copy());
	                return true;
	            } else {
	                ItemStack is = itemstack.copy();
	                itemstack.stackSize -= maxStack;
	                is.stackSize = maxStack;
	                inventory.setInventorySlotContents(slot, is);
	            }
	        } else {
	            break;
	        }
	    }
	    setContents(inventory, contents);
	    return false;
	}
	
	public static boolean consumeItem(IInventory inventory, int stackIndex, List<ItemStack> usedIngredients){
		ItemStack stack = inventory.decrStackSize(stackIndex, 1);
        if (stack != null) {
            if (stack.getItem().hasContainerItem()) {
                ItemStack containerStack = stack.getItem().getContainerItemStack(stack);
                if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
                    containerStack = null;
                }
                if (containerStack != null && !addItemToInventory(inventory, containerStack)) {
                    if (inventory.getStackInSlot(stackIndex) != null) {
                        inventory.getStackInSlot(stackIndex).stackSize++;
                    } else {
                        inventory.setInventorySlotContents(stackIndex, stack);
                    }
                    return false;
                }
            }
            usedIngredients.add(stack);
            return true;
        }
		return false;
	}
	
}
