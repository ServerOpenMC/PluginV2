package fr.openmc.api.omcplayer.feature;

import fr.openmc.api.omcplayer.OMCPlayer;

import java.util.UUID;

public interface OMCFeat {
    UUID getUniqueId();
    OMCPlayer getPlayer();
}
