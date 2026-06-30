package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EpicFishingTreasureLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.EPIC_FISHING_TREASURE.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:epic_fishing_treasure";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(this::generateEnchantedBook, Material.ENCHANTED_BOOK, 0.2, 1, 2),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KEBAB_FERMENTED,
                        0.1,
                        1,
                        3
                ),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.LEGENDARY_FISHING_TREASURE, 0.7, 1),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK,
                        0.07,
                        2,
                        6
                )
        ));
    }

    private final List<Enchantment> ENCHANTMENT_AVAILABLE = List.of(
            Enchantment.DEPTH_STRIDER,
            Enchantment.LURE,
            Enchantment.MENDING,
            Enchantment.LOOTING,
            Enchantment.UNBREAKING,
            Enchantment.LUCK_OF_THE_SEA
    );

    private ItemStack generateEnchantedBook() {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
        if (meta != null) {
            for (Enchantment enchantment : selectEnchantment(RandomUtils.randomBetween(2, 5))) {
                int level = RandomUtils.randomBetween(enchantment.getStartLevel(), enchantment.getMaxLevel());
                meta.addStoredEnchant(enchantment, level, true);
            }

            enchantedBook.setItemMeta(meta);
        }
        return enchantedBook;
    }

    private List<Enchantment> selectEnchantment(int number) {
        List<Enchantment> randomOrder = RandomUtils.generateRandomOrder(ENCHANTMENT_AVAILABLE);

        return randomOrder.subList(0, Math.min(number, randomOrder.size()));
    }
}