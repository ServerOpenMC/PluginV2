package fr.openmc.core.features.city.mascots;

import java.util.UUID;

public class MascotUtils {

    public static void addMascotForCity(String city_uuid, UUID mascotUUID){
        for (Mascot mascot : MascotsManager.mascots){
            if (mascot.getCityUuid().equals(city_uuid)){
                return;
            }
        }

        Mascot newMascot =  new Mascot(city_uuid, mascotUUID.toString(), 1, true, 10080, true);
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
                return UUID.fromString(mascot.getMascotUuid());
            }
        }

        return null;
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
                mascot.setMascotUuid(String.valueOf(uuid));
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

