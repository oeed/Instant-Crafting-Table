package me.oeed.InstantCraftingTable.helper;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ConfigHelper {
	
	public static final String KEY_CRAFTINGTABLEDEFAULT = "isVanillaCraftingTableDefault";
	
	public boolean isVanillaCraftingTableDefault;
	public Configuration config;
	
	public ConfigHelper(FMLPreInitializationEvent event){
		config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        isVanillaCraftingTableDefault = config.get(config.CATEGORY_GENERAL, "isVanillaCraftingTableDefault", true).getBoolean(true);
        config.save();
	}
	
	public boolean getValue(String key){
		if(key == "isVanillaCraftingTableDefault"){
			return isVanillaCraftingTableDefault;
		}
		return true;
	}
	
	public Object setValue(String key, boolean value){
		if(key == "isVanillaCraftingTableDefault"){
			isVanillaCraftingTableDefault = value;
			config.getCategory(config.CATEGORY_GENERAL).get(key).set(value);
	        config.save();
		}
		return null;
	}
	
}
