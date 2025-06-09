package fr.openmc.core.features.city.sub.mascots;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.UUID;

@Setter
@Getter
public class Mascot {

    private City city;
    private UUID mascotUUID;
    private int level;
    private boolean immunity;
    private boolean alive;
    private Chunk chunk;

    public Mascot(City city, UUID mascotUUID, int level, boolean immunity, boolean alive, Chunk chunk) {
        this.city = city;
        this.mascotUUID = mascotUUID;
        this.level = level;
        this.immunity = immunity;
        this.alive = alive;
        this.chunk = chunk;
    }

    public Material getMascotEgg() {
        String eggName = this.getEntity().getType().name() + "_SPAWN_EGG";
        if (Material.matchMaterial(eggName) == null) {
            return Material.ZOMBIE_SPAWN_EGG;
        }
        return Material.matchMaterial(eggName);
    }

    public Entity getEntity() {
        boolean toUnload = false;
        Chunk chunk = this.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
            toUnload = true;
        }
        UUID mascot_uuid = this.getMascotUUID();
        if (mascot_uuid == null) {
            return null;
        }
        Entity mob = Bukkit.getEntity(mascot_uuid);
        if (mob == null) {
            return null;
        }
        if (toUnload) chunk.unload();
        return mob;
    }

}

