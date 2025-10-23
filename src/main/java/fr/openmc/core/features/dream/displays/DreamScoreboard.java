package fr.openmc.core.features.dream.displays;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Classe utilitaire pour la mise à jour du Scoreboard dans la Dimension des Rêves.
 *
 * <p>Cette classe met à jour le Scoreboard d'un joueur en fonction du biome associé à la Dimension des Rêves.</p>
 */
public class DreamScoreboard {

    /**
     * Met à jour le Scoreboard du joueur dans la Dimension des Rêves.
     *
     * <p>
     * Si le biome de rêve n'est pas défini pour le joueur, aucune mise à jour n'est effectuée.
     * Les scores sont définis pour afficher le nom du joueur et le biome courant.
     * </p>
     *
     * @param player     le joueur dont le Scoreboard est mis à jour
     * @param scoreboard le Scoreboard du joueur
     * @param objective  l'Objective à mettre à jour
     */
    public static void updateDreamScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
        DreamBiome dreamBiome = DreamDimensionManager.getDreamBiome(player);

        if (dreamBiome == null) return;

        objective.getScore("§7").setScore(19);
        objective.getScore("§8• §fNom: §7" + player.getName()).setScore(18);
        objective.getScore("   ").setScore(17);
        objective.getScore("§8• §fBiome: §7" + dreamBiome.getName()).setScore(16);

        DreamStructure dreamStructure = DreamStructuresManager.getStructureAt(player.getLocation());
        if (dreamStructure != null) {
            String nameLocation = dreamStructure.type().getName();

            objective.getScore("§8• §fLocation: §7" + nameLocation).setScore(15);
        }
        // temps
        // nb orbe

        objective.getScore("   ").setScore(1);
        objective.getScore("§1      ᴘʟᴀʏ.ᴏᴘᴇɴᴍᴄ.ꜰʀ").setScore(0);
    }
}
