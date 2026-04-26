package fr.openmc.core.hooks;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.core.bootstrap.hooks.ApiHook;
import fr.openmc.core.bootstrap.hooks.Hooks;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LuckPermsHook extends Hooks implements ApiHook<LuckPerms> {
    @Getter
    private static LuckPerms api;

    public static boolean isEnable() {
        return Hooks.isEnabled(LuckPermsHook.class);
    }

    @Override
    protected String getPluginName() {
        return "LuckPerms";
    }

    @Override
    public Class<LuckPerms> apiClass() {
        return LuckPerms.class;
    }

    @Override
    public void init() {
        api = ApiHook.super.api();
    }

    /**
     * Retourne le garde d'une personne
     */
    public static String getPrefix(Player player) {
        if (!isEnable()) return "";

        User user = getApi().getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";

        String prefix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        return Objects.requireNonNullElse(prefix, "");
    }

    public static String getFormattedPAPIPrefix(Player player) {
        if (!isEnable()) return "";

        String prefix = getPrefix(player);
        if (prefix == null || prefix.isEmpty()) return "";
        String formattedPrefix = prefix.replace("&", "§");

        if (ItemsAdderHook.isEnable()) {
            return FontImageWrapper.replaceFontImages(formattedPrefix);
        }
        return formattedPrefix;
    }

    public static @NotNull Component getFormattedPAPIPrefix(Group group) {
        if (!isEnable()) return Component.empty();

        String prefix = group.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        if (prefix == null || prefix.isEmpty()) return Component.empty();

        String formattedPrefix = prefix.replace("&", "§");

        String finalPrefix = ItemsAdderHook.isEnable() ? FontImageWrapper.replaceFontImages(formattedPrefix) : formattedPrefix;

        return LegacyComponentSerializer.legacySection().deserialize(finalPrefix);
    }
}
