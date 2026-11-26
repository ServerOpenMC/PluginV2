package fr.openmc.core.features.events.contents.christmas.mecanism.advent;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.events.contents.christmas.ChristmasManager;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.registry.loottable.loots.*;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Getter
public enum AdventCalendar {
    J1(1, List.of(25), new MoneyLoot(5000, 1)),
    J2(2, List.of(38, 39, 40), new FreeClaimLoot(1, 1)),
    J3(3, List.of(9, 18), new ItemLoot(Material.PAPER, 1, 1)),
    J4(4, List.of(4, 5), new ItemLoot(Material.PAPER, 1, 20)),
    J5(5, List.of(34, 35, 43, 44, 52, 53), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 64)),
    J6(6, List.of(49, 50, 51), new ItemLoot(Material.PAPER, 1, 1)),
    J7(7, List.of(30), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.KEBAB_FERMENTED, 1, 2)),
    J8(8, List.of(21), new FreeClaimLoot(2, 1)),
    J9(9, List.of(45), new MoneyLoot(5000, 1)),
    J10(10, List.of(19, 20, 28, 29), new ItemLoot(Material.PAPER, 1, 1)),
    J11(11, List.of(41, 42), new ItemLoot(Material.PAPER, 1, 32)),
    J12(12, List.of(12, 13), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 96)),
    J13(13, List.of(27, 36), new ItemLoot(DreamItemRegistry.SOMNIFERE, 1, 3)),
    J14(14, List.of(37, 46), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.MODERN_BOX, 1, 1)),
    J15(15, List.of(0), new MoneyLoot(7000, 1)),
    J16(16, List.of(1, 2, 10, 11), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 128)),
    J17(17, List.of(26), new ItemLoot(Material.PAPER, 1, 1)),
    J18(18, List.of(14), new FreeClaimLoot(2, 1)),
    J19(19, List.of(47), new ItemLoot(Material.PAPER, 1, 64)),
    J20(20, List.of(6, 7, 8, 15, 16, 17), new ItemLoot(Material.PAPER, 1, 1)),
    J21(21, List.of(24, 33), new ItemLoot(Material.PAPER, 1, 2)),
    J22(22, List.of(48), new BitsLoot(200, 1)),
    J23(23, List.of(3), new MoneyLoot(10000, 1)),
    J24(24, List.of(22, 23, 31, 32), new ItemLoot(Material.PAPER, 1, 1));

    private final Integer day;
    private final List<Integer> slots;
    private final CustomLoot reward;

    AdventCalendar(Integer day, List<Integer> slots, CustomLoot reward) {
        this.slots = slots;
        this.reward = reward;
        this.day = day;
    }

    public void giveReward(Player player) {
        reward.run(player);
    }

    public static boolean isCalendarActive() {
        LocalDate now = LocalDate.now();

        return now.getMonth().equals(Month.NOVEMBER); // todo changer
    }

    public boolean isCaseOpened() {
        return isCalendarActive() && day <= LocalDate.now().getDayOfMonth();
    }

    public ItemMenuBuilder getGeneratedItem(Menu menu) {
        boolean unlocked = isCaseOpened();
        boolean claimed = ChristmasManager.hasClaimReward(menu.getOwner(), day);

        return new ItemMenuBuilder(menu, Material.BARRIER, meta -> {
            meta.setItemModel(NamespacedKey.fromString("air"));
            meta.displayName(TranslationManager.translation("feature.events.christmas.menu.case_opener",
                            Component.text(day))
                    .color(unlocked ? NamedTextColor.GREEN : NamedTextColor.YELLOW));

            if (!unlocked) {
                meta.lore(TranslationManager.translationLore("feature.events.christmas.menu.case_opener.lore.not_opened"));
            } else if (claimed) {
                meta.lore(TranslationManager.translationLore("feature.events.christmas.menu.case_opener.lore.opened.reclaimed",
                        this.getReward().getDisplayText()));
            } else {
                meta.lore(TranslationManager.translationLore("feature.events.christmas.menu.case_opener.lore.opened.not_reclaimed"));
            }
        }).setOnClick(e -> {
            if (!isCalendarActive()) {
                menu.getOwner().message().sendError(TranslationManager.translation("feature.events.christmas.calendar.ended"));
                return;
            }

            if (!isCaseOpened()) {
                menu.getOwner().message().sendError(TranslationManager.translation("feature.events.christmas.menu.case.not_opened"));
                return;
            }

            if (ChristmasManager.hasClaimReward(menu.getOwner(), day)) {
                menu.getOwner().message().sendSuccess(TranslationManager.translation("feature.events.christmas.menu.already_open"));
                return;
            }

            LootReward reward = this.getReward().run(menu.getOwner());
            ChristmasManager.claimReward(menu.getOwner(), day);

            menu.getOwner().message().sendSuccess(TranslationManager.translation("feature.events.christmas.menu.open_case",
                    Component.text(day),
                    reward.buildComponent()));

            menu.open();
        });
    }
}
