package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.api.hooks.LuckPermsHook;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncChatListener implements Listener {

    private final OMCPlugin plugin;
    private final LuckPerms luckperms;

    public AsyncChatListener(OMCPlugin plugin) {
        this.plugin = plugin;
        this.luckperms = LuckPermsHook.getApi();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final CachedMetaData metaData = this.luckperms.getPlayerAdapter(Player.class).getMetaData(player);

        String message = ((TextComponent) event.message()).content();
        String rawMessage = plugin.getConfig().getString("chat.message", "{prefix}{name}§7: {message}")
                .replace("{prefix}", LuckPermsHook.getFormattedPAPIPrefix(player))
                .replace("{suffix}", metaData.getSuffix() != null ? metaData.getSuffix() : "")
                .replace("{name}", player.getName())
                .replace("{message}", message);

        final String formattedMessage = colorize(translateHexColorCodes(rawMessage));

        event.renderer((source, sourceDisplayName, component, viewer) -> Component.text(formattedMessage));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (message.contains(p.getName()))
                p.playSound(p.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1, 1);
        }
    }

    private String colorize(final String message) {
        return message.replace("&", "§");
    }

    private String translateHexColorCodes(final String message) {
        final char colorChar = TextColor.HEX_CHARACTER;

        final Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(message);
        final StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }
}
