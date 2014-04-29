package me.oeed.InstantCraftingTable.lib;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.oeed.InstantCraftingTable.InstantCraftingTable;
import cpw.mods.fml.common.FMLLog;

public class LogHelper {
	private static Logger logger = Logger.getLogger(InstantCraftingTable.NAME);
	
	public static void init() {
		logger.setParent(FMLLog.getLogger());
	}
	
	public static void log(Object message){
		log(message, Level.INFO);
	}
	
	public static void log(Object message, Level logLevel) {
		if(message == null)
			logger.log(logLevel, "NULL");
		else
		logger.log(logLevel, message.toString());
	}
}