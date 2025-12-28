package fr.openmc.core.features.dream;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DreamUtils {
    public static boolean isInDreamWorld(Player player) {
        return isDreamWorld(player.getLocation());
    }

    public static boolean isDreamWorld(Location loc) {
        return isDreamWorld(loc.getWorld());
    }

    public static boolean isDreamWorld(World world) {
        return world.getName().equals(DreamDimensionManager.DIMENSION_NAME);
    }

    public static boolean isInDream(Player player) {
        if (!isInDreamWorld(player)) return false;

        return DreamManager.getDreamPlayer(player) != null;
    }

    public static void addDreamTime(Player player, Long timeToAdd, boolean sendMessage) {
        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;
        dreamPlayer.addTime(timeToAdd);
        if (sendMessage)
            MessagesManager.sendMessage(player, Component.text("Vous avez perdu §a" + DateUtils.convertSecondToTime(timeToAdd) + " §fcar vous avez pris des dégats !"), Prefix.DREAM, MessageType.WARNING, false);

    }

    public static void removeDreamTime(Player player, Long timeToRemove, boolean sendMessage) {
        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;
        dreamPlayer.removeTime(timeToRemove);
        if (sendMessage)
            MessagesManager.sendMessage(player, Component.text("Vous avez perdu §a" + DateUtils.convertSecondToTime(timeToRemove) + " §fcar vous avez pris des dégats !"), Prefix.DREAM, MessageType.WARNING, false);

    }
}
