package fr.openmc.core.features.adminshop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
public class ShopItem {
    private final String id;
    private final String name;
    private final Material material;
    private final int slot;
    private final double initialSellPrice;
    private final double initialBuyPrice;
    @Setter private double actualSellPrice;
    @Setter private double actualBuyPrice;

    public ShopItem(String id, String name, Material material, int slot,
                    double initialSellPrice, double initialBuyPrice,
                    double actualSellPrice, double actualBuyPrice) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.slot = slot;
        this.initialSellPrice = initialSellPrice;
        this.initialBuyPrice = initialBuyPrice;
        this.actualSellPrice = actualSellPrice;
        this.actualBuyPrice = actualBuyPrice;
    }

}
