package fr.openmc.core.features.events.contents.halloween.festival.commands;

import fr.openmc.core.features.events.contents.halloween.festival.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.festival.npc.WitchNPCS;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchShopMainMenu;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchShopStoreMenu;
import fr.openmc.core.features.events.contents.halloween.pumpkin.managers.HalloweenManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("halloween_festival")
public class HalloweenFestivalCommands {

    // two weeks 14L * 24 * 60 * 60 * 1000 = 1_209_600_000

    @Subcommand("start")
    @CommandPermission("omc.admins.commands.halloween_festival.start")
    public void startHalloweenFestival(long duration) {
        HalloweenFestivalManager.startEvent(duration);
    }

    @Subcommand("force-end")
    @CommandPermission("omc.admins.commands.halloween_festival.force-end")
    public void endHalloweenFestival() {
        HalloweenFestivalManager.forceStopEvent();
    }

    @Subcommand("store")
    public void openStoreMenu(Player player) {
        new WitchShopStoreMenu(player, 1).open();
    }

//    @Subcommand("place store")
//    @CommandPermission("omc.admins.commands.halloween_festival.place.store")
//    public void placeStore(Player player) {
//        HalloweenFestivalManager.placeWitchStore(player.getLocation());
//    }
//
//    @Subcommand("remove store")
//    @CommandPermission("omc.admins.commands.halloween_festival.remove.store")
//    public void removeStore(Player player) {
//        HalloweenFestivalManager.removeWitchStore();
//    }

    @Subcommand("place house")
    @CommandPermission("omc.admins.commands.halloween_festival.place.house")
    public void placeHouse(Player player) {
        HalloweenFestivalManager.placeTerrorHouse();
    }

    @Subcommand("remove house")
    @CommandPermission("omc.admins.commands.halloween_festival.remove.house")
    public void removeHouse(Player player) {
        HalloweenFestivalManager.removeTerrorHouse();
    }

    @Subcommand("place npc")
    @CommandPermission("omc.admins.commands.halloween_festival.place.npc")
    public void placeNPC(Player player) {
        WitchNPCS.createNPC(player.getLocation());
    }

    @Subcommand("remove npc")
    @CommandPermission("omc.admins.commands.halloween_festival.remove.npc")
    public void removeNPC(Player player) {
        WitchNPCS.removeNPCS();
    }
}
