package fr.openmc.core.features.dream.listeners.orb;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.altar.AltarCraftingEvent;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.DreamItem;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public class PlayerObtainOrb implements Listener {
    private final int SCULK_PLAINS_ORB = 1;
    private final int SOUL_FOREST_ORB = 2;
    private final int CLOUD_CASTLE_ORB = 3;
    private final int MUD_BEACH_ORB = 4;
    private final int GLACITE_GROTTO_ORB = 5;

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        if (event.getViewers().isEmpty()) return;
        Player player = (Player) event.getViewers().getFirst();

        if (!DreamUtils.isInDream(player)) return;

        if (recipe instanceof Keyed keyed) {
            NamespacedKey key = keyed.getKey();
            if (key.toString().contains("omc_dream") && key.toString().contains("domination_orb")) { // contains beacuse key is ex zzzfake_omc_items:aywenite
                setProgressionOrb(player, SCULK_PLAINS_ORB);
            }
        }
    }

    @EventHandler
    public void onCraft(AltarCraftingEvent event) {
        DreamItem item = event.getCraftedItem();
        if (item == null) return;

        if (!item.getName().equals("omc_dream:ame_orb")) return;

        setProgressionOrb(event.getPlayer(), SOUL_FOREST_ORB);
    }

    public static void setProgressionOrb(Player player, int progressionOrb) {
        DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);
        if (cache != null) {
            if (cache.getProgressionOrb() < progressionOrb) {
                cache.setProgressionOrb(progressionOrb);
                DreamManager.saveDreamPlayerData(cache);
            }
        } else {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;
            DreamManager.saveDreamPlayerData(dreamPlayer);
            setProgressionOrb(player, progressionOrb);
        }
    }
}
