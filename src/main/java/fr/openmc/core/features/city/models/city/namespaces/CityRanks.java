package fr.openmc.core.features.city.models.city.namespaces;

import java.util.Set;
import java.util.UUID;

import fr.openmc.core.features.city.models.db.DBCityRank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface CityRanks {

    Set<DBCityRank> getRanks();

    boolean isRanksFull();

    DBCityRank getRankByName(String rankName);

    boolean isRankExists(DBCityRank rank);

    boolean isRankExists(String rankName);

    void createRank(DBCityRank rank);

    void deleteRank(DBCityRank rank);

    void updateRank(DBCityRank oldRank, DBCityRank newRank);

    @Nullable DBCityRank getRankOfMember(UUID member);

    String getRankName(UUID member);

    void changeRank(Player sender, UUID playerUUID, DBCityRank newRank);
}
