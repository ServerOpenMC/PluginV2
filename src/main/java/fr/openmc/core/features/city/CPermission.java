package fr.openmc.core.features.city;

import lombok.Getter;

public enum CPermission {
    OWNER("Propriétaire"), //Impossible à donner sauf avec un transfert
    INVITE("Inviter"),
    KICK("Expulser"),
    PLACE("Placer des blocs"),
    BREAK("Casser des blocs"),
    OPEN_CHEST("Ouvrir les coffres"),
    INTERACT("Interagir avec les blocs (sauf coffres)"),
    CLAIM("Claim"),
    SEE_CHUNKS("Voir les Claims"),
    RENAME("Renommer"),
    MONEY_GIVE("Déposer de l'argent"),
    MONEY_BALANCE("Voir l'argent"),
    MONEY_TAKE("Retirer de l'argent"),
    PERMS("Permissions"), // Cette permission est donnée seulement par l'owner
    CHEST("Accès au Coffre de ville"),
    CHEST_UPGRADE("Améliorer le Coffre de ville"),
    TYPE("Changer le type de ville"),
    MASCOT_MOVE("Déplacer la mascotte"),
    MASCOT_SKIN("Changer le skin de la mascotte"),
    MASCOT_UPGRADE("Améliorer la mascotte"),
    MASCOT_HEAL("Soigner la mascotte"),
    LAUNCH_WAR("Lancer des guerres"),
    MANAGE_RANKS("Gérer les grades"),
    ASSIGN_RANKS("Assigner des grades")
    ;

    @Getter
    private final String displayName;

    CPermission(String displayName) {
        this.displayName = displayName;
    }
}