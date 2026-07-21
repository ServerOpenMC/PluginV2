package fr.openmc.core.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class RngUtils {
    /**
     * Renvoie un son aléatoire en fonction de la chance donnée.
     * @param player le joueur ciblé (et le monde ciblé si il y a une basse probabilité
     * @param chance la chance eu
     */
    public static void sendSoundRng(Player player, double chance) {
        if (chance <= 0.001) { // 0.1%
            player.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 0.1f));
            player.getWorld().playSound(player.getLocation(), "minecraft:entity.ender_dragon.death", 1f, 0.1f);
        } else if (chance <= 0.05) { // 5%
            player.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 0.5f));
        } else if (chance <= 0.1) { // 10%
            player.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 1.0f));
        } else if (chance <= 0.25) { // 25%
            player.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 1.3f));
        } else {
            player.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 2f, 2f));
        }
    }
}
