package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum BlocksItems implements BaseItems {
    WHITE_CONCRETE_POWDER(12, 2, 0, ShopType.WHITE_CONCRETE_POWDER, "§7Béton en poudre"),
    WHITE_WOOL(13, 2, 0, ShopType.WHITE_WOOL, "§7Laine"),
    WHITE_CONCRETE(14, 2, 0, ShopType.WHITE_CONCRETE, "§7Béton"),
    GRASS_BLOCK(19, 3, 1, ShopType.SELL_BUY, "§7Bloc d'herbe"),
    OAK_LOG(20, 2, 0, ShopType.LOG, "§7buche de chêne"),
    STONE(21, 2, 0.75, ShopType.SELL_BUY, "§7Pierre"),
    DEEPSLATE(22, 3, 0, ShopType.BUY, "§7Ardoise des abîmes"),
    AMETHYST_BLOCK(23, 20, 0, ShopType.BUY, "§7Bloc d'améthyste"),
    OBSIDIAN(24, 0, 10, ShopType.SELL, "§7Obsidienne"),
    ICE(25, 4, 0, ShopType.BUY, "§7Glace"),
    DIRT(28, 1, 0, ShopType.BUY, "§7Terre"),
    OAK_LEAVES(29, 1, 0, ShopType.LEAVES, "§7Feuilles de chêne"),
    GRAVEL(30, 0, 2, ShopType.SELL, "§7Gravier"),
    DIORITE(31, 0, 2, ShopType.SELL, "§7Diorite"),
    GRANITE(32, 0, 2, ShopType.SELL, "§7Granite"),
    ANDESITE(33, 0, 2, ShopType.SELL, "§7Andésite"),
    PACKED_ICE(34, 0, 2, ShopType.SELL, "§7Glace compactée"),
    TERRACOTTA(39, 2, 0, ShopType.TERRACOTTA, "§7Terre cuite"),
    GLASS(40, 2, 0, ShopType.GLASS, "§7Verre"),
    GLASS_PANE(41, 2, 0, ShopType.GLASS_PANE, "§7Vitre"),
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;

    BlocksItems(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
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
