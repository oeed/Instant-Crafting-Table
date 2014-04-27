package me.oeed.InstaCrafter;

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

@Mod(modid = InstaCrafter.MODID, name = "A Test", version = InstaCrafter.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class InstaCrafter
{
       public static final String MODID = "InstaCrafter";
       public static final String VERSION = "1.0";
       public static final String NAME = "InstaCrafter";
       
       // The instance of your mod that Forge uses.
       @Instance("InstaCrafter")
       public static InstaCrafter instance;
      
       // Says where the client and server 'proxy' code is loaded.
       @SidedProxy(clientSide="me.oeed.InstaCrafter.client.ClientProxy", serverSide="me.oeed.InstaCrafter.CommonProxy")
       public static CommonProxy proxy;
      
       @EventHandler
       public void preInit(FMLPreInitializationEvent event) {
               // Stub Method
       }
      
       @EventHandler
       public void load(FMLInitializationEvent event) {
               proxy.registerRenderers();
               LogHelper.log("Alive");
               
               
               if(FMLCommonHandler.instance().getSide().isClient()){
                   KeyBinding[] key = {new KeyBinding("Name of Button", Keyboard.KEY_G)};
                   boolean[] repeat = {false};
                   KeyBindingRegistry.registerKeyBinding(new InstaCrafterKeyHandler(key, repeat));
            	   
               }else{
                   LogHelper.log("Server");
            	   
               }

               
               new GuiHandler();
               //NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
               //System.out.println(new ResourceLocation("basic", "textures/gui/container.png"));
       }
      
       @EventHandler
       public void postInit(FMLPostInitializationEvent event) {
               // Stub Method
       }
}
