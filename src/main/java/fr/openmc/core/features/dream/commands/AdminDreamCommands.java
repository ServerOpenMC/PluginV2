package fr.openmc.core.features.dream.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.listeners.orb.PlayerObtainOrb;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admdream")
@CommandPermission("omc.admins.commands.admindream")
public class AdminDreamCommands {
    @Subcommand("setprogressionorb")
    @CommandPermission("omc.admins.commands.admindream.setprogressionorb")
    void setProgressionOrb(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player toPlayer,
            @Named("nb_progression_orb") @Suggest({"1", "2", "3", "4", "5"}) int orbProgression
    ) {
        PlayerObtainOrb.setProgressionOrb(toPlayer, orbProgression, null);
        DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);

        if (cache != null) {
            cache.setProgressionOrb(orbProgression);
            DreamManager.saveDreamPlayerData(cache);
            return;
        }

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;
        DreamManager.saveDreamPlayerData(dreamPlayer);

        DBDreamPlayer cache1 = DreamManager.getCacheDreamPlayer(player);
        if (cache1 == null) return;
        cache1.setProgressionOrb(orbProgression);
        DreamManager.saveDreamPlayerData(cache1);
    }
}
