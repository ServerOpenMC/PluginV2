package fr.openmc.core.features.itemsadder.elevator;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemMeta;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class ElevatorBlock extends CustomItem {

    @Getter
    public ElevatorColor color;

    public ElevatorBlock(CustomItemMeta meta, ElevatorColor color) {
        meta.add("elevator:color", color);
        this(meta);
    }

    public ElevatorBlock(CustomItemMeta meta) {
        super(meta);

        color = setColor(
                getMeta().get("elevator:color").getClass() == ElevatorColor.class ?
                        (ElevatorColor) getMeta().get("elevator:color")
                        : ElevatorColor.DEFAULT
        );
    }

    public ElevatorColor setColor(ElevatorColor color) {
        if (this.color.equals(color)) return color;
        getMeta().add("elevator:color", color);
        return color;
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.NOTE_BLOCK);
        item.editMeta(meta -> meta.itemName(
                TranslationManager.translation("itemsadder.omc_elevator.name")
        ));
        return item;
    }
}
