package fr.openmc.core.listeners;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.NavigableMap;
import java.util.TreeMap;

public class SleepListener implements Listener {
	
	/**
	 * This class is used to manage the sleep percentage in a world.
	 * The percentage is set based on the number of players in the world.
	 */
	private static final NavigableMap<Integer, Integer> PLAYER_THRESHOLDS = new TreeMap<>();
	static {
		PLAYER_THRESHOLDS.put(4, 51);
		PLAYER_THRESHOLDS.put(10, 43);
		PLAYER_THRESHOLDS.put(20, 31);
		PLAYER_THRESHOLDS.put(27, 26);
		PLAYER_THRESHOLDS.put(35, 23);
		PLAYER_THRESHOLDS.put(39, 21);
		PLAYER_THRESHOLDS.put(45, 18);
		PLAYER_THRESHOLDS.put(57, 16);
		PLAYER_THRESHOLDS.put(61, 15);
		PLAYER_THRESHOLDS.put(65, 14);
		PLAYER_THRESHOLDS.put(70, 13);
		PLAYER_THRESHOLDS.put(76, 12);
		PLAYER_THRESHOLDS.put(91, 11);
		PLAYER_THRESHOLDS.put(101, 10);
	}

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
	
	/**
	 * This method is used to get the percentage of players needed to sleep in a world.
	 * The percentage is set based on the number of players in the world.
	 *
	 * @param players The number of players in the world.
	 * @return The percentage of players needed to sleep in a world.
	 */
	private int getPercentage(int players) {
		return PLAYER_THRESHOLDS.ceilingEntry(players).getValue();
	}
}
