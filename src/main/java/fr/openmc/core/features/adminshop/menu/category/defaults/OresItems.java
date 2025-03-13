package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum OresItems implements BaseItems {
    COAL(13, 3, ShopType.SELL_BUY, "§7Charbon"),
    RAW_COPPER(20, 4, ShopType.SELL_BUY, "§7Lingot de cuivre"),
    RESIN_CLUMP(21, 3.5, ShopType.SELL_BUY, "§7Amas de résine"),
    RAW_IRON(22, 8, ShopType.SELL_BUY, "§7Lingot de fer"),
    RAW_GOLD(23, 12, ShopType.SELL_BUY, "§7Lingot d'or"),
    NETHERITE_SCRAP(24, 500000, ShopType.BUY, "§7Fragment de netherite"),
    COPPER_INGOT(29, 3, ShopType.BUY, "§7Cuivre brut"),
    RESIN_BRICK(30, 5, ShopType.BUY, "§7Brique de résine"),
    IRON_INGOT(31, 6, ShopType.BUY, "§7Fer brut"),
    GOLD_INGOT(32, 10, ShopType.BUY, "§7Or brut"),
    NETHERITE_INGOT(33, 1000000, ShopType.BUY, "§7Lingot de netherite"),
    QUARTZ(38, 4, ShopType.BUY, "§7Quartz"),
    LAPIS_LAZULI(39, 6, ShopType.SELL_BUY, "§7Lapis lazuli"),
    AMETHYST_SHARD(40, 5, ShopType.SELL, "§7Eclat d'améthyste"),
    EMERALD(41, 15, ShopType.SELL, "§7Emeraude"),
    DIAMOND(42, 20, ShopType.SELL, "§7Diamant"),
    ;

    private final int slots;
    private final double prize;
    private final ShopType type;
    private final String name;

    OresItems(int slots, double prize, ShopType type, String name) {
        this.slots = slots;
        this.prize = prize;
        this.type = type;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ShopType getType() {
        return this.type;
    }

    @Override
    public double getPrize() {
        return this.prize;
    }

    @Override
    public int getSlots() {
        return this.slots;
    }

    @Override
    public String named() {
        return name();
    }

    @Override
    public int getMaxStack() {
        Material material = Material.getMaterial(this.named());;
        return material == null ? 64 : material.getMaxStackSize();
    }

}