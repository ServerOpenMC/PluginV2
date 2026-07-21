package fr.openmc.core.features.homes.command;

import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.hooks.WorldGuardHook;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class RelocateHomeCommand {

    @Command("relocatehome")
    @Description("Déplace votre home")
    @CommandPermission("omc.commands.home.relocate")
    public void relocateHome(
            OMCPlayer player,
            @Named("home") @SuggestWith(HomeAutoComplete.class) String home
    ) {

        Location location = player.getLocation();

        if (DisabledWorldHome.isDisabledWorld(location.getWorld())) {
            player.message().sendError(TranslationManager.translation("feature.homes.command.disabled_world"), Prefix.HOME, true);
            return;
        }

        if (player.hasPermission("omc.admin.homes.relocate.other") && home.contains(":")) {
            String[] split = home.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if (!target.hasPlayedBefore()) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.OPENMC, true);
                return;
            }

            List<Home> homes = player.home().getHomes();
            for (Home h : homes) {
                if (!h.getName().equalsIgnoreCase(homeName)) {
                    continue;
                }

                player.home().relocateHome(h, location);
                player.message().sendSuccess(
                        TranslationManager.translation(
                                "feature.homes.command.relocate.other.success",
                                Component.text(h.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        true
                );
                return;
            }

            player.message().sendError(TranslationManager.translation("feature.homes.command.other_no_home_with_name"), Prefix.OPENMC, true);
            return;
        }

        List<Home> homes = player.home().getHomes();

        if (WorldGuardHook.isRegionConflict(location)) {
            player.message().sendError(TranslationManager.translation("feature.homes.command.relocate.protected_region"), Prefix.HOME, true);
            return;
        }

        for (Home h : homes) {
            if (h.getName().equalsIgnoreCase(home)) {
                player.home().relocateHome(h, location);
                player.message().sendSuccess(
                        TranslationManager.translation(
                                "feature.homes.command.relocate.self.success",
                                Component.text(h.getName()).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        true
                );
                return;
            }
        }

        player.message().sendError(TranslationManager.translation("feature.homes.command.self_no_home_with_name"), Prefix.OPENMC, true);
    }

}
