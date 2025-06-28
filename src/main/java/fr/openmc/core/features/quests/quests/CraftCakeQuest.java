package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftCakeQuest extends Quest implements Listener {

    public CraftCakeQuest() {
        super("Le pâtissier", "Craft {target} gâteaux", Material.CAKE);

        this.addTiers(
                new QuestTier(64, new QuestMoneyReward(100)),
                new QuestTier(256, new QuestMoneyReward(400))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCraft(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType().equals(Material.CAKE)) {
            this.incrementProgress(event.getWhoClicked().getUniqueId());
        }
    }

}
