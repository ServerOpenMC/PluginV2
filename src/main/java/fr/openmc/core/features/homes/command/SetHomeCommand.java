package fr.openmc.core.features.homes.command;

import fr.openmc.api.entity.player.OMCOfflinePlayer;
import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.command.autocomplete.HomeAutoComplete;
import fr.openmc.core.features.homes.events.HomeCreateEvent;
import fr.openmc.core.features.homes.icons.HomeIconRegistry;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.features.homes.utils.HomeUtil;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.UUID;

public class SetHomeCommand {

    @Command("sethome")
    @Description("Permet de définir votre home")
    @CommandPermission("omc.commands.home.sethome")
    public void setHome(
            OMCPlayer player,
            @Named("home") @SuggestWith(HomeAutoComplete.class) String name
    ) {
        if (DisabledWorldHome.isDisabledWorld(player.getWorld())) {
            player.message().sendError(TranslationManager.translation("feature.homes.command.disabled_world"), Prefix.HOME, true);
            return;
        }

        if (player.hasPermission("omc.admin.homes.sethome.other") && name.contains(":")) {
            String[] split = name.split(":");
            String targetName = split[0];
            String homeName = split[1];

            OMCOfflinePlayer target = OMCOfflinePlayer.of(Bukkit.getOfflinePlayer(targetName));
            if (!target.hasPlayedBefore()) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, true);
                return;
            }

            if (split.length < 2) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.player_not_found"), Prefix.HOME, true);
                return;
            }

            if (!HomeUtil.isValidHomeName(homeName)) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, true);
                return;
            }
            List<Home> homes = target.home().getHomes();
            for (Home home : homes) {
                if (home.getName().equalsIgnoreCase(homeName)) {
                    player.message().sendError(TranslationManager.translation("feature.homes.command.other_already_has"), Prefix.HOME, true);
                    return;
                }
            }

            Home home = new Home(UUID.randomUUID(), target.getUniqueId(), homeName, player.getLocation(), HomeIconRegistry.getDefaultIcon());
            target.home().setHome(home);

            player.message().sendSuccess(
                    TranslationManager.translation(
                            "feature.homes.command.set.other.success",
                            Component.text(homeName).color(NamedTextColor.YELLOW),
                            Component.text(targetName).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.HOME,
                    true
            );
            if (target.isOnline() && target instanceof Player targetPlayer) {
                OMCPlayer.of(targetPlayer).message().sendSuccess(
                        TranslationManager.translation(
                                "feature.homes.command.set.by_admin",
                                Component.text(homeName).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.HOME,
                        true
                );
            }

            return;
        }

        if (!HomeUtil.isValidHomeName(name)) {
            player.message().sendError(TranslationManager.translation("feature.homes.command.invalid_name"), Prefix.HOME, true);
            return;
        }

        List<Home> homes = player.home().getHomes();

        int currentHome = homes.size();
        int homesLimit = player.home().getHomeLimit().getLimit();

        if (currentHome >= homesLimit) {
            player.message().sendError(TranslationManager.translation("feature.homes.command.home_limit_reached"), Prefix.HOME, true);
            return;
        }

        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                player.message().sendError(TranslationManager.translation("feature.homes.command.already_has"), Prefix.HOME, true);
                return;
            }
        }

        Home home = new Home(UUID.randomUUID(), player.getUniqueId(), name, player.getLocation(), HomeIconRegistry.getDefaultIcon());
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new HomeCreateEvent(home, player));
        });
        player.home().setHome(home);

        player.message().sendSuccess(
                TranslationManager.translation(
                        "feature.homes.command.set.self.success",
                        Component.text(name).color(NamedTextColor.YELLOW)
                ),
                Prefix.HOME,
                true
        );
    }
}
