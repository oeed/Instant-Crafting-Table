package me.oeed.InstaCrafter.client.gui;

import java.util.logging.Level;

import me.oeed.InstaCrafter.InstaCrafter;
import me.oeed.InstaCrafter.client.gui.container.ContainerCrafter;
import me.oeed.InstaCrafter.lib.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {
	
	public GuiHandler() {
		NetworkRegistry.instance().registerGuiHandler(InstaCrafter.instance, this);
	}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		LogHelper.log("Server GuiHandler called, this shouldn't be happening!"+id, Level.WARNING);
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		LogHelper.log("Client GuiHandler called, this shouldn't be happening!"+id, Level.WARNING);
		return null;
	}
	
}
