package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Socials {
    private String removeProtocol(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        String modifiedUrl = url.replaceFirst("^[a-zA-Z]+://", "");

        if (modifiedUrl.endsWith("/")) {
            modifiedUrl = modifiedUrl.substring(0, modifiedUrl.length() - 1);
        }

        return modifiedUrl;
    }


    private Component parseText(String message, String link) {
        return Component.text(message).append(
                Component.text(removeProtocol(link))
                        .clickEvent(ClickEvent.openUrl(link))
                        .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour accéder")))
                        .color(NamedTextColor.GOLD)
        );
    }

    @Command("discord")
    @CommandPermission("omc.commands.discord")
    @Description("Donne le lien du serveur Discord")
    private void discord(CommandSender sender) {
        sender.sendMessage(parseText(
                "Venez discutez sur ",
                OMCPlugin.getConfigs().getString("discord", "INVALID CONFIG")
        ));
    }

    @Command("site")
    @CommandPermission("omc.commands.site")
    @Description("Donne le lien du site")
    private void website(CommandSender sender) {
        sender.sendMessage(parseText(
                "Découvrez nous sur ",
                OMCPlugin.getConfigs().getString("homepage", "INVALID CONFIG")
        ));
    }

    @Command("blog")
    @CommandPermission("omc.commands.blog")
    @Description("Donne le lien du blog")
    private void blog(CommandSender sender) {
        sender.sendMessage(parseText(
                "Lisez des articles sur ",
                OMCPlugin.getConfigs().getString("blog", "INVALID CONFIG")
        ));
    }

    @Command("wiki")
    @CommandPermission("omc.commands.wiki")
    @Description("Donne le lien du wiki")
    private void wiki(CommandSender sender) {
        sender.sendMessage(parseText(
                "Lisez des articles sur ",
                OMCPlugin.getConfigs().getString("wiki", "INVALID CONFIG")
        ));
    }
}
