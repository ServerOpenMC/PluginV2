package fr.openmc.core.features.events.contents.halloween.commands;

import fr.openmc.core.features.events.contents.halloween.halloween.shop.menu.WitchShopMainMenu;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("halloween")
@CommandPermission("omc.admins.commands.halloween")
public class HalloweenCommands {
    @Subcommand("end")
    public void endHalloweenCommand() {
        HalloweenManager.endEvent();
    }

    @Subcommand("test")
    public void tst(Player player) {
        new WitchShopMainMenu(player).open();
    }
}
