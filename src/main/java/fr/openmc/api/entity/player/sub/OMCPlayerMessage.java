package fr.openmc.api.entity.player.sub;

import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class OMCPlayerMessage extends OMCPlayerFeat {
    public OMCPlayerMessage(Player player) {
        super(player);
    }

    public void send(Component message, Prefix prefix, MessageType type, boolean sound) {
        MessagesManager.sendMessage(getPlayer(), message, prefix, type, sound);
    }

    public void send(Component message, Prefix prefix, MessageType type) {
        send(message, prefix, type, false);
    }

    public void send(Component message, Prefix prefix) {
        send(message, prefix, MessageType.NONE, false);
    }

    public void send(Component message, MessageType type) {
        send(message, Prefix.OPENMC, type, false);
    }

    public void send(Component message) {
        send(message, Prefix.OPENMC, MessageType.NONE, false);
    }

    public void sendSuccess(Component message, Prefix prefix, boolean sound) {
        send(message, prefix, MessageType.SUCCESS, sound);
    }

    public void sendSuccess(Component message, boolean sound) {
        sendSuccess(message, Prefix.OPENMC, sound);
    }

    public void sendSuccess(Component message, Prefix prefix) {
        sendSuccess(message, prefix, false);
    }

    public void sendSuccess(Component message) {
        sendSuccess(message, true);
    }

    public void sendError(Component message, Prefix prefix, boolean sound) {
        send(message, prefix, MessageType.ERROR, sound);
    }

    public void sendError(Component message, Prefix prefix) {
        sendError(message, prefix, false);
    }

    public void sendError(Component message, boolean sound) {
        sendError(message, Prefix.OPENMC, sound);
    }

    public void sendError(Component message) {
        sendError(message, true);
    }

    public void sendWarning(Component message, Prefix prefix, boolean sound) {
        send(message, prefix, MessageType.WARNING, sound);
    }

    public void sendWarning(Component message, boolean sound) {
        sendWarning(message, Prefix.OPENMC, sound);
    }

    public void sendWarning(Component message, Prefix prefix) {
        sendWarning(message, prefix, false);
    }

    public void sendWarning(Component message) {
        sendWarning(message, true);
    }

    public void sendInfo(Component message, Prefix prefix, boolean sound) {
        send(message, prefix, MessageType.INFO, sound);
    }

    public void sendInfo(Component message, Prefix prefix) {
        send(message, prefix, MessageType.INFO, false);
    }

    public void sendInfo(Component message, boolean sound) {
        send(message, Prefix.OPENMC, MessageType.INFO, sound);
    }

    public void sendInfo(Component message) {
        sendInfo(message, true);
    }
}
