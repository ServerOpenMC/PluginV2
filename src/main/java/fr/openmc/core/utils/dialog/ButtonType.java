package fr.openmc.core.utils.dialog;

import lombok.Getter;

public enum ButtonType {
    CONFIRM("Confirmer"),
    CANCEL("Annuler"),
    BACK("Retour"),
    NEXT("Suivant"),
    PREVIOUS("Précédent");

    @Getter
    private final String label;

    ButtonType(String label) {
        this.label = label;
    }
}
