package fr.openmc.core.features.economy.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.Transaction;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("money")
@Description("Permet de gérer votre argent")
@CommandPermission("omc.commands.money")
public class Money {
    private final MessagesManager msgOMC  = new MessagesManager(Prefix.OPENMC);

    @DefaultFor("~")
    public void getMoney(CommandSender sender, @Optional OfflinePlayer target) {
        if (sender instanceof Player player && target == null) {
            msgOMC.info(player, "§aVous avez §e" + EconomyManager.getInstance().getFormattedBalance(player.getUniqueId()) + "§a");
        } else {
            if(target == null) {
                msgOMC.error(sender, MessagesManager.Message.MISSINGARGUMENT.getMessage());
                return;
            }
            if(!(sender instanceof Player player) || player.hasPermission("omc.admin.commands.money.others")) {
                msgOMC.info(sender, "§e" + target.getName() + "§a a §e" + EconomyManager.getInstance().getFormattedBalance(target.getUniqueId()) + "§a");
            } else {
                msgOMC.error(sender, MessagesManager.Message.NOPERMISSION.getMessage());
            }
        }
    }

    @Subcommand("set")
    @Description("Permet de définir l'argent d'un joueur")
    @CommandPermission("omc.admin.commands.money.set")
    public void setMoney(CommandSender player, OfflinePlayer target, @Range(min = 1) double amount) {
        EconomyManager.getInstance().setBalance(target.getUniqueId(), amount);
        msgOMC.success(player, "§aVous avez défini l'argent de §e" + target.getName() + "§a à §e" + EconomyManager.getInstance().getFormattedNumber(amount));
        if(target.isOnline()) {
            msgOMC.info(target.getPlayer(), "§aVotre argent a été défini à §e" + EconomyManager.getInstance().getFormattedNumber(amount));
        }
    }

    @Subcommand("add")
    @Description("Permet d'ajouter de l'argent à un joueur")
    @CommandPermission("omc.admin.commands.money.add")
    public void addMoney(CommandSender player, OfflinePlayer target, @Range(min = 1) double amount) {
        EconomyManager.getInstance().addBalance(target.getUniqueId(), amount);
        msgOMC.success(player, "§aVous avez ajouté §e" + EconomyManager.getInstance().getFormattedNumber(amount) + "§a à §e" + target.getName());
        if(target.isOnline()) {
            msgOMC.info(target.getPlayer(), "§aVous avez reçu §e" + EconomyManager.getInstance().getFormattedNumber(amount));
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            new Transaction(
                    target.getUniqueId().toString(),
                    "CONSOLE",
                    amount,
                    "Admin"
            ).register();
        });
    }

    @Subcommand("remove")
    @Description("Permet de retirer de l'argent à un joueur")
    @CommandPermission("omc.admin.commands.money.remove")
    public void removeMoney(CommandSender player, OfflinePlayer target, @Range(min = 1) double amount) {
        if(EconomyManager.getInstance().withdrawBalance(target.getUniqueId(), amount)) {
            msgOMC.success(player, "§aVous avez retiré §e" + EconomyManager.getInstance().getFormattedNumber(amount) + "§a à §e" + target.getName());
            if(target.isOnline()) {
                msgOMC.info(target.getPlayer(), "§cVous avez perdu §e" + EconomyManager.getInstance().getFormattedNumber(amount));
            }

            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                new Transaction(
                        "CONSOLE",
                        target.getUniqueId().toString(),
                        amount,
                        "Admin"
                ).register();
            });
        } else {
            msgOMC.error(player, "§cLe joueur n'a pas assez d'argent");
        }
    }

    @Subcommand("reset")
    @Description("Permet de réinitialiser l'argent d'un joueur")
    @CommandPermission("omc.admin.commands.money.reset")
    public void resetMoney(CommandSender player, OfflinePlayer target) {
        EconomyManager.getInstance().setBalance(target.getUniqueId(), 0);
        msgOMC.success(player, "§aVous avez réinitialisé l'argent de §e" + target.getName() + "§a à §e" + EconomyManager.getInstance().getFormattedNumber(0));
        if(target.isOnline()) {
            msgOMC.info(target.getPlayer(), "§aVotre argent a été réinitialisé à §e" + EconomyManager.getInstance().getFormattedNumber(0));
        }
    }
}
