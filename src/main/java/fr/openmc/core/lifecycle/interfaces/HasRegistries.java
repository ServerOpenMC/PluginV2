package fr.openmc.core.lifecycle.interfaces;

import fr.openmc.core.lifecycle.registries.LifecycleRegistry;

import java.util.List;
import java.util.function.Supplier;

/**
 * Interface mettant a disposition des registres a une classe (ex. Feature)
 */
public interface HasRegistries {
    /**
     * Registres à initialiser
     */
    List<Supplier<LifecycleRegistry>> getRegistries();
}
