package fr.openmc.core.lifecycle.registries;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class SubRegistry<K, V> implements LifecycleRegistry {

    @Getter
    private final Set<V> registry = new LinkedHashSet<>();
    private boolean started;

    public abstract KeyedRegistry<K, ? super V> getParentRegistry();

    public <T extends V> T register(T value) {
        registry.add(value);
        if (started) pushToParent(value);
        return value;
    }

    public void start() {
        started = true;
        for (V value : registry) pushToParent(value);
    }

    private void pushToParent(V value) {
        KeyedRegistry<K, ? super V> parent = getParentRegistry();
        if (parent != null) parent.register(value);
    }

    /**
     * Fait pour bypass une erreur, qui empeche de lire le registre qui va etre initialisé, dans ce meme registre
     * @param registry le registre qu'on veut initialiser
     * @param assign le registre où on veut l'initialiser
     * @return le registre initialisé
     */
    public static <R extends SubRegistry<?, ?>> R boot(R registry, Consumer<R> assign) {
        assign.accept(registry);

        registry.start();

        return registry;
    }
}