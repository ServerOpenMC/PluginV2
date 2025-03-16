package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.quests.qenum.QUESTS;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

public class QuestsManager {
	
	private static final Map<UUID, PlayerQuests> playerQuests = new HashMap<>();
	private static final String TABLE_NAME = "quests";
	private static final String PLAYER_COLUMN = "player";
	private static final Connection connection = DatabaseManager.getConnection();
	
	public QuestsManager(OMCPlugin plugin) {
		plugin.registerEvents(new QuestsListener(plugin));
	}
	
	public static void init_db(Connection connection) throws SQLException {
		StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + PLAYER_COLUMN + " VARCHAR(36) PRIMARY KEY");
		
		for (QUESTS quest : QUESTS.values()) {
			sql.append(", ").append(quest.name()).append(" INT DEFAULT 0");
		}
		
		sql.append(")");
		
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql.toString());
		}
		System.out.println("Table '" + TABLE_NAME + "' created successfully.");
	}
	
	public static void loadPlayerData(Player player) throws SQLException {
		UUID playerId = player.getUniqueId();
		PlayerQuests pq = new PlayerQuests();
		
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYER_COLUMN + " = ?")) {
			statement.setString(1, playerId.toString());
			ResultSet rs = statement.executeQuery();
			
			if (rs.next()) {
				for (QUESTS quest : QUESTS.values()) {
					int progress = rs.getInt(quest.name());
					pq.getQuestsProgress().put(quest, progress);
				}
			} else {
				insertNewPlayer(playerId);
			}
		}
		
		pq.calculateAllTiers();
		
		playerQuests.put(playerId, pq);
		for (QUESTS quest : QUESTS.values()) {
			StringBuilder tierInfo = new StringBuilder();
			for (int i = 0; i < quest.getQtTiers().length; i++) {
				tierInfo.append("Tier ").append(i).append(": ").append(quest.getQt(i)).append(", ");
			}
		}
	}
	
	public static PlayerQuests getPlayerQuests(UUID player) {
		return playerQuests.get(player);
	}
	
	public static void manageQuestsPlayer(UUID uuid, QUESTS quest, int amount, String actionBar) {
		Player player = Bukkit.getPlayer(uuid);
		@NotNull PlayerQuests pq = getPlayerQuests(uuid);
		int currentTier = pq.getCurrentTier(quest);
		assert player != null;
		if (!player.isConnected() || pq == null || pq.isQuestCompleted(quest)) return;
		
		if (currentTier < 0 || currentTier >= quest.getQtTiers().length) return;
		
		pq.addProgress(quest, amount);
		sendActionBar(player, quest, pq.getProgress(quest), currentTier, actionBar);
		
		if (pq.getProgress(quest) >= quest.getQt(currentTier)) {
			if (currentTier + 1 < quest.getQtTiers().length) {
				pq.setCurrentTier(quest, currentTier + 1);
				MessagesManager.sendMessage(player, Component.text(MessagesManager.textToSmall("§6Quête tier §e" + (currentTier + 1) + " §6complétée : §e" + quest.getName())), Prefix.QUESTS, MessageType.SUCCESS, true);
				grantReward(player, quest, currentTier);
			} else {
				completeQuestTier(player, quest, currentTier);
			}
		}
	}
	
	public static void savePlayerQuestProgress(Player player, PlayerQuests pq) throws SQLException {
		StringBuilder sql = new StringBuilder("UPDATE " + TABLE_NAME + " SET ");
		List<Object> params = new ArrayList<>();
		
		for (QUESTS quest : QUESTS.values()) {
			sql.append(quest.name()).append(" = ?, ");
			params.add(pq.getProgress(quest));
		}
		
		sql.delete(sql.length() - 2, sql.length());
		sql.append(" WHERE ").append(PLAYER_COLUMN).append(" = ?");
		params.add(player.getUniqueId().toString());
		
		// Debugging Output
		
		try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}
			stmt.executeUpdate();
		}
	}
	
	
	private static boolean tableExists() throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		try (ResultSet tables = meta.getTables(null, null, TABLE_NAME, null)) {
			return tables.next();
		}
	}
	
	private static void updateQuestsTable() throws SQLException {
		for (QUESTS quest : QUESTS.values()) {
			if (!columnExists(quest.name())) {
				addColumn(quest.name());
			}
		}
	}
	
	private static void insertNewPlayer(UUID playerId) throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO " + TABLE_NAME + " (" + PLAYER_COLUMN);
		
		for(QUESTS quest : QUESTS.values()) {
			sql.append(", ").append(quest.name());
		}
		
		sql.append(") VALUES (?");
		
		sql.append(", ?".repeat(QUESTS.values().length));
		
		sql.append(")");
		
		try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
			stmt.setString(1, playerId.toString());
			for (int i = 0; i < QUESTS.values().length; i++) {
				stmt.setInt(i + 2, 0);
			}
			stmt.executeUpdate();
		}
	}
	
	private static boolean columnExists(String columnName) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		try (ResultSet rs = meta.getColumns(null, null, TABLE_NAME, columnName)) {
			return rs.next();
		}
	}
	
	private static void addColumn(String columnName) throws SQLException {
		String sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + columnName + " INT DEFAULT 0";
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		}
	}
	
	private static void sendActionBar(Player player, QUESTS quest, int progress, int currentTier, String actionBar) {
		// if (!JumpManager.isJumping(player)) { --> Jump du spawn
			player.sendActionBar(MiniMessage.miniMessage().deserialize(Prefix.QUESTS.getPrefix())
					.append(Component.text(MessagesManager.textToSmall(" §7» §6" + progress + "§l/§6" + quest.getQt(currentTier) + " " + actionBar))));
		// }
	}
	
	private static void completeQuestTier(Player player, QUESTS quest, int completedTier) {
		player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 10);
		player.sendTitle("§6QUETE ENTIEREMENT COMPLETEE", "§e" + quest.getName());
		player.sendMessage(Prefix.QUESTS.getPrefix() + MessagesManager.textToSmall("§7» §6Quête entièrement complétée : §e" + quest.getName()));
		
		switch (quest.getReward()) {
			case ITEMS:
				player.getInventory().addItem(new ItemStack(quest.getRewardsMaterial().getType(), quest.getRewardsQt(completedTier)));
				player.sendMessage(Prefix.QUESTS.getPrefix() + MessagesManager.textToSmall(" §7» §6+ " + quest.getRewardsQt(completedTier) + " " + GlobalTranslator.render(Component.translatable(quest.getRewardsMaterial()), player.locale())));
				break;
			case MONEY:
				EconomyManager.getInstance().addBalance(player.getUniqueId(), quest.getRewardsQt(completedTier));
				
				player.sendMessage(Prefix.QUESTS.getPrefix() + MessagesManager.textToSmall(" §7» §6+ " + quest.getRewardsQt(completedTier) + "$"));
				break;
		}
	}
	
	private static int calculateTier(QUESTS quest, int progress) {
		int tier = 0;
		for (int i = 0; i < quest.getQtTiers().length; i++) {
			if (progress >= quest.getQt(i)) {
				tier = i + 1;
			} else {
				break;
			}
		}
		return tier;
	}
	
	private static void grantReward(Player player, QUESTS quest, int tier) {
		switch (quest.getReward()) {
			case ITEMS:
				player.getInventory().addItem(new ItemStack(quest.getRewardsMaterial().getType(), quest.getRewardsQt(tier)));
				player.sendMessage(Prefix.QUESTS.getPrefix() + MessagesManager.textToSmall(" §7» §6+ " + quest.getRewardsQt(tier) + " " + quest.getRewardsMaterial().getType().name()));
				break;
			case MONEY:
				EconomyManager.getInstance().addBalance(player.getUniqueId(), quest.getRewardsQt(tier));
				player.sendMessage(Prefix.QUESTS.getPrefix() + MessagesManager.textToSmall(" §7» §6+ " + quest.getRewardsQt(tier) + "$"));
				break;
		}
	}
	
}