package fr.openmc.core.features.dream.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.dream.listeners.orb.PlayerObtainOrb;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admdream")
@CommandPermission("omc.admins.commands.admindream")
public class AdminDreamCommands {
    @Subcommand("setprogressionorb")
    @CommandPermission("omc.admins.commands.admndream.setprogressionorb")
    void setProgressionOrb(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player toPlayer,
            @Named("nb_progression_orb") @Suggest({"1", "2", "3", "4", "5"}) int orbProgression
    ) {
        PlayerObtainOrb.setProgressionOrb(toPlayer, orbProgression);
    }
}
