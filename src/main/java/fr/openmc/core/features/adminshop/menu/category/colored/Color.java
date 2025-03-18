package fr.openmc.core.features.adminshop.menu.category.colored;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum Color implements BaseItems {
    WHITE(12, 2, 0, ShopType.BUY, "§7Blanc"),
    LIGHT_GRAY(13, 2, 0, ShopType.BUY, "§7Gris clair"),
    GRAY(14, 2, 0, ShopType.BUY, "§7Gris"),
    BLACK(20, 2, 0, ShopType.BUY, "§7Noir"),
    RED(21, 2, 0, ShopType.BUY, "§7Rouge"),
    ORANGE(22, 2, 0, ShopType.BUY, "§7Orange"),
    YELLOW(23, 2, 0, ShopType.BUY, "§7Jaune"),
    LIME(24, 2, 0, ShopType.BUY, "§7Vert clair"),
    BROWN(29, 2, 0, ShopType.BUY, "§7Marron"),
    CYAN(30, 2, 0, ShopType.BUY, "§7Cyan"),
    LIGHT_BLUE(31, 2, 0, ShopType.BUY, "§7Bleu clair"),
    BLUE(32, 2, 0, ShopType.BUY, "§7Bleu"),
    GREEN(33, 2, 0, ShopType.BUY, "§7Vert"),
    PURPLE(39, 2, 0, ShopType.BUY, "§7Violet"),
    MAGENTA(40, 2, 0, ShopType.BUY, "§7Magenta"),
    PINK(41, 2, 0, ShopType.BUY, "§7Rose"),
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;
    Color(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
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
