package fr.openmc.core.features.dream.mecanism.sfx;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.hooks.FancyNpcsHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCloneNpc {
    private static final String prefixNpc = "npcp-";
    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();
    private static final HashMap<UUID, BukkitTask> particlesTasks = new HashMap<>();

    /**
     * Intitialize the PlayerCloneNpc system.
     * This method should be called during the plugin's initialization phase.
     * It fetches all existing NPCs after a delay to ensure that the FancyNpcs plugin has fully initialized,
     * and removes any NPCs that match the player clone prefix.
     */
    public static void init() {
        // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                    if (npc.getData().getName().startsWith(prefixNpc)) {
                        deleteCloneNpc(npc);
                    }
                });
        }, FancyNpcsHook.FANCY_INIT_DELAY);
    }

    /**
     * Get the clone NPC associated with a player, if it exists.
     * @param player The player for whom to retrieve the clone NPC.
     * @return The clone NPC associated with the player, or null if no such NPC exists.
     */
    public static Npc getCloneNpc(Player player) {
        return NPC_MANAGER.getNpc(prefixNpc + player.getUniqueId());
    }

    /**
     * Creates a clone NPC for the specified player at the given sleeping location.
     * @param player The player for whom to create the clone NPC.
     * @param sleepingLocation The location where the player is sleeping, which will be used to position the clone NPC.
     */
    public static void createCloneNpc(Player player, Location sleepingLocation, Pose pose) {
        if (!FancyNpcsHook.isEnable()) return;
        Npc existingNpc = getCloneNpc(player);
        if (existingNpc != null) {
            deleteCloneNpc(existingNpc);
        }
        String npcUUID = prefixNpc + player.getUniqueId();

        sleepingLocation = getExactSleepingLocation(sleepingLocation);
        NpcData data = new NpcData(npcUUID, player.getUniqueId(), sleepingLocation.clone());

        // * Set Configure type
        data.setType(EntityType.PLAYER);
        data.setDisplayName("<empty>");

        // * Player clone specific attributes
        data.setSkin(player.getName());
        data.addAttribute(FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "pose"), pose.name().toLowerCase());

        // * Set equipement
        Map<NpcEquipmentSlot, ItemStack> equipment = new HashMap<>();
        PlayerInventory inv = player.getInventory();

        equipment.put(NpcEquipmentSlot.HEAD, inv.getHelmet());
        equipment.put(NpcEquipmentSlot.CHEST, inv.getChestplate());
        equipment.put(NpcEquipmentSlot.LEGS, inv.getLeggings());
        equipment.put(NpcEquipmentSlot.FEET, inv.getBoots());
        equipment.put(NpcEquipmentSlot.MAINHAND, inv.getItemInMainHand());
        equipment.put(NpcEquipmentSlot.OFFHAND, inv.getItemInOffHand());

        data.setEquipment(equipment);

        // * Register and spawn npc
        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        NPC_MANAGER.registerNpc(npc);
        npc.create();
        npc.spawnForAll();

        // * SFX
        particlesTasks.put(player.getUniqueId(),
                new CloneParticlesTask(sleepingLocation.add(0, 1, 0)).runTaskTimer(OMCPlugin.getInstance(), 0L, 40L)
        );
    }

    /**
     * Deletes the specified clone NPC, removing it from the world and unregistering it from the NPC manager.
     * @param player The player whose clone NPC should be deleted. If the player does not have a clone NPC, this method will do nothing.
     */
    public static void deleteCloneNpc(Player player) {
        if (!FancyNpcsHook.isEnable()) return;
        Npc npc = getCloneNpc(player);
        if (npc == null) return;

        deleteCloneNpc(npc);
    }

    /**
     * Deletes the specified clone NPC, removing it from the world and unregistering it from the NPC manager.
     * @param npc The clone NPC to be deleted. If the NPC does not exist or is not a clone NPC, this method will do nothing.
     */
    public static void deleteCloneNpc(Npc npc) {
        if (!FancyNpcsHook.isEnable()) return;
        if (npc == null) return;
        if (NPC_MANAGER.getNpc(npc.getData().getName()) == null) return;

        Npc toRemove = NPC_MANAGER.getNpc(npc.getData().getName());
        // * cancel particles task
        UUID owner = npc.getData().getCreator();
        if (particlesTasks.containsKey(owner)) {
            particlesTasks.remove(owner).cancel();
        }

        // * remove npc
        toRemove.removeForAll();
        NPC_MANAGER.removeNpc(toRemove);
    }

    /**
     * Calculates the exact location where the clone NPC should be spawned based on the player's sleeping location.
     * @param sleepingLocation The location where the player is sleeping
     * @return The adjusted location where the clone NPC should be spawned, with the correct position and orientation to match the player's sleeping position on the bed.
     */
    public static Location getExactSleepingLocation(Location sleepingLocation) {
        Block bed = sleepingLocation.getBlock();

        // * fallback
        if (!(bed.getBlockData() instanceof Bed bedData)) {
            return sleepingLocation;
        }

        // * on part du pied de lit
        Block footBlock = bedData.getPart() == Bed.Part.FOOT ? bed : bed.getRelative(bedData.getFacing().getOppositeFace());

        // * on prends un yaw correspondant a la directon du lit
        float yaw = switch (bedData.getFacing()) {
            case NORTH -> -90f;
            case SOUTH -> 90f;
            case EAST -> 0f;
            case WEST -> 180f;
            default -> 0f;
        };

        // * on calcul nos offsets pour centrer le npc sur le lit
        BlockFace facing = bedData.getFacing();
        double offsetX = facing.getModX() * 0.5;
        double offsetZ = facing.getModZ() * 0.5;

        // * on return la loc finale
        Location result = footBlock.getLocation().add(0.5 - offsetX, 0.6, 0.5 - offsetZ);
        result.setYaw(yaw);
        result.setPitch(0f);
        return result;
    }
}
