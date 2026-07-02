package fr.openmc.core.features.dream.models.registry.items;

import fr.openmc.core.registry.items.CustomItemMeta;
import org.bukkit.Material;

public class DreamItemMeta extends CustomItemMeta {
    public DreamItemMeta(String id, String name, DreamRarity rarity, Material defaultMaterial, boolean transferable) {
        super(id);
        add("name", name);
        add("rarity", rarity);
        add("default_material", defaultMaterial);
        add("transferable", transferable);
    }

    public String getName() {
        return (String) get("name");
    }

    public DreamRarity getRarity() {
        return (DreamRarity) get("rarity");
    }

    public Material getDefaultMaterial() {
        return (Material) get("default_material");
    }

    public boolean getTransferable() {
        return (boolean) get("transferable");
    }
}
