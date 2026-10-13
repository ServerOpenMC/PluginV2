package fr.openmc.core.features.city.models.city;

import fr.openmc.core.features.city.models.CityType;
import fr.openmc.core.features.city.models.city.namespaces.*;
import fr.openmc.core.features.city.models.db.DBCity;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.utils.world.chunk.ChunkPos;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface CityInterface {
    // ** Helpers
    static CityInterface of(String name) {
        return City.of(name);
    }

    static CityInterface of(ChunkPos chunkPos) {
        return City.of(chunkPos);
    }

    static CityInterface of(Block block) {
        return City.of(block);
    }

    static CityInterface of(Location location) {
        return City.of(location);
    }

    static CityInterface of(Chunk chunk) {
        return City.of(chunk);
    }

    static CityInterface of(int x, int z) {
        return City.of(x, z);
    }

    static CityInterface of(UUID cityUUID) {
        return City.of(cityUUID);
    }

    static CityInterface ofPlayer(Player player) {
        return City.ofPlayer(player);
    }

    static CityInterface ofPlayer(UUID playerUUID) {
        return City.ofPlayer(playerUUID);
    }

    static CityInterface ofMascot(UUID entityUUID) {
        return City.ofMascot(entityUUID);
    }

    static CityInterface create(UUID uniqueId, String name, Player owner, CityType type, Chunk chunk) {
        return new City(uniqueId, name, owner, type, chunk);
    }

    // * Info globale

    UUID getUniqueId();

    String getName();

    void rename(String newName);

    CityType getType();

    void changeType();

    int getLevel();

    void setLevel(int newLevel);

    Mascot getMascot();

    boolean isImmune();

    DBCity serialize();

    // ** Namespaces

    CityMembers members();

    CityChunks chunks();

    CityChest chest();

    CityPermissions permissions();

    CityRanks ranks();

    CityEconomy economy();

    CityMayor mayor();

    CityNotations notation();

    CityStatistic statistic();

    CityWar war();
}
