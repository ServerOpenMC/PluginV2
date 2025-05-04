package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftKebabQuest extends Quest implements Listener {

    public CraftKebabQuest() {
        super(
                "Kebab",
                "Fabriquer {target} kebab{s}",
                Material.BREAD
        );

        this.addTiers(
                new QuestTier(1, new QuestItemReward(CustomItemRegistry.getByName("omc_foods:kebab").getBest(), 16)),
                new QuestTier(32, new QuestMoneyReward(100)),
                new QuestTier(128, new QuestMoneyReward(400)),
                new QuestTier(512, new QuestMoneyReward(800))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && item.isSimilar(CustomItemRegistry.getByName("omc_foods:kebab").getBest())) {
            incrementProgress(event.getWhoClicked().getUniqueId());
        }
    }

}
