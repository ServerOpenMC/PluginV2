package fr.openmc.core.listeners;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SleepListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		World world = event.getPlayer().getWorld();
		world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(world.getPlayers().size() + 1));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		World world = event.getPlayer().getWorld();
		world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(world.getPlayers().size() - 1));
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		World oldWorld = event.getFrom();
		World newWorld = event.getPlayer().getWorld();
		oldWorld.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(oldWorld.getPlayers().size()));
		newWorld.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(newWorld.getPlayers().size()));
	}
	
	private int getPercentage(int players) {
		if (players < 4) {
			return 51;
		} else if (players < 10) {
			return 43;
		} else if (players < 20) {
			return 31;
		} else if (players < 27) {
			return 26;
		} else if (players < 35) {
			return 23;
		} else if (players < 39) {
			return 21;
		} else if (players < 45) {
			return 18;
		} else if (players < 57) {
			return 16;
		} else if (players < 61) {
			return 15;
		} else if (players < 65) {
			return 14;
		} else if (players < 70) {
			return 13;
		} else if (players < 76) {
			return 12;
		} else if (players < 91) {
			return 11;
		} else if (players < 101) {
			return 10;
		} else {
			return 9;
		}
	}
}
