package fr.openmc.core.features.city.sub.notation.menu;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.dialog.ButtonType;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NotationEditionDialog {

    public static void send(Player player, String weekStr, City city) {

        List<DialogBody> body = new ArrayList<>();

        body.add(DialogBody.item(
                ItemStack.of(Material.ENDER_PEARL),
                DialogBody.plainMessage(Component.text("Se téléporter a la ville en question.").clickEvent(
                        ClickEvent.callback((audience -> {
                            if (!(audience instanceof Player playerClicked)) {
                                return;
                            }

                            playerClicked.closeInventory();

                            Location warpLocation = city.getLaw().getWarp();

                            if (warpLocation == null) {
                                playerClicked.teleportAsync(city.getMascot().getEntity().getLocation());
                                return;
                            }

                            playerClicked.teleportAsync(warpLocation);
                        }
                        )))),
                false,
                false,
                16,
                16
        ));

        List<io.papermc.paper.registry.data.dialog.input.DialogInput> inputs = new ArrayList<>();

        inputs.add(io.papermc.paper.registry.data.dialog.input.DialogInput
                .numberRange("input_note_architectural",
                        Component.text("Note Architectural").hoverEvent(
                                Component.text("Note sur " + NotationNote.NOTE_ARCHITECTURAL.getMaxNote() + " points")
                                        .append(Component.text("qui compte, les batiments, les infrastructures et l'esthétique de la ville"))
                        ), 0, NotationNote.NOTE_ARCHITECTURAL.getMaxNote()
                )
                .initial(0f)
                .step(0.5F)
                .build()
        );

        inputs.add(io.papermc.paper.registry.data.dialog.input.DialogInput
                .numberRange("input_note_coherence",
                        Component.text("Note Coherence").hoverEvent(
                                Component.text("Note sur " + NotationNote.NOTE_COHERENCE.getMaxNote() + " points")
                                        .append(Component.text("qui compte, la cohérence des builds, et le changement progressif de theme."))
                        ), 0, NotationNote.NOTE_COHERENCE.getMaxNote()
                )
                .initial(0f)
                .step(0.5F)
                .build()
        );


        inputs.add(io.papermc.paper.registry.data.dialog.input.DialogInput
                .text("input_description",
                        Component.text("Justification de la note").hoverEvent(
                                Component.text("Une justification de la note est obligatoire")
                        )
                )
                .multiline(TextDialogInput.MultilineOptions.create(5, 40))
                .build()
        );


        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Classement des Notations Semaine " + weekStr + " - Edition de la ville : " + city.getName()))
                        .body(body)
                        .inputs(inputs)
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text(ButtonType.SAVE.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                            player.closeInventory();
                                        },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build(),
                        ActionButton.builder(Component.text(ButtonType.CANCEL.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                            player.closeInventory();
                                        }, ClickCallback.Options.builder().build())
                                )
                                .build()
                ))
        );

        player.showDialog(dialog);
    }

    public static DialogBody lineEdition(City city, String weekStr) {
        CityNotation notation = city.getNotationOfWeek(weekStr);
        return DialogBody.plainMessage(Component.text(city.getName() + " ").hoverEvent(
                Component.text("Cliquez pour éditer la notation de la ville " + city.getName() + " pour la semaine " + weekStr)
        ));
    }
}
