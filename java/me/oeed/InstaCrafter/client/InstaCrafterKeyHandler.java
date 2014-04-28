package me.oeed.InstaCrafter.client;

import java.util.EnumSet;

import me.oeed.InstaCrafter.InstaCrafter;
import me.oeed.InstaCrafter.client.gui.GuiCrafter;
import me.oeed.InstaCrafter.client.gui.container.ContainerCrafter;
import me.oeed.InstaCrafter.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerWorkbench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.TickType;

public class InstaCrafterKeyHandler extends KeyHandler {

    private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
    
    public static boolean keyPressed = false;

    public InstaCrafterKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings){
            super(keyBindings, repeatings);
    }
    
    @Override
    public String getLabel(){
            return "Instant Crafing Table";
    }
    
    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat){
        System.out.println("tick d");
    	if(!tickEnd){
	            //what to do when key is pressed/down
	        System.out.println("down");
	        GuiHelper.toggleCrafter();
//	        client.thePlayer.openGui(InstaCrafter.instance, 0, client.theWorld, (int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ);
	        //FMLNetworkHandler.openGui(client.thePlayer, InstaCrafter.instance, 0, client.theWorld, (int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ);

            //NetworkRegistry.instance().openLocalGui(mc, client.thePlayer, 0, client.theWorld, (int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ);
            //NetworkRegistry.instance().

            //IGuiHandler handler = clientGuiHandlers.get(mc);
	        
	        //open client side only//handler.getClientGuiElement(modGuiId, player, world, x, y, z));
	        keyPressed = true;
    	}
    }
    
    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd){
        System.out.println("tick u");
    	if(tickEnd){
    		keyPressed = false;
    	}
    }
    
    @Override
    public EnumSet<TickType> ticks(){
            return tickTypes;
    }
}
