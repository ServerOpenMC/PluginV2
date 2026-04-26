package fr.openmc.api.input.dialog;

import lombok.Getter;

@Getter
public enum ButtonType {
    SAVE("Sauvegarder"),
    CONFIRM("Confirmer"),
    CANCEL("Annuler"),
    BACK("Retour"),
    NEXT("Suivant"),
    PREVIOUS("Précédent"),
	FINISH("Terminer"),
	IGNORE("Ignorer")
	;

    private final String label;

    ButtonType(String label) {
        this.label = label;
    }
}
