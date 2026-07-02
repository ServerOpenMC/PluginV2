package fr.openmc.core.features.dream.models.registry.items;

import fr.openmc.core.registry.items.CustomItemMeta;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;

public class DreamItemMeta extends CustomItemMeta {
    public DreamItemMeta(String id, String nameKey, DreamRarity rarity, Material defaultMaterial, boolean transferable, ComponentLike... nameArgs) {
        super(id);
        add("name_key", nameKey);
        add("name_args", nameArgs);
        add("rarity", rarity);
        add("default_material", defaultMaterial);
        add("transferable", transferable);
    }

    // todo: a tester
    public String getName() {
        return PlainTextComponentSerializer.plainText().serialize(getNameComponent());
    }

    public Component getNameComponent() {
        return TranslationManager.translation(getNameKey(), getNameArgs());
    }

    public String getNameKey() {
        return (String) get("name_key");
    }

    public ComponentLike[] getNameArgs() {
        return (ComponentLike[]) get("name_args");
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
