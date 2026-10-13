package fr.openmc.core.commands.utils;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Spawn {
    private final SpawnManager spawnManager = OMCRegistry.FEATURES.SPAWN.get();

    @Command("spawn")
    @Description("Permet de se rendre au spawn")
    @CommandPermission("omc.commands.spawn")
    public void spawn(
            CommandSender sender,
            @Named("player") @Optional @SuggestWith(OnlinePlayerAutoComplete.class) OMCPlayer target
    ) {
        Location spawnLocation = spawnManager.getSpawnLocation();

        if (sender instanceof Player player && (target == null || player.getUniqueId().equals(target.getUniqueId()))) {
            OMCPlayer omcPlayer = OMCPlayer.of(player);

            PlayerUtils.sendFadeTitleTeleport(omcPlayer, spawnLocation);

            omcPlayer.message().sendSuccess(TranslationManager.translation("command.utils.spawn.got_sent"),
                    Prefix.OPENMC, true);
        } else {
            if(!(sender instanceof Player) || sender.hasPermission("omc.admin.commands.spawn.others")) {
                PlayerUtils.sendFadeTitleTeleport(target, spawnLocation);

                MessagesManager.sendMessage(sender, TranslationManager.translation("command.utils.spawn.have_sent",
                        target.getNameWithHead().color(NamedTextColor.YELLOW)), Prefix.OPENMC, MessageType.SUCCESS, true);
                target.message().sendWarning(TranslationManager.translation("command.utils.spawn.have_sent_by",
                        TranslationManager.translation("command.utils.spawn.console").color(NamedTextColor.YELLOW)),
                        Prefix.OPENMC, true);
            } else {
                MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }
}
