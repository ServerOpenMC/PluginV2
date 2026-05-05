package fr.openmc.core.features.city.sub.notation.menu;

import fr.openmc.api.input.dialog.ButtonType;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.text.PaddingUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.utils.text.InputUtils.MAX_LENGTH_CITY;

@SuppressWarnings("UnstableApiUsage")
public class NotationDialog {
    private final static String FONT = "minecraft:mono";
    private final static int LENGTH_CASE = 9;

    public static void send(Player player, String weekStr) {
        List<DialogBody> body = new ArrayList<>();

        String[] parts = weekStr.split("-");

        int yearNumber = Integer.parseInt(parts[0]);
        int weekNumber = Integer.parseInt(parts[1]);

        body.add(lineCityNotationHeader(CityManager.getPlayerCity(player.getUniqueId()), weekStr));

        for (CityNotation notation : NotationManager.getSortedNotationForWeek(weekStr)) {
            City city = CityManager.getCity(notation.getCityUUID());

            if (city == null) continue;

            body.add(lineCityNotation(city, weekStr));
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(TranslationManager.translation(
                                "feature.city.notation.dialog.title",
                                Component.text(weekNumber),
                                Component.text(yearNumber)
                        ))
                        .body(body)
                        .canCloseWithEscape(true)
                        .build()
                )
                .type(DialogType.notice(
                        ActionButton.builder(Component.text(ButtonType.BACK.getLabel()))
                                .action(DialogAction.customClick((response, audience) -> {
                                    player.closeInventory();
                                }, ClickCallback.Options.builder().build()))
                                .build()
                ))
        );

        player.showDialog(dialog);
    }

    public static DialogBody lineCityNotationHeader(City city, String weekStr) {
        String headerCity = TranslationManager.translationString("feature.city.notation.header.city");
        String headerActivity = TranslationManager.translationString("feature.city.notation.header.activity");
        String headerEconomy = TranslationManager.translationString("feature.city.notation.header.economy");
        String headerMilitary = TranslationManager.translationString("feature.city.notation.header.military");
        String headerArchitectural = TranslationManager.translationString("feature.city.notation.header.architectural");
        String headerCoherence = TranslationManager.translationString("feature.city.notation.header.coherence");
        String headerTotal = TranslationManager.translationString("feature.city.notation.header.total");
        String headerMoney = TranslationManager.translationString("feature.city.notation.header.money");

        Component header = Component.text(PaddingUtils.format(headerCity, MAX_LENGTH_CITY)).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerActivity, 8)).hoverEvent(getHoverActivity())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerEconomy, LENGTH_CASE)).hoverEvent(getHoverEconomy())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerMilitary, LENGTH_CASE)).hoverEvent(getHoverMilitary())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerArchitectural, LENGTH_CASE)).hoverEvent(getHoverArchitectural())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerCoherence, LENGTH_CASE)).hoverEvent(getHoverCoherence())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerTotal, LENGTH_CASE)).hoverEvent(getHoverTotal(city == null ? null : city.getNotationOfWeek(weekStr)))).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format(headerMoney, LENGTH_CASE)));

        header.font(Key.key(FONT));

        return DialogBody.plainMessage(
                header,
                1000
        );
    }

    public static DialogBody lineCityNotation(City city, String weekStr) {
        CityNotation notation = city.getNotationOfWeek(weekStr);

        String cityName = city.getName();

        String centeredCityName = PaddingUtils.format(cityName, MAX_LENGTH_CITY);

        Component hoverCityName = TranslationManager.translation(
                "feature.city.notation.hover.city.mascot_level",
                Component.text(city.getMascot().getLevel()).color(NamedTextColor.RED)
        ).color(NamedTextColor.GRAY)
                .appendNewline()
                .append(TranslationManager.translation(
                        "feature.city.notation.hover.city.status",
                        city.getType().getDisplayName()
                ).color(NamedTextColor.GRAY))
                .appendNewline()
                .append(TranslationManager.translation(
                        "feature.city.notation.hover.city.members",
                        Component.text(city.getMembers().size()).color(NamedTextColor.GREEN)
                ).color(NamedTextColor.GRAY))
                .appendNewline()
                .append(TranslationManager.translation("feature.city.notation.hover.city.more_info")
                        .color(NamedTextColor.YELLOW));

        Component base = Component.empty();

        if (notation != null) {
            double noteActivity = notation.getNoteActivity() != null ? notation.getNoteActivity() : 0;
            double noteEconomy = notation.getNoteEconomy() != null ? notation.getNoteEconomy() : 0;
            double noteMilitary = notation.getNoteMilitary() != null ? notation.getNoteMilitary() : 0;

            String activity = String.format("%.2f/" + NotationNote.NOTE_ACTIVITY.getMaxNote(), Math.round(noteActivity * 100.0) / 100.0);
            String eco = String.format("%.2f/" + NotationNote.NOTE_PIB.getMaxNote(), Math.round(noteEconomy * 100.0) / 100.0);
            String military = String.format("%.2f/" + NotationNote.NOTE_MILITARY.getMaxNote(), Math.round(noteMilitary * 100.0) / 100.0);
            String arch = String.format("%.2f/" + NotationNote.NOTE_ARCHITECTURAL.getMaxNote(), Math.round(notation.getNoteArchitectural() * 100.0) / 100.0);
            String coh = String.format("%.2f/" + NotationNote.NOTE_COHERENCE.getMaxNote(), Math.round(notation.getNoteCoherence() * 100.0) / 100.0);
            String total = String.format("%.2f/%.0f", Math.round(notation.getTotalNote() * 100.0) / 100.0, NotationNote.getMaxTotalNote());
            String money = String.format("%.1f", notation.getMoney());

            base = base
                    .append(Component.text(centeredCityName).hoverEvent(hoverCityName)
                            .clickEvent(ClickEvent.callback(audience -> {
                                if (!(audience instanceof Player player)) return;
                                new CityListDetailsMenu(player, city).open();
                            })))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(activity, 8)).hoverEvent(getHoverActivity()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(eco, LENGTH_CASE)).hoverEvent(getHoverEconomy()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(military, LENGTH_CASE)).hoverEvent(getHoverMilitary()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(arch, LENGTH_CASE)).hoverEvent(getHoverArchitectural()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(coh, LENGTH_CASE)).hoverEvent(getHoverCoherence()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(total, LENGTH_CASE)).hoverEvent(getHoverTotal(city == null ? null : city.getNotationOfWeek(weekStr))))
                    .append(Component.text(" | "))
                    .append(Component.text("+ " + PaddingUtils.format(money, LENGTH_CASE)).color(NamedTextColor.GOLD));

        } else {
            base = base.append(TranslationManager.translation("feature.city.notation.table.none"));
        }

        base.font(Key.key(FONT));

        return DialogBody.plainMessage(
                base,
                1000
        );
    }

    public static Component getHoverTotal(CityNotation notation) {
        if (notation == null) {
            return TranslationManager.translation("feature.city.notation.hover.total.none");
        }


        double noteActivity = notation.getNoteActivity() != null ? notation.getNoteActivity() : 0;
        double noteEconomy = notation.getNoteEconomy() != null ? notation.getNoteEconomy() : 0;
        double noteMilitary = notation.getNoteMilitary() != null ? notation.getNoteMilitary() : 0;

        return TranslationManager.translation("feature.city.notation.hover.total.title")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD)
                .appendNewline()
                .append(TranslationManager.translation(
                        "feature.city.notation.hover.total.activity",
                        Component.text(noteActivity).color(NamedTextColor.DARK_AQUA)
                ).color(NamedTextColor.DARK_GRAY))
                .appendNewline()
                .append(TranslationManager.translation(
                        "feature.city.notation.hover.total.economy",
                        Component.text(noteEconomy).color(NamedTextColor.DARK_AQUA)
                ).color(NamedTextColor.DARK_GRAY))
                .appendNewline()
                .append(TranslationManager.translation(
                        "feature.city.notation.hover.total.military",
                        Component.text(noteMilitary).color(NamedTextColor.DARK_AQUA)
                ).color(NamedTextColor.DARK_GRAY))
                .appendNewline()
                .append(TranslationManager.translation(
                        "feature.city.notation.hover.total.architecture",
                        Component.text(notation.getNoteArchitectural()).color(NamedTextColor.DARK_AQUA)
                ).color(NamedTextColor.DARK_GRAY))
                .appendNewline()
                .append(Component.text("§8Cohérence " + notation.getNoteCoherence()))
                .appendNewline()
                .appendNewline()
                .append(TranslationManager.translation("feature.city.notation.hover.total.justification")
                        .color(NamedTextColor.DARK_AQUA)
                        .decorate(TextDecoration.BOLD))
                .appendNewline()
                .append(Component.text(notation.getDescription())
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true));
    }

    public static Component getHoverActivity() {
        return TranslationManager.translation(
                "feature.city.notation.hover.activity",
                Component.text(NotationNote.NOTE_ACTIVITY.getMaxNote()).color(NamedTextColor.DARK_AQUA)
        ).color(NamedTextColor.GRAY);
    }

    public static Component getHoverEconomy() {
        return TranslationManager.translation(
                "feature.city.notation.hover.economy",
                Component.text(NotationNote.NOTE_PIB.getMaxNote()).color(NamedTextColor.DARK_AQUA)
        ).color(NamedTextColor.GRAY);
    }

    public static Component getHoverMilitary() {
        return TranslationManager.translation(
                "feature.city.notation.hover.military",
                Component.text(NotationNote.NOTE_PIB.getMaxNote()).color(NamedTextColor.DARK_AQUA)
        ).color(NamedTextColor.GRAY);
    }

    public static Component getHoverCoherence() {
        return TranslationManager.translation(
                "feature.city.notation.hover.coherence",
                Component.text(NotationNote.NOTE_COHERENCE.getMaxNote()).color(NamedTextColor.DARK_AQUA)
        ).color(NamedTextColor.GRAY);
    }

    public static Component getHoverArchitectural() {
        return TranslationManager.translation(
                "feature.city.notation.hover.architectural",
                Component.text(NotationNote.NOTE_ARCHITECTURAL.getMaxNote()).color(NamedTextColor.DARK_AQUA)
        ).color(NamedTextColor.GRAY);
    }
}
