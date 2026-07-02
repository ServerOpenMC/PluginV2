package fr.openmc.core.registry.loottable.contents;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
                                meta.displayName(Component.text("Peluche Seinyy", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
                                meta.lore(List.of(Component.text("Une petite peluche comme Seinyy !", NamedTextColor.GRAY)));
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
                                meta.displayName(Component.text("Diamants", NamedTextColor.AQUA, TextDecoration.BOLD));
                                meta.lore(List.of(Component.text("Ohhhh mais qu'est ce que c'est précieux ce truc !?", NamedTextColor.GRAY)));
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
                                    meta.displayName(Component.text("Lingots de Fer", NamedTextColor.GRAY, TextDecoration.BOLD));
                                    meta.lore(List.of(Component.text("Simplement du fer, rien de fou quoi...", NamedTextColor.GRAY)));
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
                                    meta.displayName(Component.text("Lingot De Netherite", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                                    meta.lore(List.of(Component.text("Le truc le plus rare du jeu !", NamedTextColor.GRAY)));
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
                                    meta.displayName(Component.text("Bûches de Chêne", NamedTextColor.GOLD, TextDecoration.BOLD));
                                    meta.lore(List.of(Component.text("De quoi te faire une petite maison hihi", NamedTextColor.GRAY)));
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
                                    meta.displayName(Component.text("Steaks", NamedTextColor.RED, TextDecoration.BOLD));
                                    meta.lore(List.of(Component.text("Miam miam, de la bonne viande !", NamedTextColor.GRAY)));
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
                                    meta.displayName(Component.text("Charbon", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));
                                    meta.lore(List.of(Component.text("De quoi faire du feu", NamedTextColor.GRAY)));
                                }
                        ),
                        0.145,
                        16,
                        16
                )
        ));
    }
}
