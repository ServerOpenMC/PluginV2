package fr.openmc.core.commands.debug;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.chronometer.ChronometerType;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public class ChronometerCommand {
    @Command("debug chronometer start")
    @CommandPermission("omc.debug.chronometer.start")
    @Description("Test du chronometre")
    private void chronometerStart(OMCPlayer target, @Named("time") int time) {
        if (time > 90) {
            target.message().sendError(TranslationManager.translation("command.debug.chronometer.cant_90s_chronometer"), false);
            return;
        }
        target.chronometer().startChronometer("debug", time, ChronometerType.ACTION_BAR, null, ChronometerType.ACTION_BAR, null);
    }

    @Command("debug chronometer stopall")
    @CommandPermission("omc.debug.chronometer.stopall")
    @Description("Test du chronometre")
    private void chronometerStopAll(OMCPlayer target) {
        target.chronometer().stopAllChronometer(ChronometerType.ACTION_BAR, null);
    }

    @Command("debug chronometer stop")
    @CommandPermission("omc.debug.chronometer.stop")
    @Description("Test du chronometre")
    private void chronometerStop(OMCPlayer target, @Named("group") String group) {
        target.chronometer().stopChronometer(group, ChronometerType.ACTION_BAR, null);
    }

    @Command("debug chronometer list")
    @CommandPermission("omc.debug.chronometer.list")
    @Description("Test du chronometre")
    private void chronometerList(OMCPlayer owner, @Named("target") OMCPlayer target) {
        UUID entitytUUID = target.getUniqueId();

        if (Chronometer.chronometer.containsKey(entitytUUID)) {
            owner.sendMessage(TranslationManager.translation("api.chronometer.chronometer_on"));
            Chronometer.chronometer.get(entitytUUID).forEach((group, time) ->
                    owner.sendMessage(TranslationManager.translation("api.chronometer.chronometer_on_list",
                            Component.text(group), Component.text(time).color(NamedTextColor.GOLD)).color(NamedTextColor.YELLOW)));
        } else {
            owner.sendMessage(TranslationManager.translation("api.chronometer.none_chronometer_player"));
        }
    }
}
