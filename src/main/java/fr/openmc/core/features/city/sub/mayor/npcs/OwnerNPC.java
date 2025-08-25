package fr.openmc.core.features.city.sub.mayor.npcs;

import de.oliver.fancynpcs.api.Npc;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

public class OwnerNPC {

    @Getter
    private final Npc npc;
    @Getter
    private final UUID cityUUID;
    @Getter
    @Setter
    private Location location;

    public OwnerNPC(Npc npc, UUID cityUUID, Location location) {
        this.npc = npc;
        this.cityUUID = cityUUID;
        this.location = location;
    }
}