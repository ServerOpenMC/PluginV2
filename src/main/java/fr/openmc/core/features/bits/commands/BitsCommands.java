package fr.openmc.core.features.bits.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.bits.menu.BitsMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.github.commands.autocomplete.PlayerNameLinkedAutocomplete;
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

@Command("bits")
@Description("Permet de gérer vos bits")
@CommandPermission("omc.commands.bits")
public class BitsCommands {

    private final BitsManager bitsManager;
    private final GitHubHook gitHubHook;

    public BitsCommands(BitsManager bitsManager, GitHubHook gitHubHook) {
        this.bitsManager = bitsManager;
        this.gitHubHook = gitHubHook;
    }

    @CommandPlaceholder()
    public void getBits(
            Player sender
    ) {
        new BitsMenu(sender).open();
    }

    @Subcommand("set")
    @Description("Permet de définir les bits d'un joueur")
    @CommandPermission("omc.admin.commands.bits.set")
    public void setBits(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target, @Range(min = 1E-10) double amount) {
        bitsManager.setBits(target.getUniqueId(), amount);
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.bits.set.success",
                        Component.text(target.getName()).color(NamedTextColor.YELLOW),
                        Component.text(bitsManager.getFormattedBits(target.getUniqueId())).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        if (target.isOnline()) {
            MessagesManager.sendMessage(target.getPlayer(),
                    TranslationManager.translation(
                            "feature.economy.bits.set.target",
                            Component.text(bitsManager.getFormattedBits(target.getUniqueId())).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("add")
    @Description("Permet d'ajouter des bits à un joueur")
    @CommandPermission("omc.admin.commands.bits.add")
    public void addBits(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target, @Range(min = 1E-10) double amount) {
        bitsManager.addBits(target.getUniqueId(), amount);
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.bits.add.success",
                        Component.text(bitsManager.getFormattedBits(target.getUniqueId())).color(NamedTextColor.YELLOW),
                        Component.text(target.getName()).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        if (target.isOnline()) {
            MessagesManager.sendMessage(target.getPlayer(),
                    TranslationManager.translation(
                            "feature.economy.bits.add.target",
                            Component.text(bitsManager.getFormattedBits(target.getUniqueId())).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("remove")
    @Description("Permet de retirer des bits à un joueur")
    @CommandPermission("omc.admin.commands.bits.remove")
    public void removeBits(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target, @Range(min = 1E-10) double amount) {
        if (bitsManager.withdrawBits(target.getUniqueId(), amount)) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.bits.remove.success",
                            Component.text(bitsManager.getFormattedBits(target.getUniqueId())).color(NamedTextColor.YELLOW),
                            Component.text(target.getName()).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.SUCCESS, true);
            if (target.isOnline()) {
                MessagesManager.sendMessage(target.getPlayer(),
                        TranslationManager.translation(
                                "feature.economy.bits.remove.target",
                                Component.text(EconomyManager.getFormattedNumber(amount)).color(NamedTextColor.YELLOW)
                        ),
                        Prefix.OPENMC, MessageType.INFO, true);
            }
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.economy.bits.remove.not_enough"), Prefix.OPENMC, MessageType.ERROR, true);
        }
    }

    @Subcommand("reset")
    @Description("Permet de réinitialiser les bits d'un joueur")
    @CommandPermission("omc.admin.commands.bits.reset")
    public void resetBits(CommandSender player, @SuggestWith(OnlinePlayerAutoComplete.class) OfflinePlayer target) {
        bitsManager.setBits(target.getUniqueId(), 0);
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.bits.reset.success",
                        Component.text(target.getName()).color(NamedTextColor.YELLOW),
                        Component.text(EconomyManager.getFormattedNumber(0)).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
        if (target.isOnline()) {
            MessagesManager.sendMessage(target.getPlayer(),
                    TranslationManager.translation(
                            "feature.economy.bits.reset.target",
                            Component.text(EconomyManager.getFormattedNumber(0)).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.OPENMC, MessageType.INFO, true);
        }
    }

    @Subcommand("forceBitsUpdate")
    @Description("Permet de forcer l'update des bits d'un joueur")
    @CommandPermission("omc.admin.commands.bits.forceupdate")
    public void forceBitsUpdate(CommandSender player, @SuggestWith(PlayerNameLinkedAutocomplete.class) Player target) {
        Long githubId = gitHubHook.getContributorId(target.getUniqueId());

        if (githubId == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.economy.bits.forceupdate.not_linked"
                    ),
                    Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        bitsManager.applyContributorBitsUpdate(githubId);
        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.economy.bits.forceupdate.success",
                        Component.text(target.getName()).color(NamedTextColor.YELLOW)
                ),
                Prefix.OPENMC, MessageType.SUCCESS, true);
    }
}