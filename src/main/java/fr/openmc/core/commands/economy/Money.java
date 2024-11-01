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
@Description("Permet de gérer son argent")
@CommandPermission("omc.commands.money")
public class Money {

    @DefaultFor("~")
    @Description("Permet de voir son argent")
    public static void getMoney(Player player, @Optional OfflinePlayer target) {
        if(target == null) {
            String balanceFormatted = EconomyManager.getInstance().getFormattedBalance(player.getUniqueId());
            MessagesManager.sendMessageType(player, "§aVous avez §e" + balanceFormatted, Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            if(player.hasPermission("omc.admin.commands.money.others")) {
                String targetBalanceFormatted = EconomyManager.getInstance().getFormattedBalance(target.getUniqueId());
                MessagesManager.sendMessageType(player, "§e" + target.getName() + "§a a §e" + targetBalanceFormatted, Prefix.OPENMC, MessageType.SUCCESS, true);
            } else {
                MessagesManager.sendMessageType(player, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }

    @Subcommand("transfer")
    @Description("Permet de transférer de l'argent")
    @CommandPermission("omc.commands.money.transfer")
    public void transfer(Player player, @Named("player") Player target, @Named("montant") @Range(min = 1) int amount) {
        if(EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), amount)) {
            EconomyManager.getInstance().addBalance(target.getUniqueId(), amount);
            MessagesManager.sendMessageType(player, "§aVous avez envoyé §e" + amount + "€ §aà §e" + target.getName(), Prefix.OPENMC, MessageType.SUCCESS, true);
            MessagesManager.sendMessageType(target, "§e" + player.getName() + " §a vous a envoyé §e" + amount + "€", Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessageType(player, "§cVous n'avez pas assez d'argent", Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @Subcommand("add")
    @Description("Permet d'ajouter de l'argent")
    @CommandPermission("omc.admin.commands.money.add")
    public void addMonet(CommandSender sender, @Named("player") OfflinePlayer target, @Named("montant") @Range(min = 1) int amount) {
        EconomyManager.getInstance().addBalance(target.getUniqueId(), amount);
        MessagesManager.sendMessageType(sender, "§aVous avez ajouté §e" + amount + "€ §aà §e" + target.getName(), Prefix.OPENMC, MessageType.SUCCESS, true);
    }

    @Subcommand("remove")
    @Description("Permet de retirer de l'argent")
    @CommandPermission("omc.admin.commands.money.remove")
    public void removeMoney(CommandSender sender, @Named("player") OfflinePlayer target, @Named("montant") @Range(min = 1) int amount) {
        if(EconomyManager.getInstance().withdrawBalance(target.getUniqueId(), amount)) {
            MessagesManager.sendMessageType(sender, "§aVous avez retiré §e" + amount + "€ §aà §e" + target.getName(), Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessageType(sender, "§cLe joueur n'a pas assez d'argent", Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @Subcommand("set")
    @Description("Permet de définir un montant d'argent")
    @CommandPermission("omc.admin.commands.money.set")
    public void setMoney(CommandSender sender, @Named("player") OfflinePlayer target, @Named("montant") @Range(min = 1) int amount) {
        EconomyManager.getInstance().setBalance(target.getUniqueId(), amount);
        MessagesManager.sendMessageType(sender, "§aVous avez défini le solde de §e" + target.getName() + " §aà §e" + amount + "€", Prefix.OPENMC, MessageType.SUCCESS, true);
    }

}
