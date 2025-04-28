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

    /**
     * List of materials that have color variants.
     */
    private static final List<String> COLOR_VARIANTS_MATERIALS = Arrays.asList(
            "WOOL", "CONCRETE", "CONCRETE_POWDER", "TERRACOTTA", "GLASS"
    );

    /**
     * Constructor for ShopItem.
     *
     * @param id                The ID of the item.
     * @param name              The name of the item.
     * @param material          The material of the item.
     * @param slot              The slot of the item in the shop menu.
     * @param initialSellPrice  The initial sell price of the item.
     * @param initialBuyPrice   The initial buy price of the item.
     * @param actualSellPrice   The actual sell price of the item.
     * @param actualBuyPrice    The actual buy price of the item.
     */
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

    /**
     * Checks if the material has color variants.
     *
     * @param material The material to check.
     * @return true if the material has color variants, false otherwise.
     */
    private boolean hasColorVariants(Material material) {
        String materialName = material.name();
        for (String colorVariant : COLOR_VARIANTS_MATERIALS)
            if (materialName.contains(colorVariant))
                return true;
        return false;
    }

    /**
     * Gets the base type of the material, ignoring color variants.
     *
     * @return The base type of the material.
     */
    public String getBaseType() {
        String materialName = material.name();
        for (String baseType : COLOR_VARIANTS_MATERIALS)
            if (materialName.equals(baseType) || materialName.endsWith("_" + baseType))
                return baseType;
        return materialName;
    }
}
