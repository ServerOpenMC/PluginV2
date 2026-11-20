package fr.openmc.core.features.events.contents.christmas.gingerbread.commands;

import org.bukkit.entity.EntityType;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.christmas.gingerbread.managers.GingerBreadManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("christmas admin gingerbread")
@CommandPermission("omc.admins.commands.christmas.gingerbread")
public class GingerbreadCommands {
    @Subcommand("placenpc")
    public void placeNpcCommand(OMCPlayer player) {
        NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        Npc existing = npcManager.getNpc(GingerBreadManager.NPC_ID);

        if (existing != null) {
            existing.getData().setLocation(player.getLocation());
            existing.updateForAll();
            player.message().sendError(Component.text("NPC déplacé !"), Prefix.CHRISTMAS);
            return;
        }

        NpcData data = new NpcData(GingerBreadManager.NPC_ID, player.getUniqueId(), player.getLocation());
        data.setType(EntityType.PLAYER);
        data.setDisplayName("<gradient:#FF1B53:#FF9797:#FFD4D4:#FF9797:#FF1B53>Gertrude</gradient>");
        data.setVisibilityDistance(50);
        data.setSkin("https://s.namemc.com/i/ac439108099ca164.png");
        data.setGlowing(true);
        data.setGlowingColor(NamedTextColor.RED);
        data.isTurnToPlayer();

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        npcManager.registerNpc(npc);
        npc.create();
        npc.spawnForAll();
        player.message().send(Component.text("NPC placé !"), Prefix.CHRISTMAS);
    }

    @Subcommand("end")
    public void endGingerbreadCommand() {
        GingerBreadManager.endEvent();
    }

    @Subcommand("reset")
    public void resetGingerbreadCmd() {
        GingerBreadManager.resetDatabase();
    }
}
