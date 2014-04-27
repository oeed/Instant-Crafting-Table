package me.oeed.InstaCrafter.lib;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.oeed.InstaCrafter.InstaCrafter;
import cpw.mods.fml.common.FMLLog;

public class LogHelper {
	private static Logger logger = Logger.getLogger(InstaCrafter.NAME);
	
	public static void init() {
		logger.setParent(FMLLog.getLogger());
	}
	
	public static void log(Object message){
		log(message, Level.INFO);
	}
	
	public static void log(Object message, Level logLevel) {
		logger.log(logLevel, message.toString());
	}
}