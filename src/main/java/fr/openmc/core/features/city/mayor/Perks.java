package fr.openmc.core.features.city.mayor;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public enum Perks {
    FOU_DE_RAGE(
            1,
            "Fou de Rage",
            List.of(
                    Component.text("eded")
            ),
            Material.BLAZE_POWDER
    )
    ;

    @Getter private final int id;
    @Getter private final String name;
    @Getter private final List<Component> lore;
    @Getter private final Material material;

    Perks(int id, String name, List<Component> lore, Material material) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.material = material;
    }

}
