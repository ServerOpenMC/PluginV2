package fr.openmc.core.features.homes.command.autocomplete;

import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.features.homes.models.Home;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.stream.StringStream;

import java.util.ArrayList;
import java.util.List;

public class HomeAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        List<String> suggestions = new ArrayList<>();

        Player player = context.actor().requirePlayer();
        OMCPlayer omcPlayer = OMCPlayer.of(player);
        String commandName = context.command().usage().split(" ")[0];

        StringStream args = context.input();

        if (player == null)
            return suggestions;

        if (args.isEmpty()) {
            if (player.hasPermission("omc.admin.homes.teleport.others")) {
                Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName() + ":"));
                if (isHomeCommand(commandName)) {
                    omcPlayer.home().getHomes().forEach(home -> suggestions.add(home.getName()));
                }
            }
        } else {
            String arg = args.peekString();

            if (arg.contains(":") && player.hasPermission("omc.admin.homes.teleport.others")) {
                if (isHomeCommand(commandName)) {
                    String[] split = arg.split(":", 2);
                    OfflinePlayer target = Bukkit.getOfflinePlayer(split[0]);

                    if (target.hasPlayedBefore()) {
                        String prefix = split[0] + ":";
                        omcPlayer.home().getHomesNames().forEach(home -> suggestions.add(prefix + home));
                    }
                }
            } else {
                if (player.hasPermission("omc.admin.homes.teleport.others")) {
                    Bukkit.getOnlinePlayers().stream()
                            .map(OfflinePlayer::getName)
                            .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                            .map(name -> name + ":")
                            .forEach(suggestions::add);
                }

                if (isHomeCommand(commandName)) {
                    omcPlayer.home().getHomes().stream()
                            .map(Home::getName)
                            .filter(name -> name.toLowerCase().startsWith(arg.toLowerCase()))
                            .forEach(suggestions::add);
                }
            }
        }

        if (isHomeCommand(commandName)) {
            suggestions.addAll(omcPlayer.home().getHomesNames());
        }

        return suggestions;
    }

    private boolean isHomeCommand(String name) {
        return name.equalsIgnoreCase("home")
                || name.equalsIgnoreCase("delhome")
                || name.equalsIgnoreCase("relocatehome")
                || name.equalsIgnoreCase("renamehome");
    }
}
