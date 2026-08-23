package fr.openmc.core.features.corpse.npc;

import de.oliver.fancynpcs.api.*;
import de.oliver.fancynpcs.api.data.property.NpcVisibility;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.corpse.CorpseManager;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.cache.CachePlayerName;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CorpseNPCManager {

    public static final HashMap<UUID, CorpseNPC> corpseNpcMap = new HashMap<>();

    public static final String COOLDOWN_GROUP = "corpse";

    public static void init() {

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                if (npc.getData().getName().startsWith("corpse-")) {
                    UUID ownerUUID = UUID.fromString(npc.getData().getName().replace("corpse-", ""));
                    if (CorpseManager.hasCorpseDB(ownerUUID)) {
                        corpseNpcMap.put(ownerUUID,
                                new CorpseNPC(
                                    npc,
                                    npc.getData().getLocation(),
                                    ownerUUID,
                                    npc.getData().getEquipment().get(NpcEquipmentSlot.HEAD),
                                    npc.getData().getEquipment().get(NpcEquipmentSlot.CHEST),
                                    npc.getData().getEquipment().get(NpcEquipmentSlot.LEGS),
                                    npc.getData().getEquipment().get(NpcEquipmentSlot.FEET),
                                    Pose.valueOf(npc.getData().getAttributes()
                                            .get(FancyNpcsPlugin.get().getAttributeManager()
                                            .getAttributeByName(EntityType.PLAYER, "pose")).toUpperCase()),
                                    CorpseManager.getCorpsesDB().get(ownerUUID).isKillByPlayer()
                                )
                        );
                    } else {
                        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
                        npc.removeForAll();
                    }
                }
            });

        }, 20L * 30);

    }

    public static boolean createNPCS(Player owner, CorpseNPC corpseNPC) {
        return createNPCS(owner,
                corpseNPC.getLocation(),
                corpseNPC.getHelmet(),
                corpseNPC.getChestplate(),
                corpseNPC.getLeggings(),
                corpseNPC.getBoots(),
                corpseNPC.getPose(),
                corpseNPC.isKillByPlayer()
        );
    }

    public static boolean createNPCS(Player owner, DBCorpse corpse) {
        return createNPCS(owner,
                corpse.getLocation(),
                null,
                null,
                null,
                null,
                Pose.SWIMMING,
                corpse.isKillByPlayer()
        );
    }

    public static boolean createNPCS(Player owner, Location deathLocation, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, Pose pose,  boolean all) {
        if (!FancyNpcsHook.isEnable()) return false;

        UUID ownerUUID = owner.getUniqueId();

        NpcData dataCorpse = new NpcData("corpse-" + ownerUUID, ownerUUID, deathLocation);
        String ownerName = CachePlayerName.getName(ownerUUID);

        if (ownerName == null) return false;

        dataCorpse.setType(EntityType.PLAYER);

        dataCorpse.setSkin(ownerName);

        dataCorpse.setDisplayName("<red>☠ </red><gray><lang:feature.corpse.npc.display.owner:" + ownerName + "></gray>");

        dataCorpse.addEquipment(NpcEquipmentSlot.HEAD, helmet);
        dataCorpse.addEquipment(NpcEquipmentSlot.CHEST, chestplate);
        dataCorpse.addEquipment(NpcEquipmentSlot.LEGS, leggings);
        dataCorpse.addEquipment(NpcEquipmentSlot.FEET, boots);

        dataCorpse.addAttribute(
                FancyNpcsPlugin.get().getAttributeManager()
                        .getAttributeByName(EntityType.PLAYER, "pose"),
                pose.name().toLowerCase()
        );

        if (!all) {
            dataCorpse.setVisibility(NpcVisibility.PERMISSION_REQUIRED);
            owner.addAttachment(OMCPlugin.getInstance(), "fancynpcs.npc." + dataCorpse.getName() + ".see", true);
        }
        else
            dataCorpse.setVisibility(NpcVisibility.ALL);

        Npc npcCorpse = FancyNpcsPlugin.get().getNpcAdapter().apply(dataCorpse);

        corpseNpcMap.put(ownerUUID, new CorpseNPC(npcCorpse, deathLocation, ownerUUID, helmet, chestplate, leggings, boots, pose, all));

        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcCorpse);

        npcCorpse.create();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), npcCorpse::spawnForAll);

        if (DynamicCooldownManager.getCooldowns(owner.getUniqueId()) != null
                && DynamicCooldownManager.getCooldowns(owner.getUniqueId()).containsKey(COOLDOWN_GROUP))
            DynamicCooldownManager.clear(owner.getUniqueId(), COOLDOWN_GROUP, false);
        DynamicCooldownManager.use(owner.getUniqueId(), COOLDOWN_GROUP, 20 * 60 * 60 * 100); // 2h -> 7200000

        return true;
    }

    public static void removeNPCS(UUID ownerUUID) {
        if (!FancyNpcsHook.isEnable()) return;
        if (!corpseNpcMap.containsKey(ownerUUID)) return;

        Npc corpseNpc = corpseNpcMap.remove(ownerUUID).getNpc();

        FancyNpcsPlugin.get().getNpcManager().removeNpc(corpseNpc);
        corpseNpc.removeForAll();
    }

    public static void updateNPCS(Player owner) {
        if (!FancyNpcsHook.isEnable()) return;

        if (!CorpseManager.hasCorpseDB(owner.getUniqueId())) return;

        CorpseNPC corpseNPC = corpseNpcMap.get(owner.getUniqueId());

        if (corpseNPC == null) return;

        if (!owner.isOnline()) return;

        removeNPCS(owner.getUniqueId());
        createNPCS(owner, corpseNPC);
    }

    public static void updateAllNPCS() {
        if (!FancyNpcsHook.isEnable()) return;

        Set<UUID> ownerUUIDs = new HashSet<>(corpseNpcMap.keySet()); // Copie

        for (UUID ownerUUID : ownerUUIDs) {

            if (!CorpseManager.hasCorpseDB(ownerUUID)) continue;

            CorpseNPC corpseNPC = corpseNpcMap.get(ownerUUID);

            if (corpseNPC == null) continue;

            Player owner = Bukkit.getPlayer(ownerUUID);
            if (owner == null || !owner.isOnline()) continue;

            removeNPCS(ownerUUID);
            createNPCS(owner, corpseNPC);
        }
    }

    public static void moveNPC(Location location, UUID ownerUUID) {
        if (!FancyNpcsHook.isEnable()) return;

        if (!CorpseManager.hasCorpseDB(ownerUUID)) return;

        CorpseNPC corpseNPC = corpseNpcMap.get(ownerUUID);
        if (corpseNPC != null) {
            corpseNPC.getNpc().getData().setLocation(location);
            corpseNPC.setLocation(location);
        }
    }

    public static boolean hasNPC(UUID ownerUUID) {
        if (!FancyNpcsHook.isEnable()) return false;
        return corpseNpcMap.containsKey(ownerUUID);
    }

    public static CorpseNPC getNPC(UUID ownerUUID) {
        if (!FancyNpcsHook.isEnable()) return null;
        if (!corpseNpcMap.containsKey(ownerUUID)) return null;
        return corpseNpcMap.get(ownerUUID);
    }
}
