package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.quests.BreakStoneQuest;
import fr.openmc.core.features.quests.quests.CraftDiamondArmorQuest;
import fr.openmc.core.features.quests.quests.WalkQuests;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestsManager {
    final Map<String, Quest> quests = new HashMap<>();
    final OMCPlugin plugin = OMCPlugin.getInstance();
    @Getter static QuestsManager instance;
    QuestProgressSaveManager progressSaveManager;

    public QuestsManager() {
        instance = this;
        this.progressSaveManager = new QuestProgressSaveManager(this.plugin, this);
        this.loadDefaultQuests();
        this.progressSaveManager.loadAllQuestProgress();
    }

    public void registerQuest(Quest quest) {
        if (!this.quests.containsKey(quest.getName())) {
            this.quests.put(quest.getName(), quest);
            Bukkit.getPluginManager().registerEvents((Listener)quest, this.plugin);
        } else {
            this.plugin.getLogger().warning("Quest " + quest.getName() + " is already registered.");
        }
    }

    public void registerQuests(Quest... quests) {
        for (Quest quest : quests) {
            this.registerQuest(quest);
        }
    }

    public void loadDefaultQuests() {
        this.registerQuests(
                new BreakStoneQuest(),
                new WalkQuests(),
                new CraftDiamondArmorQuest()
        );
    }

    public List<Quest> getAllQuests() {
        return this.quests.values().stream().toList();
    }

    public void saveQuests() {
        this.progressSaveManager.saveAllQuestProgress();
    }

    public void saveQuests(UUID playerUUID) {
        this.progressSaveManager.savePlayerQuestProgress(playerUUID);
    }
}
