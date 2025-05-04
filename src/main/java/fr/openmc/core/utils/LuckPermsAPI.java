package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.listeners.UserUnloadListener;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LuckPermsAPI {
    @Getter private static LuckPerms api;
    private static boolean hasLuckPerms;

    public LuckPermsAPI() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            hasLuckPerms = false;
            return;
        } else {
            hasLuckPerms = true;
        }

        api = OMCPlugin.getInstance().getServer().getServicesManager().load(LuckPerms.class);

        if (api != null) {
            UserUnloadListener.register(OMCPlugin.getInstance(), api);
        }
    }

    public static boolean hasLuckPerms() {
        return hasLuckPerms;
    }

    public static String getPrefix(Player player) {
        if (!hasLuckPerms) return "";

        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";

        String prefix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        return Objects.requireNonNullElse(prefix, "");
    }

    public static String getFormattedPAPIPrefix(Player player) {
        if (!hasLuckPerms) return "";

        String prefix = getPrefix(player);
        if (prefix == null || prefix.isEmpty()) return "";
        String formattedPrefix = prefix.replace("&", "§");
        formattedPrefix = formattedPrefix.replaceAll(":([a-zA-Z0-9_]+):", "%img_$1%");

        return PlaceholderAPI.setPlaceholders(player, formattedPrefix) + " ";
    }

    public static @NotNull Component getFormattedPAPIPrefix(Group group) {
        if (!hasLuckPerms) return Component.empty();

        String prefix = group.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        if (prefix == null || prefix.isEmpty()) return Component.empty();

        String formattedPrefix = prefix.replace("&", "§");
        formattedPrefix = formattedPrefix.replaceAll(":([a-zA-Z0-9_]+):", "%img_$1%");

        return Component.text(PlaceholderAPI.setPlaceholders(null, formattedPrefix) + " ");
    }
}
