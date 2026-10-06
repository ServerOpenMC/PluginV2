package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityFeaturesRegistry;
import fr.openmc.core.features.quests.command.QuestCommand;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.quests.*;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasRegistries;
import fr.openmc.core.lifecycle.registries.LifecycleRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;
import lombok.Getter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.function.Supplier;

/**
 * QuestsManager is responsible for managing quests in the game.
 * <p>
 * It handles the registration of quests, loading default quests,
 * and saving quest progress for players.
 */
@Credit(developers = {"Axeno"}, graphist = {"Gexary"})
public class QuestsManager extends Feature implements HasCommands, HasRegistries {
    @Getter
    private final Map<String, Quest> quests = new HashMap<>();
    private QuestProgressSaveManager questProgressSaveManager;

    /**
     * Initialisation for QuestsManager.
     * This constructor initializes the instance of QuestsManager,
     * loads default quests, and loads all quest progress.
     */
    @Override
    public void init() {
        questProgressSaveManager = OMCRegistry.QUEST_FEATURES.QUEST_PROGRESS;

        loadDefaultQuests();
        questProgressSaveManager.loadAllQuestProgress();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new QuestCommand()
        );
    }

    @Override
    public void save() {
        this.saveQuests();
    }

    @Override
    public List<Supplier<LifecycleRegistry>> getRegistries() {
        return new ArrayList<>(List.of(
                () -> SubRegistry.boot(new QuestsFeatureRegistry(),
                        r -> OMCRegistry.QUEST_FEATURES = r)
        ));
    }

    /**
     * Register a quest.
     * If the quest is already registered, it will not be registered again.
     *
     * @param quest the quest to register
     */
    public void registerQuest(Quest quest) {
        String questName = PlainTextComponentSerializer.plainText().serialize(quest.getName());
        if (!quests.containsKey(questName)) {
            quests.put(questName, quest);
            if (quest instanceof Listener questL) {
                Bukkit.getPluginManager().registerEvents(questL, OMCPlugin.getInstance());
            }
        } else {
            OMCLogger.warn("Quest {} is already registered.", questName, new Exception());
        }
    }

    /**
     * Register multiple quests at once.
     *
     * @param quests the quests to register
     */
    public void registerQuests(Quest... quests) {
        for (Quest quest : quests) {
            registerQuest(quest);
        }
    }

    /**
     * Load default quests.
     * This method is called in the constructor of QuestsManager.
     */
    public void loadDefaultQuests() {
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
    public List<Quest> getAllQuests() {
        return quests.values().stream().toList();
    }

    /**
     * Save all quests.
     * <p>
     * This method is called when the server is shutting down.
     */
    public void saveQuests() {
        questProgressSaveManager.saveAllQuestProgress();
    }

    /**
     * Save quests for a specific player.
     * <p>
     * This method is called when a player logs out or when the server is shutting down.
     *
     * @param playerUUID the UUID of the player
     */
    public void saveQuests(UUID playerUUID) {
        questProgressSaveManager.savePlayerQuestProgress(playerUUID);
    }
}
