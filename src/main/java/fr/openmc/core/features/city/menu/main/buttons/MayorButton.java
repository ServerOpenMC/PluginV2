package fr.openmc.core.features.city.menu.main.buttons;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.actions.MayorCommandAction;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public class MayorButton {
    public static void init(Menu menu, City city, int[] slots) {
        Player player = menu.getOwner();

        MenuUtils.runDynamicButtonItem(player, menu, slots, getItemSupplier(menu, city, player))
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L * 60);

    }

    private static Supplier<ItemMenuBuilder> getItemSupplier(Menu menu, City city, Player player) {
        return () ->
                new ItemMenuBuilder(menu, Material.PAPER, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.city.menus.main.mayor.title"));
                    itemMeta.lore(getDynamicLore(city, player));
                    itemMeta.setItemModel(NamespacedKey.minecraft("air"));
                }).setOnClick(inventoryClickEvent -> MayorCommandAction.launchInteractionMenu(player));
    }

    private static List<Component> getDynamicLore(City city, Player player) {
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);
        Component mayorName = (city.getMayor() != null && city.getMayor().getName() != null)
                ? city.getMayor().getName()
                : TranslationManager.translation("messages.menus.none");
        NamedTextColor mayorColor = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getMayorColor() : NamedTextColor.DARK_GRAY;
        Component mayorComponent = mayorName.color(mayorColor).decoration(TextDecoration.ITALIC, false);

        List<Component> lore;
        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
            lore = switch (city.getMayorPhase()) {
                case MayorPhase.MAYOR_ELECTED -> TranslationManager.translationLore(
                        "feature.city.menus.main.mayor.locked.phase2",
                        Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR)).color(NamedTextColor.RED)
                );
                case MayorPhase.OPEN_ELECTION -> TranslationManager.translationLore(
                        "feature.city.menus.main.mayor.locked.phase1",
                        Component.text(DateUtils.getTimeUntilNextDay(MayorPhase.MAYOR_ELECTED.getStartDay())).color(NamedTextColor.RED),
                        Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR)).color(NamedTextColor.RED)
                );
            };
        } else {
            if (city.getElectionType() == ElectionType.ELECTION) {
                lore = switch (city.getMayorPhase()) {
                    case MayorPhase.MAYOR_ELECTED -> TranslationManager.translationLore(
                            "feature.city.menus.main.mayor.election.phase2",
                            mayorComponent
                    );
                    case MayorPhase.OPEN_ELECTION -> TranslationManager.translationLore(
                            "feature.city.menus.main.mayor.election.phase1",
                            Component.text(DateUtils.getTimeUntilNextDay(MayorPhase.MAYOR_ELECTED.getStartDay())).color(NamedTextColor.RED)
                    );
                };
            } else {
                switch (city.getMayorPhase()) {
                    case MayorPhase.MAYOR_ELECTED -> lore = TranslationManager.translationLore(
                            "feature.city.menus.main.mayor.owner.phase2",
                            mayorComponent,
                            Component.text(DateUtils.getTimeUntilNextDay(MayorPhase.OPEN_ELECTION.getStartDay())).color(NamedTextColor.RED)
                    );
                    case MayorPhase.OPEN_ELECTION -> {
                        if (hasPermissionOwner) {
                            if (city.hasMayor()) {
                                lore = TranslationManager.translationLore(
                                        "feature.city.menus.main.mayor.owner.phase1.has_mayor",
                                        Component.text(MayorManager.MEMBER_REQUEST_ELECTION).color(NamedTextColor.BLUE),
                                        Component.text(DateUtils.getTimeUntilNextDay(MayorPhase.MAYOR_ELECTED.getStartDay())).color(NamedTextColor.RED)
                                );
                            } else {
                                lore = TranslationManager.translationLore(
                                        "feature.city.menus.main.mayor.owner.phase1.no_mayor",
                                        Component.text(MayorManager.MEMBER_REQUEST_ELECTION).color(NamedTextColor.BLUE),
                                        Component.text(DateUtils.getTimeUntilNextDay(MayorPhase.MAYOR_ELECTED.getStartDay())).color(NamedTextColor.RED)
                                );
                            }
                        } else {
                            lore = TranslationManager.translationLore(
                                    "feature.city.menus.main.mayor.owner.phase1.viewer",
                                    Component.text(MayorManager.MEMBER_REQUEST_ELECTION).color(NamedTextColor.BLUE),
                                    Component.text(DateUtils.getTimeUntilNextDay(MayorPhase.MAYOR_ELECTED.getStartDay())).color(NamedTextColor.RED)
                            );
                        }
                    }
                    default -> lore = TranslationManager.translationLore("feature.city.menus.main.mayor.owner.error");
                }
            }
        }

        return lore;
    }
}
