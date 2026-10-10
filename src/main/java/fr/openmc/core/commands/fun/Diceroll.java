package fr.openmc.core.commands.fun;

import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.ThreadLocalRandom;

public class Diceroll {
    @Command("diceroll")
    @CommandPermission("omc.commands.diceroll")
    @Description("Faire un lancé de dés (Donne un nombre aléatoire entre 1 et 10)")
    private void diceroll(Player player) {
        int result = ThreadLocalRandom.current().nextInt(10) + 1;

        MessagesManager.sendMessage(player, TranslationManager.translation("command.fun.diceroll.success",
                Component.text(result).color(NamedTextColor.GOLD)), Prefix.OPENMC, MessageType.INFO, true);
    }
}
