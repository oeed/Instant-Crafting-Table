package me.oeed.InstaCrafter;

import me.oeed.InstaCrafter.lib.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.AchievementList;

public class CrafterSlot extends Slot {
	
	private EntityPlayer player;
	private IRecipe recipe;
	
	public CrafterSlot(EntityPlayer player, IInventory crafterInventory, int i, int x, int y){
		super(crafterInventory, i, x, y);
		this.player = player;
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack){
        return true;
    }
	
	public void setRecipe(IRecipe theIRecipe)
	{
		recipe = theIRecipe;
	}
	
	public IRecipe getRecipe()
	{
		return recipe;
	}
	
	public void onPickupFromSlot(ItemStack itemstack){
		LogHelper.log("Hello");
		itemstack.onCrafting(player.worldObj, player, 1);
		
        if(itemstack.itemID == Block.workbench.blockID){
            player.addStat(AchievementList.buildWorkBench, 1);
        } 
        else if(itemstack.itemID == Item.pickaxeWood.itemID){
            player.addStat(AchievementList.buildPickaxe, 1);
        }
        else if(itemstack.itemID == Block.furnaceIdle.blockID){
            player.addStat(AchievementList.buildFurnace, 1);
        } 
        else if(itemstack.itemID == Item.hoeWood.itemID){
            player.addStat(AchievementList.buildHoe, 1);
        }
        else if(itemstack.itemID == Item.bread.itemID){
            player.addStat(AchievementList.makeBread, 1);
        }
        else if(itemstack.itemID == Item.cake.itemID){
            player.addStat(AchievementList.bakeCake, 1);
        }
        else if(itemstack.itemID == Item.pickaxeStone.itemID){
            player.addStat(AchievementList.buildBetterPickaxe, 1);
        }
        else if(itemstack.itemID == Item.swordWood.itemID){
            player.addStat(AchievementList.buildSword, 1);
        }
        else if(itemstack.itemID == Block.enchantmentTable.blockID){
            player.addStat(AchievementList.enchantments, 1);
        }
        else if(itemstack.itemID == Block.bookShelf.blockID){
            player.addStat(AchievementList.bookcase, 1);
        }
    }

	
}
	