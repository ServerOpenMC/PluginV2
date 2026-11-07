package fr.openmc.core.commands.fun;

import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;


@Command("taille")
public class PlayerSizeCommand {

    @CommandPermission("omc.commands.taille")
    @Description("Afficher les commandes de la gestion de la taille")
    public void playerSizeCommand(Player player) {
        MessagesManager.sendMessage(player, TranslationManager.translation("§6§Commendes pour changer sa taille"), Prefix.OPENMC, MessageType.INFO, false);
        MessagesManager.sendMessage(player, TranslationManager.translation("§e/taille petit - Taille 0.5"), Prefix.OPENMC, MessageType.INFO, false);
        MessagesManager.sendMessage(player, TranslationManager.translation("§e/taille grand - Taille 1.5"), Prefix.OPENMC, MessageType.INFO, false);
        MessagesManager.sendMessage(player, TranslationManager.translation("§e/taille normal - Taille 1.0"), Prefix.OPENMC, MessageType.INFO, false);

    }

    @Subcommand("small")
    @Description("Player size: 0.5")
    public void small(Player player) {
        changePlayerSize(player, 0.5);
    }

    @Subcommand("big")
    @Description("Player size: 1.5")
    public void big(Player player) {
        changePlayerSize(player, 1.5);
    }

    @Subcommand("normal")
    @Description("Mettre la taille normalement")
    public void normal(Player player) {
        changePlayerSize(player, 1.0);
    }

    private void changePlayerSize(Player player, double taille) {
        AttributeInstance attribute = player.getAttribute(Attribute.SCALE);

        if (attribute == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("§cImpossible de modifier votre taille."), Prefix.OPENMC, MessageType.INFO, false);
            return;
        }

        attribute.setBaseValue(taille);
        MessagesManager.sendMessage(player, TranslationManager.translation("§aVotre taille est maintenant de §e" + taille), Prefix.OPENMC, MessageType.INFO, false);
    }
}