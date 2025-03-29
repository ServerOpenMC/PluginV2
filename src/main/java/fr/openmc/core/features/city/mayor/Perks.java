package fr.openmc.core.features.city.mayor;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

@Getter
public enum Perks {
    FOU_DE_RAGE(
            1,
            "§e§lFou de Rage",
            List.of(
                    Component.text("§7Donne §3un effet de force I §7dans une ville adverse"),
                    Component.text("§7Et donne §3un effet de protection I§7 dans sa ville")
            ),
            Material.BLAZE_POWDER,
            PerkType.BASIC
    ),
    IMPOT(
            2,
            "§e§lPrévélement d'Impot",
            List.of(
                    Component.text("§7Possibilité de lancer un §3événement §7pour préléver les Impots"),
                    Component.text("§7Limite de perte d'argent : §35k")
            ),
            Material.GOLD_BLOCK,
            PerkType.EVENT
    ),
    MINER(
            3,
            "§e§lMineur Dévoué",
            List.of(
                    Component.text("§7Donne §3Haste I §7aux membres de la ville")
            ),
            Material.GOLDEN_PICKAXE,
            PerkType.BASIC
    ),
    FRUIT_DEMON(
            4,
            "§e§lFruit du Démon",
            List.of(
                    Component.text("§7Augmente §3la portée §7de tous les membres de la ville"),
                    Component.text("§7de §31 §7bloc")
            ),
            Material.CHORUS_FRUIT,
            PerkType.BASIC
    )
    ;

    private final int id;
    private final String name;
    private final List<Component> lore;
    private final Material material;
    private final PerkType type;

    Perks(int id, String name, List<Component> lore, Material material, PerkType type) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.type = type;
    }


}
