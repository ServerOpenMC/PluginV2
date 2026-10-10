package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.registry.items.options.UsableItem;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MeteoWand extends DreamItem implements UsableItem {
    private static final long COOLDOWN_METEO_WAND = 8 * 60 * 60 * 1000L; // 2 jours

    public MeteoWand() {
        super(new DreamItemMeta(
                "omc_dream:meteo_wand",
                TranslationManager.translation("feature.dream.item.meteo_wand.name"),
                DreamRarity.LEGENDARY,
                Material.PAPER,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }

    @Override
    public void onRightClick(OMCPlayer player, PlayerInteractEvent event) {
        World world = player.getWorld();
        if (!world.getName().equals("world")) {
            player.message().send(TranslationManager.translation("feature.dream.item.meteo_wand.message.must_be_overworld"), Prefix.OPENMC, MessageType.WARNING, false);
            return;
        }

        if (!player.cooldown().isReady("player:meteo_wand")) {
            player.message().send(TranslationManager.translation("feature.dream.item.meteo_wand.message.cooldown",
                    Component.text(DateUtils.convertMillisToTime(player.cooldown().getRemaining("player:meteo_wand"))).color(NamedTextColor.GREEN)),
                    Prefix.OPENMC, MessageType.ERROR, false);
            return;
        }

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 12) {
                    cancel();
                    return;
                }

                long newTime = (world.getTime() + 1000L) % 24000L;
                world.setTime(newTime);
                world.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 10f, 0.3f);

                count++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 40L);

        player.message().send(TranslationManager.translation("feature.dream.item.meteo_wand.message.success"), Prefix.OPENMC, MessageType.SUCCESS, false);
        player.cooldown().use("player:meteo_wand", COOLDOWN_METEO_WAND);
    }
}
