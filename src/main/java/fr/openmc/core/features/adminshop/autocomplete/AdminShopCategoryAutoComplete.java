package fr.openmc.core.features.adminshop.autocomplete;

import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopCategory;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Comparator;
import java.util.List;

/**
 * Suggère les catégories de l'adminshop, dans le même ordre que le menu principal.
 */
public class AdminShopCategoryAutoComplete implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return AdminShopManager.getCategories().stream()
                .sorted(Comparator.comparingInt(ShopCategory::position))
                .map(ShopCategory::id)
                .toList();
    }
}
