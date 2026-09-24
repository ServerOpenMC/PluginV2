package fr.openmc.core.lifecycle.registries;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

public final class RegistryContext {
    private final Supplier<? extends LifecycleRegistry> registry;
    private final Set<RegistryLoadingType> loadingTypes;
    private LifecycleRegistry instance;

    /**
     * Création d'un context pour les registres.
     * prends un registre à initialiser, et les conditions d'initialisations du registre.
     * Creer pour éviter des erreurs de {@link org.bukkit.Registry} lié au Material ou autre, qui ne sont pas encore initialisé lors du bootstrap.
     * @param registry le constructeur du registre
     * @param loadingTypes les conditions de chargements
     */
    public RegistryContext(Supplier<? extends LifecycleRegistry> registry, RegistryLoadingType... loadingTypes) {
        this.registry = registry;
        this.loadingTypes = EnumSet.copyOf(Arrays.asList(loadingTypes));
    }

    public LifecycleRegistry get() {
        if (instance == null) instance = registry.get();
        return instance;
    }

    public boolean has(RegistryLoadingType type) { return loadingTypes.contains(type); }
}
