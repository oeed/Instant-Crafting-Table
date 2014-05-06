package me.oeed.InstantCraftingTable;

import java.util.logging.Level;

import me.oeed.InstantCraftingTable.client.ClientKeyHandler;
import me.oeed.InstantCraftingTable.client.ClientTickHandler;
import me.oeed.InstantCraftingTable.helper.ConfigHelper;
import me.oeed.InstantCraftingTable.helper.LogHelper;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = InstantCraftingTable.MODID, name = InstantCraftingTable.NAME, version = InstantCraftingTable.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class InstantCraftingTable
{
       public static final String MODID = "InstantCraftingTable";
       public static final String VERSION = "1.0";
       public static final String NAME = "InstantCraftingTable";
       public ConfigHelper configHelper;
       
       // The instance of your mod that Forge uses.
       @Instance("InstantCraftingTable")
       public static InstantCraftingTable instance;
      
      
       @EventHandler
       public void preInit(FMLPreInitializationEvent event) {
    	   configHelper = new ConfigHelper(event);
       }
      
       @EventHandler
       public void load(FMLInitializationEvent event) {
           if(FMLCommonHandler.instance().getSide().isClient()){
               LogHelper.log("Registering keybind...");
               KeyBinding[] key = {new KeyBinding("Toggle Crafting Interface", Keyboard.KEY_CIRCUMFLEX)};
               boolean[] repeat = {false};
               KeyBindingRegistry.registerKeyBinding(new ClientKeyHandler(key, repeat));
               	LogHelper.log("Registering tick handler...");
               	TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT); 
           }else{
               LogHelper.log("You shouldn't be running this ("+InstantCraftingTable.NAME+") as a server mod! While it *shouldn't* cause issues it is a waste of resources.", Level.WARNING);
           }
       }
      
       @EventHandler
       public void postInit(FMLPostInitializationEvent event) {
       }
       
}
