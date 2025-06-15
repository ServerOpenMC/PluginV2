package fr.openmc.core.features.city.models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

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
}
