package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.quests.*;
import lombok.Getter;
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
    final Map<String, Quest> quests = new HashMap<>();
    final OMCPlugin plugin = OMCPlugin.getInstance();
    @Getter static QuestsManager instance;
    QuestProgressSaveManager progressSaveManager;

    /**
     * Constructor for QuestsManager.
     * This constructor initializes the instance of QuestsManager,
     * loads default quests, and loads all quest progress.
     */
    public QuestsManager() {
        instance = this;
        this.progressSaveManager = new QuestProgressSaveManager(this.plugin, this);
        this.loadDefaultQuests();
        this.progressSaveManager.loadAllQuestProgress();
    }

    /**
     * Register a quest.
     * If the quest is already registered, it will not be registered again.
     *
     * @param quest the quest to register
     */
    public void registerQuest(Quest quest) {
        if (!this.quests.containsKey(quest.getName())) {
            this.quests.put(quest.getName(), quest);
            if (quest instanceof Listener questL) {
                Bukkit.getPluginManager().registerEvents(questL, this.plugin);
            }
        } else {
            this.plugin.getLogger().warning("Quest " + quest.getName() + " is already registered.");
        }
    }

    /**
     * Register multiple quests at once.
     *
     * @param quests the quests to register
     */
    public void registerQuests(Quest... quests) {
        for (Quest quest : quests) {
            this.registerQuest(quest);
        }
    }

    /**
     * Load default quests.
     * This method is called in the constructor of QuestsManager.
     */
    public void loadDefaultQuests() {
        this.registerQuests(
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
                new CityCreateQuest(),
                new WinContestQuest(),
                new CraftKebabQuest(),
                new ConsumeKebabQuest(),
                new MineAyweniteQuest()
        );
    }

    /**
     * Get all quests.
     *
     * @return the quest if found, null otherwise
     */
    public List<Quest> getAllQuests() {
        return this.quests.values().stream().toList();
    }

    /**
     * Save all quests.
     * <p>
     * This method is called when the server is shutting down.
     */
    public void saveQuests() {
        this.progressSaveManager.saveAllQuestProgress();
    }

    /**
     * Save quests for a specific player.
     * <p>
     * This method is called when a player logs out or when the server is shutting down.
     *
     * @param playerUUID the UUID of the player
     */
    public void saveQuests(UUID playerUUID) {
        this.progressSaveManager.savePlayerQuestProgress(playerUUID);
    }
}
