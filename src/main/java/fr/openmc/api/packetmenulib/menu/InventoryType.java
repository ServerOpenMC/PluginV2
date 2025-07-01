package fr.openmc.api.packetmenulib.menu;

import lombok.Getter;
import net.minecraft.world.inventory.MenuType;

@Getter
public enum InventoryType {
    // https://minecraft.wiki/w/Java_Edition_protocol/Inventory
    GENERIC_9X1(0, 45),                // generic_9x1       A 1-row inventory, not used by the notchian server.
    GENERIC_9X2(1, 54),                // generic_9x2       A 2-row inventory, not used by the notchian server.
    GENERIC_9X3(2, 63),                // generic_9x3	     General-purpose 3-row inventory. Used by Chest, Minecart with Chest, Ender Chest, and Barrel.
    GENERIC_9X4(3, 72),                // generic_9x4       A 4-row inventory, not used by the notchian server.
    GENERIC_9X5(4, 81),                // generic_9x5       5-row inventory, not used by the notchian server.
    GENERIC_9X6(5, 90),                // generic_9x6	     General-purpose 6-row inventory, used by Large Chest.
    GENERIC_3X3(6, 45),                // generic_3x3	     General-purpose 3-by-3 square inventory, used by Dispenser and Dropper.
    CRAFTER_3X3(7, 45),                // crafter_3x3       Crafter
    ANVIL(8, 39),                      // anvil	         Anvil
    BEACON(9, 37),                     // beacon	         Beacon
    BLAST_FURNACE(10, 39),             // blast_furnace     Blast Furnace
    BREWING_STAND(11, 41),             // brewing_stand     Brewing Stand
    CRAFTING(12, 46),                  // crafting	         Crafting Table
    ENCHANTMENT(13, 38),               // enchantment	     Enchanting Table
    FURNACE(14, 39),                   // furnace	         Furnace
    GRINDSTONE(15, 39),                // grindstone	     Grindstone
    HOPPER(16, 41),                    // hopper	         Hopper or Minecart with Hopper
    LECTERN(17, 1),                    // lectern	         Lectern -- I don't recommend using it
    LOOM(18, 40),                      // loom	             Loom
    MERCHANT(19, 39),                  // merchant	         Villager or Wandering Trader
    SHULKER_BOX(20, 63),               // shulker_box	     Shulker Box -- The exact same thing as generic_9x3 but is a different thing for minecraft
    SMITHING(21, 40),                  // smithing	         Smithing Table
    SMOKER(22, 39),                    // smoker	         Smoker
    CARTOGRAPHY_TABLE(23, 39),         // cartography_table Cartography Table
    STONECUTTER(24, 38);               // stonecutter	     Stonecutter

    private final int id;
    private final int slots;
    private final MenuType<?> menuType;

    InventoryType(int id, int slots) {
        this.id = id;
        this.slots = slots;
        menuType = switch (id) {
            case 0 -> MenuType.GENERIC_9x1;
            case 1 -> MenuType.GENERIC_9x2;
            case 2 -> MenuType.GENERIC_9x3;
            case 3 -> MenuType.GENERIC_9x4;
            case 4 -> MenuType.GENERIC_9x5;
            case 5 -> MenuType.GENERIC_9x6;
            case 6 -> MenuType.GENERIC_3x3;
            case 7 -> MenuType.CRAFTER_3x3;
            case 8 -> MenuType.ANVIL;
            case 9 -> MenuType.BEACON;
            case 10 -> MenuType.BLAST_FURNACE;
            case 11 -> MenuType.BREWING_STAND;
            case 12 -> MenuType.CRAFTING;
            case 13 -> MenuType.ENCHANTMENT;
            case 14 -> MenuType.FURNACE;
            case 15 -> MenuType.GRINDSTONE;
            case 16 -> MenuType.HOPPER;
            case 17 -> MenuType.LECTERN;
            case 18 -> MenuType.LOOM;
            case 19 -> MenuType.MERCHANT;
            case 20 -> MenuType.SHULKER_BOX;
            case 21 -> MenuType.SMITHING;
            case 22 -> MenuType.SMOKER;
            case 23 -> MenuType.CARTOGRAPHY_TABLE;
            case 24 -> MenuType.STONECUTTER;
            default -> throw new IllegalArgumentException("Invalid inventory type id: " + id);
        };
    }

}