package fr.openmc.core.features.city.mayor.npcs;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

public class OwnerNPC {

    @Getter
    private NPC npc;
    @Getter
    private String cityUUID;
    @Getter
    @Setter
    private Location location;

    public OwnerNPC(NPC npc, String cityUUID, Location location) {
        this.npc = npc;
        this.cityUUID=cityUUID;
        this.location=location;
    }
}