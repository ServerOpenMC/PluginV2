package fr.openmc.core.features.corporation;

import lombok.Getter;

@Getter
public enum CorpPermission {
    OWNER("Propriétaire"),
    //CITYMEMBER("Membre de la ville"),
    SUPERIOR("Gérant des permission"),
    SETCUT("Définir la part de l'entrprise"),
    INVITE("Inviter"),
    FIRE("Expulser"),
    SUPPLY("Réapprovisionner"),
    SELLER("Ajouter/Retirer des items en vente"),
    LIQUIDATESHOP("Liquider un shop"),
    CREATESHOP("Créer un shop"),
    DELETESHOP("Supprimer un shop"),
    HIRINGER("Embaucher les postulants"),
    WITHDRAW("Retirer x$ de l'entreprise"),
    DEPOSIT("Ajouter x$ dans l'entreprise")
    ;

    private final String displayName;

    CorpPermission(String displayName) {
        this.displayName = displayName;
    }
}
