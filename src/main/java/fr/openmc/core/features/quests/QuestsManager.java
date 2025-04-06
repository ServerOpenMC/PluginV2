package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.quests.BreakStoneQuest;
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

    // TODO: - Ajouter un système de sauvegarde des quêtes (YML)
    // TODO: - Ajouter un système d'étapes de quêtes (Une quête peut avoir plusieurs étapes)
    //    ex: "Craft une armure en diamant" -> "Craft un plastron en diamant" -> "Craft un pantalon en diamant" -> "Craft des bottes en diamant"

    public QuestsManager() {
        instance = this;
        this.loadDefaultQuests();
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
                new WalkQuests()
        );
    }

    public List<Quest> getPlayerQuests(UUID playerUUID) {
        return this.quests.values().stream().toList();
    }
}
