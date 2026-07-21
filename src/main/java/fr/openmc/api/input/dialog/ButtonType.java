package fr.openmc.api.input.dialog;

import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public enum ButtonType {
    SAVE("api.dialoginput.button.save"),
    CONFIRM("api.dialoginput.button.confirm"),
    CANCEL("api.dialoginput.button.cancel"),
    BACK("api.dialoginput.button.back"),
    NEXT("api.dialoginput.button.next"),
    PREVIOUS("api.dialoginput.button.previous"),
	FINISH("api.dialoginput.button.finish"),
	IGNORE("api.dialoginput.button.ignore")
	;

    @Getter
    private final String labelKey;

    ButtonType(String labelKey) {
        this.labelKey = labelKey;
    }

    public Component getLabelComponent() {
        return TranslationManager.translation(labelKey);
    }
}
