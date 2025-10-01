package fr.openmc.core.events;

import fr.openmc.core.utils.ArmorType;
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
}
