package fr.openmc.core.commands.economy;

import fr.openmc.core.features.utils.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("money")
@Description("Permet de gérer votre argent")
@CommandPermission("omc.commands.money")
public class Money {

    @DefaultFor("~")
    public void getMoney(CommandSender sender, @Optional OfflinePlayer target) {
        if (sender instanceof Player player && target == null) {
            MessagesManager.sendMessageType(player, "§aVous avez §e" + EconomyManager.getInstance().getFormattedBalance(player.getUniqueId()) + "§a", Prefix.OPENMC, MessageType.INFO,  true);
        } else {
            if(target == null) {
                MessagesManager.sendMessageType(sender, MessagesManager.Message.MISSINGARGUMENT.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            if(!(sender instanceof Player player) || player.hasPermission("omc.admin.commands.money.others")) {
                MessagesManager.sendMessageType(sender, "§e" + target.getName() + "§a a §e" + EconomyManager.getInstance().getFormattedBalance(target.getUniqueId()) + "§a", Prefix.OPENMC, MessageType.INFO, true);
            } else {
                MessagesManager.sendMessageType(sender, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }

    @Subcommand("set")
    @Description("Permet de définir l'argent d'un joueur")
    @CommandPermission("omc.admin.commands.money.set")
    public void setMoney(CommandSender player, OfflinePlayer target, @Range(min = 1) double amount) {
        EconomyManager.getInstance().setBalance(target.getUniqueId(), amount);
        MessagesManager.sendMessageType(player, "§aVous avez défini l'argent de §e" + target.getName() + "§a à §e" + EconomyManager.getInstance().getFormattedNumber(amount), Prefix.OPENMC, MessageType.SUCCESS, true);
        if(target.isOnline()) {
            MessagesManager.sendMessageType(target.getPlayer(), "§aVotre argent a été défini à §e" + EconomyManager.getInstance().getFormattedNumber(amount), Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("add")
    @Description("Permet d'ajouter de l'argent à un joueur")
    @CommandPermission("omc.admin.commands.money.add")
    public void addMoney(CommandSender player, OfflinePlayer target, @Range(min = 1) double amount) {
        EconomyManager.getInstance().addBalance(target.getUniqueId(), amount);
        MessagesManager.sendMessageType(player, "§aVous avez ajouté §e" + EconomyManager.getInstance().getFormattedNumber(amount) + "§a à §e" + target.getName(), Prefix.OPENMC, MessageType.SUCCESS, true);
        if(target.isOnline()) {
            MessagesManager.sendMessageType(target.getPlayer(), "§aVous avez reçu §e" + EconomyManager.getInstance().getFormattedNumber(amount), Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("remove")
    @Description("Permet de retirer de l'argent à un joueur")
    @CommandPermission("omc.admin.commands.money.remove")
    public void removeMoney(CommandSender player, OfflinePlayer target, @Range(min = 1) double amount) {
        if(EconomyManager.getInstance().withdrawBalance(target.getUniqueId(), amount)) {
            MessagesManager.sendMessageType(player, "§aVous avez retiré §e" + EconomyManager.getInstance().getFormattedNumber(amount) + "§a à §e" + target.getName(), Prefix.OPENMC, MessageType.SUCCESS, true);
            if(target.isOnline()) {
                MessagesManager.sendMessageType(target.getPlayer(), "§cVous avez perdu §e" + EconomyManager.getInstance().getFormattedNumber(amount), Prefix.OPENMC, MessageType.INFO, true);
            }
        } else {
            MessagesManager.sendMessageType(player, "§cLe joueur n'a pas assez d'argent", Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @Subcommand("reset")
    @Description("Permet de réinitialiser l'argent d'un joueur")
    @CommandPermission("omc.admin.commands.money.reset")
    public void resetMoney(CommandSender player, OfflinePlayer target) {
        EconomyManager.getInstance().setBalance(target.getUniqueId(), 0);
        MessagesManager.sendMessageType(player, "§aVous avez réinitialisé l'argent de §e" + target.getName() + "§a à §e" + EconomyManager.getInstance().getFormattedNumber(0), Prefix.OPENMC, MessageType.SUCCESS, true);
        if(target.isOnline()) {
            MessagesManager.sendMessageType(target.getPlayer(), "§aVotre argent a été réinitialisé à §e" + EconomyManager.getInstance().getFormattedNumber(0), Prefix.OPENMC, MessageType.INFO, true);
        }
    }
}
