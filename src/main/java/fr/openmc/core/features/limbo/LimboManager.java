package fr.openmc.core.features.limbo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.limbo.packets.PositionClientPacket;
import fr.openmc.core.features.limbo.packets.RespawnPacket;
import fr.openmc.core.features.limbo.packets.ServerPingPacket;
import fr.openmc.core.features.limbo.packets.TabListPacket;
import fr.openmc.core.features.scoreboards.ScoreboardManager;
import fr.openmc.core.utils.SchematicsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;


public class LimboManager {
    //TODO refaire la structure du limbo (classe par paquet)
    public static final HashMap<UUID, LimboPlayer> playersInLimbo = new HashMap<>();
    private static final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private static ProtocolManager protocolManager;

    public static final Set<UUID> blockedPlayers = new HashSet<>();

    public LimboManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(new LimboListener(), OMCPlugin.getInstance());

        // filteur de packets
        new LimboPacketBlocker().register();

        // enregistreur de packet
        new RespawnPacket(protocolManager);
        new TabListPacket(protocolManager);
        new ServerPingPacket(protocolManager);
        new PositionClientPacket(protocolManager);

        SchematicsUtils.extractSchematic("limbo");

    }

    public static LimboPlayer getLimboPlayer(UUID playerUUID) {
        return playersInLimbo.get(playerUUID);
    }

    public static boolean isInLimbo(UUID playerUUID) {
        return playersInLimbo.containsKey(playerUUID);
    }

    public static void addPlayerToLimbo(Player player) {
        if (!playersInLimbo.containsKey(player.getUniqueId())) {
            playersInLimbo.put(player.getUniqueId(), new LimboPlayer(player, null));
        }
    }

    public static void removePlayerToLimbo(Player player) {
        if (!playersInLimbo.containsKey(player.getUniqueId())) {
            playersInLimbo.remove(player.getUniqueId());
        }
    }


    public static void sendPlayerLimbo(Player player) {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("ProtocolLib n'est pas installé, le Limbo ne peut pas être utilisé.");
            return;
        }

        try {
            saveInventory(player);

            RespawnPacket.send(player);

            System.out.println("Envoi du joueur dans le limbo : " + player.getName());

            addPlayerToLimbo(player);

            ScoreboardManager.removePlayerScoreboard(player);
            ScoreboardManager.createNewScoreboard(player);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void exitLimbo(Player player) {
        if (playersInLimbo.containsKey(player.getUniqueId())) {
            applySavedInventory(player);

            removePlayerToLimbo(player);
        }
    }

    public static void showStructure(File schemFile, Player player, Location location, Runnable whenDone) {
        Location origin = player.getLocation().clone().subtract(19, 34, 25);
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        if (format == null) {
            Bukkit.getLogger().severe("Format inconnu pour le fichier : " + schemFile.getName());
            return;
        }
            try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                Clipboard clipboard = reader.read();
                ClipboardHolder holder = new ClipboardHolder(clipboard);

                Transform transform = holder.getTransform();
                Region region = clipboard.getRegion();
                BlockVector3 min = region.getMinimumPoint();
                Extent transformedExtent = new BlockTransformExtent(clipboard, transform);

                for (int x = min.getX(); x <= region.getMaximumPoint().getX(); x++) {
                    for (int y = min.getY(); y <= region.getMaximumPoint().getY(); y++) {
                        for (int z = min.getZ(); z <= region.getMaximumPoint().getZ(); z++) {
                            BlockVector3 pos = BlockVector3.at(x, y, z);
                            BlockState block = transformedExtent.getBlock(pos);
                            if (block.getBlockType().getMaterial().isAir()) continue;

                            String blockDataString = block.getAsString();

                            try {
                                BlockData blockData = Bukkit.createBlockData(blockDataString);
                                Vector3 transformedVec = transform.apply(pos.toVector3());
                                Location target = origin.clone().add(
                                        transformedVec.getX() - min.getX(),
                                        transformedVec.getY() - min.getY(),
                                        transformedVec.getZ() - min.getZ()
                                );

                                PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                                packet.getBlockPositionModifier().write(0,
                                        new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ()));
                                packet.getBlockData().write(0, WrappedBlockData.createData(blockData));
                                protocolManager.sendServerPacket(player, packet);

                            } catch (IllegalArgumentException e) {
                                Bukkit.getLogger().warning("BlockData invalide : " + blockDataString);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), whenDone);
    }

    private static void saveInventory(Player player) {
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents().clone());

        player.getInventory().clear();
    }

    private static void applySavedInventory(Player player) {
        ItemStack[] saved = savedInventories.remove(player.getUniqueId());
        if (saved != null) {
            player.getInventory().setContents(saved);
        }
        playersInLimbo.remove(player.getUniqueId());

        player.updateInventory();
    }
}