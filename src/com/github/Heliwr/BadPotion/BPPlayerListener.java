package com.github.Heliwr.BadPotion;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BPPlayerListener implements Listener {
	public static BadPotion pl;
	
	public BPPlayerListener(BadPotion instance) {
		pl = instance;
	}

	public Material[] TileEntity = { Material.BED, Material.BED_BLOCK,
			Material.BREWING_STAND, Material.CHEST, Material.DISPENSER,
			Material.ENCHANTMENT_TABLE, Material.LEVER, Material.STONE_BUTTON,
			Material.TRAP_DOOR, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, 
			Material.WOOD_DOOR, Material.IRON_DOOR, Material.CAULDRON,
			Material.FENCE_GATE, Material.CAKE_BLOCK };

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (player.getItemInHand().getTypeId() == 373) {

				if (player.hasPermission("badpotion.bypass")
						|| in_array(TileEntity, event.getClickedBlock()))
					return;

				boolean badPotionUsed = false;
				int potionDurability = player.getItemInHand().getDurability();
				
				if (in_array(pl.PotionsOptions, potionDurability)) {
					badPotionUsed = true;
				}

				if ((badPotionUsed
						|| Boolean.parseBoolean(String.valueOf(pl.config.get("blockall"))))
						&& Math.random() < (Float) pl.config.get("dmgchance")) {
					int time = 1800;
					int amp = 1;
					
					player.sendMessage((String) pl.config.get("blockmsg"));
					event.setCancelled(true);
					player.setItemInHand(null);
					if(pl.POISON.contains(potionDurability)) {
						if(potionDurability == 16388) {
							time = 900;
						} else if(potionDurability == 16420) {
							time = 440;
							amp = 2;
						} else time = 2400;
						player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, time, amp));						
					} else if(pl.WEAK.contains(potionDurability)) {
						if(potionDurability == 16456)
							time = 4800;
						player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, amp));						
					} else if(pl.SLOW.contains(potionDurability)) {
						if(potionDurability == 16458)
							time = 4800;
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, amp));						
					} else if(pl.HARM.contains(potionDurability)) {
						if(potionDurability == 16428)
							amp = 2;
						time = 1;
						player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, time, amp));						
					}
				}
			}
		}
	}

	public boolean in_array(Map<Integer, Boolean> potionList, int needle) {
		if (potionList.containsKey(needle)) {
			return potionList.get(needle);
		} else {
			return false;
		}
	}

	public boolean in_array(Material[] list, Block contains) {
		if (contains == null)
			return false;

		for (int i = 0; i < list.length; i++) {
			if (list[i] == contains.getType()) {
				return true;
			}
		}
		return false;
	}
}
