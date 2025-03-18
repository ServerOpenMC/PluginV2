package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum MiscItems implements BaseItems {

    BREEZE_ROD(11, 4, 0, ShopType.BUY, "§7Bâton de Breeze"),
    GLOWSTONE_DUST(12, 3, 0.5, ShopType.SELL_BUY, "§7Poudre lumineuse"),
    ENDER_PEARL(13, 8, 0, ShopType.BUY, "§7Pearl de l'ender"),
    BONE(14, 0, 0.5, ShopType.SELL, "§7Os"),
    SHULKER_SHELL(15, 0, 5, ShopType.SELL, "§7Carapace de Shulker"),
    BLAZE_ROD(20, 4, 0, ShopType.BUY, "§7Bâton de blaze"),
    REDSTONE(21, 2, 0.25, ShopType.SELL_BUY, "§7Poudre de redstone"),
    MAGMA_CREAM(22, 3, 1.5, ShopType.SELL_BUY, "§7Crème de magma"),
    ROTTEN_FLESH(23, 0, 0.75, ShopType.SELL, "§7Chair putréfiée"),
    POPPED_CHORUS_FRUIT(24, 0, 2, ShopType.SELL, "§7Chorus éclaté"),
    STICK(29, 1, 0, ShopType.BUY, "§7Bâton"),
    GUNPOWDER(30, 6, 0, ShopType.BUY, "§7Poudre à canon"),
    SLIME_BALL(31, 6, 0, ShopType.BUY, "§7Boule de slime"),
    FERMENTED_SPIDER_EYE(32, 5, 2.75, ShopType.SELL_BUY, "§7Oeil d'araignée fermenté"),
    NETHER_STAR(33, 20200, 0, ShopType.BUY, "§7Etoile du nether"),
    WHITE_DYE(39, 2, 0, ShopType.DYE, "§7Teinture Blanche"),
    GLOW_INK_SAC(41, 3.5, 0, ShopType.BUY, "§7Poche d'encre luisante")
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;

    MiscItems(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
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
