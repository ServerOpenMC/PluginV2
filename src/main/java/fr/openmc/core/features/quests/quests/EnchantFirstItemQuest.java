package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantFirstItemQuest extends Quest implements Listener {

    public EnchantFirstItemQuest() {
        super("Abracadabra", "Enchanter un objet pour la première fois", Material.ENCHANTING_TABLE);

        this.addTiers(
                new QuestTier(1, new QuestMoneyReward(200))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() != Material.AIR) {
            this.incrementProgress(event.getEnchanter().getUniqueId());
        }
    }
}
