package fr.openmc.core.features.adminshop.menu.category.defaults;

import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import org.bukkit.Material;

public enum FoodsItems implements BaseItems {
    COOKED_BEEF(13, 10, 0, ShopType.BUY, "§7Steak"),
    CARROT(20, 3, 0, ShopType.BUY, "§7Carotte"),
    BREAD(21, 6, 0, ShopType.BUY, "§7Pain"),
    APPLE(22, 12, 0, ShopType.BUY, "§7Pomme"),
    SWEET_BERRIES(23, 2, 0, ShopType.BUY, "§7Baie sauvage"),
    COOKED_CHICKEN(24, 10, 0, ShopType.BUY, "§7Poulet cuit"),
    GOLDEN_CARROT(29, 15, 0, ShopType.BUY, "§7Carotte dorée"),
    BAKED_POTATO(30, 8, 0, ShopType.BUY, "§7Patate cuite"),
    COOKED_MUTTON(31, 10, 0, ShopType.BUY, "§7Mouton cuit"),
    GLOW_BERRIES(32, 3, 0, ShopType.BUY, "§7Baie lumineuse"),
    COOKED_RABBIT(33, 10, 0, ShopType.BUY, "§7Lapin cuit"),
    COOKIE(40, 5, 0, ShopType.BUY, "§7Cookie"),
    ;

    private final int slots;
    private final double buyPrize;
    private final double sellPrize;
    private final ShopType type;
    private final String name;

    FoodsItems(int slots, double buyPrize, double sellPrize, ShopType type, String name) {
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
