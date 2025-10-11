package fr.openmc.core.features.dimsopener;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DimensionConfig(String dimensionKey, String name, ItemStack icon, List<DimensionStep> steps) {
    @Override
    public @NotNull String toString() {
        return "DimensionConfig{" +
                "dimensionKey='" + dimensionKey + '\'' +
                ", name='" + name + '\'' +
                ", icon=" + icon +
                ", steps=" + steps +
                '}';
    }
}
