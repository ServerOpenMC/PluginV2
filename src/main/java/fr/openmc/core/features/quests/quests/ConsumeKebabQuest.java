package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ConsumeKebabQuest extends Quest implements Listener {

    public ConsumeKebabQuest() {
        super("Miam Miam", "Manger {target} kebab{s}", CustomItemRegistry.getByName("omc_foods:kebab").getBest());

        this.addTiers(
                new QuestTier(10, new QuestMoneyReward(30)),
                new QuestTier(64, new QuestMoneyReward(80)),
                new QuestTier(256, new QuestMoneyReward(160)),
                new QuestTier(1024, new QuestMoneyReward(1000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.isSimilar(CustomItemRegistry.getByName("omc_foods:kebab").getBest())) {
            this.incrementProgress(event.getPlayer().getUniqueId());
        }
    }

}
