package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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


    private Component parseText(String messageKey, String link) {
        return TranslationManager.translation(messageKey).append(
                Component.text(removeProtocol(link))
                        .clickEvent(ClickEvent.openUrl(link))
                        .hoverEvent(HoverEvent.showText(TranslationManager.translation("command.utils.socials.hover_access")))
        );
    }

    @Command("socials")
    @CommandPermission("omc.commands.socials")
    @Description("Donne les liens des réseaux sociaux")
    private void socials(CommandSender sender) {
        sender.sendMessage(parseText(
                "command.utils.socials.discord",
                OMCPlugin.getConfigs().getString("discord", "INVALID CONFIG")
        ));
        sender.sendMessage(parseText(
                "command.utils.socials.site",
                OMCPlugin.getConfigs().getString("homepage", "INVALID CONFIG")
        ));
        sender.sendMessage(parseText(
                "command.utils.socials.wiki",
                OMCPlugin.getConfigs().getString("wiki", "INVALID CONFIG")
        ));
        sender.sendMessage(parseText(
                "command.utils.socials.github",
                OMCPlugin.getConfigs().getString("repoV2", "INVALID CONFIG")
        ));
    }

    @Command("discord")
    @CommandPermission("omc.commands.discord")
    @Description("Donne le lien du serveur Discord")
    private void discord(CommandSender sender) {
        sender.sendMessage(parseText(
                "command.utils.socials.discord",
                OMCPlugin.getConfigs().getString("discord", "INVALID CONFIG")
        ));
    }

    @Command("site")
    @CommandPermission("omc.commands.site")
    @Description("Donne le lien du site")
    private void website(CommandSender sender) {
        sender.sendMessage(parseText(
                "command.utils.socials.site",
                OMCPlugin.getConfigs().getString("homepage", "INVALID CONFIG")
        ));
    }

    @Command("blog")
    @CommandPermission("omc.commands.blog")
    @Description("Donne le lien du blog")
    private void blog(CommandSender sender) {
        sender.sendMessage(parseText(
                "command.utils.socials.blog",
                OMCPlugin.getConfigs().getString("blog", "INVALID CONFIG")
        ));
    }

    @Command("wiki")
    @CommandPermission("omc.commands.wiki")
    @Description("Donne le lien du wiki")
    private void wiki(CommandSender sender) {
        sender.sendMessage(parseText(
                "command.utils.socials.wiki",
                OMCPlugin.getConfigs().getString("wiki", "INVALID CONFIG")
        ));
    }
}
