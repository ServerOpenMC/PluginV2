package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.LuckPermsAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncChatListener implements Listener {

    private final OMCPlugin plugin;
    private final LuckPerms luckperms;

    public AsyncChatListener(OMCPlugin plugin) {
        this.plugin = plugin;
        this.luckperms = LuckPermsAPI.getApi();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final CachedMetaData metaData = this.luckperms.getPlayerAdapter(Player.class).getMetaData(player);

        String rawMessage = plugin.getConfig().getString("chat.message", "{prefix}{name}§7: {message}")
                .replace("{prefix}", LuckPermsAPI.getFormattedPAPIPrefix(player))
                .replace("{suffix}", metaData.getSuffix() != null ? metaData.getSuffix() : "")
                .replace("{name}", player.getName())
                .replace("{message}", PlainTextComponentSerializer.plainText().serialize(event.message()));

        final String formattedMessage = colorize(translateHexColorCodes(rawMessage));

        event.renderer((source, sourceDisplayName, message, viewer) -> Component.text(formattedMessage));
    }

    private String colorize(final String message) {
        return message.replace("&", "§");
    }

    private String translateHexColorCodes(final String message) {
        final char colorChar = NamedTextColor.HEX_CHARACTER;

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
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        OMCPlugin.getInstance().getServer().getOnlinePlayers().forEach(player -> {
            if (event.getMessage().contains(player.getName())) {
                player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1, 1);
            }
        });
    }
}
