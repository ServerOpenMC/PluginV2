package fr.openmc.core.registry.loottable.contents;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class MachineBallLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {
        return "omc:machine_ball";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        Set.of(OMCRegistry.CUSTOM_ITEMS.PELUCHE_SEINYY.getBest()),
                        new ItemBuilder(
                            OMCRegistry.CUSTOM_ITEMS.PELUCHE_SEINYY,
                            meta -> {
                                meta.displayName(Component.text("§d§lPeluche Seinyy"));
                                meta.lore(List.of(Component.text("§7Une petite peluche comme Seinyy !")));
                            }
                        ),
                        10.0,
                        1,
                        1
                ),
                new CustomLoot(
                        Set.of(new ItemStack(Material.DIAMOND, 3)),
                        new ItemBuilder(
                            Material.DIAMOND,
                            meta -> {
                                meta.displayName(Component.text("§b§lDiamants"));
                                meta.lore(List.of(Component.text("§7Ohhhh mais qu'est ce que c'est précieux ce truc !?")));
                            }
                        ),
                        15.0,
                        1,
                        1
                ),
                new CustomLoot(
                        Set.of(new ItemStack(Material.IRON_INGOT, 10)),
                        new ItemBuilder(
                                Material.IRON_INGOT,
                                meta -> {
                                    meta.displayName(Component.text("§7§lLingots de Fer"));
                                    meta.lore(List.of(Component.text("§7Simplement du fer, rien de fou quoi...")));
                                }
                        ),
                        20.0,
                        1,
                        1
                ),
                new CustomLoot(
                        Set.of(new ItemStack(Material.NETHERITE_INGOT)),
                        new ItemBuilder(
                                Material.NETHERITE_INGOT,
                                meta -> {
                                    meta.displayName(Component.text("§4§lLingot De Netherite"));
                                    meta.lore(List.of(Component.text("§7Le truc le plus rare du jeu !")));
                                }
                        ),
                        0.5,
                        1,
                        1
                ),
                new CustomLoot(
                        Set.of(new ItemStack(Material.OAK_LOG, 32)),
                        new ItemBuilder(
                                Material.OAK_LOG,
                                meta -> {
                                    meta.displayName(Component.text("§6§lBûches de Chêne"));
                                    meta.lore(List.of(Component.text("§7De quoi te faire une petite maison hihi")));
                                }
                        ),
                        25,
                        1,
                        1
                ),
                new CustomLoot(
                        Set.of(new ItemStack(Material.COOKED_BEEF, 16)),
                        new ItemBuilder(
                                Material.COOKED_BEEF,
                                meta -> {
                                    meta.displayName(Component.text("§c§lSteaks"));
                                    meta.lore(List.of(Component.text("§7Miam miam, de la bonne viande !")));
                                }
                        ),
                        15,
                        1,
                        1
                ),
                new CustomLoot(
                        Set.of(new ItemStack(Material.COAL, 16)),
                        new ItemBuilder(
                                Material.COAL,
                                meta -> {
                                    meta.displayName(Component.text("§8§lCharbon"));
                                    meta.lore(List.of(Component.text("§7De quoi faire du feu")));
                                }
                        ),
                        14.5,
                        1,
                        1
                )
        );
    }
}
