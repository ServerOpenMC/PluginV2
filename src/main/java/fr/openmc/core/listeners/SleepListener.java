package fr.openmc.core.listeners;

import org.bukkit.GameMode;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class SleepListener implements Listener {
	
	/**
	 * This class is used to manage the sleep percentage in the world.
	 * The percentage is set based on the number of players in the world.
	 */
	private static final NavigableMap<Integer, Integer> PLAYER_THRESHOLDS = new TreeMap<>();
	static {
		PLAYER_THRESHOLDS.put(4, 40);
		PLAYER_THRESHOLDS.put(10, 35);
		PLAYER_THRESHOLDS.put(20, 28);
		PLAYER_THRESHOLDS.put(27, 22);
		PLAYER_THRESHOLDS.put(35, 18);
		PLAYER_THRESHOLDS.put(39, 16);
		PLAYER_THRESHOLDS.put(45, 14);
		PLAYER_THRESHOLDS.put(57, 12);
		PLAYER_THRESHOLDS.put(61, 11);
		PLAYER_THRESHOLDS.put(65, 10);
		PLAYER_THRESHOLDS.put(70, 9);
		PLAYER_THRESHOLDS.put(76, 8);
		PLAYER_THRESHOLDS.put(91, 7);
		PLAYER_THRESHOLDS.put(101, 6);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		World world = event.getPlayer().getWorld();
		world.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(world.getPlayers()));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		World world = event.getPlayer().getWorld();
		world.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(world.getPlayers()));
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		World oldWorld = event.getFrom();
		World newWorld = event.getPlayer().getWorld();
		oldWorld.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(oldWorld.getPlayers()));
		newWorld.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, getPercentage(newWorld.getPlayers()));
	}
	
	/**
	 * This method is used to get the percentage of players needed to sleep in the world.
	 * The percentage is set based on the number of players in the world.
	 *
	 * @param players The number of players in the world.
	 * @return The percentage of players needed to sleep in the world.
	 */
	private int getPercentage(List<Player> players) {
		long playersCount = players.stream()
				.filter(player -> player.getGameMode().equals(GameMode.SURVIVAL))
				.count();

		return PLAYER_THRESHOLDS.ceilingEntry((int) playersCount).getValue();
	}
}
