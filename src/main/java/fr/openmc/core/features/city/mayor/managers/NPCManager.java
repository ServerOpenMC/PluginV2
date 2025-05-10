package fr.openmc.core.features.city.mayor.managers;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.npcs.MayorNPC;
import fr.openmc.core.features.city.mayor.npcs.OwnerNPC;
import fr.openmc.core.features.city.menu.mayor.npc.MayorNpcMenu;
import fr.openmc.core.features.city.menu.mayor.npc.OwnerNpcMenu;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.api.FancyNpcApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class NPCManager implements Listener {
    private static final HashMap<String, OwnerNPC> ownerNpcMap = new HashMap<>();
    private static final HashMap<String, MayorNPC> mayorNpcMap = new HashMap<>();

    public static void debug() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String uuid : ownerNpcMap.keySet()) {
                    OwnerNPC ownerNpc = ownerNpcMap.get(uuid);
                    System.out.println("OwnerNPC: " + ownerNpc.getNpc().getData().getName() + " " + ownerNpc.getCityUUID());
                }
                for (String uuid : mayorNpcMap.keySet()) {
                    MayorNPC mayorNpc = mayorNpcMap.get(uuid);
                    System.out.println("MayorNPC: " + mayorNpc.getNpc().getData().getName() + " " + mayorNpc.getCityUUID());
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0, 600L); // 600 ticks = 30 secondes
    }


    public static void createNPCS(String cityUUID, Location urnLocation, Player player) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        Location locationMayor = urnLocation.add(3, 0, 0);
        locationMayor = urnLocation.getWorld().getHighestBlockAt(locationMayor).getLocation().add(0, 1, 0);

        Location locationOwner = urnLocation.add(-3, 0, 0);
        locationOwner = urnLocation.getWorld().getHighestBlockAt(locationOwner).getLocation().add(0, 1, 0);

        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        NpcData dataMayor = new NpcData("mayor-" + cityUUID, player.getUniqueId(), locationMayor);
        if (city.getMayor() != null) {
            String mayorName = CacheOfflinePlayer.getOfflinePlayer(city.getMayor().getUUID()).getName();
            dataMayor.setSkin(mayorName);
            dataMayor.setDisplayName("§6Maire " + mayorName);
        } else {
            dataMayor.setSkin("https://s.namemc.com/i/1971f3c39cb8e3ef.png");
            dataMayor.setDisplayName("§8Inconnu");
        }

        Npc npcMayor = FancyNpcsPlugin.get().getNpcAdapter().apply(dataMayor);

        NpcData dataOwner = new NpcData("owner-" + cityUUID, player.getUniqueId(), locationOwner);
        String ownerName = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();
        dataOwner.setSkin(ownerName);
        dataOwner.setDisplayName("<yellow>Propriétaire " + ownerName + "</yellow>");

        Npc npcOwner = FancyNpcsPlugin.get().getNpcAdapter().apply(dataOwner);

        ownerNpcMap.put(cityUUID, new OwnerNPC(npcOwner, cityUUID, urnLocation));
        mayorNpcMap.put(cityUUID, new MayorNPC(npcMayor, cityUUID, urnLocation));

        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcMayor);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcOwner);

        npcMayor.create();
        npcMayor.spawnForAll();
        npcOwner.create();
        npcOwner.spawnForAll();
    }

    public static void removeNPCS(String cityUUID) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        Npc ownerNpc = ownerNpcMap.remove(cityUUID).getNpc();
        Npc mayorNpc = mayorNpcMap.remove(cityUUID).getNpc();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(ownerNpc);
        ownerNpc.removeForAll();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(mayorNpc);
        mayorNpc.removeForAll();
    }

    public static boolean hasNPCS(String cityUUID) {
        if (!FancyNpcApi.hasFancyNpc()) return false;

        return ownerNpcMap.containsKey(cityUUID) && mayorNpcMap.containsKey(cityUUID);
    }

    @EventHandler
    public void onInteractWithMayorNPC(NpcInteractEvent event) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        Player player = event.getPlayer();

        if (MayorManager.getInstance().phaseMayor == 1) {
            MessagesManager.sendMessage(player, Component.text("§8§o*les elections sont en cours... on ne sait pas ce qu'il décide de prendre*"), Prefix.MAYOR, MessageType.INFO, true);
            return;
        }

        Npc npc = event.getNpc();

        if (npc.getData().getName().startsWith("mayor-")) {
            String cityUUID = npc.getData().getName().replace("mayor-", "");
            City city = CityManager.getCity(cityUUID);
            if (city == null) return;

            if (city.getElectionType() == ElectionType.OWNER_CHOOSE) {
                MessagesManager.sendMessage(player, Component.text("§8§o*mhh cette ville n'a pas encore débloquée les éléctions*"), Prefix.MAYOR, MessageType.INFO, true);
                return;
            }

            new MayorNpcMenu(player, city).open();
        }

    }

    @EventHandler
    public void onInteractWithOwnerNPC(NpcInteractEvent event) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        Player player = event.getPlayer();

        if (MayorManager.getInstance().phaseMayor == 1) {
            MessagesManager.sendMessage(player, Component.text("§8§o*les elections sont en cours... on ne sait pas ce qu'il décide de prendre*"), Prefix.MAYOR, MessageType.INFO, true);
            return;
        }

        Npc npc = event.getNpc();

        if (npc.getData().getName().startsWith("owner-")) {
            String cityUUID = npc.getData().getName().replace("owner-", "");
            City city = CityManager.getCity(cityUUID);
            if (city == null) return;

            new OwnerNpcMenu(player, city, city.getElectionType()).open();
        }

    }
}
