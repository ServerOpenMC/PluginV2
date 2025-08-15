package fr.openmc.core.features.city.sub.notation.menu;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.PaddingUtils;
import fr.openmc.core.utils.dialog.ButtonType;
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

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH_CITY;

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
            body.add(lineCityNotation(CityManager.getCity(notation.getCityUUID()), weekStr));
        }

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Classement des Villes - Semaine " + weekNumber + " de " + yearNumber))
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
        Component header = Component.text(PaddingUtils.format("Ville", MAX_LENGTH_CITY)).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format("Activ.", 8)).hoverEvent(getHoverActivity())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format("Econo.", LENGTH_CASE)).hoverEvent(getHoverEconomy())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format("Arch.", LENGTH_CASE)).hoverEvent(getHoverArchitectural())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format("Coh.", LENGTH_CASE)).hoverEvent(getHoverCoherence())).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format("Total", LENGTH_CASE)).hoverEvent(getHoverTotal(city.getNotationOfWeek(weekStr)))).append(Component.text(" | "))
                .append(Component.text(PaddingUtils.format("Argent", LENGTH_CASE)));

        header.font(Key.key(FONT));

        return DialogBody.plainMessage(
                header,
                600
        );
    }

    public static DialogBody lineCityNotation(City city, String weekStr) {
        CityNotation notation = city.getNotationOfWeek(weekStr);

        String cityName = city.getName();

        String centeredCityName = PaddingUtils.format(cityName, MAX_LENGTH_CITY);

        Component hoverCityName = Component.text("§7Niveau de la mascotte : §c" + city.getMascot().getLevel())
                .append(Component.newline())
                .append(Component.text("§7Status : " + (city.getType() == CityType.WAR ? "§cGuerre" : "§aPaix")))
                .append(Component.newline())
                .append(Component.text("§7Membres : §2" + city.getMembers().size()))
                .append(Component.newline())
                .append(Component.text("§eCliquez ici pour avoir plus d'info sur la ville"));

        Component base = Component.empty();

        if (notation != null) {
            String activity = String.format("%.2f/" + NotationNote.NOTE_ACTIVITY.getMaxNote(), notation.getNoteActivity());
            String eco = String.format("%.2f/" + NotationNote.NOTE_PIB.getMaxNote(), notation.getNoteEconomy());
            String arch = String.format("%.2f/" + NotationNote.NOTE_ARCHITECTURAL.getMaxNote(), notation.getNoteArchitectural());
            String coh = String.format("%.2f/" + NotationNote.NOTE_COHERENCE.getMaxNote(), notation.getNoteCoherence());
            String total = String.format("%.2f/%.0f", notation.getTotalNote(), NotationNote.getMaxTotalNote());
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
                    .append(Component.text(PaddingUtils.format(arch, LENGTH_CASE)).hoverEvent(getHoverArchitectural()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(coh, LENGTH_CASE)).hoverEvent(getHoverCoherence()))
                    .append(Component.text(" | "))
                    .append(Component.text(PaddingUtils.format(total, LENGTH_CASE)).hoverEvent(getHoverTotal(city.getNotationOfWeek(weekStr))))
                    .append(Component.text(" | "))
                    .append(Component.text("§6 +" + PaddingUtils.format(money, LENGTH_CASE)));

        } else {
            base = base.append(Component.text("Aucune notation"));
        }

        base.font(Key.key(FONT));

        return DialogBody.plainMessage(
                base,
                500
        );
    }

    public static Component getHoverTotal(CityNotation notation) {
        return Component.text("§6§lDétails")
                .appendNewline()
                .append(Component.text("§8Activité " + notation.getNoteActivity()))
                .appendNewline()
                .append(Component.text("§8Economie " + notation.getNoteEconomy()))
                .appendNewline()
                .append(Component.text("§8Architecture " + notation.getNoteArchitectural()))
                .appendNewline()
                .append(Component.text("§8Cohérence " + notation.getNoteCoherence()))
                .appendNewline()
                .appendNewline()
                .append(Component.text("§3§lJustification de la note"))
                .appendNewline()
                .append(Component.text(notation.getDescription())).color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true);
    }

    public static Component getHoverActivity() {
        return Component.text("Note allant de 0 à " + NotationNote.NOTE_ACTIVITY.getMaxNote() + " points, qui comprends, le nombre de joueurs actifs dans la ville par le temps de jeu d'une ville")
                .appendNewline()
                .append(Component.text("Note sur §3" + NotationNote.NOTE_ACTIVITY.getMaxNote() + " §fpoints"));
    }

    public static Component getHoverEconomy() {
        return Component.text("Note qui comprends, la richesse de la ville et le PIB par habitant de la ville.")
                .appendNewline()
                .append(Component.text("Note sur §3" + NotationNote.NOTE_PIB.getMaxNote() + " §fpoints"));
    }

    public static Component getHoverCoherence() {
        return Component.text("Note de cohérence qui comprends, la cohérence des constructions entre elles, l'harmonie des couleurs, la transitition entre 2 thèmes, ect...")
                .append(Component.text("Note sur §3" + NotationNote.NOTE_COHERENCE.getMaxNote() + " §fpoints"));
    }

    public static Component getHoverArchitectural() {
        return Component.text("Note d'architecture qui comprends, la diversité des blocs utilisées, l'architecture des builds ainsi que la végétation.")
                .appendNewline()
                .append(Component.text("Note sur §3" + NotationNote.NOTE_ARCHITECTURAL.getMaxNote() + " §fpoints"));
    }
}
