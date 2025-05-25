package fr.openmc.core.features.limbo.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.limbo.LimboManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;

@Command({"limbo"})
@Description("Les abysses")
@CommandPermission("omc.commands.limbo")
public class Limbo {

    @DefaultFor("~")
    public void sendPlayerLimbo(Player player) {
        if (!LimboManager.isInLimbo(player.getUniqueId())) return;

        LimboManager.exitLimbo(player);
    }

    @Subcommand("sd")
    @Description("Envoie un joueur dans le Limbo")
    @CommandPermission("omc.admin.commands.limbo.send")
    public void esendPlayerLimbo(CommandSender sender, Player player) {
        File schemFile = new File(OMCPlugin.getInstance().getDataFolder() + "/schem", "limbo.schem");
        LimboManager.showStructure(schemFile, player, player.getLocation(), null);
    }

    @Subcommand("send")
    @Description("Envoie un joueur dans le Limbo")
    @CommandPermission("omc.admin.commands.limbo.send")
    public void sendPlayerLimbo(CommandSender sender, Player player) {
        try {
            LimboManager.sendPlayerLimbo(player);
            sender.sendMessage("§aLe joueur a été envoyé dans le Limbo.");
        } catch (Exception e) {
            sender.sendMessage("§cUne erreur est survenue lors de l'envoi du joueur dans le Limbo.");
            e.printStackTrace();
        }
    }
}
