package fr.openmc.api.input;

import fr.openmc.core.utils.dialog.ButtonType;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class DialogInput {

    public static void send(Player player, Component lore, int maxLength, Consumer<String> callback) {

        List<DialogBody> body = new ArrayList<>();

        body.add(DialogBody.plainMessage(lore));

        Dialog inputDialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Rentrer du Texte"))
                        .body(body)
                        .inputs(List.of(
                                        io.papermc.paper.registry.data.dialog.input.DialogInput
                                                .text("inputtextomc",
                                                        Component.text("Rentrer du texte ici")
                                                )
                                                .maxLength(maxLength)
                                                .build()
                                )
                        )
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.confirmation(
                                ActionButton.builder(Component.text(ButtonType.CONFIRM.getLabel()))
                                        .action(DialogAction.customClick((response, audience) -> {
                                            callback.accept(response.getText("inputtextomc"));
                                        }, ClickCallback.Options.builder().build()))
                                        .build(), ActionButton.builder(Component.text(ButtonType.CANCEL.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                    callback.accept(null);
                                }, ClickCallback.Options.builder().build()))
                                .build()
                        )
                )
        );

        player.showDialog(inputDialog);
    }
}
