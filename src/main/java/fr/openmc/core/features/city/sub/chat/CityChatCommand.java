package fr.openmc.core.features.city.sub.chat;

import fr.openmc.core.OMCRegistry;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityChatCommand {
    private final CityChatManager cityChatManager;

    public CityChatCommand() {
        this.cityChatManager = OMCRegistry.CITY_FEATURES.CITY_CHAT;
    }

    @Command({"cc", "city chat", "ville chat"})
    @CommandPermission("omc.commands.city.chat")
    @Description("Activer ou désactiver le chat de ville")
    public void onCityChat(Player sender) {
        if (!cityChatManager.isCityChatMember(sender)) {
            cityChatManager.addCityChatMember(sender);
        } else {
            cityChatManager.removeCityChatMember(sender);
        }
    }
}
