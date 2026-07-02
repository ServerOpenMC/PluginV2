package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteTrade;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerObtainOrb implements Listener {
    private final int SCULK_PLAINS_ORB = 1;
    public static final int SOUL_FOREST_ORB = 2;
    private final int CLOUD_CASTLE_ORB = 3;
    private final int MUD_BEACH_ORB = 4;
    private final int GLACITE_GROTTO_ORB = 5;

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
        if (dreamItem == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!dreamItem.getId().equals(DreamItemRegistry.DOMINATION_ORB.getId())) return;

        setProgressionOrb(player, SCULK_PLAINS_ORB, DreamBiome.SOUL_FOREST);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.TRIAL_SPAWNER_DETECTION, 15, 15, 0.5,  null);
    }

    @EventHandler
    public void onAltarCraft(AltarCraftingEvent event) {
        DreamItem item = event.getCraftedItem();

        if (item == null) return;
        if (!item.getId().equals(DreamItemRegistry.SOUL_ORB.getId())) return;

        Player player = event.getPlayer();

        setProgressionOrb(player, SOUL_FOREST_ORB, DreamBiome.CLOUD_LAND);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.SCULK_SOUL, 15, 15, 0.5,  null);
    }

    @EventHandler
    public void onCloudOrbDispense(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack dispensed = event.getItem().getItemStack();

        DreamItem dreamItem = DreamItemRegistry.getByItemStack(dispensed);

        if (dreamItem == null) return;
        if (!dreamItem.getId().equals(DreamItemRegistry.CLOUD_ORB.getId())) return;

        setProgressionOrb(player, CLOUD_CASTLE_ORB, DreamBiome.MUD_BEACH);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.GUST, 15, 15, 0.5,  null);
    }

    @EventHandler
    public void onMetalDetectorLoot(MetalDetectorLootEvent event) {
        Player player = event.getPlayer();

        for (CustomLoot loot : event.getLoot()) {
            if (!(loot instanceof ItemLoot itemLoot)) continue;

            for (ItemStack item : itemLoot.getItems()) {
                DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);

                if (dreamItem == null) continue;
                if (!dreamItem.getId().equals(DreamItemRegistry.MUD_ORB.getId())) continue;

                setProgressionOrb(player, MUD_BEACH_ORB, DreamBiome.GLACITE_GROTTO);

                // * SFX
                player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
                ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.ASH, 15, 15, 0.5, null);
                break;
            }
        }
    }

    @EventHandler
    public void onGlaciteTrade(GlaciteTradeEvent event) {
        Player player = event.getPlayer();

        if (!event.getTrade().equals(GlaciteTrade.ORB_GLACITE)) return;

        setProgressionOrb(player, GLACITE_GROTTO_ORB, null);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.SNOWFLAKE, 15, 15, 0.5,  null);
    }

    public static void setProgressionOrb(Player player, int progressionOrb, DreamBiome unlocked) {
        DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);

        if (cache == null) {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            DreamManager.saveDreamPlayerData(dreamPlayer);
            cache = DreamManager.getCacheDreamPlayer(player);
            if (cache == null) {
                OMCLogger.warn("player ({}) had no cache even after saving it. [PlayerObtainOrb#setProgressionOrb]", player.getUniqueId());
                return;
            }
        }

        int current = cache.getProgressionOrb();

        if (current >= progressionOrb) return;

        cache.setProgressionOrb(progressionOrb);
        DreamManager.saveDreamPlayerData(cache);
        if (unlocked != null)
            sendMessageProgression(player, unlocked);
        sendBroadcastMessageOrb(player, progressionOrb);
    }

    private static void sendBroadcastMessageOrb(Player player, int progressionOrb) {
        Component orb = switch (progressionOrb) {
            case 1 -> TranslationManager.translation("feature.dream.item.domination_orb.name");
            case 2 -> TranslationManager.translation("feature.dream.item.ame_orb.name");
            case 3 -> TranslationManager.translation("feature.dream.item.cloud_orb.name");
            case 4 -> TranslationManager.translation("feature.dream.item.mud_orb.name");
            case 5 -> TranslationManager.translation("feature.dream.item.glacite_orb.name");
            default -> TranslationManager.translation("feature.dream.item.unknown_orb.name");
        };

        MessagesManager.broadcastMessage(player.getWorld(), TranslationManager.translation(
                "feature.dream.orb.message.obtained",
                Component.text(player.getName()),
                orb
        ), Prefix.DREAM, MessageType.INFO);
    }

    private static void sendMessageProgression(Player player, DreamBiome biome) {
        Component biomeName = switch (biome) {
            case SOUL_FOREST -> TranslationManager.translation("feature.dream.biome.progression.soul_forest");
            case CLOUD_LAND -> TranslationManager.translation("feature.dream.biome.progression.cloud_land");
            case MUD_BEACH -> TranslationManager.translation("feature.dream.biome.progression.mud_beach");
            case GLACITE_GROTTO -> TranslationManager.translation("feature.dream.biome.progression.glacite_grotto");
            default -> TranslationManager.translation("feature.dream.biome.progression.unknown");
        };

        MessagesManager.sendMessage(player, TranslationManager.translation(
                "feature.dream.biome.message.unlocked",
                biomeName
        ), Prefix.DREAM, MessageType.SUCCESS, false);
    }
}
