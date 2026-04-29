package fr.openmc.core.utils;

import fr.openmc.core.listeners.ArmorListener;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
    HELMET(5),
    CHESTPLATE(6),
    LEGGINGS(7),
    BOOTS(8),
    ;

    @Getter
    private final int slot;

    /**
     * Constructor for ArmorType enum.
     *
     * @param slot The inventory slot associated with the armor type.
     */
    ArmorType(int slot) {
        this.slot = slot;
    }

    /**
     * Checks if the item match with the armor type.
     *
     * @param itemStack  The ItemStack to check.
     * @return true      if the item matches an armor type, false otherwise.
     */
    public static ArmorType match(final ItemStack itemStack) {
        if(ArmorListener.isAirOrNull(itemStack)) return null;
        String type = itemStack.getType().name();
        if(type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD")) return HELMET;
        else if(type.endsWith("_CHESTPLATE") || type.equals("ELYTRA")) return CHESTPLATE;
        else if(type.endsWith("_LEGGINGS")) return LEGGINGS;
        else if(type.endsWith("_BOOTS")) return BOOTS;
        else return null;
    }
}
