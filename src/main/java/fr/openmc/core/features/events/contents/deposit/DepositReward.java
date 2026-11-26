package fr.openmc.core.features.events.contents.deposit;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record DepositReward(double money, List<ItemStack> items) {

    public static DepositReward money(double money) {
        return new DepositReward(money, List.of());
    }

    public static DepositReward of(double money, ItemStack... items) {
        return new DepositReward(money, List.of(items));
    }

    public static List<ItemStack> copies(ItemStack base, int n) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(base.clone());
        return list;
    }

    public DepositReward with(List<ItemStack> more) {
        List<ItemStack> all = new ArrayList<>(items);
        all.addAll(more);
        return new DepositReward(money, all);
    }
}
