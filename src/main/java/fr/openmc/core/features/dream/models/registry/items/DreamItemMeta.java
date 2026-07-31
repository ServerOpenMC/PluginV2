package fr.openmc.core.features.dream.models.registry.items;

import fr.openmc.core.registry.items.CustomItemMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class DreamItemMeta extends CustomItemMeta {
    public DreamItemMeta(String id, Component name, DreamRarity rarity, Material defaultMaterial, boolean transferable) {
        super(id);
        add("name", name);
        add("rarity", rarity);
        add("default_material", defaultMaterial);
        add("transferable", transferable);
    }

    public Component getName() {
        return (Component) get("name");
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
