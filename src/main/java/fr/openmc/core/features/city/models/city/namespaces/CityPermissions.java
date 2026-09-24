package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.models.CityPermission;

import java.util.Set;
import java.util.UUID;

public interface CityPermissions {

    UUID getPlayerWithPermission(CityPermission permission);

    Set<CityPermission> getPermissions(UUID player);

    boolean hasPermission(UUID uuid, CityPermission permission);

    void addPermission(UUID playerUUID, CityPermission permission);

    void removePermission(UUID playerUUID, CityPermission permission);

    void clearPermissions(UUID playerUUID);
}
