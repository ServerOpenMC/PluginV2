package fr.openmc.api.input.location;

import fr.openmc.api.chronometer.ChronometerInfo;
import org.bukkit.inventory.ItemStack;

public record InteractionInfo(ItemStack item, ChronometerInfo chronometerInfo) {
}
