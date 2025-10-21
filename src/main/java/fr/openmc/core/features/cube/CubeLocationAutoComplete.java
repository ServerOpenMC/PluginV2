package fr.openmc.core.features.cube;

import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CubeLocationAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return MultiBlockManager.getMultiBlocks().stream()
                .filter(mb -> mb instanceof Cube)
                .map(mb -> {
                    Location loc = mb.origin;
                    return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
                })
                .toList();
    }
}
