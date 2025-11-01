package fr.openmc.api.scoreboard.repository;

import fr.openmc.api.scoreboard.SternalBoard;

import java.util.UUID;

public interface ObjectCacheRepository<T extends SternalBoard> extends Iterable<T> {

    void create(T object);

    void delete(UUID id);

    T find(UUID id);

    boolean exists(UUID id);

    boolean exists(T object);

    void clear();

}
