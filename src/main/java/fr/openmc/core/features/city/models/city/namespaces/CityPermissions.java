package fr.openmc.core.features.city.models.city.namespaces;

import fr.openmc.core.features.city.models.CityPermission;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface CityPermissions {

    UUID getPlayerWithPermission(CityPermission permission);

    Set<CityPermission> getPermissions(UUID player);

    boolean hasPermission(UUID playerUUID, CityPermission permission);

    boolean hasPermission(Player player, CityPermission permission);

    void addPermission(UUID playerUUID, CityPermission permission);

    void addPermission(Player player, CityPermission permission);

    void removePermission(UUID playerUUID, CityPermission permission);

    void removePermission(Player player, CityPermission permission);

    void clearPermissions(UUID playerUUID);

    void clearPermissions(Player player);
}
