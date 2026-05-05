package fr.openmc.core.features.city.sub.mayor.managers;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.menu.npc.MayorNpcMenu;
import fr.openmc.core.features.city.sub.mayor.menu.npc.OwnerNpcMenu;
import fr.openmc.core.features.city.sub.mayor.npcs.MayorNPC;
import fr.openmc.core.features.city.sub.mayor.npcs.OwnerNPC;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
    private static final HashMap<UUID, OwnerNPC> ownerNpcMap = new HashMap<>();
    private static final HashMap<UUID, MayorNPC> mayorNpcMap = new HashMap<>();

    public NPCManager() {
        // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                if (npc.getData().getName().startsWith("owner-")) {
                    UUID cityUUID = UUID.fromString(npc.getData().getName().replace("owner-", ""));
                    if (CityManager.getCity(cityUUID) != null) {
                        ownerNpcMap.put(cityUUID, new OwnerNPC(npc, cityUUID, npc.getData().getLocation()));
                    } else {
                        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
                        npc.removeForAll();
                    }
                } else if (npc.getData().getName().startsWith("mayor-")) {
                    UUID cityUUID = UUID.fromString(npc.getData().getName().replace("mayor-", ""));
                    if (CityManager.getCity(cityUUID) != null) {
                        mayorNpcMap.put(cityUUID, new MayorNPC(npc, cityUUID, npc.getData().getLocation()));
                    } else {
                        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
                        npc.removeForAll();
                    }
                }
            });
        }, 20L * 30);
    }

    public static void createNPCS(UUID cityUUID, Location locationMayor, Location locationOwner, UUID creatorUUID) {
        if (!FancyNpcsHook.isEnable()) return;


        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        NpcData dataMayor = new NpcData("mayor-" + cityUUID, creatorUUID, locationMayor);
        if (city.getMayor().getMayorUUID() != null && city.getElectionType() == ElectionType.ELECTION) {
            String mayorName = CacheOfflinePlayer.getOfflinePlayer(city.getMayor().getMayorUUID()).getName();
            dataMayor.setSkin(mayorName);
            String mayorDisplayName = "<gold>" + TranslationManager.translationString("feature.city.mayor.npc.display.mayor", Component.text(mayorName)) + "</gold>";
            dataMayor.setDisplayName(mayorDisplayName);

            dataMayor.addEquipment(NpcEquipmentSlot.HEAD, CustomItemRegistry.getByName("omc_items:suit_helmet").getBest());
            dataMayor.addEquipment(NpcEquipmentSlot.CHEST, CustomItemRegistry.getByName("omc_items:suit_chestplate").getBest());
            dataMayor.addEquipment(NpcEquipmentSlot.LEGS, CustomItemRegistry.getByName("omc_items:suit_leggings").getBest());
            dataMayor.addEquipment(NpcEquipmentSlot.FEET, CustomItemRegistry.getByName("omc_items:suit_boots").getBest());
        } else {
            dataMayor.setSkin("https://s.namemc.com/i/1971f3c39cb8e3ef.png");
            String unknownDisplayName = "<dark_gray>" + TranslationManager.translationString("feature.city.mayor.npc.display.unknown") + "</dark_gray>";
            dataMayor.setDisplayName(unknownDisplayName);
        }

        Npc npcMayor = FancyNpcsPlugin.get().getNpcAdapter().apply(dataMayor);

        NpcData dataOwner = new NpcData("owner-" + cityUUID, creatorUUID, locationOwner);
        String ownerName = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER)).getName();
        dataOwner.setSkin(ownerName);
        String ownerDisplayName = TranslationManager.translationString("feature.city.mayor.npc.display.owner", Component.text(ownerName));
        dataOwner.setDisplayName("<yellow>" + ownerDisplayName + "</yellow>");

        Npc npcOwner = FancyNpcsPlugin.get().getNpcAdapter().apply(dataOwner);

        ownerNpcMap.put(cityUUID, new OwnerNPC(npcOwner, cityUUID, locationOwner));
        mayorNpcMap.put(cityUUID, new MayorNPC(npcMayor, cityUUID, locationMayor));

        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcMayor);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcOwner);

        npcMayor.create();
        npcMayor.spawnForAll();
        npcMayor.getData().setSpawnEntity(city.getElectionType().equals(ElectionType.ELECTION));
        npcOwner.create();
        npcOwner.spawnForAll();
    }

    public static void removeNPCS(UUID cityUUID) {
        if (!FancyNpcsHook.isEnable()) return;
        if (!ownerNpcMap.containsKey(cityUUID) || !mayorNpcMap.containsKey(cityUUID)) return;

        Npc ownerNpc = ownerNpcMap.remove(cityUUID).getNpc();
        Npc mayorNpc = mayorNpcMap.remove(cityUUID).getNpc();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(ownerNpc);
        ownerNpc.removeForAll();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(mayorNpc);
        mayorNpc.removeForAll();
    }

    public static void updateNPCS(UUID cityUUID) {
        if (!FancyNpcsHook.isEnable()) return;

        OwnerNPC ownerNPC = ownerNpcMap.get(cityUUID);
        MayorNPC mayorNPC = mayorNpcMap.get(cityUUID);

        if (ownerNPC == null || mayorNPC == null) return;

        City city = CityManager.getCity(cityUUID);
        if (city == null) return;

        removeNPCS(cityUUID);
        createNPCS(cityUUID, mayorNPC.getLocation(), ownerNPC.getLocation(), ownerNPC.getNpc().getData().getCreator());
    }

    public static void updateAllNPCS() {
        if (!FancyNpcsHook.isEnable()) return;

        Set<UUID> cityUUIDs = new HashSet<>(ownerNpcMap.keySet()); // Copie

        for (UUID cityUUID : cityUUIDs) {
            OwnerNPC ownerNPC = ownerNpcMap.get(cityUUID);
            MayorNPC mayorNPC = mayorNpcMap.get(cityUUID);

            if (ownerNPC == null || mayorNPC == null) continue;

            City city = CityManager.getCity(cityUUID);
            if (city == null) continue;

            removeNPCS(cityUUID);
            createNPCS(cityUUID, mayorNPC.getLocation(), ownerNPC.getLocation(), ownerNPC.getNpc().getData().getCreator());
        }
    }

    public static void moveNPC(String type, Location location, UUID cityUUID) {
        if (!FancyNpcsHook.isEnable()) return;

        if (type.equalsIgnoreCase("owner")) {
            OwnerNPC ownerNPC = ownerNpcMap.get(cityUUID);
            if (ownerNPC != null) {
                ownerNPC.getNpc().getData().setLocation(location);
                ownerNPC.setLocation(location);
            }
        } else if (type.equalsIgnoreCase("mayor")) {
            MayorNPC mayorNPC = mayorNpcMap.get(cityUUID);
            if (mayorNPC != null) {
                mayorNPC.getNpc().getData().setLocation(location);
                mayorNPC.setLocation(location);
            }
        }
    }

    public static boolean hasNPCS(UUID cityUUID) {
        if (!FancyNpcsHook.isEnable()) return false;

        return ownerNpcMap.containsKey(cityUUID) && mayorNpcMap.containsKey(cityUUID);
    }

    @EventHandler
    public void onInteractWithMayorNPC(NpcInteractEvent event) {
        if (!FancyNpcsHook.isEnable()) return;

        Player player = event.getPlayer();

        Npc npc = event.getNpc();

        if (npc.getData().getName().startsWith("mayor-")) {
            UUID cityUUID = UUID.fromString(npc.getData().getName().replace("mayor-", ""));
            City city = CityManager.getCity(cityUUID);
            if (city == null) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.error.not_in_city"), Prefix.MAYOR, MessageType.ERROR, false);
                removeNPCS(cityUUID);
                return;
            }

            if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
	            MessagesManager.sendMessage(player, TranslationManager.translation(
                        "feature.city.mayor.npc.error.feature_locked",
                        Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR))
                ), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            Chunk chunkTest = event.getNpc().getData().getLocation().getChunk();
            int chunkX = chunkTest.getX();
            int chunkZ = chunkTest.getZ();

            if (!city.hasChunk(chunkX, chunkZ)) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.error.not_in_city"), Prefix.MAYOR, MessageType.ERROR, false);
                removeNPCS(cityUUID);
                return;
            }

            if (MayorManager.phaseMayor == 1) {
                if (!event.getPlayer().getUniqueId().equals(city.getPlayerWithPermission(CityPermission.OWNER))) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.info.no_mayor_yet"), Prefix.MAYOR, MessageType.INFO, true);
                    return;
                }

                Component message = TranslationManager.translation("feature.city.mayor.npc.move.prompt")
                        .clickEvent(ClickEvent.callback(audience -> {
                            List<Component> loreItemNPC = List.of(
                                    TranslationManager.translation("feature.city.mayor.npc.move.item.lore")
                            );
                            ItemStack itemToGive = new ItemStack(Material.STICK);
                            ItemMeta itemMeta = itemToGive.getItemMeta();

                            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.npc.move.item.name"));
                            itemMeta.lore(loreItemNPC);
                            itemToGive.setItemMeta(itemMeta);
                            ItemInteraction.runLocationInteraction(
                                    player,
                                    itemToGive,
                                    "mayor:mayor-npc-move",
                                    300,
                                    TranslationManager.translation("feature.city.mayor.npc.move.interaction.remaining", Component.text("300s").color(NamedTextColor.GRAY)),
                                    TranslationManager.translation("feature.city.mayor.npc.move.interaction.timeout"),
                                    locationClick -> {
                                        if (locationClick == null) return true;

                                        Chunk chunk = locationClick.getChunk();

                                        City cityByChunk = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());
                                        if (cityByChunk == null) {
                                            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.move.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                                            return false;
                                        }

                                        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                                        if (playerCity == null) {
                                            return false;
                                        }

                                        if (!cityByChunk.getUniqueId().equals(playerCity.getUniqueId())) {
                                            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.move.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                                            return false;
                                        }

                                        NPCManager.moveNPC("mayor", locationClick, city.getUniqueId());
                                        NPCManager.updateNPCS(city.getUniqueId());
                                        return true;
                                    },
                                    null
                            );
                        }))
                        .hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.city.mayor.npc.move.hover")));

                MessagesManager.sendMessage(player, message, Prefix.MAYOR, MessageType.INFO, false);

                event.setCancelled(true);
                return;
            }

            if (city.getElectionType() == ElectionType.OWNER_CHOOSE) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.info.no_election_unlocked"), Prefix.MAYOR, MessageType.INFO, true);
                return;
            }

            new MayorNpcMenu(player, city).open();
        } else if (npc.getData().getName().startsWith("owner-")) {
            UUID cityUUID = UUID.fromString(npc.getData().getName().replace("owner-", ""));
            City city = CityManager.getCity(cityUUID);
            if (city == null) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.error.not_in_city"), Prefix.MAYOR, MessageType.ERROR, false);
                removeNPCS(cityUUID);
                return;
            }

            if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
	            MessagesManager.sendMessage(player, TranslationManager.translation(
                        "feature.city.mayor.npc.error.feature_locked",
                        Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR)).color(NamedTextColor.GOLD)
                ), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            Chunk npcChuck = event.getNpc().getData().getLocation().getChunk();

            if (!city.hasChunk(npcChuck.getX(), npcChuck.getZ())) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.error.not_in_city"), Prefix.MAYOR, MessageType.ERROR, false);
                removeNPCS(cityUUID);
                return;
            }

            if (MayorManager.phaseMayor == 1) {
                if (!event.getPlayer().getUniqueId().equals(city.getPlayerWithPermission(CityPermission.OWNER))) return;

                Component message = TranslationManager.translation("feature.city.mayor.npc.move.prompt")
                        .clickEvent(ClickEvent.callback(audience -> {
                            List<Component> loreItemNPC = List.of(
                                    TranslationManager.translation("feature.city.mayor.npc.move.item.lore")
                            );
                            ItemStack itemToGive = new ItemStack(Material.STICK);
                            ItemMeta itemMeta = itemToGive.getItemMeta();

                            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.npc.move.item.name"));
                            itemMeta.lore(loreItemNPC);
                            itemToGive.setItemMeta(itemMeta);
                            ItemInteraction.runLocationInteraction(
                                    player,
                                    itemToGive,
                                    "mayor:owner-npc-move",
                                    300,
                                    TranslationManager.translation("feature.city.mayor.npc.move.interaction.remaining", Component.text("300s").color(NamedTextColor.GRAY)),
                                    TranslationManager.translation("feature.city.mayor.npc.move.interaction.timeout"),
                                    locationClick -> {
                                        if (locationClick == null) return true;

                                        Chunk chunk = locationClick.getChunk();

                                        City cityByChunk = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());
                                        if (cityByChunk == null) {
                                            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.move.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                                            return false;
                                        }

                                        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                                        if (playerCity == null) {
                                            return false;
                                        }

                                        if (!cityByChunk.getUniqueId().equals(playerCity.getUniqueId())) {
                                            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.move.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                                            return false;
                                        }

                                        NPCManager.moveNPC("owner", locationClick, city.getUniqueId());
                                        NPCManager.updateNPCS(city.getUniqueId());
                                        return true;
                                    },
                                    null
                            );
                        }))
                        .hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.city.mayor.npc.move.hover")));

                MessagesManager.sendMessage(player, message, Prefix.MAYOR, MessageType.INFO, false);

                event.setCancelled(true);
                return;
            }

            new OwnerNpcMenu(player, city, city.getElectionType()).open();
        }
    }
}
