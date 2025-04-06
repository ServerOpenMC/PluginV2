package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class WalkQuests extends Quest implements Listener {
    public WalkQuests() {
        super("Marcheur", "Marcher des blocs pour obtenir de l'argent", new ItemStack(Material.DIAMOND_BOOTS));
        this.addTier(new QuestTier(50, new QuestMoneyReward(100.0F), "Marcher {number} blocs"));
        this.addTier(new QuestTier(100, new QuestItemReward(Material.DIAMOND, 1), "Marcher {number} blocs"));
        this.addTier(new QuestTier(200, new QuestMoneyReward(200.0F), "Marcher {number} blocs"));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() != null && !event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            this.incrementProgress(event.getPlayer().getUniqueId(), 1);
        }
    }
}
