package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakWheatQuest extends Quest implements Listener {

    public BreakWheatQuest() {
        super(
                "Fermier Dévoué",
                "Récolter {target} blé",
                new ItemStack(Material.WHEAT)
        );

        this.addTiers(
                new QuestTier(
                        100,
                        new QuestMoneyReward(500),
                        new QuestItemReward(Material.WOODEN_HOE, 1)
                ),

                new QuestTier(
                        500,
                        new QuestMoneyReward(1500),
                        new QuestItemReward(getEnchantedHoe(Material.STONE_HOE, 1, 1), 1)
                ),

                new QuestTier(
                        2000,
                        new QuestMoneyReward(5000),
                        new QuestItemReward(getEnchantedHoe(Material.IRON_HOE, 2, 1), 1)
                ),

                new QuestTier(
                        5000,
                        new QuestMoneyReward(10000),
                        new QuestItemReward(getEnchantedHoe(Material.DIAMOND_HOE, 3, 2), 1)
                )
        );
    }

    /**
     * Crée une houe enchantée pour les récompenses.
     * @param material Le matériau de la houe (ex: Material.IRON_HOE)
     * @param efficiencyLevel Niveau d'Efficiency
     * @param unbreakingLevel Niveau d'Unbreaking
     * @return ItemStack enchanté
     */
    private ItemStack getEnchantedHoe(Material material, int efficiencyLevel, int unbreakingLevel) {
        ItemStack hoe = new ItemStack(material, 1);
        hoe.editMeta(meta -> {
            meta.addEnchant(Enchantment.EFFICIENCY, efficiencyLevel, true);
            meta.addEnchant(Enchantment.UNBREAKING, unbreakingLevel, true);
        });
        return hoe;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.WHEAT && event.getBlock().getBlockData() instanceof Ageable ageable &&  ageable.getAge() == ageable.getMaximumAge()) {
            this.incrementProgress(event.getPlayer().getUniqueId(), 1);
        }
    }
}