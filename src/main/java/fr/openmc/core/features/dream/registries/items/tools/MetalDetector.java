package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorTask;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.features.dream.models.registry.items.DreamUsableItem;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorListener.findRandomChestLocation;
import static fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorManager.hiddenChests;

public class MetalDetector extends DreamUsableItem {
    public MetalDetector(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.EPIC;
    }

    @Override
    public boolean isTransferable() {
        return false;
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.STICK);

        item.getItemMeta().itemName(Component.text("Détecteur à métaux"));
        return item;
    }

    @Override
    public void onSneakClick(Player player, PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        World world = player.getWorld();
        if (!DreamUtils.isDreamWorld(world)) {
            MessagesManager.sendMessage(player, Component.text("Vous devez être dans la dimension des rêves pour reset la position du coffre"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        UUID playerUUID = player.getUniqueId();

        if (!hiddenChests.containsKey(playerUUID)) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        if (player.hasCooldown(item)) {
            MessagesManager.sendMessage(player, Component.text("Vous devez attendre avant de reset la position du coffre"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        player.setCooldown(item, 15 * 20);

        MetalDetectorTask oldTask = hiddenChests.get(playerUUID);
        oldTask.getChestLocation().getBlock().setType(Material.MUD);
        Location newLoc = findRandomChestLocation(player.getLocation());
        MetalDetectorTask newTask = new MetalDetectorTask(player, newLoc);
        newTask.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
        hiddenChests.put(playerUUID, newTask);
        oldTask.cancel();
    }
}
