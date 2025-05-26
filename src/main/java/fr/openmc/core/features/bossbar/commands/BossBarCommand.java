package fr.openmc.core.features.bossbar.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.bossbar.BossbarManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

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
    public void toggleCommand(CommandSender sender, Player target) {
        BossbarManager.getInstance().toggleBossBar(target);
        sender.sendMessage("§aBossbar " + (BossbarManager.getInstance().hasBossBar(target) ? "activée" : "désactivée") + " pour " + target.getName());
    }
}