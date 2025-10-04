package fr.openmc.core.features.cosmetics;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmetic;
import fr.openmc.core.features.cosmetics.bodycostmetics.BodyCosmeticsManager;
import fr.openmc.core.features.cosmetics.bodycostmetics.cosmetics.BlueBackPack;
import fr.openmc.core.features.cosmetics.bodycostmetics.cosmetics.GoldWings;
import fr.openmc.core.features.cosmetics.bodycostmetics.cosmetics.PinkBackPack;
import fr.openmc.core.features.cosmetics.commands.CosmeticCommand;
import fr.openmc.core.features.cosmetics.listeners.CosmeticListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CosmeticManager {

    @Getter
    private static BodyCosmeticsManager bodyCosmeticsManager;
    @Getter
    private static Set<BodyCosmetic> cosmetics;
    @Getter
    private static Map<UUID, Set<BodyCosmetic>> ownedCosmetics;
    @Getter
    private static Map<UUID, BodyCosmetic> activatedCosmetics;

    public static void enable(JavaPlugin plugin) {
        bodyCosmeticsManager = new BodyCosmeticsManager(plugin);
        new CosmeticListener(plugin, bodyCosmeticsManager);
        cosmetics = Set.of(new GoldWings(), new BlueBackPack(), new PinkBackPack());
        ownedCosmetics = new HashMap<>();
        activatedCosmetics = new HashMap<>();
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("cosmetics",
                (args, sender, command) -> cosmetics.stream().map(BodyCosmetic::getName).collect(Collectors.toList())
        );
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("ownedCosmetics",
                (args, sender, command) -> {
                    Set<BodyCosmetic> owned = ownedCosmetics.getOrDefault(sender.getUniqueId(), Set.of());
                    if (owned.isEmpty()) {
                        return List.of("Vous ne possédez aucune cosmétique.");
                    }
                    return owned.stream().map(BodyCosmetic::getName).collect(Collectors.toList());
                }
        );
        CommandsManager.getHandler().register(new CosmeticCommand());
    }

    public static @Nullable BodyCosmetic getCosmeticByName(String name) {
        return cosmetics.stream()
                .filter(cosmetic -> cosmetic.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static void addOwnedCosmetic(UUID playerUUID, BodyCosmetic cosmetic) {
        ownedCosmetics.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(cosmetic);
    }

    public static boolean ownsCosmetic(UUID playerUUID, BodyCosmetic cosmetic) {
        return ownedCosmetics.getOrDefault(playerUUID, Set.of()).contains(cosmetic);
    }

    public static @Nullable BodyCosmetic getActivatedCosmetic(UUID playerUUID) {
        return activatedCosmetics.get(playerUUID);
    }
}
