package fr.openmc.core.features.report;

import fr.openmc.api.input.dialog.ButtonType;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ReportPlayerDialog {

    public static void send(Player sender, OfflinePlayer target) {
        List<DialogInput> inputs = new ArrayList<>();

        inputs.add(DialogInput
                .text("input_reason",
                        TranslationManager.translation("feature.city.notation.edit.input.justification").hoverEvent(
                                TranslationManager.translation("feature.city.notation.edit.input.justification.hover")
                        )
                )
                .multiline(TextDialogInput.MultilineOptions.create(7, 40))
                .build()
        );


        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(TranslationManager.translation(
                                "feature.report.dialog.title",
                                PlayerNameCache.name(target.getUniqueId()).color(NamedTextColor.YELLOW)
                        ))
                        .inputs(inputs)
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(ButtonType.SAVE.getLabelComponent())
                                .action(DialogAction.customClick((response, audience) -> {
                                            String reason = response.getText("input_reason");
                                            ReportManager.sendReport(sender, target, reason);
                                            audience.closeDialog();
                                        },
                                        ClickCallback.Options.builder().build()
                                ))
                                .build(),
                        ActionButton.builder(ButtonType.CANCEL.getLabelComponent())
                                .action(DialogAction.customClick((response, audience) ->
                                        audience.closeDialog(), ClickCallback.Options.builder().build())
                                )
                                .build()
                ))
        );

        sender.showDialog(dialog);
    }
}
