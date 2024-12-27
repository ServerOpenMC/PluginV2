package fr.openmc.core.features.city;

import lombok.Getter;

public enum CPermission {
    OWNER("Propriétaire"), //Impossible à donner sauf avec un transfert
    INVITE("Inviter"),
    KICK("Expulser"),
    CLAIM("Claim"),
    RENAME("Renommer"),
    MONEY_GIVE("Donner de l'argent"),
    MONEY_BALANCE("Voir l'argent"),
    MONEY_TAKE("Prendre de l'argent"),
    PERMS("Permissions"), // Cette permission est donnée seulement par l'owner
    CHEST("Accès au Coffre"),
    CHEST_UPGRADE("Améliorer le coffre")
    ;

    @Getter
    private final String displayName;

    CPermission(String displayName) {
        this.displayName = displayName;
    }
}