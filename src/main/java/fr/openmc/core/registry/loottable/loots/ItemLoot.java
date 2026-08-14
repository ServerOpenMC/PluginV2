package fr.openmc.core.registry.loottable.loots;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.utils.bukkit.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter
public class ItemLoot implements CustomLoot, RepresentedItem {
    @Setter
    private double chance;
    private final ItemStack item;
    private Predicate<Player> predicateToLoot = null;
    private Supplier<ItemStack> itemSupplier = null;
    private final int minAmount;
    private final int maxAmount;

    public ItemLoot(Supplier<ItemStack> itemSupplier, ItemStack displayedItem, double chance, int minAmount, int maxAmount) {
        this.chance = chance;
        this.item = displayedItem;
        this.itemSupplier = itemSupplier;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public ItemLoot(Supplier<ItemStack> itemSupplier, Material displayedItem, double chance, int minAmount, int maxAmount) {
        this(itemSupplier, ItemStack.of(displayedItem), chance, minAmount, maxAmount);
    }

    public ItemLoot(ItemStack item, double chance, int minAmount, int maxAmount) {
        this.chance = chance;
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public ItemLoot(ItemStack item, double chance, int amount) {
        this(item,
                chance,
                amount,
                amount);
    }

    public ItemLoot(Material item, double chance, int minAmount, int maxAmount) {
        this(ItemStack.of(item),
                chance,
                minAmount,
                maxAmount);
    }

    public ItemLoot(Material material, double chance, int amount) {
        this(ItemStack.of(material),
                chance,
                amount);
    }

    public ItemLoot(CustomItem item, double chance, int amount) {
        this(item.getBest(),
                chance,
                amount);
    }

    public ItemLoot(CustomItem item, Predicate<Player> predicate, double chance, int amount) {
        this(item.getBest(),
                chance,
                amount);

        this.predicateToLoot = predicate;
    }

    public ItemLoot(CustomItem item, double chance, int minAmount, int maxAmount) {
        this(item.getBest(),
                chance,
                minAmount,
                maxAmount);
    }

    public ItemStack getItemLootWithAmount() {
        ItemStack itemLoot = item.clone();
        itemLoot.setAmount(this.getRandomAmount());
        return itemLoot;
    }

    public int getRandomAmount() {
        if (minAmount == maxAmount) return minAmount;
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }

    @Override
    public Component getDisplayText() {
        return item.displayName();
    }

    /**
     * Renvoie un nom d'item simple, sans crochet et sans hover
     * @return un component contenant le nouveau nom du loot
     */
    public Component getSimpleText() {
        return item.effectiveName().asHoverEvent().value()
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    @Override
    public LootReward run(Player receiver) {
        if (predicateToLoot != null && !predicateToLoot.test(receiver)) return null;

        return runBase((items) -> {
            for (ItemStack item : items) {
                if (ItemUtils.hasEnoughSpace(receiver, item)) {
                    receiver.getInventory().addItem(item);
                } else {
                    receiver.getWorld().dropItemNaturally(receiver.getLocation(), item);
                }
            }
        });
    }

    public LootReward run(Player receiver, Location location) {
        if (predicateToLoot != null && !predicateToLoot.test(receiver)) return null;

        return runBase((items) -> {
            for (ItemStack item : items) {
                receiver.getWorld().dropItemNaturally(location, item);
            }
        });
    }

    private LootReward runBase(Consumer<List<ItemStack>> consumer) {
        ItemStack item;

        if (itemSupplier != null) {
            item = itemSupplier.get();
        } else {
            item = this.item.clone();
        }

        item.setAmount(this.getRandomAmount());

        consumer.accept(ItemUtils.splitAmountIntoStack(item));

        return LootReward.loots(Collections.singleton(this));
    }

    @Override
    public ItemStack getRepresentativeItem() {
        ItemStack loot = getItemLootWithAmount();
        if (loot.getAmount() > 99)
            return item;

        return getItemLootWithAmount();
    }
}