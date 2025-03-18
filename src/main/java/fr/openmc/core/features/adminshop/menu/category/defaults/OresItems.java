package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum OresItems implements BaseItems {
    COAL(13, 3, 1, ShopType.SELL_BUY, "§7Charbon"),
    RAW_COPPER(20, 4, 1.5, ShopType.SELL_BUY, "§7Lingot de cuivre"),
    RESIN_CLUMP(21, 3.5, 2, ShopType.SELL_BUY, "§7Amas de résine"),
    RAW_IRON(22, 8, 3, ShopType.SELL_BUY, "§7Lingot de fer"),
    RAW_GOLD(23, 12, 4, ShopType.SELL_BUY, "§7Lingot d'or"),
    NETHERITE_SCRAP(24, 500000, 0, ShopType.BUY, "§7Fragment de netherite"),
    COPPER_INGOT(29, 3, 0, ShopType.BUY, "§7Cuivre brut"),
    RESIN_BRICK(30, 5, 0, ShopType.BUY, "§7Brique de résine"),
    IRON_INGOT(31, 6, 0, ShopType.BUY, "§7Fer brut"),
    GOLD_INGOT(32, 10, 0, ShopType.BUY, "§7Or brut"),
    NETHERITE_INGOT(33, 1000000, 0, ShopType.BUY, "§7Lingot de netherite"),
    QUARTZ(38, 4, 0, ShopType.BUY, "§7Quartz"),
    LAPIS_LAZULI(39, 6, 2.5, ShopType.SELL_BUY, "§7Lapis lazuli"),
    AMETHYST_SHARD(40, 0, 5, ShopType.SELL, "§7Eclat d'améthyste"),
    EMERALD(41, 0, 15, ShopType.SELL, "§7Emeraude"),
    DIAMOND(42, 0, 20, ShopType.SELL, "§7Diamant"),
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;

    OresItems(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
        this.slots = slots;
        this.buyPrize = buyPrize;
        this.sellPrize = sellPrize;
        this.type = type;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ShopType getType() {
        return type;
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
    public int getSlots() {
        return slots;
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