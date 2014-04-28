package me.oeed.InstaCrafter;

import java.util.logging.Level;

import me.oeed.InstaCrafter.client.ClientTickHandler;
import me.oeed.InstaCrafter.client.ClientKeyHandler;
import me.oeed.InstaCrafter.client.gui.GuiHandler;
import me.oeed.InstaCrafter.lib.LogHelper;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = InstaCrafter.MODID, name = InstaCrafter.NAME, version = InstaCrafter.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class InstaCrafter
{
       public static final String MODID = "InstaCrafter";
       public static final String VERSION = "1.0";
       public static final String NAME = "InstaCrafter";
       
       // The instance of your mod that Forge uses.
       @Instance("InstaCrafter")
       public static InstaCrafter instance;
      
      
       @EventHandler
       public void preInit(FMLPreInitializationEvent event) {
               // Stub Method
       }
      
       @EventHandler
       public void load(FMLInitializationEvent event) {
               if(FMLCommonHandler.instance().getSide().isClient()){
                   LogHelper.log("Starting client...");
                   LogHelper.log("Registering keybind...");
                   KeyBinding[] key = {new KeyBinding("Toggle Crafting Interface", Keyboard.KEY_CIRCUMFLEX)};
                   boolean[] repeat = {false};
                   KeyBindingRegistry.registerKeyBinding(new ClientKeyHandler(key, repeat));
                   LogHelper.log("Keybind registered");
                   

	               	LogHelper.log("Registering tick handler...");
	               	TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
	               	LogHelper.log("Registered tick handler");
                   //new GuiHandler();
                  
               }else{
                   LogHelper.log("You shouldn't be running this ("+InstaCrafter.NAME+") as a server mod! While it *shouldn't* cause issues it is a waste of resources.", Level.WARNING);
            	   
               }

               
//               LogHelper.log("reg gui");
               //NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
               //System.out.println(new ResourceLocation("basic", "textures/gui/container.png"));
       }
      
       @EventHandler
       public void postInit(FMLPostInitializationEvent event) {
               // Stub Method
           LogHelper.log("post init");
       }
       
}
