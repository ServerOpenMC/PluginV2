package fr.openmc.core.features.animations;

import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.animations.listeners.EmoteListener;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class DebugAnimationCommand {
    @Command("debug animation resetHead")
    @CommandPermission("omc.debug.animation.resetHead")
    public void resetHead(Player player) {
        EmoteListener.sendCamera(player, player);
    }
}