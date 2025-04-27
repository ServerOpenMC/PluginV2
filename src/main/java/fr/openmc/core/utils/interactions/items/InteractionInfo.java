package fr.openmc.core.utils.interactions.items;

import fr.openmc.core.utils.chronometer.ChronometerInfo;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class InteractionInfo {
    private final ItemStack item;
    private final ChronometerInfo chronometerInfo;

    public InteractionInfo(ItemStack item, ChronometerInfo chronometerInfo) {
        this.chronometerInfo = chronometerInfo;
        this.item = item;
    }
}
