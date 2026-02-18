package fr.openmc.core.features.dream.listeners.orb;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteTrade;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
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
        if (!(recipe instanceof Keyed keyed)) return;

        NamespacedKey key = keyed.getKey();
        if (key.toString().contains("omc_dream") && key.toString().contains("domination_orb")) { // contains beacuse key is ex zzzfake_omc_items:aywenite
            setProgressionOrb(player, SCULK_PLAINS_ORB, DreamBiome.SOUL_FOREST);
        }
    }

    @EventHandler
    public void onAltarCraft(AltarCraftingEvent event) {
        DreamItem item = event.getCraftedItem();

        if (item == null) return;
        if (!item.getName().equals("omc_dream:ame_orb")) return;

        Player player = event.getPlayer();

        setProgressionOrb(player, SOUL_FOREST_ORB, DreamBiome.CLOUD_LAND);
    }

    @EventHandler
    public void onCloudOrbDispense(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack dispensed = event.getItem().getItemStack();

        DreamItem dreamItem = DreamItemRegistry.getByItemStack(dispensed);

        if (dreamItem == null) return;
        if (!dreamItem.getName().equals("omc_dream:cloud_orb")) return;

        setProgressionOrb(player, CLOUD_CASTLE_ORB, DreamBiome.MUD_BEACH);
    }

    @EventHandler
    public void onMetalDetectorLoot(MetalDetectorLootEvent event) {
        Player player = event.getPlayer();

        for (ItemStack item : event.getLoot()) {
            DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);

            if (dreamItem == null) continue;
            if (!dreamItem.getName().equals("omc_dream:mud_orb")) continue;

            setProgressionOrb(player, MUD_BEACH_ORB, DreamBiome.GLACITE_GROTTO);
            break;
        }
    }

    @EventHandler
    public void onGlaciteTrade(GlaciteTradeEvent event) {
        Player player = event.getPlayer();

        if (!event.getTrade().equals(GlaciteTrade.ORB_GLACITE)) return;

        setProgressionOrb(player, GLACITE_GROTTO_ORB, null);
    }

    public static void setProgressionOrb(Player player, int progressionOrb, DreamBiome unlocked) {
        DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);

        if (cache == null) {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            DreamManager.saveDreamPlayerData(dreamPlayer);
            cache = DreamManager.getCacheDreamPlayer(player);
            if (cache == null) {
                OMCPlugin.getInstance().getSLF4JLogger().warn("player ({}) had no cache even after saving it. [PlayerObtainOrb#setProgressionOrb]", player.getUniqueId());
                return;
            }
        }

        int current = cache.getProgressionOrb();

        if (current >= progressionOrb) return;

        cache.setProgressionOrb(progressionOrb);
        DreamManager.saveDreamPlayerData(cache);
        if (unlocked != null)
            sendMessageProgression(player, unlocked);
    }

    private static void sendMessageProgression(Player player, DreamBiome biome) {
        String strBiome;
        switch (biome) {
            case SOUL_FOREST -> strBiome = "la Forêt des Âmes";
            case CLOUD_LAND -> strBiome = "le Château des Nuages";
            case MUD_BEACH -> strBiome = "la Plage de Boue";
            case GLACITE_GROTTO -> strBiome = "la Grotte de Glacite";
            default -> strBiome = "Inconnu";
        }

        MessagesManager.sendMessage(player, Component.text("Vous avez débloqué " + strBiome + " !"), Prefix.DREAM, MessageType.SUCCESS, false);
    }
}
