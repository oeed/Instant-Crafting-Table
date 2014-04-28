package me.oeed.InstaCrafter;

import java.util.logging.Level;

import me.oeed.InstaCrafter.client.InstaCrafterKeyHandler;
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
      
       // Says where the client and server 'proxy' cod e is loaded.
       //@SidedProxy(clientSide="me.oeed.InstaCrafter.client.ClientProxy", serverSide="me.oeed.InstaCrafter.CommonProxy")
       //public static CommonProxy proxy;
      
       @EventHandler
       public void preInit(FMLPreInitializationEvent event) {
               // Stub Method
       }
      
       @EventHandler
       public void load(FMLInitializationEvent event) {
            //   proxy.registerRenderers();
               
               
               if(FMLCommonHandler.instance().getSide().isClient()){
                   LogHelper.log("Starting client...");
                   LogHelper.log("Registering keybind...");
                   KeyBinding[] key = {new KeyBinding("Toggle Crafting Interface", Keyboard.KEY_CIRCUMFLEX)};
                   boolean[] repeat = {false};
                   KeyBindingRegistry.registerKeyBinding(new InstaCrafterKeyHandler(key, repeat));
                   LogHelper.log("Keybind registered");
                   new GuiHandler();
            	   
               }else{
                   LogHelper.log("You shouldn't be running this ("+InstaCrafter.NAME+") as a server mod! While it shouldn't cause issues it is a waste of resources.", Level.WARNING);
            	   
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
