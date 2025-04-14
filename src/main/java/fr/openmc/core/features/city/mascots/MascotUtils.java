package fr.openmc.core.features.city.mascots;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class MascotUtils {

    public static void addMascotForCity(String city_uuid, UUID mascotUUID, Chunk chunk){
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                return;
            }
        }

        Mascot newMascot =  new Mascot(city_uuid, mascotUUID, 1, true, 10080, true, chunk);
        MascotsManager.mascots.add(newMascot);
    }

    public static void removeMascotOfCity(String city_uuid){
        for (Mascot mascot : MascotsManager.mascots) {
            if (mascot.getCityUuid().equals(city_uuid)) {
                MascotsManager.mascots.remove(mascot);
                return;
            }
        }
    }

    public static UUID getMascotUUIDOfCity(String city_uuid) {

        for (Mascot mascot : MascotsManager.mascots) {
            if (mascot.getCityUuid().equals(city_uuid)) {
                return mascot.getMascotUuid();
            }
        }

        return null;
    }

    public static Mascot getMascotByUUID(UUID uuid){
        for (Mascot mascot : MascotsManager.mascots) {
            if (mascot.getMascotUuid().equals(uuid)) {
                return mascot;
            }
        }

        return null;
    }

    public static Mascot getMascotOfCity(String city_uuid){
        for (Mascot mascot : MascotsManager.mascots) {
            if (mascot.getCityUuid().equals(city_uuid)) {
                return mascot;
            }
        }

        return null;
    }

    public static Mascot getMascotByEntity(Entity entity) {
        if (entity != null) {
            for (Mascot mascot : MascotsManager.mascots) {
                if (mascot.getMascotUuid().equals(entity.getUniqueId())) {
                    return mascot;
                }
            }
        }
        return null;
    }

    public static LivingEntity loadMascot(Mascot mascot){
        boolean toUnload = false;
        Chunk chunk = mascot.getChunk();
        if (!chunk.isLoaded()){
            chunk.load();
            toUnload = true;
        }
        UUID mascot_uuid = getMascotUUIDOfCity(mascot.getCityUuid());
        if (mascot_uuid == null) {
            return null;
        }
        Entity mob = Bukkit.getEntity(mascot_uuid);
        if (mob == null){
            return null;
        }
        if (toUnload) chunk.unload();
        return (LivingEntity) mob;
    }

    public static boolean mascotsContains(String city_uuid) {
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                return true;
            }
        }
        return false;
    }

    public static int getMascotLevel(String city_uuid) {
        for (Mascot mascot : MascotsManager.mascots) {
            if (mascot.getCityUuid().equals(city_uuid)) {
                return mascot.getLevel();
            }
        }

        return 0;
    }

    public static boolean getMascotState(String city_uuid) {
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                return mascot.isAlive();
            }
        }
        return false;
    }

    public static boolean getMascotImmunity(String city_uuid) {
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                return mascot.isImmunity();
            }
        }
        return false;
    }

    public static long getMascotImmunityTime(String city_uuid) {
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                return mascot.getImmunity_time();
            }
        }
        return 0;
    }

    public static void setMascotLevel(String city_uuid, int level){
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                mascot.setLevel(level);
                return;
            }
        }
    }

    public static void setMascotUUID(String city_uuid, UUID uuid){
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                mascot.setMascotUuid(uuid);
                return;
            }
        }
    }

    public static void setImmunityTime(String city_uuid, long time) {
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                mascot.setImmunity_time(time);
                return;
            }
        }
    }

    public static void changeMascotState(String city_uuid, boolean alive){
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                mascot.setAlive(alive);
                return;
            }
        }
    }

    public static void changeMascotImmunity(String city_uuid, boolean immunity){
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                mascot.setImmunity(immunity);
                return;
            }
        }
    }
}

