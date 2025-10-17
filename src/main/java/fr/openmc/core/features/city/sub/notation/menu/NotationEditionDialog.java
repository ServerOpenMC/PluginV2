package fr.openmc.core.features.city.sub.notation.menu;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.dialog.ButtonType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
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

@SuppressWarnings("UnstableApiUsage")
public class NotationEditionDialog {

    public static void send(Player player, String weekStr, List<City> cities, Integer cityEditIndex) {

        cityEditIndex = cityEditIndex == null ? 0 : cityEditIndex;
        City cityEdited = cities.get(cityEditIndex);

        List<DialogBody> body = new ArrayList<>();

        Integer finalCityEditIndex1 = cityEditIndex;
        body.add(DialogBody.item(
                ItemStack.of(Material.ENDER_PEARL),
                DialogBody.plainMessage(Component.text("Se téléporter a la ville en question.").clickEvent(
                        ClickEvent.callback((audience -> {
                            if (!(audience instanceof Player playerClicked)) {
                                return;
                            }

                            playerClicked.closeInventory();

                            MessagesManager.sendMessage(player, Component.text("Vous avez été téléporté à la ville " + cityEdited.getName() + ". Cliquez sur le message pour continuer l'édition.")
                                            .clickEvent(ClickEvent.callback((audience1) -> {
                                                if (!(audience instanceof Player playerClicked1)) {
                                                    return;
                                                }

                                                send(playerClicked1, weekStr, cities, finalCityEditIndex1);
                                            })),
                                    Prefix.STAFF, MessageType.SUCCESS, false);

                            Location warpLocation = cityEdited.getLaw().getWarp();

                            if (warpLocation == null) {
                                playerClicked.teleportAsync(cityEdited.getMascot().getEntity().getLocation());

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

        List<DialogInput> inputs = new ArrayList<>();

        inputs.add(DialogInput
                .numberRange("input_note_architectural",
                        Component.text("Note Architecturale").hoverEvent(
                                Component.text("Note sur " + NotationNote.NOTE_ARCHITECTURAL.getMaxNote() + " points")
                                        .append(Component.text("qui prend en compte, les bâtiments, les infrastructures et l'esthétique de la ville"))
                        ), 0, NotationNote.NOTE_ARCHITECTURAL.getMaxNote()
                )
                .initial(0f)
                .step(0.5F)
                .build()
        );

        inputs.add(DialogInput
                .numberRange("input_note_coherence",
                        Component.text("Note Cohérence").hoverEvent(
                                Component.text("Note sur " + NotationNote.NOTE_COHERENCE.getMaxNote() + " points")
                                        .append(Component.text("qui prend en compte, la cohérence des builds et le changement progressif de thème."))
                        ), 0, NotationNote.NOTE_COHERENCE.getMaxNote()
                )
                .initial(0f)
                .step(0.5F)
                .build()
        );


        inputs.add(DialogInput
                .text("input_description",
                        Component.text("Justification de la note").hoverEvent(
                                Component.text("Une justification de la note est obligatoire")
                        )
                )
                .multiline(TextDialogInput.MultilineOptions.create(5, 40))
                .build()
        );


        int finalCityEditIndex = cityEditIndex;
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Classement des notations semaine " + weekStr + " - Édition de la ville : " + cityEdited.getName() + " (" + (finalCityEditIndex + 1) + "/" + cities.size() + ")"))
                        .body(body)
                        .inputs(inputs)
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text(ButtonType.SAVE.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                            float noteArchitectural = response.getFloat("input_note_architectural");
                                            float noteCoherence = response.getFloat("input_note_architectural");
                                            String description = response.getText("input_description");

                                            if (noteArchitectural > NotationNote.NOTE_ARCHITECTURAL.getMaxNote()) {
                                                noteArchitectural = NotationNote.NOTE_ARCHITECTURAL.getMaxNote();
                                            }

                                            if (noteCoherence > NotationNote.NOTE_COHERENCE.getMaxNote()) {
                                                noteCoherence = NotationNote.NOTE_COHERENCE.getMaxNote();
                                            }

                                            CityNotation cityNotation = new CityNotation(
                                                    cityEdited.getUniqueId(),
                                                    noteArchitectural,
                                                    noteCoherence,
                                                    description,
                                                    weekStr
                                            );

                                            NotationManager.createOrUpdateNotation(cityNotation);

                                            if (finalCityEditIndex + 1 < cities.size()) {
                                                NotationEditionDialog.send(player, weekStr, cities, finalCityEditIndex + 1);
                                            } else {
                                                MessagesManager.sendMessage(player, Component.text("Les notations pour le " + weekStr + " ont été totalement faites"), Prefix.STAFF, MessageType.SUCCESS, false);
                                                player.closeInventory();
                                            }
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
}
