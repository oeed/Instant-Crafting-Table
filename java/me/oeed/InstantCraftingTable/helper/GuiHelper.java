package me.oeed.InstantCraftingTable.helper;

import me.oeed.InstantCraftingTable.InstantCraftingTable;
import me.oeed.InstantCraftingTable.client.gui.GuiButtonCheckbox;
import me.oeed.InstantCraftingTable.client.gui.GuiCrafter;
import me.oeed.InstantCraftingTable.client.gui.GuiCraftingCustom;
import me.oeed.InstantCraftingTable.client.gui.container.ContainerCrafter;
import me.oeed.InstantCraftingTable.lib.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.ContainerWorkbench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;

public class GuiHelper {
	
	//toggles between the crafting table interface and the instant crafting interface
	public static void toggleCrafter(){
		Minecraft client = FMLClientHandler.instance().getClient();
        if(client.thePlayer.openContainer instanceof ContainerWorkbench){
    		CraftingHelper.emptyCraftingTable(client.thePlayer.inventory, (ContainerWorkbench)client.thePlayer.openContainer, ((ContainerWorkbench)client.thePlayer.openContainer).craftMatrix);
        	displayInstantCraftingInterface();
        }
        else if(client.thePlayer.openContainer instanceof ContainerCrafter){
        	//TODO: prevent items from being destoryed when switching
        	//client.thePlayer.displayGUIWorkbench((int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ);
    		CraftingHelper.emptyCraftingTable(client.thePlayer.inventory, ((ContainerCrafter)client.thePlayer.openContainer).craftingTable, ((ContainerCrafter)client.thePlayer.openContainer).craftingTable.craftMatrix);
        	displayCustomCraftingInterface();
        }
	}
	
	public static void displayDefaultInterface(){
		LogHelper.log("Replacing vanilla crafting interface...");
		if(InstantCraftingTable.instance.configHelper.getValue(ConfigHelper.KEY_CRAFTINGTABLEDEFAULT)){
        	displayCustomCraftingInterface();
		}
		else{
			displayInstantCraftingInterface();
		}
	}
	
	//display the instant crafting interface
	public static void displayInstantCraftingInterface(){
		Minecraft client = FMLClientHandler.instance().getClient();
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(InstantCraftingTable.instance);
        FMLCommonHandler.instance().showGuiScreen(new GuiCrafter(client.thePlayer.inventory, true, (ContainerWorkbench)client.thePlayer.openContainer));	
	}
	
	//display the (customised) vanilla interface
	public static void displayCustomCraftingInterface(){
		Minecraft client = FMLClientHandler.instance().getClient();
		FMLCommonHandler.instance().showGuiScreen(new GuiCraftingCustom(client.thePlayer.inventory, client.thePlayer.worldObj, (int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ));
	}
	
	//if the vanilla crafting interface is open switch to the custom one
	public static void convertToCustomCraftingInterface(){
		Minecraft client = FMLClientHandler.instance().getClient();
        if(client.currentScreen instanceof GuiCrafting && client.thePlayer.openContainer instanceof ContainerWorkbench)
        	displayDefaultInterface();
	}
}
