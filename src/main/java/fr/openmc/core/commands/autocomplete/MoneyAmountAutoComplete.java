package fr.openmc.core.commands.autocomplete;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

/**
 * Suggère le mot-clé "all" pour les arguments de montant d'argent
 * (banque personnelle, banque de ville).
 */
public class MoneyAmountAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return List.of("all");
    }
}
