package fr.openmc.core.features.adminshop.menu.category.colored;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum Leaves implements BaseItems {
    OAK(20, 0, 1, ShopType.SELL, "§7de Chêne"),
    SPRUCE(21, 0, 0.5, ShopType.SELL, "§7de Sapin"),
    BIRCH(22, 0, 1, ShopType.SELL, "§7de Bouleau"),
    JUNGLE(23, 0, 1, ShopType.SELL, "§7de d'Acajou"),
    ACACIA(24, 0, 1, ShopType.SELL, "§7de Acacia"),
    DARK_OAK(29, 0, 1, ShopType.SELL, "§7de Chêne noir"),
    MANGROVE(30, 0, 0.5, ShopType.SELL, "§7de Palétuvier"),
    PALE_OAK(31, 0, 1, ShopType.SELL, "§7de Chêne pâle"),
    AZALEA(32, 0, 1.5, ShopType.SELL, "§7d'Azalée"),
    FLOWERING_AZALEA(33, 0, 3, ShopType.SELL, "d'Azalée fleurie"),
    CHERRY(40, 0, 1, ShopType.SELL, "§7de Cerisier"),
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;
    Leaves(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
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
