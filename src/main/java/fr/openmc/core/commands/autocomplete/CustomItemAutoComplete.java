package fr.openmc.core.commands.autocomplete;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public class CustomItemAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return OMCRegistry.CUSTOM_ITEMS.values()
                .stream()
                .map(CustomItem::getId)
                .filter(id -> !id.startsWith("omc_dream:"))
                .map(id -> id.split(":")[1])
                .toList();
    }
}
