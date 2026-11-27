package fr.openmc.core.features.events.contents.halloween.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import fr.openmc.core.features.events.contents.halloween.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.npc.WitchNPCS;
import fr.openmc.core.features.events.contents.halloween.shop.menu.WitchShopStoreMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.Duration;

@Command("halloween")
public class HalloweenFestivalCommands {

    // two weeks 14L * 24 * 60 * 60 * 1000 = 1_209_600_000

    @Subcommand("start")
    @CommandPermission("omc.admins.commands.halloween.start")
    public void startHalloweenEvent(long duration) {
        DepositEventManager.start(OMCRegistry.DEPOSIT_EVENTS.HALLOWEEN, Duration.ofMillis(duration));
        HalloweenFestivalManager.startEvent(duration);
    }

    @Subcommand("force-end")
    @CommandPermission("omc.admins.commands.halloween.force-end")
    public void endHalloweenFestival() {
        DepositEventManager.end(OMCRegistry.DEPOSIT_EVENTS.HALLOWEEN);
        HalloweenFestivalManager.forceStopEvent();
    }

    @Subcommand("store")
    public void openStoreMenu(Player player) {
        new WitchShopStoreMenu(player, 1).open();
    }

    @Subcommand("place house")
    @CommandPermission("omc.admins.commands.halloween.place.house")
    public void placeHouse(Player player) {
        HalloweenFestivalManager.placeTerrorHouse();
    }

    @Subcommand("remove house")
    @CommandPermission("omc.admins.commands.halloween.remove.house")
    public void removeHouse(Player player) {
        HalloweenFestivalManager.removeTerrorHouse();
    }

    @Subcommand("place npc")
    @CommandPermission("omc.admins.commands.halloween.place.npc")
    public void placeNPC(Player player) {
        WitchNPCS.createNPC(player.getLocation());
    }

    @Subcommand("remove npc")
    @CommandPermission("omc.admins.commands.halloween.remove.npc")
    public void removeNPC(Player player) {
        WitchNPCS.removeNPCS();
    }
}
