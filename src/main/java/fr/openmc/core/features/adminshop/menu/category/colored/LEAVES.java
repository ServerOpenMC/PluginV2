package fr.openmc.core.features.adminshop.menu.category.colored;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum LEAVES implements BaseItems {
    OAK(20, 1, ShopType.SELL, "§7de chêne"),
    SPRUCE(21, 0.5, ShopType.SELL, "§7de sapin"),
    BIRCH(22, 1, ShopType.SELL, "§7de bouleau"),
    JUNGLE(23, 1, ShopType.SELL, "§7de d'acajou"),
    ACACIA(24, 1, ShopType.SELL, "§7de acacia"),
    DARK_OAK(29, 1, ShopType.SELL, "§7de chêne noir"),
    MANGROVE(30, 0.5, ShopType.SELL, "§7de palétuvier"),
    PALE_OAK(31, 1, ShopType.SELL, "§7de chêne pâle"),
    AZALEA(32, 1.5, ShopType.SELL, "§7s'azalée"),
    FLOWERING_AZALEA(33, 3, ShopType.SELL, "d'azalée fleurie"),
    CHERRY(40, 1, ShopType.SELL, "§7de cerisier"),
    ;

    private final int slots;
    private final double prize;
    private final ShopType type;
    private final String name;
    LEAVES(int slots, double prize, ShopType type, String name) {
        this.slots = slots;
        this.prize = prize;
        this.type = type;
        this.name = name;
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public ShopType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrize() {
        return prize;
    }

    @Override
    public String named() {
        return name();
    }

    @Override
    public int getMaxStack() {
        Material material = Material.getMaterial(this.named());
        return material == null ? 64 : material.getMaxStackSize();
    }
}
