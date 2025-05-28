package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingQuest extends Quest implements Listener {

    public FishingQuest() {
        super(
                "Pêcheur Expérimenté",
                "Pêcher {target} poissons",
                new ItemStack(Material.FISHING_ROD)
        );

        this.addTiers(
                new QuestTier(
                        50,
                        new QuestMoneyReward(1000),
                        new QuestItemReward(getEnchantedRod(1, 0, 1), 1)  // Luck I, Unbreaking I
                ),

                new QuestTier(
                        150,
                        new QuestMoneyReward(3000),
                        new QuestItemReward(getEnchantedRod(2, 1, 1), 1)  // Luck II, Lure I, Unbreaking I
                ),

                new QuestTier(
                        500,
                        new QuestMoneyReward(8000),
                        new QuestItemReward(getEnchantedRod(3, 2, 1, true), 1)  // + Mending
                ),

                new QuestTier(
                        1000,
                        new QuestMoneyReward(15000),
                        new QuestItemReward(getEnchantedRod(3, 3, 3, true), 1)  // Toutes les enchantements max
                )
        );
    }

    /**
     * Crée une canne à pêche enchantée.
     * @param luckLevel Niveau de "Luck of the Sea"
     * @param lureLevel Niveau de "Lure"
     * @param unbreakingLevel Niveau de "Unbreaking"
     * @param hasMending Si true, ajoute "Mending"
     * @return ItemStack enchanté
     */
    private ItemStack getEnchantedRod(int luckLevel, int lureLevel, int unbreakingLevel, boolean hasMending) {
        ItemStack rod = new ItemStack(Material.FISHING_ROD, 1);
        rod.editMeta(meta -> {
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, luckLevel, true);
            meta.addEnchant(Enchantment.LURE, lureLevel, true);
            meta.addEnchant(Enchantment.UNBREAKING, unbreakingLevel, true);
            if (hasMending) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
            }
        });
        return rod;
    }

    private ItemStack getEnchantedRod(int luckLevel, int lureLevel, int unbreakingLevel) {
        return getEnchantedRod(luckLevel, lureLevel, unbreakingLevel, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            this.incrementProgress(event.getPlayer().getUniqueId(), 1);
        }
    }
}