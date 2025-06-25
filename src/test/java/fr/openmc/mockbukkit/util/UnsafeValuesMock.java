package fr.openmc.mockbukkit.util;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class UnsafeValuesMock extends org.mockbukkit.mockbukkit.util.UnsafeValuesMock {
    @Override
    public Material fromLegacy(MaterialData materialData, boolean itemPriority) {
        try {
            return super.fromLegacy(materialData, itemPriority);
        } catch (Exception e) {
            return materialData.getItemType();
        }
    }
}
