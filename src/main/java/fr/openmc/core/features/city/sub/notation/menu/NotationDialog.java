package fr.openmc.core.features.city.sub.notation.menu;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
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

public class NotationDialog {

    public static void send(Player player, String weekStr) {

        List<DialogBody> body = new ArrayList<>();

        body.add(lineEdition(CityManager.getPlayerCity(player.getUniqueId()), weekStr));

        String[] parts = weekStr.split("-");

        int yearNumber = Integer.parseInt(parts[0]);
        int weekNumber = Integer.parseInt(parts[1]);

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Classement des Notations - Semaine " + weekNumber + " de " + yearNumber))
                        .body(body)
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text(ButtonType.SAVE.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {

                                }, ClickCallback.Options.builder().build()))
                                .build(),
                        ActionButton.builder(Component.text(ButtonType.CANCEL.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                    player.closeInventory();
                                }, ClickCallback.Options.builder().build()))
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
