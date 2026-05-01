package fr.openmc.core.features.quests.quests;

import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestTier;
import fr.openmc.core.features.quests.rewards.QuestItemReward;
import fr.openmc.core.features.quests.rewards.QuestMoneyReward;
import fr.openmc.core.utils.bukkit.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BreakLogQuest extends Quest implements Listener {

    private final static Map<Material, Integer> logWeights = new EnumMap<>(Material.class);
    static {
        // Définir les poids (progression plus rapide pour les bûches rares)
        logWeights.put(Material.OAK_LOG, 1);
        logWeights.put(Material.BIRCH_LOG, 1);
        logWeights.put(Material.SPRUCE_LOG, 1);
        logWeights.put(Material.ACACIA_LOG, 2);
        logWeights.put(Material.DARK_OAK_LOG, 3);
        logWeights.put(Material.JUNGLE_LOG, 3);
        logWeights.put(Material.MANGROVE_LOG, 4);
        logWeights.put(Material.CHERRY_LOG, 4);
        logWeights.put(Material.CRIMSON_STEM, 5);
        logWeights.put(Material.WARPED_STEM, 5);
    }

    private static List<Component> getLore() {
        List<Component> lore = new ArrayList<>();

        for (var entry : logWeights.entrySet()) {
            lore.add(Component.text("- ").color(NamedTextColor.DARK_GRAY)
                    .append(ItemUtils.getItemTranslation(entry.getKey()))
                    .append(Component.text(" : "))
                    .append(Component.text(entry.getValue()).color(NamedTextColor.AQUA))
                    .append(Component.text(" points"))
                    .decoration(TextDecoration.ITALIC, false)
                    .color(NamedTextColor.GRAY)
            );
        }

        return lore;
    }

    public BreakLogQuest() {
        super("Bûcheron de l'extrême", List.of("Casser {target} bûches"), new ItemStack(Material.IRON_AXE));

        this.setAdditionalLore(getLore());
        this.addTiers(
                new QuestTier(500, new QuestMoneyReward(300), new QuestItemReward(Material.IRON_AXE, 1)),
                new QuestTier(1500, new QuestMoneyReward(500), new QuestItemReward(getEnchantedAxe(Material.IRON_AXE), 1)),
                new QuestTier(5000, new QuestMoneyReward(1500), new QuestItemReward(getEnchantedAxe(Material.GOLDEN_AXE), 1)),
                new QuestTier(15000, new QuestMoneyReward(3000), new QuestItemReward(getEnchantedAxe(Material.DIAMOND_AXE), 1))

        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        if (logWeights.containsKey(type)) {
            int progress = logWeights.get(type);
            this.incrementProgress(event.getPlayer().getUniqueId(), progress);
        }
    }

    /**
     * Crée une hâche enchantée pour les récompenses.
     * @param material Le matériau de la hache (ex: Material.IRON_AXE)
     * @return ItemStack enchanté
     */
    private ItemStack getEnchantedAxe(Material material) {
        ItemStack axe = ItemStack.of(material);
        axe.editMeta(meta -> {
            meta.addEnchant(Enchantment.EFFICIENCY, 3, true); // Efficacité III
            meta.addEnchant(Enchantment.UNBREAKING, 2, true); // Solidité II
        });
        return axe;
    }
}
