package fr.openmc.core.features.city.sub.mascots.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

@Getter
@Setter
@DatabaseTable(tableName = "mascots")
public class Mascot {
    @DatabaseField(id = true)
    private String cityUUID;
    @DatabaseField(canBeNull = false)
    private int level;
    @DatabaseField(canBeNull = false)
    private UUID mascotUUID;
    @DatabaseField(canBeNull = false)
    private boolean immunity;
    @DatabaseField(canBeNull = false)
    private boolean alive;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int z;

    Mascot() {
        // required by ORMLite
    }

    public Mascot(String cityUUID, UUID mascotUUID, int level, boolean immunity, boolean alive, int x, int z) {
        this.cityUUID = cityUUID;
        this.level = level;
        this.mascotUUID = mascotUUID;
        this.immunity = immunity;
        this.alive = alive;
        this.x = x;
        this.z = z;
    }

    public Chunk getChunk() {
        return Bukkit.getWorld("world").getChunkAt(x, z);
    }

    public void setChunk(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    public Material getMascotEgg() {
        LivingEntity entity = (LivingEntity) this.getEntity();
        if (entity == null) {
            return Material.BARRIER; // Default fallback
        }
        String eggName = entity.getType().name() + "_SPAWN_EGG";
        if (Material.matchMaterial(eggName) == null) {
            return Material.BARRIER;
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
        LivingEntity mob = (LivingEntity) Bukkit.getEntity(mascot_uuid);
        if (mob == null) {
            return null;
        }
        if (toUnload) chunk.unload();
        return mob;
    }

    public City getCity() {
        return CityManager.getCity(this.cityUUID);
    }
}
