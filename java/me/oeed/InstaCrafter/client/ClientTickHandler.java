package me.oeed.InstaCrafter.client;

import java.util.EnumSet;

import me.oeed.InstaCrafter.helper.GuiHelper;
import me.oeed.InstaCrafter.lib.LogHelper;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		GuiHelper.convertToCustomCraftingInterface();

	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Instant Crafter Tickhandler";
	}

}
