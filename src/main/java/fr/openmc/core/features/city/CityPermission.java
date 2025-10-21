package fr.openmc.core.features.city;

import lombok.Getter;
import org.bukkit.Material;

public enum CityPermission {
    OWNER("Propriétaire", Material.NETHERITE_BLOCK), //Impossible à donner sauf avec un transfert
    INVITE("Inviter", Material.OAK_DOOR),
    KICK("Expulser", Material.IRON_DOOR),
    PLACE("Placer des blocs", Material.OAK_LOG),
    BREAK("Casser des blocs", Material.STONE_PICKAXE),
    OPEN_CHEST("Ouvrir les coffres", Material.CHEST),
    INTERACT("Interagir avec les blocs (sauf coffres)", Material.LEVER),
    CLAIM("Claim", Material.GRASS_BLOCK),
    SEE_CHUNKS("Voir les claims", Material.MAP),
    RENAME("Renommer", Material.NAME_TAG),
    MONEY_GIVE("Déposer de l'argent", Material.EMERALD),
    MONEY_BALANCE("Voir l'argent", Material.GOLD_INGOT),
    MONEY_TAKE("Retirer de l'argent", Material.DIAMOND),
    PERMS("Permissions", Material.DIAMOND_BLOCK), // Cette permission est donnée seulement par l'owner
    CHEST("Accès au coffre de ville", Material.ENDER_CHEST),
    CHEST_UPGRADE("Améliorer le coffre de ville", Material.OAK_CHEST_BOAT),
    TYPE("Changer le type de ville", Material.BIRCH_SIGN),
    MASCOT_MOVE("Déplacer la mascotte", Material.LEAD),
    MASCOT_SKIN("Changer le skin de la mascotte", Material.ZOMBIE_SPAWN_EGG),
    MASCOT_UPGRADE("Améliorer la mascotte", Material.BONE),
    MASCOT_HEAL("Soigner la mascotte", Material.POTION),
    LAUNCH_WAR("Lancer des guerres", Material.IRON_SWORD),
    MANAGE_RANKS("Gérer les grades", Material.PAPER),
    ASSIGN_RANKS("Assigner des grades", Material.BOOK)
    ;

    @Getter
    private final String displayName;
    @Getter
    private final Material icon;
    
    CityPermission(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
}