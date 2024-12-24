package fr.openmc.core.commands.fun;

import java.util.Random;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Diceroll {
    @Command("diceroll")
    @CommandPermission("omc.commands.diceroll")
    @Description("Faire un lancé de dés (Donne un nombre aléatoire entre 1 et 10)")
    private void diceroll(Player player) {
        Random rand = new Random();
        int result = rand.nextInt(10) + 1;


        MessagesManager.sendMessageType(player, Component.text("🎲 Le résultat est: §6" + result + "§r 🎲"), Prefix.OPENMC, MessageType.INFO, true);
    }
}
