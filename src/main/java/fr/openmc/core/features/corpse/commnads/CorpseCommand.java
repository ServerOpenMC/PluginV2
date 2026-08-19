package fr.openmc.core.features.corpse.commnads;

import fr.openmc.core.features.corpse.CorpseManager;
import fr.openmc.core.features.corpse.FoundTypes;
import fr.openmc.core.features.corpse.npc.CorpseNPC;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("corpse")
public class CorpseCommand {

    @Subcommand("abort")
    @Description("Abandonner votre cadavre")
    void onAbort(Player sender) {
        if (CorpseNPCManager.getNPC(sender.getUniqueId()) instanceof CorpseNPC npc) {

            if (npc.isKillByPlayer()) {
                MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.command.abort_not_allowed"),
                        Prefix.CORPSE, MessageType.WARNING, true);
                return;
            }

            CorpseManager.deleteCorpse(sender.getUniqueId(), FoundTypes.ABORT);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }

    @Subcommand("locate")
    @CommandPermission("omc.admins.commands.corpse.locate")
    void onLocate(CommandSender sender, @Named("player") @SuggestWith(CorpseOwnersAutoComplete.class) OfflinePlayer target) {
        if (CorpseNPCManager.getNPC(target.getUniqueId()) instanceof CorpseNPC npc) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.command.locate", Component.text(npc.getLocation().toString())),
                    Prefix.CORPSE, MessageType.SUCCESS, true);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse_found"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }

    @Subcommand("teleport")
    @CommandPermission("omc.admins.commands.corpse.teleport")
    void onTeleport(Player sender, @Named("player") @SuggestWith(CorpseOwnersAutoComplete.class) OfflinePlayer target) {
        if (CorpseNPCManager.getNPC(target.getUniqueId()) instanceof CorpseNPC npc) {

            sender.teleport(npc.getLocation());

            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.command.teleport", Component.text(npc.getLocation().toString())),
                    Prefix.CORPSE, MessageType.SUCCESS, true);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse_found"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }
}
