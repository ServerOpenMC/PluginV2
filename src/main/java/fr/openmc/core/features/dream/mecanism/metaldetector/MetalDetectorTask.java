package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.text.DirectionUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Getter
@Setter
public class MetalDetectorTask extends BukkitRunnable {

    private final Player player;
    private final UUID uuid;
    private Location chestLocation;

    public MetalDetectorTask(Player player, Location chestLocation) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.chestLocation = chestLocation;
    }

    @Override
    public void run() {
        if (!player.isOnline() || !DreamUtils.isInDream(player)) {
            this.cancel();
            return;
        }

        DreamItem item = DreamItemRegistry.getByItemStack(player.getInventory().getItemInMainHand());
        if (item == null) return;
        if (!item.getId().equals("omc_dream:metal_detector")) return;

        double distance = player.getLocation().distance(chestLocation);
        String direction = DirectionUtils.getDirectionArrow(player, chestLocation);
        player.sendActionBar(
                TranslationManager.translation(
                        "feature.dream.actionbar.distance",
                        Component.text((int) distance).color(NamedTextColor.GOLD),
                        Component.text(direction)
                )
        );

        if (distance <= 3) {
            Block block = chestLocation.getBlock();
            if (block.getType() != Material.CHEST) {
                block.setType(Material.CHEST);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }
        }
    }
}
