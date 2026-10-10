package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.chronometer.ChronometerType;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.corpse.CorpseManager;
import fr.openmc.core.features.corpse.FoundTypes;
import fr.openmc.core.features.corpse.npc.CorpseNPC;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class OMCPlayerCorpse extends OMCPlayerFeat {
    private final CorpseManager corpseManager = OMCRegistry.FEATURES.CORPSE.get();

    public OMCPlayerCorpse(Player player) {
        super(player);
    }

    public boolean createCorpse(boolean killByPlayer, EntityDamageEvent.DamageCause cause) {
        return corpseManager.createCorpse(getPlayer(), killByPlayer, cause);
    }

    public void deleteCorpse(FoundTypes found) {
        corpseManager.deleteCorpse(getUniqueId(), found);
    }

    public boolean hasCorpseDB() {
        return corpseManager.hasCorpseDB(getUniqueId());
    }

    public Component getCorpseDirection(CorpseNPC corpse) {
        return corpseManager.getCorpseDirection(getPlayer(), corpse);
    }

    public Component getRemainingTime() {
        return corpseManager.getRemainingTime(getPlayer());
    }
}

