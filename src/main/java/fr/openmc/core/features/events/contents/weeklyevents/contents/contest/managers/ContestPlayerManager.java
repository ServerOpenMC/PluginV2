package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models.ContestPlayer;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Setter
public class ContestPlayerManager extends Feature {
    private final ContestManager contestManager = OMCRegistry.FEATURES.CONTEST.get();

    /**
     * Map reliant un nombre de points à un titre correspondant.
     * Par exemple, 10000 points correspondent à "Dictateur en ".
     */
    private final Map<Integer, String> RANKS = new LinkedHashMap<>() {{
        put(10000, "feature.events.contest.prefix.dictator");
        put(2500, "feature.events.contest.prefix.colonel");
        put(2000, "feature.events.contest.prefix.addict");
        put(1500, "feature.events.contest.prefix.dieu");
        put(1000, "feature.events.contest.prefix.legende");
        put(750, "feature.events.contest.prefix.senior");
        put(500, "feature.events.contest.prefix.pro");
        put(250, "feature.events.contest.prefix.semi_pro");
        put(100, "feature.events.contest.prefix.amateur");
        put(0, "feature.events.contest.prefix.noob");
    }};

    /**
     * Map reliant le nombre de points minimum à atteindre pour obtenir le rang suivant
     * au nombre de points à partir desquels ce rang est débloqué.
     * Par exemple, pour 2500 points, le rang suivant commence à 10000 points.
     */
    private final Map<Integer, Integer> GOAL_POINTS = new LinkedHashMap<>() {{
        put(10000, 0);
        put(2500, 10000);
        put(2000, 2500);
        put(1500, 2000);
        put(1000, 1500);
        put(750, 1000);
        put(500, 750);
        put(250, 500);
        put(100, 250);
        put(0, 100);
    }};

    /**
     * Map convertissant le nombre de points en un rang numérique compris entre 1 et 10.
     * Par exemple, 10000 points correspondent au rang 10.
     */
    private final Map<Integer, Integer> POINTS_TO_INT_RANK = new LinkedHashMap<>() {{
        put(10000, 10);
        put(2500, 9);
        put(2000, 8);
        put(1500, 7);
        put(1000, 6);
        put(750, 5);
        put(500, 4);
        put(250, 3);
        put(100, 2);
        put(0, 1);
    }};

    /**
     * Map des multiplicateurs d'argent pour la récompense en fonction du rang.
     * Chaque clé est le rang numérique et chaque valeur le multiplicateur correspondant.
     */
    private final HashMap<Integer, Double> MULTIPLICATOR_MONEY = new HashMap<>(
            Map.of(
                    1, 1.0,
                    2, 1.1,
                    3, 1.3,
                    4, 1.4,
                    5, 1.5,
                    6, 1.6,
                    7, 1.7,
                    8, 1.8,
                    9, 2.0,
                    10, 2.4
            )
    );

    public Component getPlayerCampComponent(Player player) {
        int campInteger = contestManager.getDataPlayer().get(player.getUniqueId()).getCamp();
        return contestManager.getData().getCampComponent(campInteger);
    }

    /**
     * Met à jour le nombre de points d’un joueur.
     * Cette opération écrase les points précédemment enregistrés.
     *
     * @param playerUUID L’UUID du joueur à mettre à jour.
     * @param points Le nouveau nombre de points du joueur.
     */
    public void setPointsPlayer(UUID playerUUID, int points) {
        ContestPlayer data = contestManager.getDataPlayer().get(playerUUID);
        if (data != null) {
            data.setPoints(points);
        }
    }

    /**
     * Retourne le titre associé à un nombre de points donné.
     *
     * @param points Le nombre de points d’un joueur.
     * @return Le titre correspondant aux points.
     */
    public Component getTitleWithPoints(int points) {
        for (Map.Entry<Integer, String> entry : RANKS.entrySet()) {
            if (points >= entry.getKey()) {
                return TranslationManager.translation(entry.getValue()).appendSpace();
            }
        }
        return Component.empty();
    }

    /**
     * Retourne le titre du contest d’un joueur en fonction de ses points actuels.
     *
     * @param player Le joueur dont on veut obtenir le titre.
     * @return Le titre correspondant au joueur.
     */
    public Component getTitleContest(Player player) {
        int points = contestManager.getDataPlayer().get(player.getUniqueId()).getPoints();

        return getTitleWithPoints(points);
    }

    /**
     * Retourne le nombre de points nécessaires pour atteindre le rang suivant.
     *
     * @param player Le joueur dont on veut calculer le prochain palier.
     * @return Le nombre de points requis pour monter de rang.
     *         Retourne -1 si aucun palier trouvé.
     */
    public int getGoalPointsToRankUp(Player player) {
        int points = contestManager.getDataPlayer().get(player.getUniqueId()).getPoints();

        for (Map.Entry<Integer, Integer> entry : GOAL_POINTS.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }

        return -1;
    }

    /**
     * Retourne le rang numérique d’un joueur hors ligne en fonction de ses points.
     *
     * @param player Le joueur hors ligne.
     * @return Le rang sous forme d’un entier (1 à 10).
     */
    public int getRankContestFromOfflineInt(OfflinePlayer player) {
        int points = contestManager.getDataPlayer().get(player.getUniqueId()).getPoints();

        for (Map.Entry<Integer, Integer> entry : POINTS_TO_INT_RANK.entrySet()) {
            if (points >= entry.getKey()) {
                return entry.getValue();
            }
        }

        return 0;
    }

    /**
     * Vérifie si le joueur fait partie de l’équipe gagnante du contest.
     *
     * @param player Le joueur hors ligne à vérifier.
     * @return true si le joueur est dans le camp gagnant, false sinon.
     */
    public boolean hasWinInCampFromOfflinePlayer(OfflinePlayer player) {
        int playerCamp = contestManager.getDataPlayer().get(player.getUniqueId()).getCamp();

        int points1 = contestManager.getData().getPoints1();
        int points2 = contestManager.getData().getPoints2();


        int vote1 = contestManager.getVoteTaux(1);
        int vote2 = contestManager.getVoteTaux(2);
        int totalvote = vote1 + vote2;
        int vote1Taux = (int) (((double) vote1 / totalvote) * 100);
        int vote2Taux = (int) (((double) vote2 / totalvote) * 100);
        int multiplicateurPoint = Math.abs(vote1Taux - vote2Taux)/16;

        if (vote1Taux > vote2Taux) {
            points2*=multiplicateurPoint;
        } else if (vote1Taux < vote2Taux) {
            points1*=multiplicateurPoint;
        }

        if (points1 > points2 && playerCamp == 1) {
            return true;
        }

        return points2 > points1 && playerCamp == 2;
    }

    /**
     * Retourne le multiplicateur de récompense en fonction du rang du joueur.
     *
     * @param rang Le rang numérique du joueur (1 à 10).
     * @return Le multiplicateur correspondant pour le calcul des récompenses.
     */
    public double getMultiplicatorFromRank(int rang) {
        return MULTIPLICATOR_MONEY.getOrDefault(rang, 1.0);
    }
}
