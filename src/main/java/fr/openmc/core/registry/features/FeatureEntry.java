package fr.openmc.core.registry.features;

import com.google.common.base.Supplier;
import lombok.Getter;

import java.util.Set;

public class FeatureEntry<F extends Feature> {
    private final Supplier<F> supplier;
    @Getter
    private final FeatureLoadingType loadingType;
    private final Set<FeatureFlag> flags;
    private F instance;

    public FeatureEntry(Supplier<F> supplier, FeatureLoadingType loadingType, FeatureFlag... flags) {
        this.supplier = supplier;
        this.loadingType = loadingType;
        this.flags = Set.of(flags);
    }

    public static <V extends Feature> FeatureEntry<V> of(FeatureLoadingType loadingType, Supplier<V> supplier, FeatureFlag... flags) {
        return new FeatureEntry<>(supplier, loadingType, flags);
    }

    public F create() {
        if (instance == null) {
            instance = supplier.get();
        }
        return instance;
    }

    public F get() {
        if (instance == null)
            throw new IllegalStateException("La feature n'est pas encore crée");
        return instance;
    }

    public boolean has(FeatureFlag flag) { return flags.contains(flag); }
}
