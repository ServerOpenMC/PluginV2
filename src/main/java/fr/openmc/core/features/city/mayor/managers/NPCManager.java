package fr.openmc.core.features.city.mayor.managers;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import fr.openmc.api.input.location.ItemInteraction;
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
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class NPCManager implements Listener {
    private static final HashMap<String, OwnerNPC> ownerNpcMap = new HashMap<>();
    private static final HashMap<String, MayorNPC> mayorNpcMap = new HashMap<>();

    public NPCManager() {
        // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                if (npc.getData().getName().startsWith("owner-")) {
                    String cityUUID = npc.getData().getName().replace("owner-", "");
                    ownerNpcMap.put(cityUUID, new OwnerNPC(npc, cityUUID, npc.getData().getLocation()));
                } else if (npc.getData().getName().startsWith("mayor-")) {
                    String cityUUID = npc.getData().getName().replace("mayor-", "");
                    mayorNpcMap.put(cityUUID, new MayorNPC(npc, cityUUID, npc.getData().getLocation()));
                }
            });
        }, 20L * 30);
    }

    public static void createNPCS(String cityUUID, Location locationMayor, Location locationOwner, UUID creatorUUID) {
        if (!FancyNpcApi.hasFancyNpc()) return;


        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        NpcData dataMayor = new NpcData("mayor-" + cityUUID, creatorUUID, locationMayor);
        if (city.getMayor().getUUID() != null && city.getElectionType() == ElectionType.ELECTION) {
            String mayorName = CacheOfflinePlayer.getOfflinePlayer(city.getMayor().getUUID()).getName();
            dataMayor.setSkin(mayorName);
            dataMayor.setDisplayName("§6Maire " + mayorName);

            dataMayor.addEquipment(NpcEquipmentSlot.HEAD, CustomItemRegistry.getByName("omc_items:suit_helmet").getBest());
            dataMayor.addEquipment(NpcEquipmentSlot.CHEST, CustomItemRegistry.getByName("omc_items:suit_chestplate").getBest());
            dataMayor.addEquipment(NpcEquipmentSlot.LEGS, CustomItemRegistry.getByName("omc_items:suit_leggings").getBest());
            dataMayor.addEquipment(NpcEquipmentSlot.FEET, CustomItemRegistry.getByName("omc_items:suit_boots").getBest());
        } else {
            dataMayor.setSkin("https://s.namemc.com/i/1971f3c39cb8e3ef.png");
            dataMayor.setDisplayName("§8Inconnu");
        }

        Npc npcMayor = FancyNpcsPlugin.get().getNpcAdapter().apply(dataMayor);

        NpcData dataOwner = new NpcData("owner-" + cityUUID, creatorUUID, locationOwner);
        String ownerName = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();
        dataOwner.setSkin(ownerName);
        dataOwner.setDisplayName("<yellow>Propriétaire " + ownerName + "</yellow>");

        Npc npcOwner = FancyNpcsPlugin.get().getNpcAdapter().apply(dataOwner);

        ownerNpcMap.put(cityUUID, new OwnerNPC(npcOwner, cityUUID, locationOwner));
        mayorNpcMap.put(cityUUID, new MayorNPC(npcMayor, cityUUID, locationMayor));

        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcMayor);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcOwner);

        npcMayor.create();
        if (city.getElectionType() == ElectionType.ELECTION) npcMayor.spawnForAll();
        npcOwner.create();
        npcOwner.spawnForAll();
    }

    public static void removeNPCS(String cityUUID) {
        if (!FancyNpcApi.hasFancyNpc()) return;
        if (!ownerNpcMap.containsKey(cityUUID) || !mayorNpcMap.containsKey(cityUUID)) return;

        Npc ownerNpc = ownerNpcMap.remove(cityUUID).getNpc();
        Npc mayorNpc = mayorNpcMap.remove(cityUUID).getNpc();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(ownerNpc);
        ownerNpc.removeForAll();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(mayorNpc);
        mayorNpc.removeForAll();
    }

    public static void updateNPCS(String cityUUID) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        OwnerNPC ownerNPC = ownerNpcMap.get(cityUUID);
        MayorNPC mayorNPC = mayorNpcMap.get(cityUUID);

        if (ownerNPC == null || mayorNPC == null) return;

        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        removeNPCS(cityUUID);
        createNPCS(cityUUID, mayorNPC.getLocation(), ownerNPC.getLocation(), ownerNPC.getNpc().getData().getCreator());
    }

    public static void updateAllNPCS() {
        if (!FancyNpcApi.hasFancyNpc()) return;

        Set<String> cityUUIDs = new HashSet<>(ownerNpcMap.keySet()); // Copie

        for (String cityUUID : cityUUIDs) {
            OwnerNPC ownerNPC = ownerNpcMap.get(cityUUID);
            MayorNPC mayorNPC = mayorNpcMap.get(cityUUID);

            if (ownerNPC == null || mayorNPC == null) continue;

            City city = CityManager.getCity(cityUUID);
            if (city == null) continue;

            removeNPCS(cityUUID);
            createNPCS(cityUUID, mayorNPC.getLocation(), ownerNPC.getLocation(), ownerNPC.getNpc().getData().getCreator());
        }
    }

    public static void moveNPC(String type, Location location, String city_uuid) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        if (type.equalsIgnoreCase("owner")) {
            OwnerNPC ownerNPC = ownerNpcMap.get(city_uuid);
            if (ownerNPC != null) {
                ownerNPC.getNpc().getData().setLocation(location);
                ownerNPC.setLocation(location);
            }
        } else if (type.equalsIgnoreCase("mayor")) {
            MayorNPC mayorNPC = mayorNpcMap.get(city_uuid);
            if (mayorNPC != null) {
                mayorNPC.getNpc().getData().setLocation(location);
                mayorNPC.setLocation(location);
            }
        }
    }

    public static boolean hasNPCS(String cityUUID) {
        if (!FancyNpcApi.hasFancyNpc()) return false;

        return ownerNpcMap.containsKey(cityUUID) && mayorNpcMap.containsKey(cityUUID);
    }

    @EventHandler
    public void onInteractWithMayorNPC(NpcInteractEvent event) {
        if (!FancyNpcApi.hasFancyNpc()) return;

        Player player = event.getPlayer();

        Npc npc = event.getNpc();

        if (npc.getData().getName().startsWith("mayor-")) {
            if (MayorManager.getInstance().phaseMayor == 1) {
                MessagesManager.sendMessage(player, Component.text("§8§o*les elections sont en cours... on ne sait pas ce qu'il décide de prendre*"), Prefix.MAYOR, MessageType.INFO, true);
                event.setCancelled(true);
                return;
            }

            String cityUUID = npc.getData().getName().replace("mayor-", "");
            City city = CityManager.getCity(cityUUID);
            if (city == null) return;

            if (city.getElectionType() == ElectionType.OWNER_CHOOSE) {
                MessagesManager.sendMessage(player, Component.text("§8§o*mhh cette ville n'a pas encore débloquée les éléctions*"), Prefix.MAYOR, MessageType.INFO, true);
                return;
            }

            new MayorNpcMenu(player, city).open();
        } else if (npc.getData().getName().startsWith("owner-")) {
            String cityUUID = npc.getData().getName().replace("owner-", "");
            City city = CityManager.getCity(cityUUID);
            if (city == null) return;

            if (MayorManager.getInstance().phaseMayor == 1) {
                Component message = Component.text("§8§o*Bonjour ? Tu veux me bouger ? Clique ici !*")
                        .clickEvent(ClickEvent.callback(audience -> {
                            List<Component> loreItemNPC = List.of(
                                    Component.text("§7Cliquez sur l'endroit où vous voulez déplacer le §9NPC")
                            );
                            ItemStack itemToGive = new ItemStack(Material.STICK);
                            ItemMeta itemMeta = itemToGive.getItemMeta();

                            itemMeta.displayName(Component.text("§7Emplacement du §9NPC"));
                            itemMeta.lore(loreItemNPC);
                            itemToGive.setItemMeta(itemMeta);
                            ItemInteraction.runLocationInteraction(
                                    player,
                                    itemToGive,
                                    "mayor:owner-npc-move",
                                    300,
                                    "§7Vous avez 300s pour séléctionner votre emplacement",
                                    "§7Vous n'avez pas eu le temps de déplacer votre NPC",
                                    locationClick -> {
                                        if (locationClick == null) return true;

                                        Chunk chunk = locationClick.getChunk();

                                        City cityByChunk = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());
                                        if (cityByChunk == null) {
                                            MessagesManager.sendMessage(player, Component.text("§cImpossible de mettre le NPC en dehors de votre ville"), Prefix.CITY, MessageType.ERROR, false);
                                            return false;
                                        }

                                        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                                        if (playerCity == null) {
                                            return false;
                                        }

                                        if (!cityByChunk.getUUID().equals(playerCity.getUUID())) {
                                            MessagesManager.sendMessage(player, Component.text("§cImpossible de mettre le NPC en dehors de votre ville"), Prefix.CITY, MessageType.ERROR, false);
                                            return false;
                                        }

                                        NPCManager.moveNPC("owner", locationClick, city.getUUID());
                                        NPCManager.updateNPCS(city.getUUID());
                                        return true;
                                    }
                            );
                        }))
                        .hoverEvent(HoverEvent.showText(Component.text("Déplacer ce NPC")));

                MessagesManager.sendMessage(player, message, Prefix.MAYOR, MessageType.INFO, false);

                event.setCancelled(true);
                return;
            }

            new OwnerNpcMenu(player, city, city.getElectionType()).open();
        }

    }
}
