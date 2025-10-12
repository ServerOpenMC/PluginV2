package fr.openmc.core.features.displays.bossbar.commands;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@Command({"omcbossbar", "bb", "bossbaromc"})
public class BossBarCommand {

    @DefaultFor("~")
    public void mainCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.NO_PERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        BossbarManager.toggleBossBar(player);
    }

    @CommandPermission("omc.admin.commands.bossbar.reload")
    @Subcommand("reload")
    public void reloadCommand(CommandSender sender) {
        BossbarManager.reloadMessages();
        MessagesManager.sendMessage(sender, Component.text("§aBossbar rechargée avec succès !"), Prefix.OPENMC, MessageType.SUCCESS, true);
    }

    @CommandPermission("omc.admin.commands.bossbar.toggle")
    @Subcommand("toggle")
    public void toggleCommand(CommandSender sender) {
        BossbarManager.toggleGlobalBossBar();
        if (sender instanceof Player player) {
            MessagesManager.sendMessage(player, Component.text("§aBossbar " + (BossbarManager.hasBossBar() ? "activée" : "désactivée") + " pour vous."), Prefix.OPENMC, MessageType.SUCCESS, true);
        }
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("manage")
    public void manageCommand(BukkitCommandActor actor) {
        if (!(actor.getSender() instanceof Player player)) {
            return;
        }

        List<Component> messages = BossbarManager.getHelpMessages();
        
        Component header = Component.text("\n§6§lGestion des messages de la bossbar\n")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);

        MessagesManager.sendMessage(player, header, Prefix.OPENMC, MessageType.NONE, false);

        Component addButton = Component.text("[Ajouter un message]")
                .color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.suggestCommand("/omcbossbar add <message>"))
                .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour ajouter un message")));

        MessagesManager.sendMessage(player, addButton, Prefix.OPENMC, MessageType.NONE, false);

        for (int i = 0; i < messages.size(); i++) {
            Component messageLine = Component.text((i + 1) + ". ", NamedTextColor.GRAY)
                    .append(messages.get(i))
                    .append(Component.space())
                    .append(createActionButton("✎ Éditer", "/omcbossbar edit " + i + " " + messages.get(i), NamedTextColor.YELLOW))
                    .append(Component.space())
                    .append(createActionButton("✖ Supprimer", "/omcbossbar confirm " + i, NamedTextColor.RED));

            MessagesManager.sendMessage(player, messageLine, Prefix.OPENMC, MessageType.NONE, false);
        }

        Component refreshButton = Component.text("\n[Rafraîchir]")
                .color(NamedTextColor.BLUE)
                .clickEvent(ClickEvent.runCommand("/omcbossbar manage"))
                .hoverEvent(HoverEvent.showText(Component.text("Actualiser la liste")));

        MessagesManager.sendMessage(player, refreshButton, Prefix.OPENMC, MessageType.NONE, false);
    }

    private Component createActionButton(String text, String command, NamedTextColor color) {
        return Component.text(text)
                .color(color)
                .clickEvent(ClickEvent.suggestCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text("Exécuter : " + command)));
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("add")
    public void addMessage(BukkitCommandActor actor, String message) {
        try {
            Component component = MiniMessage.miniMessage().deserialize(message);
            BossbarManager.addMessage(component);
            BossbarManager.reloadMessages();
            MessagesManager.sendMessage(actor.getSender(), Component.text("§aMessage ajouté avec succès !"), Prefix.OPENMC, MessageType.SUCCESS, true);
            manageCommand(actor);
        } catch (Exception e) {
            MessagesManager.sendMessage(actor.getSender(), Component.text("§cErreur lors de l'ajout du message ! Assurez-vous que le format est correct."), Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("edit")
    public void editMessage(BukkitCommandActor actor, int index, String newMessage) {
        try {
            Component component = MiniMessage.miniMessage().deserialize(newMessage);
            BossbarManager.updateMessage(index, component);
            BossbarManager.reloadMessages();
            MessagesManager.sendMessage(actor.getSender(), Component.text("§aMessage mis à jour avec succès !"), Prefix.OPENMC, MessageType.SUCCESS, true);
            manageCommand(actor);
        } catch (Exception e) {
            MessagesManager.sendMessage(actor.getSender(), Component.text("§cFormat de message ou index invalide !"), Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("confirm")
    public void confirmDelete(BukkitCommandActor actor, int index) {
        Component confirmation = Component.text()
                .append(Component.text("§eÊtes-vous sûr de vouloir supprimer ce message ?"))
                .append(Component.text("[OUI]")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/omcbossbar delete " + index))
                        .hoverEvent(HoverEvent.showText(Component.text("Confirmer la suppression"))))
                .build();

        actor.reply(confirmation);
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("delete")
    public void deleteMessage(BukkitCommandActor actor, int index) {
        BossbarManager.removeMessage(index);
        BossbarManager.reloadMessages();
        MessagesManager.sendMessage(actor.getSender(), Component.text("Message supprimé avec succès."), Prefix.OPENMC, MessageType.SUCCESS, true);
        manageCommand(actor);
    }
}
