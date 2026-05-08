package fr.openmc.core.features.dream.mecanism.cold;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ColdTask extends BukkitRunnable {
    private int counter = 0;
    private int lastColdLevel = 0;
    private final DreamPlayer dreamPlayer;
    private final Player player;

    public ColdTask(DreamPlayer dreamPlayer) {
        this.dreamPlayer = dreamPlayer;
        this.player = dreamPlayer.getPlayer();
    }

    @Override
    public void run() {
        counter += 20;
        int cold = dreamPlayer.getCold();
        boolean nearHeat = ColdManager.isNearHeatSource(player);
        boolean isInBaseCamp = DreamStructuresManager.isInsideStructure(player.getLocation(), DreamStructure.DreamType.BASE_CAMP);
        double resistance = ColdManager.calculateColdResistance(player);
        boolean inColdBiome = player.getLocation().getBlock().getBiome().equals(DreamBiome.GLACITE_GROTTO.getBiome());

        // Retrait du froid que si le joueur n'est pas en crea ou en spec
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) return;

        // Retrait du froid si dans une structure BASE_CAMP
        if (isInBaseCamp) {
            cold = Math.max(0, cold - 15);
        } else if (nearHeat) {
            if (counter % 40 == 0) {
                cold = Math.max(0, cold - 1);
            }
        }

        // Retrait du froid si pas dans une glacite grotto
        if (!inColdBiome && counter % 40 == 0) {
            cold = Math.max(0, cold - 1);
        }

        // Ajout du froid dans une glacite grotto
        if (!nearHeat && !isInBaseCamp && inColdBiome && counter % (60 + (int) (resistance * 10)) == 0) {
            cold = Math.min(100, cold + 1);
        }

        // On arrete la task si plus dans glacite grotto
        if (!inColdBiome && cold == 0) {
            cancel();
            return;
        }

        int newLevel = ColdManager.getColdLevel(cold);
        if (newLevel > lastColdLevel) {
            ColdManager.sendColdLevelMessage(player, newLevel);
        }
        lastColdLevel = newLevel;

        ColdManager.applyColdEffects(player, cold);
        dreamPlayer.setCold(cold);
    }
}
