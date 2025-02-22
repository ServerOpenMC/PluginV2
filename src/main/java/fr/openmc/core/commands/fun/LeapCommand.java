package fr.openmc.core.commands.fun;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.entity.Boat;

import java.util.concurrent.TimeUnit;

/**
 * THE Leap command.
 * <p>
 * Usage: /leap
 */

public final class LeapCommand {
    @Command("leap")
    @Description("Envolez-vous vers d'autres cieux telle la team rocket.'")
    @Cooldown(value = 5, unit = TimeUnit.MINUTES)
    public void onCommand(Player player) {
        player.sendMessage("Vers l'infini et au delà!'");

        // Make the player jump
        final Vector currentVelocity = player.getVelocity();
        currentVelocity.setY(10d);

        player.setVelocity(currentVelocity);
    }
}
