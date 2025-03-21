package fr.openmc.core.features.city;

import lombok.Getter;

public enum CPermission {
    OWNER("Propriétaire"), //Impossible à donner sauf avec un transfert
    INVITE("Inviter"),
    KICK("Expulser"),
    CLAIM("Claim"),
    SEE_CHUNKS("Voir les Claims"),
    RENAME("Renommer"),
    MONEY_GIVE("Donner de l'argent"),
    MONEY_BALANCE("Voir l'argent"),
    MONEY_TAKE("Prendre de l'argent"),
    PERMS("Permissions"), // Cette permission est donnée seulement par l'owner
    CHEST("Accès au Coffre"),
    CHEST_UPGRADE("Améliorer le coffre"),
    TYPE("Changer le type de ville"),
    MASCOT_MOVE("Déplacer la mascotte"),
    MASCOT_SKIN("Changer le skin de la mascotte"),
    MASCOT_UPGRADE("Améliorer la mascotte"),
    MASCOT_HEAL("Soigner la mascotte")
    ;

    @Getter
    private final String displayName;

    CPermission(String displayName) {
        this.displayName = displayName;
    }
}