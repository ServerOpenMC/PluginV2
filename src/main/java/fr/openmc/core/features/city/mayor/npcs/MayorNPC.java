package fr.openmc.core.features.city.mayor.npcs;

import de.oliver.fancynpcs.api.Npc;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class MayorNPC {

    @Getter
    private Npc npc;
    @Getter
    private String cityUUID;
    @Getter
    @Setter
    private Location location;

    public MayorNPC(Npc npc, String cityUUID, Location location) {
        this.npc = npc;
        this.cityUUID=cityUUID;
        this.location=location;
    }
}