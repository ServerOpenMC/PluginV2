package fr.openmc.riftengine.api.registry.scanner;

/**
 * Contexte, un scanner est grosso merdo une représentation des données structurés dans les .yml d'ItemsAdder,
 * mais peut etre élargi pour objectifier (nv verbe), les yml.
 */
public abstract class AbstractScanner<T, P> {
    private T cache = null;

    public T getCache(P param) throws Exception {
        if (cache == null)
            cache = scan(param);

        return cache;
    }

    public abstract T scan(P param) throws Exception;
}
