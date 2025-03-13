package fr.openmc.core.features.adminshop.menu.category.colored;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum LOGTYPES implements BaseItems {
    OAK(10, 2, ShopType.BUY, "§7de chêne"),
    STRIPPED_OAK(11, 2.5, ShopType.BUY, "§7de chêne écorcé"),
    SPRUCE(12, 2, ShopType.BUY, "§7de sapin"),
    STRIPPED_SPRUCE(13, 2.5, ShopType.BUY, "§7de sapin écorcé"),
    BIRCH(14, 2, ShopType.BUY, "§7de bouleau"),
    STRIPPED_BIRCH(15, 2.5, ShopType.BUY, "§7de bouleau écorcé"),
    JUNGLE(16, 2, ShopType.BUY, "§7de d'acajou"),
    STRIPPED_JUNGLE(19, 2.5, ShopType.BUY, "§7de d'acajou écorcé"),
    ACACIA(20, 2, ShopType.BUY, "§7de acacia"),
    STRIPPED_ACACIA(21, 2.5, ShopType.BUY, "§7de acacia écorcé"),
    DARK_OAK(22, 2, ShopType.BUY, "§7de chêne noir"),
    STRIPPED_DARK_OAK(23, 2.5, ShopType.BUY, "§7de chêne noir écorcé"),
    MANGROVE(24, 2, ShopType.BUY, "§7de mangrove"),
    STRIPPED_MANGROVE(25, 2.5, ShopType.BUY, "§7de mangrove écorcé"),
    CHERRY(29, 2, ShopType.BUY, "§7de cerisier"),
    STRIPPED_CHERRY(30, 2.5, ShopType.BUY, "§7de cerisier écorcé"),
    PALE_OAK(31, 2, ShopType.BUY, "§7de chêne pâle"),
    STRIPPED_PALE_OAK(32, 2.5, ShopType.SELL, "§7de chêne pâle écorcé"),
    CRIMSON(33, 0.5, ShopType.SELL, "§7tige pourpre"),
    WARPED(40, 0.5, ShopType.SELL, "§7tige déformée"),
    ;

    private final int slots;
    private final double prize;
    private final ShopType type;
    private final String name;
    LOGTYPES(int slots, double prize, ShopType type, String name) {
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
