package fr.openmc.core.registry.loottable.contents;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ItemBuilder;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MachineBallLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.MACHINE_BALL.getName();
    }

    @Override
    public String getNamespace() {
        return "omc:machine_ball";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        Set.of(OMCRegistry.CUSTOM_ITEMS.PELUCHE_SEINYY.getBest()),
                        new ItemBuilder(
                            OMCRegistry.CUSTOM_ITEMS.PELUCHE_SEINYY,
                            meta -> {
                                meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.seinyy.name"));
                                meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.seinyy.lore"));
                            }
                        ),
                        0.1,
                        1,
                        1
                ),
                new ItemLoot(
                        Set.of(ItemStack.of(Material.DIAMOND)),
                        new ItemBuilder(
                            Material.DIAMOND,
                            meta -> {
                                meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.diamond.name"));
                                meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.diamond.lore"));
                            }
                        ),
                        0.15,
                        3,
                        3
                ),
                new ItemLoot(
                        Set.of(ItemStack.of(Material.IRON_INGOT)),
                        new ItemBuilder(
                                Material.IRON_INGOT,
                                meta -> {
                                    meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.iron.name"));
                                    meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.iron.lore"));
                                }
                        ),
                        0.2,
                        10,
                        10
                ),
                new ItemLoot(
                        Set.of(ItemStack.of(Material.NETHERITE_INGOT)),
                        new ItemBuilder(
                                Material.NETHERITE_INGOT,
                                meta -> {
                                    meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.netherite.name"));
                                    meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.netherite.lore"));
                                }
                        ),
                        0.05,
                        1,
                        1
                ),
                new ItemLoot(
                        Set.of(ItemStack.of(Material.OAK_LOG)),
                        new ItemBuilder(
                                Material.OAK_LOG,
                                meta -> {
                                    meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.oak_log.name"));
                                    meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.oak_log.lore"));
                                }
                        ),
                        0.25,
                        32,
                        32
                ),
                new ItemLoot(
                        Set.of(ItemStack.of(Material.COOKED_BEEF)),
                        new ItemBuilder(
                                Material.COOKED_BEEF,
                                meta -> {
                                    meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.steak.name"));
                                    meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.steak.lore"));
                                }
                        ),
                        0.15,
                        16,
                        16
                ),
                new ItemLoot(
                        Set.of(ItemStack.of(Material.COAL)),
                        new ItemBuilder(
                                Material.COAL,
                                meta -> {
                                    meta.displayName(TranslationManager.translation("feature.tickets.loot.machine_ball.coal.name"));
                                    meta.lore(TranslationManager.translationLore("feature.tickets.loot.machine_ball.coal.lore"));
                                }
                        ),
                        0.145,
                        16,
                        16
                )
        ));
    }
}
