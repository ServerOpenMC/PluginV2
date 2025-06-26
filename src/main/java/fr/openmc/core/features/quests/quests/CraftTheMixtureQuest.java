package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftTheMixtureQuest extends Quest implements Listener {

    public CraftTheMixtureQuest() {
        super(
                "Un étrange mélange",
                "Fabriquer {target} The Mixture{s}",
                CustomItemRegistry.getByName("omc_foods:the_mixture").getBest()
        );

        this.addTiers(
                new QuestTier(1, new QuestMoneyReward(250))),
                new QuestTier(32, new QuestMoneyReward(500)),
                new QuestTier(128, new QuestMoneyReward(1000)),
                new QuestTier(512, new QuestMoneyReward(2000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.isSimilar(CustomItemRegistry.getByName("omc_foods:the_mixture").getBest()))
            return;

        // Le joueur ne craft pas plus d'un de The Mixture
        if (!event.isShiftClick()) {
            incrementProgress(event.getWhoClicked().getUniqueId());
            return;
        }

        // Calcul le nombre maximum de The Mixture pouvant être craftés avec les items dans la table de craft
        int maxCraftable = ItemUtils.getMaxCraftAmount(event.getInventory());
        // Calcul le nombre maximum de The Mixture que le joueur peut stocker en plus
        int capacity = ItemUtils.getFreePlacesForItem((Player) event.getWhoClicked(), item);

        maxCraftable = Math.min(maxCraftable, capacity);
        if (maxCraftable == 0)
            return;

        incrementProgress(event.getWhoClicked().getUniqueId(), maxCraftable);
    }
}
