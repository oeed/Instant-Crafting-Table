package me.oeed.InstantCraftingTable.client;

import java.util.EnumSet;

import me.oeed.InstantCraftingTable.InstantCraftingTable;
import me.oeed.InstantCraftingTable.client.gui.GuiCrafter;
import me.oeed.InstantCraftingTable.client.gui.container.ContainerCrafter;
import me.oeed.InstantCraftingTable.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerWorkbench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.TickType;

public class ClientKeyHandler extends KeyHandler {

    private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
    
    public static boolean keyPressed = false;

    public ClientKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings){
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
	        GuiHelper.toggleCrafter();
	        keyPressed = true;
    	}
    }
    
    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd){
    	if(tickEnd){
    		keyPressed = false;
    	}
    }
    
    @Override
    public EnumSet<TickType> ticks(){
            return tickTypes;
    }
}
