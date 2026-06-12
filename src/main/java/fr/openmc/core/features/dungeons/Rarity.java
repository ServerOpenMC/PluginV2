package fr.openmc.core.features.dungeons;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public enum Rarity {
    COMMON("COMMUN", NamedTextColor.GREEN),
    RARE("RARE", NamedTextColor.BLUE),
    EPIC("ÉPIQUE", NamedTextColor.DARK_PURPLE),
    LEGENDARY("LÉGENDAIRE", NamedTextColor.GOLD),
    MASTER("MAÎTRE", NamedTextColor.RED),
    ABSOLUTE_MASTER("GRAND MAÎTRE" ,NamedTextColor.LIGHT_PURPLE);

    private String name;
    private NamedTextColor color;


     Rarity(String name, NamedTextColor color) {
         this.name = name;
         this.color = color;
    }
}
