package fr.openmc.core.registry.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.registry.loottable.CustomLootTable;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

@Getter
public abstract class CustomLootbox {
    private final String namespace;
    private final Component name;
    private final CustomLootTable lootTable;
    private LootboxOptions options = new LootboxOptions( // default options
            InventorySize.LARGER,
            60,
            IntStream.range(19, 26).boxed().toList(),
            22
    );

    public CustomLootbox(String namespace, Component name, CustomLootTable lootTable) {
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
    }

    public CustomLootbox(String namespace, Component name, CustomLootTable lootTable, LootboxOptions options) {
        this.namespace = namespace;
        this.name = name;
        this.lootTable = lootTable;
        this.options = options;
    }

    public void open(Player player) {
        new LootboxOpenMenu(player, this).open();
    }
}
