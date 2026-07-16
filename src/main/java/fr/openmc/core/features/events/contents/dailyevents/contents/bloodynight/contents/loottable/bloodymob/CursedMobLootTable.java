package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.loottable.bloodymob;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CursedMobLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.bloody_night.loot_table.cursed_mob");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:cursed_mobs";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(Material.IRON_BLOCK,0.3, 1, 2),
                new ItemLoot(Material.GOLD_BLOCK,0.2, 1, 2),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE_BLOCK,0.2, 2, 4),
                new ItemLoot(Material.DIAMOND,0.2, 1, 2),
                new ItemLoot(ItemType.SPLASH_POTION.createItemStack(p -> {
                    p.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 5, 1), true);
                }),0.13, 1),
                new ItemLoot(Material.DIAMOND_BLOCK,0.1, 1),
                new ItemLoot(Material.NETHERITE_SCRAP,0.04, 1)
        ));
    }
}
