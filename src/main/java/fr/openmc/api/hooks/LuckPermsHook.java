package fr.openmc.api.hooks;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LuckPermsHook {
    @Getter private static LuckPerms api;
    @Getter
    private static boolean hasLuckPerms;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null)
            return;

        hasLuckPerms = true;
        api = OMCPlugin.getInstance().getServer().getServicesManager().load(LuckPerms.class);
    }

    /**
     * Retourne le garde d'une personne
     */
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

        return FontImageWrapper.replaceFontImages(formattedPrefix);
    }

    public static @NotNull Component getFormattedPAPIPrefix(Group group) {
        if (!hasLuckPerms) return Component.empty();

        String prefix = group.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        if (prefix == null || prefix.isEmpty()) return Component.empty();

        String formattedPrefix = prefix.replace("&", "§");

        return Component.text(FontImageWrapper.replaceFontImages(formattedPrefix));
    }
}
