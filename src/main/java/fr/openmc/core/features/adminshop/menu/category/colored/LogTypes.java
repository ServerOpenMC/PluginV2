package fr.openmc.core.features.adminshop.menu.category.colored;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum LogTypes implements BaseItems {
    OAK(10, 2, 0, ShopType.BUY, "§7de Chêne"),
    STRIPPED_OAK(11, 2.5, 0, ShopType.BUY, "§7de Chêne écorcé"),
    SPRUCE(12, 2, 0, ShopType.BUY, "§7de Sapin"),
    STRIPPED_SPRUCE(13, 2.5, 0, ShopType.BUY, "§7de Sapin écorcé"),
    BIRCH(14, 2, 0, ShopType.BUY, "§7de Bouleau"),
    STRIPPED_BIRCH(15, 2.5, 0, ShopType.BUY, "§7de Vouleau écorcé"),
    JUNGLE(16, 2, 0, ShopType.BUY, "§7de d'Acajou"),
    STRIPPED_JUNGLE(19, 2.5, 0, ShopType.BUY, "§7de d'Acajou écorcé"),
    ACACIA(20, 2, 0, ShopType.BUY, "§7de Acacia"),
    STRIPPED_ACACIA(21, 2.5, 0, ShopType.BUY, "§7de Acacia écorcé"),
    DARK_OAK(22, 2, 0, ShopType.BUY, "§7de Chêne noir"),
    STRIPPED_DARK_OAK(23, 2.5, 0, ShopType.BUY, "§7de Chêne noir écorcé"),
    MANGROVE(24, 2, 0, ShopType.BUY, "§7de Mangrove"),
    STRIPPED_MANGROVE(25, 2.5, 0, ShopType.BUY, "§7de Mangrove écorcé"),
    CHERRY(29, 2, 0, ShopType.BUY, "§7de Cerisier"),
    STRIPPED_CHERRY(30, 2.5, 0, ShopType.BUY, "§7de Cerisier écorcé"),
    PALE_OAK(31, 2, 0, ShopType.BUY, "§7de Chêne pâle"),
    STRIPPED_PALE_OAK(32, 2.5, 0, ShopType.BUY, "§7de Chêne pâle écorcé"),
    CRIMSON(33, 0, 0.5, ShopType.SELL, "§7Tige pourpre"),
    WARPED(40, 0, 0.5, ShopType.SELL, "§7Tige déformée"),
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;
    LogTypes(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
        this.slots = slots;
        this.buyPrize = buyPrize;
        this.sellPrize = sellPrize;
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
    public double getBuyPrize() {
        return buyPrize;
    }

    @Override
    public double getSellPrize() {
        return sellPrize;
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
