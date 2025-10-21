package fr.openmc.core.commands.utils;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.MessagesManager.Message;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Spawn {

    @Command("spawn")
    @Description("Permet de se rendre au spawn")
    @CommandPermission("omc.commands.spawn")
    public void spawn(
            CommandSender sender,
            @Named("player") @Optional @SuggestWith(OnlinePlayerAutoComplete.class) Player target
    ) {
        
        Location spawnLocation = SpawnManager.getSpawnLocation();

        if (sender instanceof Player player && (target == null || player.getUniqueId().equals(target.getUniqueId()))) {
            PlayerUtils.sendFadeTitleTeleport(player, spawnLocation);
            MessagesManager.sendMessage(player, Component.text("§aVous avez été envoyé au spawn"), Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            if(!(sender instanceof Player) || sender.hasPermission("omc.admin.commands.spawn.others")) {
                PlayerUtils.sendFadeTitleTeleport(target, spawnLocation);
                MessagesManager.sendMessage(sender, Component.text("§aVous avez envoyé §e" + target.getName() + "§a au spawn"), Prefix.OPENMC, MessageType.SUCCESS, true);
                MessagesManager.sendMessage(target, Component.text("§aVous avez été envoyé au spawn par §e" + (sender instanceof Player player ? player.getName() : "Console") + "§a"), Prefix.OPENMC, MessageType.WARNING, true);
            } else {
                MessagesManager.sendMessage(sender, Message.NO_PERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }
}
