package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChickenThrowerQuest extends Quest implements Listener {

    public ChickenThrowerQuest() {
        super(
                "Lanceur de poules",
                List.of("Lancer {target} oeufs"),
                new ItemStack(Material.EGG)
        );

        this.addTiers(
                new QuestTier(50, new QuestMoneyReward(500)),
                new QuestTier(200, new QuestMoneyReward(2000)),
                new QuestTier(500, new QuestMoneyReward(5000)),
                new QuestTier(1000, new QuestMoneyReward(10000))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEggThrow(PlayerEggThrowEvent event) {
        this.incrementProgress(event.getPlayer().getUniqueId(), 1);
    }
}