package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.actions.MayorCommandAction;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.DateUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_1_DAY;
import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_2_DAY;

public class MayorButton {
    public static void init(Menu menu, City city, int[] slots) {
        Player player = menu.getOwner();

        MenuUtils.runDynamicButtonItem(player, menu, slots, getItemSupplier(menu, city, player))
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L * 60);

    }

    private static Supplier<ItemBuilder> getItemSupplier(Menu menu, City city, Player player) {
        return () ->
                new ItemBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(Component.text("§9Les élections"));
                    itemMeta.lore(getDynamicLore(city, player));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> MayorCommandAction.launchInteractionMenu(player));
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);
        String mayorName = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getName() : "§7Aucun";
        NamedTextColor mayorColor = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getMayorColor() : NamedTextColor.DARK_GRAY;

        List<Component> lore;
        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
            lore = switch (MayorManager.phaseMayor) {
                case 2 -> List.of(
                        Component.text("§7En ce moment, les maires sont tous appliqués dans les villes !"),
                        Component.text("§7Sauf la votre !"),
                        Component.empty(),
                        Component.text("§cVous devez être niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR) + " pour débloquer ceci")
                );
                case 1 -> List.of(
                        Component.text("§7Les élections sont actuellement §9ouverte"),
                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                        Component.text("§7Mais vous ne pouvez pas y accéder !"),
                        Component.empty(),
                        Component.text("§cVous devez être niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR) + " pour débloquer ceci")
                );
                default -> List.of(
                        Component.text("§cErreur"),
                        Component.text("§cVous devez être niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR) + " pour débloquer ceci")
                );
            };
        } else {
            if (city.getElectionType() == ElectionType.ELECTION) {
                lore = switch (MayorManager.phaseMayor) {
                    case 2 -> List.of(
                            Component.text("§7Votre ville a un §9maire !"),
                            Component.text("§7Maire : ").append(Component.text(mayorName)).color(mayorColor).decoration(TextDecoration.ITALIC, false),
                            Component.empty(),
                            Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX INFORMATIONS")
                    );
                    case 1 -> List.of(
                            Component.text("§7Les élections sont actuellement §9ouverte"),
                            Component.empty(),
                            Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                            Component.empty(),
                            Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX ELECTIONS")
                    );
                    default -> List.of(
                            Component.text("§cErreur")
                    );
                };
            } else {
                switch (MayorManager.phaseMayor) {
                    case 2 -> lore = List.of(
                            Component.text("§7Votre ville a un §9maire !"),
                            Component.text("§7Maire §7: ").append(Component.text(mayorName)).color(mayorColor).decoration(TextDecoration.ITALIC, false),
                            Component.text("§cOuverture des élections dans " + DateUtils.getTimeUntilNextDay(PHASE_1_DAY)),
                            Component.empty(),
                            Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX INFORMATIONS")
                    );
                    case 1 -> {
                        if (hasPermissionOwner) {
                            if (city.hasMayor()) {
                                lore = List.of(
                                        Component.text("§7Les élections sont §9désactivées"),
                                        Component.text("§cIl vous faut au moins §9" + MayorManager.MEMBER_REQUEST_ELECTION + " §cmembres"),
                                        Component.empty(),
                                        Component.text("§7Vous avez déjà choisis vos §3réformes §7!"),
                                        Component.text("§7Cependant vous pouvez changer votre couleur !"),
                                        Component.empty(),
                                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY))
                                );
                            } else {
                                lore = List.of(
                                        Component.text("§7Les élections sont §9désactivées"),
                                        Component.text("§cIl vous faut au moins §9" + MayorManager.MEMBER_REQUEST_ELECTION + " §cmembres"),
                                        Component.empty(),
                                        Component.text("§7Seul le propriétaire peut choisir §3les réformes §7qu'il veut."),
                                        Component.empty(),
                                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                                        Component.empty(),
                                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR VOS REFORMES")
                                );
                            }
                        } else {
                            lore = List.of(
                                    Component.text("§7Les élections sont §9désactivées"),
                                    Component.text("§cIl vous faut au moins §9" + MayorManager.MEMBER_REQUEST_ELECTION + " §cmembres"),
                                    Component.empty(),
                                    Component.text("§7Seul le propriétaire peut choisir §3les réformes §7qu'il veut."),
                                    Component.empty(),
                                    Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY))
                            );
                        }
                    }
                    default -> lore = List.of(Component.text("§cErreur"));
                }
            }
        }

        return lore;
    }
}
