package fr.openmc.core.events;

import fr.openmc.core.listeners.ArmorListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArmorEquipEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    @Setter
    @Getter
    private boolean cancelled = false;
    @Getter
    private final EquipMethod equipMethod;
    @Getter
    private final ArmorType type;
    @Getter
    @Setter
    private ItemStack oldArmorPiece,
            newArmorPiece;

    /**
     * Constructor for the ArmorEquipEvent.
     *
     * @param player        The player who is equipping or unequipping the armor.
     * @param equipMethod   The method by which the armor is being equipped or unequipped.
     * @param armorType     The type of armor being equipped or unequipped.
     * @param oldArmorPiece The previous armor piece in the slot (can be null).
     * @param newArmorPiece The new armor piece being placed in the slot (can be null).
     */
    public ArmorEquipEvent(Player player, EquipMethod equipMethod, ArmorType armorType, ItemStack oldArmorPiece, ItemStack newArmorPiece) {
        super(player);
        this.equipMethod = equipMethod;
        this.type = armorType;
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum EquipMethod {
        SHIFT_CLICK,
        DRAG,
        PICK_DROP,
        HOTBAR,
        HOTBAR_SWAP,
        DISPENSER,
        BROKE,
        DEATH
    }

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
}
