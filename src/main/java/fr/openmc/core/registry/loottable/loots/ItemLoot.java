package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

@Getter
public class ItemLoot implements CustomLoot {
    private final double chance;
    private final Set<ItemStack> items;
    private final ItemStack displayedItem;
    private final int minAmount;
    private final int maxAmount;

    public ItemLoot(Set<ItemStack> items, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {
        this.chance = chance;
        this.items = items;
        this.displayedItem = displayedItem;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public ItemLoot(ItemStack item, double chance, int minAmount, int maxAmount) {
        this(Collections.singleton(item),
                null,
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(CustomItem item, double chance, int minAmount, int maxAmount) {
        if (item == null) {
            throw new IllegalArgumentException("CustomItem cannot be null");
        }
        this(Collections.singleton(item.getBest()),
                null,
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(Material item, Material displayedItem, double chance, int minAmount, int maxAmount) {
        this(ItemStack.of(item),
                ItemStack.of(displayedItem),
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(ItemStack item, Material displayedItem, double chance, int minAmount, int maxAmount) {
        this(item,
                ItemStack.of(displayedItem),
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(ItemStack item, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {
        this(Collections.singleton(item),
                displayedItem,
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(CustomItem item, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {
        if (item == null) {
            throw new IllegalArgumentException("CustomItem cannot be null");
        }
        this(Collections.singleton(item.getBest()),
                displayedItem,
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(CustomItem item, CustomItem displayedItem, double chance, int minAmount, int maxAmount) {
        if (item == null) throw new IllegalArgumentException("CustomItem cannot be null");
        if (displayedItem == null) throw new IllegalArgumentException("CustomItem cannot be null");

        this(Collections.singleton(item.getBest()),
                displayedItem.getBest(),
                chance,
                minAmount,
                maxAmount);
    }

    public ItemStack getFirstLoot() {
        if (items.size() == 1) {
            return items.iterator().next();
        }
        return items.stream().findFirst().orElse(null);
    }

    public int getRandomAmount() {
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }

    @Override
    public Component getDisplayText() {
        System.out.println(getFirstLoot());
        return getFirstLoot().displayName();
    }

    @Override
    public void run(Player receiver) {
        for (ItemStack lootItem : this.getItems()) {
            ItemStack item = lootItem.clone();
            item.setAmount(this.getRandomAmount());
            receiver.getInventory().addItem(item);
        }
    }
}