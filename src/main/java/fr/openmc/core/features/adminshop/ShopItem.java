package fr.openmc.core.features.adminshop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

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
    private final boolean hasColorVariant;

    private static final List<String> COLOR_VARIANTS_MATERIALS = Arrays.asList(
            "WOOL", "CONCRETE", "CONCRETE_POWDER", "TERRACOTTA", "GLASS"
    );

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
        this.hasColorVariant = hasColorVariants(material);
    }

    private boolean hasColorVariants(Material material) {
        String materialName = material.name();
        for (String colorVariant : COLOR_VARIANTS_MATERIALS)
            if (materialName.contains(colorVariant))
                return true;
        return false;
    }

    public String getBaseType() {
        String materialName = material.name();
        for (String baseType : COLOR_VARIANTS_MATERIALS)
            if (materialName.equals(baseType) || materialName.endsWith("_" + baseType))
                return baseType;
        return materialName;
    }

    public String toString() {
        return "ShopItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", material=" + material +
                ", slot=" + slot +
                ", initialSellPrice=" + initialSellPrice +
                ", initialBuyPrice=" + initialBuyPrice +
                ", actualSellPrice=" + actualSellPrice +
                ", actualBuyPrice=" + actualBuyPrice +
                ", hasColorVariant=" + hasColorVariant +
                '}';
    }
}
