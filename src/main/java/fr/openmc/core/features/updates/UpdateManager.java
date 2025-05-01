package fr.openmc.core.features.updates;

import org.bukkit.entity.Player;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class UpdateManager {
    @Getter
    static UpdateManager instance;
    @Getter
    Component message;

    public UpdateManager() {
        instance = this;

        String version = OMCPlugin.getInstance().getDescription().getVersion();
        String milestoneUrl = "https://github.com/ServerOpenMC/PluginV2/releases/";

        message = Component.text("Vous jouez actuellement sur la version ")
                .append(Component.text(version).clickEvent(ClickEvent.openUrl(milestoneUrl)))
                .append(Component.text(" du plugin §aOpenMC§r."))
                .append(Component.text(" Cliquez ici pour voir les changements.")
                        .clickEvent(ClickEvent.openUrl(milestoneUrl)));
    }

    public void sendUpdateMessage(Player player) {
        MessagesManager.sendMessage(player, message, Prefix.OPENMC, MessageType.INFO, false);
    }
}
