package fr.openmc.core.features.city.models.city.namespaces;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface CityMembers {

    Set<UUID> getMembers();

    Set<UUID> getOnlineMembers();

    boolean isMember(Player player);

    void addPlayer(UUID playerUUID);

    void removePlayer(UUID playerUUID);

    void changeOwner(UUID player);
}
