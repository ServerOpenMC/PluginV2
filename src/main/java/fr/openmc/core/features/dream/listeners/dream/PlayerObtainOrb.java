package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.events.AltarCraftingEvent;
import fr.openmc.core.features.dream.events.GlaciteTradeEvent;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.mecanism.tradernpc.GlaciteTrade;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamBiome;
import fr.openmc.core.features.dream.registries.DreamOrb;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerObtainOrb implements Listener {
    private final DreamManager dreamManager;

    public PlayerObtainOrb(DreamManager manager) {
        this.dreamManager = manager;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        DreamItem dreamItem = OMCRegistry.DREAM_ITEM.getByItemStack(item);
        if (dreamItem == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!dreamItem.getId().equals(OMCRegistry.DREAM_ITEM.DOMINATION_ORB.getId())) return;

        dreamManager.setProgressionOrb(player, DreamOrb.SCULK_PLAINS_ORB.getStepInt(), DreamBiome.SOUL_FOREST);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.TRIAL_SPAWNER_DETECTION, 15, 15, 0.5,  null);
    }

    @EventHandler
    public void onAltarCraft(AltarCraftingEvent event) {
        DreamItem item = event.getCraftedItem();

        if (item == null) return;
        if (!item.getId().equals(OMCRegistry.DREAM_ITEM.SOUL_ORB.getId())) return;

        Player player = event.getPlayer();

        dreamManager.setProgressionOrb(player, DreamOrb.SOUL_FOREST_ORB.getStepInt(), DreamBiome.CLOUD_LAND);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.SCULK_SOUL, 15, 15, 0.5,  null);
    }

    @EventHandler
    public void onCloudOrbDispense(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack dispensed = event.getItem().getItemStack();

        DreamItem dreamItem = OMCRegistry.DREAM_ITEM.getByItemStack(dispensed);

        if (dreamItem == null) return;
        if (!dreamItem.getId().equals(OMCRegistry.DREAM_ITEM.CLOUD_ORB.getId())) return;

        dreamManager.setProgressionOrb(player, DreamOrb.CLOUD_CASTLE_ORB.getStepInt(), DreamBiome.MUD_BEACH);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.GUST, 15, 15, 0.5,  null);
    }

    @EventHandler
    public void onMetalDetectorLoot(MetalDetectorLootEvent event) {
        Player player = event.getPlayer();

        for (CustomLoot loot : event.getLoot()) {
            if (!(loot instanceof ItemLoot itemLoot)) continue;

            DreamItem dreamItem = OMCRegistry.DREAM_ITEM.getByItemStack(itemLoot.getItemLootWithAmount());

            if (dreamItem == null) continue;
            if (!dreamItem.getId().equals(OMCRegistry.DREAM_ITEM.MUD_ORB.getId())) continue;

            dreamManager.setProgressionOrb(player, DreamOrb.MUD_BEACH_ORB.getStepInt(), DreamBiome.GLACITE_GROTTO);

            // * SFX
            player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
            ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.ASH, 15, 15, 0.5, null);
        }
    }

    @EventHandler
    public void onGlaciteTrade(GlaciteTradeEvent event) {
        Player player = event.getPlayer();

        if (!event.getTrade().equals(GlaciteTrade.ORB_GLACITE)) return;

        dreamManager.setProgressionOrb(player, DreamOrb.GLACITE_GROTTO_ORB.getStepInt(), null);

        // * SFX
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.wither.spawn", 1f, 2f);
        ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.SNOWFLAKE, 15, 15, 0.5,  null);
    }
}
