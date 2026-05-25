package fr.openmc.core.registry.lootboxes.contents;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.lootboxes.LootboxOptions;
import net.kyori.adventure.text.Component;

import java.util.stream.IntStream;

public class MachineBallLootbox extends CustomLootbox {
    public MachineBallLootbox() {
        super(
                "omc:machine_ball",
                Component.text("§6§lLa Machine à boules"),
                OMCRegistry.CUSTOM_LOOT_TABLES.get("omc:machine_ball"),
                new LootboxOptions(
                        InventorySize.NORMAL,
                        60,
                        IntStream.range(10, 17).boxed().toList(),
                        13
                )
        );
    }
}
