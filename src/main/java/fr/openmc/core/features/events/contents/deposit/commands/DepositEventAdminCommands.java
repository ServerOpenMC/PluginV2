package fr.openmc.core.features.events.contents.deposit.commands;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import fr.openmc.core.features.events.contents.deposit.NpcSettings;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"deposit admin"})
@CommandPermission("omc.admins.commands.depositevent")
public class DepositEventAdminCommands {

    @Subcommand("placenpc")
    public void placeNpc(OMCPlayer player, String eventId) {
        DepositEvent event = find(player, eventId);
        if (event == null) return;

        NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        Npc existing = npcManager.getNpc(event.getNpcId());

        if (existing != null) {
            existing.getData().setLocation(player.getLocation());
            existing.updateForAll();
            player.message().send(Component.text("NPC déplacé !"), Prefix.STAFF);
            return;
        }

        NpcSettings s = event.getNpcSettings();
        NpcData data = new NpcData(event.getNpcId(), player.getUniqueId(), player.getLocation());
        data.setType(EntityType.PLAYER);
        data.setDisplayName(s.displayName());
        data.setVisibilityDistance(s.visibilityDistance());
        data.setSkin(s.skinUrl());
        data.setGlowing(true);
        data.setGlowingColor(s.glowingColor());
        data.setTurnToPlayer(true);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        npcManager.registerNpc(npc);
        npc.create();
        npc.spawnForAll();
        player.message().send(Component.text("NPC placé !"), event.getPrefix());
    }

    @Subcommand("start")
    public void start(OMCPlayer player, String eventId, int hours) {
        DepositEvent event = find(player, eventId);
        if (event == null) return;
        DepositEventManager.start(event, java.time.Duration.ofHours(hours));
        player.message().send(Component.text("Event démarré pour " + hours + "h !"), Prefix.STAFF);
    }

    @Subcommand("end")
    public void end(OMCPlayer player, String eventId) {
        DepositEvent event = find(player, eventId);
        if (event != null) DepositEventManager.end(event);
    }

    @Subcommand("reset")
    public void reset(OMCPlayer player, String eventId) {
        DepositEvent event = find(player, eventId);
        if (event != null) DepositEventManager.reset(event);
    }

    private DepositEvent find(OMCPlayer player, String id) {
        return DepositEventManager.getEvent(id).orElseGet(() -> {
            player.message().sendError(Component.text("Event inconnu : " + id
                    + " (disponibles : " + String.join(", ", DepositEventManager.getEventIds()) + ")"), Prefix.STAFF);
            return null;
        });
    }
}