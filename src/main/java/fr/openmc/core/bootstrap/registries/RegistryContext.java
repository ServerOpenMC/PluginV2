package fr.openmc.core.bootstrap.registries;

import java.util.function.Supplier;

/**
 * Création d'un context pour les registres.
 * prends un registre à initialiser, et les conditions d'initialisations du registre.
 * Creer pour éviter des erreurs de {@link org.bukkit.Registry} lié au Material ou autre, qui ne sont pas encore initialisé lors du bootstrap.
 * @param registry le constructeur du registre
 * @param loadingTypes les conditions de chargements
 */
public record RegistryContext(Supplier<LifecycleRegistry> registry, RegistryLoadingType... loadingTypes) {
}
