package fr.openmc.core.features.city.sub.notation.menu;

import fr.openmc.api.input.dialog.ButtonType;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
                DialogBody.plainMessage(TranslationManager.translation("feature.city.notation.edit.teleport").clickEvent(
                        ClickEvent.callback((audience -> {
                            if (!(audience instanceof Player playerClicked)) {
                                return;
                            }

                            playerClicked.closeInventory();

                            MessagesManager.sendMessage(player, TranslationManager.translation(
                                            "feature.city.notation.edit.teleport.success",
                                            Component.text(cityEdited.getName())
                                    )
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
                        TranslationManager.translation("feature.city.notation.edit.input.architectural").hoverEvent(
                                TranslationManager.translation(
                                        "feature.city.notation.edit.input.architectural.hover",
                                        Component.text(NotationNote.NOTE_ARCHITECTURAL.getMaxNote())
                                )
                        ), 0, NotationNote.NOTE_ARCHITECTURAL.getMaxNote()
                )
                .initial(0f)
                .step(0.5F)
                .build()
        );

        inputs.add(DialogInput
                .numberRange("input_note_coherence",
                        TranslationManager.translation("feature.city.notation.edit.input.coherence").hoverEvent(
                                TranslationManager.translation(
                                        "feature.city.notation.edit.input.coherence.hover",
                                        Component.text(NotationNote.NOTE_COHERENCE.getMaxNote())
                                )
                        ), 0, NotationNote.NOTE_COHERENCE.getMaxNote()
                )
                .initial(0f)
                .step(0.5F)
                .build()
        );


        inputs.add(DialogInput
                .text("input_description",
                        TranslationManager.translation("feature.city.notation.edit.input.justification").hoverEvent(
                                TranslationManager.translation("feature.city.notation.edit.input.justification.hover")
                        )
                )
                .multiline(TextDialogInput.MultilineOptions.create(7, 40))
                .build()
        );


        int finalCityEditIndex = cityEditIndex;
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(TranslationManager.translation(
                                "feature.city.notation.edit.title",
                                Component.text(weekStr),
                                Component.text(cityEdited.getName()),
                                Component.text(finalCityEditIndex + 1),
                                Component.text(cities.size())
                        ))
                        .body(body)
                        .inputs(inputs)
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text(ButtonType.SAVE.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                            float noteArchitectural = response.getFloat("input_note_architectural");
                                            float noteCoherence = response.getFloat("input_note_coherence");
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
                                                MessagesManager.sendMessage(player, TranslationManager.translation(
                                                        "feature.city.notation.edit.completed",
                                                        Component.text(weekStr)
                                                ), Prefix.STAFF, MessageType.SUCCESS, false);
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
