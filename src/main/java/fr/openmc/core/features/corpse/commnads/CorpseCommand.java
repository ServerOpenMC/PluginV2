package fr.openmc.core.features.corpse.commnads;

import fr.openmc.core.features.corpse.CorpseManager;
import fr.openmc.core.features.corpse.FoundTypes;
import fr.openmc.core.features.corpse.npc.CorpseNPC;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("corpse")
public class CorpseCommand {

    private final CorpseManager corpseManager;
    private final CorpseNPCManager corpseNPCManager;

    public CorpseCommand(CorpseManager corpseManager) {
        this.corpseManager = corpseManager;
        this.corpseNPCManager = corpseManager.CORPSE_NPC_MANAGER;
    }

    @Subcommand("abort")
    @Description("Abandonner votre cadavre")
    void onAbort(Player sender) {
        if (corpseNPCManager.getNPC(sender.getUniqueId()) instanceof CorpseNPC npc) {

            if (npc.isKillByPlayer()) {
                MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.command.abort_not_allowed"),
                        Prefix.CORPSE, MessageType.WARNING, true);
                return;
            }

            corpseManager.deleteCorpse(sender.getUniqueId(), FoundTypes.ABORT);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }

    @Subcommand("locate")
    void onLocate(Player sender) {
        if (corpseNPCManager.getNPC(sender.getUniqueId()) instanceof CorpseNPC npc) {
            MessagesManager.sendMessage(sender, corpseManager.getCorpseDirection(sender, npc),
                    Prefix.CORPSE, MessageType.SUCCESS, true);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse_found"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }

    @Subcommand("locate")
    @CommandPermission("omc.admins.commands.corpse.locate")
    void onLocateAdmin(CommandSender sender, @Named("player") @SuggestWith(CorpseOwnersAutoComplete.class) OfflinePlayer target) {
        if (corpseNPCManager.getNPC(target.getUniqueId()) instanceof CorpseNPC npc) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.command.locate",
                            corpseManager.getLocation(npc.getLocation())),
                    Prefix.CORPSE, MessageType.SUCCESS, true);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse_found"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }

    @Subcommand("teleport")
    @CommandPermission("omc.admins.commands.corpse.teleport")
    void onTeleport(Player sender, @Named("player") @SuggestWith(CorpseOwnersAutoComplete.class) OfflinePlayer target) {
        if (corpseNPCManager.getNPC(target.getUniqueId()) instanceof CorpseNPC npc) {

            sender.teleport(npc.getLocation());

            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.command.teleport", corpseManager.getLocation(npc.getLocation())),
                    Prefix.CORPSE, MessageType.SUCCESS, true);
        } else
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.corpse.no_corpse_found"),
                    Prefix.CORPSE, MessageType.WARNING, true);
    }
}
