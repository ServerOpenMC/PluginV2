package fr.openmc.core.features.privatemessage.command;

import fr.openmc.core.features.privatemessage.PrivateMessageManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("socialspy")
public class SocialSpyCommand {

    @DefaultFor("~")
    @Description("Active ou désactive le social spy")
    @CommandPermission("omc.admin.commands.privatemessage.socialspy")
    public void toggleSocialSpyCommand(Player player) {
        PrivateMessageManager.getInstance().getSpyManager().toggleSocialSpy(player);
    }

    @Subcommand("toggle")
    @Description("Active ou désactive le social spy pour un joueur spécifique")
    @CommandPermission("omc.admin.commands.privatemessage.socialspy.admin")
    public void toggleSocialSpyForPlayer(Player admin, @Named("target") Player target) {
        PrivateMessageManager.getInstance().getSpyManager().toggleSocialSpy(target);

        String status = PrivateMessageManager.getInstance().getSpyManager().hasSocialSpyEnabled(target)
                ? "activé" : "désactivé";

        MessagesManager.sendMessage(admin,
                Component.text("§aSocial Spy " + status + " pour " + target.getName() + "."),
                Prefix.OPENMC, MessageType.SUCCESS, true);
    }

    @Subcommand("list")
    @Description("Liste les joueurs ayant le social spy activé")
    @CommandPermission("omc.admin.commands.privatemessage.socialspy.admin")
    public void listSocialSpyPlayers(Player admin) {
        int spyCount = 0;
        StringBuilder spyList = new StringBuilder("§6Joueurs avec Social Spy activé:\n");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (PrivateMessageManager.getInstance().getSpyManager().hasSocialSpyEnabled(onlinePlayer)) {
                spyList.append("§7- §a").append(onlinePlayer.getName()).append("\n");
                spyCount++;
            }
        }

        if (spyCount == 0) {
            MessagesManager.sendMessage(admin,
                    Component.text("§cAucun joueur n'a le social spy activé."),
                    Prefix.OPENMC, MessageType.INFO, true);
        } else {
            spyList.append("§6Total: §e").append(spyCount).append(" joueur(s)");
            admin.sendMessage(spyList.toString());
        }
    }
}
