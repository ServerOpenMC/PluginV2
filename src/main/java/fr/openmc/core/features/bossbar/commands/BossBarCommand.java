package fr.openmc.core.features.bossbar.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.bossbar.BossbarManager;
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

@Command({"omcbossbar"})
public class BossBarCommand {

    @DefaultFor("~")
    public void mainCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs.");
            return;
        }

        BossbarManager.getInstance().toggleBossBar(player);
    }

    @CommandPermission("omc.admin.commands.bossbar.reload")
    @Subcommand("reload")
    public void reloadCommand(CommandSender sender) {
        BossbarManager.getInstance().reloadMessages();
        sender.sendMessage("§aMessages de la bossbar rechargés.");
    }

    @CommandPermission("omc.admin.commands.bossbar.toggle")
    @Subcommand("toggle")
    public void toggleCommand(CommandSender sender) {
        BossbarManager.getInstance().toggleGlobalBossBar();
        sender.sendMessage("§aBossbar " + (BossbarManager.getInstance().hasBossBar() ? "activée" : "désactivée" + " pour tous les joueurs."));
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("manage")
    public void manageCommand(BukkitCommandActor actor) {
        if (!(actor.getSender() instanceof Player player)) {
            return;
        }

        List<Component> messages = BossbarManager.getInstance().getHelpMessages();

        Component header = Component.text("\n§6§lGestion des messages de Bossbar\n")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);

        player.sendMessage(header);

        Component addButton = Component.text("[Ajouter un message]")
                .color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.suggestCommand("/omcbossbar add <message>"))
                .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour ajouter un message")));

        player.sendMessage(addButton);

        for (int i = 0; i < messages.size(); i++) {
            Component messageLine = Component.text((i + 1) + ". ", NamedTextColor.GRAY)
                    .append(messages.get(i))
                    .append(Component.space())
                    .append(createActionButton("✎ Éditer", "/omcbossbar edit " + i + " " + messages.get(i), NamedTextColor.YELLOW))
                    .append(Component.space())
                    .append(createActionButton("✖ Supprimer", "/omcbossbar confirm " + i, NamedTextColor.RED));

            player.sendMessage(messageLine);
        }

        Component refreshButton = Component.text("\n[Rafraîchir]")
                .color(NamedTextColor.BLUE)
                .clickEvent(ClickEvent.runCommand("/omcbossbar manage"))
                .hoverEvent(HoverEvent.showText(Component.text("Actualiser la liste")));

        player.sendMessage(refreshButton);
    }

    private Component createActionButton(String text, String command, NamedTextColor color) {
        return Component.text(text)
                .color(color)
                .clickEvent(ClickEvent.suggestCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text("Exécuter: " + command)));
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("add")
    public void addMessage(BukkitCommandActor actor, String message) {
        try {
            Component component = MiniMessage.miniMessage().deserialize(message);
            BossbarManager.getInstance().addMessage(component);
            BossbarManager.getInstance().reloadMessages();
            actor.reply("§aMessage ajouté avec succès!");
            manageCommand(actor);
        } catch (Exception e) {
            actor.reply("§cFormat de message invalide!");
        }
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("edit")
    public void editMessage(BukkitCommandActor actor, int index, String newMessage) {
        try {
            Component component = MiniMessage.miniMessage().deserialize(newMessage);
            BossbarManager.getInstance().updateMessage(index, component);
            BossbarManager.getInstance().reloadMessages();
            actor.reply("§aMessage modifié avec succès!");
            manageCommand(actor);
        } catch (Exception e) {
            actor.reply("§cFormat de message invalide ou index incorrect!");
        }
    }

    @CommandPermission("omc.admin.commands.bossbar.manage")
    @Subcommand("confirm")
    public void confirmDelete(BukkitCommandActor actor, int index) {
        Component confirmation = Component.text()
                .append(Component.text("§eÊtes-vous sûr de vouloir supprimer ce message? "))
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
        BossbarManager.getInstance().removeMessage(index);
        BossbarManager.getInstance().reloadMessages();
        actor.reply("§aMessage supprimé avec succès!");
        manageCommand(actor);
    }
}
