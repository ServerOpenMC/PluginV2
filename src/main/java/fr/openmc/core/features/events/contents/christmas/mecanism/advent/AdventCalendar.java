package fr.openmc.core.features.events.contents.christmas.mecanism.advent;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.loots.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public enum AdventCalendar {
    J1(List.of(25), new MoneyLoot(5000, 1)),
    J2(List.of(38, 39, 40), new FreeClaimLoot(1, 1)),
    J3(List.of(9, 18), new ItemLoot(Material.PAPER, 1, 1)),
    J4(List.of(4, 5), new ItemLoot(Material.PAPER, 1, 20)),
    J5(List.of(34, 35, 43, 44, 52, 53), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 64)),
    J6(List.of(49, 50, 51), new ItemLoot(Material.PAPER, 1, 1)),
    J7(List.of(30), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.KEBAB_FERMENTED, 1, 2)),
    J8(List.of(21), new FreeClaimLoot(2, 1)),
    J9(List.of(45), new MoneyLoot(5000, 1)),
    J10(List.of(19, 20, 28, 29), new ItemLoot(Material.PAPER, 1, 1)),
    J11(List.of(41, 42), new ItemLoot(Material.PAPER, 32, 1)),
    J12(List.of(12, 13), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 96)),
    J13(List.of(27, 36), new ItemLoot(DreamItemRegistry.SOMNIFERE, 1, 3)),
    J14(List.of(37, 46), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.MODERN_BOX, 1, 1)),
    J15(List.of(0), new MoneyLoot(7000, 1)),
    J16(List.of(1, 2, 10, 11), new ItemLoot(OMCRegistry.CUSTOM_ITEMS.AYWENITE, 1, 128)),
    J17(List.of(26), new ItemLoot(Material.PAPER, 1, 1)),
    J18(List.of(14), new FreeClaimLoot(2, 1)),
    J19(List.of(47), new ItemLoot(Material.PAPER, 1, 64)),
    J20(List.of(6, 7, 8, 15, 16, 17), new ItemLoot(Material.PAPER, 1, 1)),
    J21(List.of(24, 33), new ItemLoot(Material.PAPER, 1, 2)),
    J22(List.of(48), new BitsLoot(200, 1)),
    J23(List.of(3), new MoneyLoot(10000, 1)),
    J24(List.of(22, 23, 31, 32), new ItemLoot(Material.PAPER, 1, 1));

    private final List<Integer> slots;
    private final CustomLoot reward;

    AdventCalendar(List<Integer> slots, CustomLoot reward) {
        this.slots = slots;
        this.reward = reward;
    }

    public void giveReward(Player player) {
        reward.run(player);
    }

    public boolean isCalendarActive() {
        LocalDate now = LocalDate.now();

        return now.getMonth().equals(Month.DECEMBER);
    }

    public boolean isCaseOpened(Integer day) {
        LocalDate now = LocalDate.now();

        return now.getMonth().equals(Month.DECEMBER) && day >= now.getDayOfMonth();
    }
}
