package me.oeed.InstantCraftingTable.client;

import java.util.EnumSet;

import me.oeed.InstantCraftingTable.helper.GuiHelper;
import me.oeed.InstantCraftingTable.helper.LogHelper;
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
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "Instant Crafter Tickhandler";
	}

}
