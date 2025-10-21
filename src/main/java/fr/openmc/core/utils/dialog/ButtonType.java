package fr.openmc.core.utils.dialog;

import lombok.Getter;

@Getter
public enum ButtonType {
    SAVE("Sauvegarder"),
    CONFIRM("Confirmer"),
    CANCEL("Annuler"),
    BACK("Retour"),
    NEXT("Suivant"),
    PREVIOUS("Précédent");

    private final String label;

    ButtonType(String label) {
        this.label = label;
    }
}
