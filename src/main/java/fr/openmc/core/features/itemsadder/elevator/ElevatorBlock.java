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

    public ElevatorBlock(ElevatorColor color) {
        super(new CustomItemMeta(color.getElevator()));
        this.color = color;
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.NOTE_BLOCK);
        item.editMeta(meta -> meta.itemName(
                TranslationManager.translation("itemsadder.omc_elevator.elevator.name")
        ));
        return item;
    }
}
