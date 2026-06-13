package fr.openmc.core.features.economy.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("money")
@Description("Permet de gérer votre argent")
@CommandPermission("omc.commands.money")
public class Money {

    @CommandPlaceholder()
    public void getMoney(
            CommandSender sender,
            @Named("joueur") @Optional @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target
    ) {
        if (sender instanceof Player player && target == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.money.self",
                            Component.text(EconomyManager.getFormattedBalance(player.getUniqueId())).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO,  true);
        } else {
            if (target == null) {
                MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.missing_arg"), Prefix.OPENMC, MessageType.ERROR, true);
                return;
            }
            if (!(sender instanceof Player player) || player.hasPermission("omc.admin.commands.money.others")) {
                MessagesManager.sendMessage(sender,
                        TranslationManager.translation(
                                "feature.economy.money.others",
                                Component.text(target.getName()).color(NamedTextColor.YELLOW),
                                Component.text(EconomyManager.getFormattedBalance(target.getUniqueId())).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.OPENMC, MessageType.INFO, true);
            } else {
                MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }

    @Subcommand("set")
    @Description("Permet de définir l'argent d'un joueur")
    @CommandPermission("omc.admin.commands.money.set")
    public void setMoney(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target, @Range(min = 1E-10) double amount) {
        EconomyManager.setBalance(target.getUniqueId(), amount);
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.money.set.success",
                        Component.text(target.getName()).color(NamedTextColor.YELLOW),
                        Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        if (target.isOnline()) {
            MessagesManager.sendMessage(target.getPlayer(),
                    TranslationManager.translation(
                            "feature.economy.money.set.target",
                            Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("add")
    @Description("Permet d'ajouter de l'argent à un joueur")
    @CommandPermission("omc.admin.commands.money.add")
    public void addMoney(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target, @Range(min = 1E-10) double amount) {
        EconomyManager.addBalance(target.getUniqueId(), amount, "Admin - Ajout par " + player == null ? "Console" : player.getName());
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.money.add.success",
                        Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW),
                        Component.text(target.getName()).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        if (target.isOnline()) {
            MessagesManager.sendMessage(target.getPlayer(),
                    TranslationManager.translation(
                            "feature.economy.money.add.target",
                            Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("remove")
    @Description("Permet de retirer de l'argent à un joueur")
    @CommandPermission("omc.admin.commands.money.remove")
    public void removeMoney(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target, @Range(min = 1E-10) double amount) {
        if (EconomyManager.withdrawBalance(target.getUniqueId(), amount, "Admin  - Retirer par " + player == null ? "Console" : player.getName())) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.money.remove.success",
                            Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW),
                            Component.text(target.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.SUCCESS, true);
            if (target.isOnline()) {
                MessagesManager.sendMessage(target.getPlayer(),
                        TranslationManager.translation(
                                "feature.economy.money.remove.target",
                                Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.OPENMC, MessageType.INFO, true);
            }
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.economy.money.remove.not_enough"), Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @Subcommand("reset")
    @Description("Permet de réinitialiser l'argent d'un joueur")
    @CommandPermission("omc.admin.commands.money.reset")
    public void resetMoney(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target) {
        EconomyManager.setBalance(target.getUniqueId(), 0);
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.money.reset.success",
                        Component.text(target.getName()).color(NamedTextColor.YELLOW),
                        Component.text(EconomyManager.getFormattedNumber(0)).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        if (target.isOnline()) {
            MessagesManager.sendMessage(target.getPlayer(),
                    TranslationManager.translation(
                            "feature.economy.money.reset.target",
                            Component.text(EconomyManager.getFormattedNumber(0)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }
}
