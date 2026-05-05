package fr.openmc.core.commands.utils;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("command.utils.spawn.got_sent"),
                    Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            if(!(sender instanceof Player) || sender.hasPermission("omc.admin.commands.spawn.others")) {
                PlayerUtils.sendFadeTitleTeleport(target, spawnLocation);
                MessagesManager.sendMessage(sender, TranslationManager.translation("command.utils.spawn.have_sent",
                        Component.text(target.getName()).color(NamedTextColor.YELLOW)), Prefix.OPENMC, MessageType.SUCCESS, true);
                MessagesManager.sendMessage(target, TranslationManager.translation("command.utils.spawn.have_sent_by",
                        (sender instanceof Player player ? Component.text(player.getName()).color(NamedTextColor.YELLOW) :
                                Component.text("Console").color(NamedTextColor.YELLOW))),
                        Prefix.OPENMC, MessageType.WARNING, true);
            } else {
                MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }
}
