package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.quests.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * QuestsManager is responsible for managing quests in the game.
 * <p>
 * It handles the registration of quests, loading default quests,
 * and saving quest progress for players.
 */
public class QuestsManager {
    static final Map<String, Quest> quests = new HashMap<>();

    /**
     * Constructor for QuestsManager.
     * This constructor initializes the instance of QuestsManager,
     * loads default quests, and loads all quest progress.
     */
    public QuestsManager() {
        loadDefaultQuests();
        QuestProgressSaveManager.loadAllQuestProgress();
    }

    /**
     * Register a quest.
     * If the quest is already registered, it will not be registered again.
     *
     * @param quest the quest to register
     */
    public static void registerQuest(Quest quest) {
        if (!quests.containsKey(quest.getName())) {
            quests.put(quest.getName(), quest);
            if (quest instanceof Listener questL) {
                Bukkit.getPluginManager().registerEvents(questL, OMCPlugin.getInstance());
            }
        } else {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Quest {} is already registered.", quest.getName(), new Exception());
        }
    }

    /**
     * Register multiple quests at once.
     *
     * @param quests the quests to register
     */
    public static void registerQuests(Quest... quests) {
        for (Quest quest : quests) {
            registerQuest(quest);
        }
    }

    /**
     * Load default quests.
     * This method is called in the constructor of QuestsManager.
     */
    public static void loadDefaultQuests() {
        registerQuests(
                new BreakStoneQuest(),
                new WalkQuests(),
                new CraftDiamondArmorQuest(),
                new BreakDiamondQuest(),
                new KillPlayersQuest(),
                new CraftCakeQuest(),
                new EnchantFirstItemQuest(),
                new KillSuperCreeperQuest(),
                new KillZombieQuest(),
                new SmeltIronQuest(),
                new SaveTheEarthQuest(),
                new WinContestQuest(),
                new CraftTheMixtureQuest(),
                new ConsumeKebabQuest(),
                new MineAyweniteQuest(),
                new ChickenThrowerQuest(),
                new BreakWheatQuest(),
                new BreakLogQuest(),
                new FishingQuest()

        );
    }

    /**
     * Get all quests.
     *
     * @return the quest if found, null otherwise
     */
    public static List<Quest> getAllQuests() {
        return quests.values().stream().toList();
    }

    /**
     * Save all quests.
     * <p>
     * This method is called when the server is shutting down.
     */
    public static void saveQuests() {
        QuestProgressSaveManager.saveAllQuestProgress();
    }

    /**
     * Save quests for a specific player.
     * <p>
     * This method is called when a player logs out or when the server is shutting down.
     *
     * @param playerUUID the UUID of the player
     */
    public static void saveQuests(UUID playerUUID) {
        QuestProgressSaveManager.savePlayerQuestProgress(playerUUID);
    }
}
