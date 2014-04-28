package me.oeed.InstaCrafter.helper;

import me.oeed.InstaCrafter.InstaCrafter;
import me.oeed.InstaCrafter.client.gui.GuiCrafter;
import me.oeed.InstaCrafter.client.gui.GuiCraftingCustom;
import me.oeed.InstaCrafter.client.gui.container.ContainerCrafter;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerWorkbench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;

public class GuiHelper {
	
	//toggles between the crafting table interface and the instant crafting interface
	public static void toggleCrafter(){
		Minecraft client = FMLClientHandler.instance().getClient();
        
        if(client.thePlayer.openContainer instanceof ContainerWorkbench){
	        ModContainer mc = FMLCommonHandler.instance().findContainerFor(InstaCrafter.instance);
            FMLCommonHandler.instance().showGuiScreen(new GuiCrafter(client.thePlayer.inventory, true, (ContainerWorkbench)client.thePlayer.openContainer));
        }
        else if(client.thePlayer.openContainer instanceof ContainerCrafter){
        	//TODO: prevent items from being destoryed when switching
        	//client.thePlayer.displayGUIWorkbench((int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ);

            FMLCommonHandler.instance().showGuiScreen(new GuiCraftingCustom(client.thePlayer.inventory, client.thePlayer.worldObj, (int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ));
        }
	}
}
