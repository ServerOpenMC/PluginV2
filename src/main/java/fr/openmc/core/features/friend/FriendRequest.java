package fr.openmc.core.features.friend;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendRequest extends BukkitRunnable {

    private final FriendManager friendManager;
    private final OMCPlugin plugin;
    private final List<UUID> uuids = new ArrayList<>();

    public FriendRequest(FriendManager friendManager, OMCPlugin plugin, UUID firstUUID, UUID secondUUID) {
        this.friendManager = friendManager;
        this.plugin = plugin;
        this.uuids.add(firstUUID);
        this.uuids.add(secondUUID);
    }

    public UUID getSenderUUID() {
        return uuids.get(0);
    }

    public UUID getReceiverUUID() {
        return uuids.get(1);
    }

    public boolean containsUUID(UUID uuid) {
        return uuids.contains(uuid);
    }

    public void sendRequest() {
        this.runTaskLater(plugin, 6000L);
    }

    public void removeRequest() {
        sendExpiryMessage(uuids.get(0));
        sendExpiryMessage(uuids.get(1));
        friendManager.removeRequest(this);
        uuids.clear();
    }

    private void sendExpiryMessage(UUID playerUUID) {
        if (isPlayerOnline(playerUUID)) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                MessagesManager.sendMessage(player, Component.text("§cLa demande d'ami a expiré."), Prefix.FRIEND, MessageType.INFO, true);
            }
        }
    }

    @Override
    public void run() {
        removeRequest();
    }

    private boolean isPlayerOnline(UUID playerUUID) {
        return Bukkit.getOfflinePlayer(playerUUID).isOnline();
    }
}
