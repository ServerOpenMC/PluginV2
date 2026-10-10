package fr.openmc.core.features.city.sub.mayor.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.actions.MayorCommandAction;
import fr.openmc.core.features.city.sub.mayor.actions.MayorSetWarpAction;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.MayorPhase;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class MayorCommands {
    @Command({"city mayor", "ville maire"})
    @CommandPermission("omc.commands.city.mayor")
    @Description("Ouvre le menu des maires")
    void mayor(OMCPlayer sender) {
        MayorCommandAction.launchInteractionMenu(sender);
    }

    @Command({"city warp", "ville warp"})
    @Description("Teleporte au warp commun de la ville")
    void warp(OMCPlayer player) {
        City playerCity = player.city().getCity();

        if (playerCity == null) return;

        CityLaw law = playerCity.getLaw();
        Location warp = law.getWarp();

        if (warp == null) {
            if (playerCity.getMayorPhase().equals(MayorPhase.MAYOR_ELECTED)) {
                player.message().sendInfo(TranslationManager.translation("feature.city.mayor.command.warp.not_set.phase2"), Prefix.CITY, true);
                return;
            }
            player.message().sendInfo(TranslationManager.translation("feature.city.mayor.command.warp.not_set.no_mayor"), Prefix.CITY, true);
            return;
        }

        PlayerUtils.sendFadeTitleTeleport(
                player,
                warp
        );
    }

    @Command({"city setwarp", "ville setwarp"})
    @Description("Déplacer le warp de votre ville")
    void setWarpCommand(OMCPlayer player) {
        MayorSetWarpAction.setWarp(player);
    }
}
