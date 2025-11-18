package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftTheMixtureQuest extends Quest implements Listener {

    public CraftTheMixtureQuest() {
        super(
                "The Mixture",
                List.of("Fabriquer {target} The Mixture{s}"),
                CustomItemRegistry.getByName("omc_foods:the_mixture").getBest()
        );

        this.addTiers(
                new QuestTier(1, new QuestItemReward(CustomItemRegistry.getByName("omc_foods:the_mixture").getBest(), 16)),
                new QuestTier(32, new QuestMoneyReward(100)),
                new QuestTier(128, new QuestMoneyReward(300)),
                new QuestTier(512, new QuestMoneyReward(700))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.isSimilar(CustomItemRegistry.getByName("omc_foods:the_mixture").getBest()))
            return;

        // Le joueur ne craft pas plus d'un kebab
        if (!event.isShiftClick()) {
            incrementProgress(event.getWhoClicked().getUniqueId());
            return;
        }

        // Calcul le nombre maximum de kebabs pouvant être craftés avec les items dans la table de craft
        int maxCraftable = ItemUtils.getMaxCraftAmount(event.getInventory());
        // Calcul le nombre maximum de kebabs que le joueur peut stocker en plus
        int capacity = ItemUtils.getFreePlacesForItem((Player) event.getWhoClicked(), item);

        maxCraftable = Math.min(maxCraftable, capacity);
        if (maxCraftable == 0)
            return;

        incrementProgress(event.getWhoClicked().getUniqueId(), maxCraftable);
    }
}
