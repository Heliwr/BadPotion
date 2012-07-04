package com.github.Heliwr.BadPotion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class BadPotion extends JavaPlugin {

	public final BPPlayerListener playerListener = new BPPlayerListener(this);
	
	Map<String, Object> config = new HashMap<String, Object>();
	
	public Map<Integer,Boolean> PotionsOptions = new HashMap<Integer,Boolean>();
	
	public int[] goodPotions = {
			8193, //Regeneration Potion (0:45)
			8257, //Regeneration Potion (2:00)
			8225, //Regeneration Potion II (0:22)
			
			8194, //Swiftness Potion (3:00)
			8258, //Swiftness Potion (8:00)
			8226, //Swiftness Potion II (1:30)
			
			8195, //Fire Resistance Potion (3:00)
			8227, //Fire Resistance Potion (3:00)
			8259, //Fire Resistance Potion (8:00)
			
			8197, //Healing Potion
			8261, //Healing Potion
			8229, //Healing Potion II
			
			8201, //Strength Potion (3:00)
			8265, //Strength Potion (8:00)
			8233, //Strength Potion II (1:30)
	};
	
	public int[] badPotions = {
			8196, //Poison Potion (0:45)
			8260, //Poison Potion (2:00)
			8228, //Poison Potion II (0:22)
			
			8200, //Weakness Potion (1:30)
			8232, //Weakness Potion (1:30)
			8264, //Weakness Potion (4:00)
			
			8202, //Slowness Potion (1:30)
			8234, //Slowness Potion (1:30)
			8266, //Slowness Potion (4:00)
			
			8204, //Harming Potion
			8268, //Harming Potion
	};
	
	public int[] goodSlashPotions = {
			16387, //Fire Resistance Splash (2:15)
			16451, //Fire Resistance Splash (6:00)
			16419, //Fire Resistance Splash (2:15)
			
			16386, //Swiftness Splash (2:15)
			16450, //Swiftness Splash (6:00)
			16418, //Swiftness Splash II (1:07)
			
			16389, //Healing Splash
			16453, //Healing Splash
			16421, //Healing Splash II
			
			16457, //Strength Splash (6:00)
			16393, //Strength Splash (2:15)
			16425, //Strength Splash II (1:07)
			
			16385, //Regeneration Splash (0:33)
			16449, //Regeneration Splash (1:30)
			16417, //Regeneration Splash II (0:16)
	};
	
	public int[] badSplashPotions = {
			16388, //Poison Splash (0:45)
			16452, //Poison Splash (2:00)
			16420, //Poison Splash II (0:22)
			
			16424, //Weakness Splash (1:30)
			16392, //Weakness Splash (1:30)
			16456, //Weakness Splash (4:00)
			
			16394, //Slowness Splash (1:30)
			16426, //Slowness Splash (1:30)
			16458, //Slowness Splash (4:00)

			16396, //Harming Splash
			16460, //Harming Splash
			16428, //Harming Splash II
	};

	final Set<Integer> POISON = new HashSet<Integer> (Arrays.asList(
			new Integer[] {badSplashPotions[0], badSplashPotions[1], badSplashPotions[2]}));
	final Set<Integer> WEAK = new HashSet<Integer> (Arrays.asList(
			new Integer[] {badSplashPotions[3], badSplashPotions[4], badSplashPotions[5]}));
	final Set<Integer> SLOW = new HashSet<Integer> (Arrays.asList(
			new Integer[] {badSplashPotions[6], badSplashPotions[7], badSplashPotions[8]}));
	final Set<Integer> HARM = new HashSet<Integer> (Arrays.asList(
			new Integer[] {badSplashPotions[9], badSplashPotions[10], badSplashPotions[11]}));

	public void onEnable() {
		//Config
		Map<String, Object> confdefaults = new LinkedHashMap<String, Object>();
		
		confdefaults.put("config.blockall", false);
		confdefaults.put("config.blockmsg", "&4Your are not permitted to use this Potion!");
		confdefaults.put("config.dmgchance", "0.33");
		
		for(int potion : goodPotions) confdefaults.put("good."+potion, false);
		
		for(int potion : goodSlashPotions) confdefaults.put("goodsplash."+potion, false);
		
		for(int potion : badPotions) confdefaults.put("bad."+potion, false);
		
		for(int potion : badSplashPotions) confdefaults.put("badsplash."+potion, false);
		
		for (final Entry<String, Object> e : confdefaults.entrySet()){
			if (!getConfig().contains(e.getKey())){
				getConfig().set(e.getKey(), e.getValue());
			}
		}
		saveConfig();
		//END Config
		
		//Load all config options.
		//Option to block ALL Potions.
		config.put("blockall", getConfig().getBoolean("config.blockall"));
		config.put("blockmsg", ColorMsg(getConfig().getString("config.blockmsg")));

		try {
	      float f = Float.valueOf((String) getConfig().getString("config.dmgchance")).floatValue();
	      config.put("dmgchance", f);
	    }
	    catch (NumberFormatException nfe) {
	      System.out.println("[BadPotion] NumberFormatException: " + nfe.getMessage());
	      System.out.println("[BadPotion] Assigning default value for dmgchance.");
	      config.put("dmgchance", 0.33);
	    }
					
		ConfigurationSection goodSec = getConfig().getConfigurationSection("good");
		for (String potion : goodSec.getKeys(false)) {
			Boolean allowed = goodSec.getBoolean(potion);
			PotionsOptions.put(Integer.valueOf(potion), allowed);
		}
		
		ConfigurationSection badSec = getConfig().getConfigurationSection("bad");
		for (String potion : badSec.getKeys(false)) {
			Boolean allowed = badSec.getBoolean(potion);
			PotionsOptions.put(Integer.valueOf(potion), allowed);
		}
		
		ConfigurationSection goodSplashSec = getConfig().getConfigurationSection("goodsplash");
		for (String potion : goodSplashSec.getKeys(false)) {
			Boolean allowed = goodSplashSec.getBoolean(potion);
			PotionsOptions.put(Integer.valueOf(potion), allowed);
		}
		
		ConfigurationSection badSplashSec = getConfig().getConfigurationSection("badsplash");
		for (String potion : badSplashSec.getKeys(false)) {
			Boolean allowed = badSplashSec.getBoolean(potion);
			PotionsOptions.put(Integer.valueOf(potion), allowed);
		}
		
		getLogger().info("Config Loaded");
		
		//Events
		getServer().getPluginManager().registerEvents(playerListener, this);
		//END Events
	}
	
    //Colorize a string (i.e. replace all &0, &1, ..., &f) with the associated color
    public String ColorMsg(String message){
        return message.replaceAll("&([0-9a-f])", (char)0xA7 + "$1");
    }
}
