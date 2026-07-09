package fr.openmc.core.features.mailboxes.utils;


import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class MailboxUtils {
    public static Component getPlayerName(OfflinePlayer player) {
        String pName = player.getName();
        Component displayName = pName == null
                ? TranslationManager.translation("feature.mailboxes.player.unknown")
                : Component.text(pName);
        return Component.text("⬤ ", player.isConnected() ? NamedTextColor.DARK_GREEN : NamedTextColor.DARK_RED)
                        .append(displayName.color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .decoration(TextDecoration.ITALIC, false);
    }

    public static ItemStack getHead(OfflinePlayer player, Component name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.displayName(nonItalic(name));
        head.setItemMeta(meta);
        return head;
    }

    public static Component colorText(String text, NamedTextColor color, boolean nonItalic) {
        Component component = Component.text(text, color);
        return nonItalic ? nonItalic(component) : component;
    }

    public static Component nonItalic(Component name) {
        return name.decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull HoverEvent<Component> getHoverEvent(String message) {
        return HoverEvent.showText(Component.text(message, NamedTextColor.GRAY));
    }

    public static ItemStack getHead(OfflinePlayer player) {
        return getHead(player, getPlayerName(player));
    }
}
