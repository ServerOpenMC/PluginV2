package fr.openmc.core.features.dimopener;

import org.bukkit.Material;

public interface IStepDimension {

    String getName();

    String getDescription();

    Type getType();

    int getRequired();

    Material getMaterial();

    long getCooldownSeconds();

    enum Type {
        ITEMS,
        MONEY
    }
}
