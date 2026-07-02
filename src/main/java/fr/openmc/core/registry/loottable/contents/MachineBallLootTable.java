package fr.openmc.core.registry.loottable.contents;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ItemBuilder;
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
                                meta.displayName(Component.text("§d§lPeluche Seinyy"));
                                meta.lore(List.of(Component.text("§7Une petite peluche comme Seinyy !")));
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
                                meta.displayName(Component.text("§b§lDiamants"));
                                meta.lore(List.of(Component.text("§7Ohhhh mais qu'est ce que c'est précieux ce truc !?")));
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
                                    meta.displayName(Component.text("§7§lLingots de Fer"));
                                    meta.lore(List.of(Component.text("§7Simplement du fer, rien de fou quoi...")));
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
                                    meta.displayName(Component.text("§4§lLingot De Netherite"));
                                    meta.lore(List.of(Component.text("§7Le truc le plus rare du jeu !")));
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
                                    meta.displayName(Component.text("§6§lBûches de Chêne"));
                                    meta.lore(List.of(Component.text("§7De quoi te faire une petite maison hihi")));
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
                                    meta.displayName(Component.text("§c§lSteaks"));
                                    meta.lore(List.of(Component.text("§7Miam miam, de la bonne viande !")));
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
                                    meta.displayName(Component.text("§8§lCharbon"));
                                    meta.lore(List.of(Component.text("§7De quoi faire du feu")));
                                }
                        ),
                        0.145,
                        16,
                        16
                )
        ));
    }
}
