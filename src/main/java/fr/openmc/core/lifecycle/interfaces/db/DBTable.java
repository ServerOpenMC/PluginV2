package fr.openmc.core.lifecycle.interfaces.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.experimental.Delegate;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Wrapper d'un DAO pour mieux abstraire la création de la base de db dans les features
 * @param <T> Le classe de l'objet (Mayor, DBCity, ...)
 * @param <P> Le type de la clé primaire (généralement utilisé pour les requetes via un DAO)
 */
public class DBTable<T, P> {
    private static final Map<Class<?>, DBTable<?, ?>> tables = new ConcurrentHashMap<>();

    private final Class<T> type;
    @Delegate
    private Dao<T, P> dao;

    public DBTable(Class<T> type) {
        this.type = type;
    }

    public static <T, P> DBTable<T, P> of(Class<T> type) {
        return (DBTable<T, P>) tables.computeIfAbsent(type, _ -> new DBTable<>(type));
    }

    public static void clearCache() {
        tables.clear();
    }

    public void init(ConnectionSource source) throws SQLException {
        TableUtils.createTableIfNotExists(source, type);
        dao = DaoManager.createDao(source, type);
    }

    public void save(T entity) {
        try {
            dao.createOrUpdate(entity);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de sauvegarde de " + type.getSimpleName(), e);
        }
    }

    public List<T> loadList() {
        try {
            return dao.queryForAll();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de chargement de " + type.getSimpleName(), e);
        }
    }

    public <K> Map<K, T> loadMap(Function<T, K> key) {
        Map<K, T> map = new HashMap<>();

        loadList().forEach(e ->
                map.put(key.apply(e), e)
        );

        return map;
    }

    public <K, V> Map<K, V> loadMap(Function<T, K> key, Function<T, V> value) {
        Map<K, V> map = new HashMap<>();

        loadList().forEach(e ->
                map.put(key.apply(e), value.apply(e))
        );

        return map;
    }

    public <K> Map<K, List<T>> loadMapList(Function<T, K> keyOf) {
        Map<K, List<T>> map = new HashMap<>();

        for (T entity : loadList()) {
            map.computeIfAbsent(keyOf.apply(entity), _ -> new ArrayList<>()).add(entity);
        }

        return map;
    }

    public <K> Map<K, Set<T>> loadMapSet(Function<T, K> keyOf) {
        Map<K, Set<T>> map = new HashMap<>();

        for (T entity : loadList()) {
            map.computeIfAbsent(keyOf.apply(entity), _ -> new HashSet<>()).add(entity);
        }

        return map;
    }

    public void saveAll(Collection<T> entities) {
        try {
            dao.callBatchTasks(() -> {
                for (T e : entities)
                    dao.createOrUpdate(e);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Erreur de sauvegarde de " + type.getSimpleName(), e);
        }
    }

    public <K> void saveMap(Map<K, T> source) {
        saveAll(source.values());
    }

    public <K> void saveMapList(Map<K, List<T>> source) {
        saveAll(source.values().stream().flatMap(List::stream).toList());
    }

    public <K> void saveMapSet(Map<K, Set<T>> source) {
        saveAll(source.values().stream().flatMap(Set::stream).toList());
    }

    public void clearTable() {
        try {
            TableUtils.clearTable(dao.getConnectionSource(), type);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de nettoyage de la table " + type.getSimpleName(), e);
        }
    }
}
