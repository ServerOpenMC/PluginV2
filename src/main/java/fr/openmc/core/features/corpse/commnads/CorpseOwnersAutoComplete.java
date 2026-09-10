package fr.openmc.core.features.corpse.commnads;

import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;
import java.util.Objects;

public class CorpseOwnersAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {

        return CorpseNPCManager.corpseNpcMap.keySet()
                .stream()
                .map(CacheOfflinePlayer::getOfflinePlayer)
                .filter(Objects::nonNull)
                .map(OfflinePlayer::getName)
                .toList();
    }
}
