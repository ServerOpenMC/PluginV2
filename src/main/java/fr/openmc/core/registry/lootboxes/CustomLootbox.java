package fr.openmc.core.registry.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.lootboxes.menu.LootboxOpenMenu;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.menu.LootsInfoMenu;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

@Getter
public abstract class CustomLootbox {
    private final ItemStack itemDisplayed;
    private final String namespace;
    private final Component name;
    private final CustomLootTable lootTable;
    private LootboxOptions options = new LootboxOptions( // default options
            InventorySize.LARGER,
            60,
            IntStream.range(19, 26).boxed().toList(),
            22
    );

    public CustomLootbox(ItemStack itemDisplayed, String namespace, Component name, CustomLootTable lootTable) {
        this.itemDisplayed = itemDisplayed;
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
    }

    public CustomLootbox(String namespace, Component name, CustomLootTable lootTable) {
        this.itemDisplayed = null;
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
    }

    public CustomLootbox(CustomItem itemDisplayed, String namespace, Component name, CustomLootTable lootTable, LootboxOptions options) {
        this.itemDisplayed = itemDisplayed.getBest();
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
        this.options = options;
    }

    public CustomLootbox(ItemStack itemDisplayed, String namespace, Component name, CustomLootTable lootTable, LootboxOptions options) {
        this.itemDisplayed = itemDisplayed;
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
        this.options = options;
    }

    public CustomLootbox(String namespace, Component name, CustomLootTable lootTable, LootboxOptions options) {
        this.itemDisplayed = null;
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
        this.options = options;
    }

    public void open(Player player) {
        new LootboxOpenMenu(player, this).open();
    }

    public void openInfo(Player player) {
        new LootsInfoMenu(player, this.getName(), this.getLootTable().getLoots()).open();
    }
}
