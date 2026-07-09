package fr.openmc.core.features.dream;

import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.message.time_lost_damage", Component.text(DateUtils.convertSecondToTime(timeToAdd)).color(NamedTextColor.GREEN)), Prefix.DREAM, MessageType.WARNING, false);

    }

    public static void removeDreamTime(Player player, Long timeToRemove, boolean sendMessage) {
        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;
		if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        dreamPlayer.removeTime(timeToRemove);
        if (sendMessage)
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.message.time_lost_damage", Component.text(DateUtils.convertSecondToTime(timeToRemove)).color(NamedTextColor.GREEN)), Prefix.DREAM, MessageType.WARNING, false);
    }
}
