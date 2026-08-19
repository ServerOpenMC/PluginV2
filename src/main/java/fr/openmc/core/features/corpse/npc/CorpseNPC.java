package fr.openmc.core.features.corpse.npc;

import de.oliver.fancynpcs.api.Npc;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class CorpseNPC {

    Npc npc;
    @Setter
    Location location;
    UUID ownerUUID;
    @Setter
    ItemStack helmet;
    @Setter
    ItemStack chestplate;
    @Setter
    ItemStack leggings;
    @Setter
    ItemStack boots;
    Pose pose;
    boolean killByPlayer;

    public CorpseNPC(Npc npc, Location location, UUID ownerUUID, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, Pose pose, boolean killByPlayer) {
        this.npc = npc;
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.pose = pose;
        this.killByPlayer = killByPlayer;
    }

}
