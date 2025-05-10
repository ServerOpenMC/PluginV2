package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.npcs.MayorNPC;
import fr.openmc.core.features.city.mayor.npcs.OwnerNPC;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.api.CitizensApi;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class NPCManager {
    private static final HashMap<String, OwnerNPC> ownerNpcMap = new HashMap<>();
    private static final HashMap<String, MayorNPC> mayorNpcMap = new HashMap<>();

    public static void debug() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String uuid : ownerNpcMap.keySet()) {
                    OwnerNPC ownerNpc = ownerNpcMap.get(uuid);
                    System.out.println("OwnerNPC: " + ownerNpc.getNpc().getName() + " " + ownerNpc.getCityUUID());
                }
                for (String uuid : mayorNpcMap.keySet()) {
                    MayorNPC mayorNpc = mayorNpcMap.get(uuid);
                    System.out.println("MayorNPC: " + mayorNpc.getNpc().getName() + " " + mayorNpc.getCityUUID());
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 600L); // 600 ticks = 30 secondes
    }


    public static void createNPCS(String cityUUID, Location urnLocation) {
        if (!CitizensApi.isHasCitizens()) return;

        Location locationMayor = urnLocation.add(3, 0, 0);
        locationMayor = urnLocation.getWorld().getHighestBlockAt(locationMayor).getLocation().add(0, 1, 0);

        Location locationOwner = urnLocation.add(-3, 0, 0);
        locationOwner = urnLocation.getWorld().getHighestBlockAt(locationOwner).getLocation().add(0, 1, 0);

        NPC mayorNpc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "mayor-" + cityUUID.toString());
        NPC ownerNpc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "owner-" + cityUUID.toString());

        ownerNpcMap.put(cityUUID, new OwnerNPC(ownerNpc, cityUUID, urnLocation));
        mayorNpcMap.put(cityUUID, new MayorNPC(mayorNpc, cityUUID, urnLocation));

        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        // APPLY SKIN
        // for owner
        String ownerName = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();
        mayorNpc.setName("§e§lCLIQUEZ ICI");
        mayorNpc.getOrAddTrait(HologramTrait.class).addLine("§ePropriétaire " + ownerName);
        mayorNpc.getOrAddTrait(SkinTrait.class).setSkinName(ownerName);

        // for mayor
        if (city.getMayor() != null) {
            String mayorName = CacheOfflinePlayer.getOfflinePlayer(city.getMayor().getUUID()).getName();
            mayorNpc.setName("§e§lCLIQUEZ ICI");
            mayorNpc.getOrAddTrait(HologramTrait.class).addLine("§6Maire " + mayorName);
            mayorNpc.getOrAddTrait(SkinTrait.class).setSkinName(mayorName);
        } else {
            mayorNpc.setName("§8Inconnu");
            mayorNpc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                    "mayor_none",
                    "EX/6W2Dp2HNt9Uebizu8gL5UU8+SxaTQq2SNmV9hIDdeXDmdZfoRPv+IRgkEnjkDhpcD0nF0ksr9EaF0oB3Mr5In0wrqqsyrsiN1U52gexi8hNGCFIK+L3YR+IOSi+HH4LMs+IM4RI3FMr6glRqz/lIjSWoAaN91JNffcU8J2NjdvKOMr7sKryhfjtJMRScpzN3iGpTvWq83l1KyeTNJetI6BRtihS1WJS9v3YBgbJxEXoa3cfh4ISAfsyg8Nc+7C/cVWLQIJGEYNMaW3x6uK8GDGs/HcV3x3hjy71vp3st4zQHlK/Z0nPAdj3C0v1KvqTAmsPfUxtJ+AA/BJOF9ypTtrHzU/Eczd8kJfBPJh5bJ15P6ziNaXsjXYxYCvZTA5t1maHowoa4atS+5UtXWwnXUbRGfz1yPBnlpQv7MYPESljxcOkUeJUvxuSc5/+ppSWs2/La8WxVo8b03hEeXaEgSUt5ms4Skv1wa5IyfMaXwRmc5pe6plZVx+97bmgA1OcPrPnLbAewUGjt1Wgxzj7gRSd9WNT00JmzEEfXsHzAG0mstHS2GaKbYy9XWatmM7vH03s1uepbdHvVPwkG8M/DiVUQh1weSPwUx9vJsJ8mZyYhGAiLtn4rnBkaUbllWlf+2dSo2qVYC+QKVAE1ggyb5XNluMk9OojpXlLu28Bk=",
                    "ewogICJ0aW1lc3RhbXAiIDogMTYxMTU5OTU5MjE5MCwKICAicHJvZmlsZUlkIiA6ICIzYTNmNzhkZmExZjQ0OTllYjE5NjlmYzlkOTEwZGYwYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb19jcmVyYXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFlMjk2M2NjNWJhYWI1ZWRjZjQxYTYxYWE0YWJiZjU0NWJmN2VhYTJjMTE0MDUzYmY3OWNjNGUwY2I0NjE2OCIKICAgIH0KICB9Cn0="
            );
        }

        mayorNpc.spawn(locationMayor);
        ownerNpc.spawn(locationOwner);
    }

    public static void removeNPCS(String cityUUID) {
        if (!CitizensApi.isHasCitizens()) return;

        NPC ownerNpc = ownerNpcMap.get(cityUUID).getNpc();
        NPC mayorNpc = mayorNpcMap.get(cityUUID).getNpc();

        if (ownerNpc != null) {
            ownerNpc.destroy();
            ownerNpcMap.remove(cityUUID);
        }

        if (mayorNpc != null) {
            mayorNpc.destroy();
            mayorNpcMap.remove(cityUUID);
        }
    }

    public static boolean hasNPCS(String cityUUID) {
        if (!CitizensApi.isHasCitizens()) return false;

        return ownerNpcMap.containsKey(cityUUID) && mayorNpcMap.containsKey(cityUUID);
    }
}
