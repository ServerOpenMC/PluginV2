package fr.openmc.core.utils.nms.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class ArmorStandNMS {

    public static ArmorStand createFakeStand(Player playerOf, int entityId, Location loc) {
        ServerLevel level = ((CraftWorld) playerOf.getWorld()).getHandle();

        ArmorStand stand = new ArmorStand(level, loc.getX(), loc.getY(), loc.getZ());
        stand.setId(entityId);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setMarker(false);

        return stand;
    }
}
