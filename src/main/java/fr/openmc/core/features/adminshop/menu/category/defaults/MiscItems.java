package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum MiscItems implements BaseItems {

    BREEZE_ROD(11, 4, ShopType.BUY, "§7Bâton de Breeze"),
    GLOWSTONE_DUST(12, 3, ShopType.SELL_BUY, "§7Poudre lumineuse"),
    ENDER_PEARL(13, 8, ShopType.BUY, "§7Pearl de l'ender"),
    BONE(14, 0.5, ShopType.SELL, "§7Os"),
    SHULKER_SHELL(15, 5, ShopType.SELL, "§7Carapace de Shulker"),
    BLAZE_ROD(20, 4, ShopType.BUY, "§7Bâton de blaze"),
    REDSTONE(21, 2.5, ShopType.SELL_BUY, "§7Poudre de redstone"),
    MAGMA_CREAM(22, 3, ShopType.SELL_BUY, "§7Crème de magma"),
    ROTTEN_FLESH(23, 0.75, ShopType.SELL, "§7Chair putréfiée"),
    POPPED_CHORUS_FRUIT(24, 2, ShopType.SELL, "§7Chorus éclaté"),
    STICK(29, 1, ShopType.BUY, "§7Bâton"),
    GUNPOWDER(30, 6, ShopType.BUY, "§7Poudre à canon"),
    SLIME_BALL(31, 6, ShopType.BUY, "§7Boule de slime"),
    FERMENTED_SPIDER_EYE(32, 5, ShopType.SELL_BUY, "§7Oeil d'araignée fermenté"),
    NETHER_STAR(33, 20200, ShopType.BUY, "§7Etoile du nether"),
    WHITE_DYE(39, 2, ShopType.DYE, "§7Teinture Blanche"),
    GLOW_INK_SAC(41, 3.5, ShopType.BUY, "§7Poche d'encre luisante")
    ;

    private final int slots;
    private final double prize;
    private final ShopType type;
    private final String name;

    MiscItems(int slots, double prize, ShopType type, String name) {
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
