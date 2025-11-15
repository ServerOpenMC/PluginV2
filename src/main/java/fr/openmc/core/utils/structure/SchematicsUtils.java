package fr.openmc.core.utils.structure;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicsUtils {

    private static final Map<String, CachedSchematic> CACHE = new HashMap<>();

    /**
     * /!\ must be put in ressources folder
     *
     * @param nameSchem Just name of file ex. limbo without .schem
     */
    public static void extractSchematic(String nameSchem) {
        OMCPlugin plugin = OMCPlugin.getInstance();
        File schemFolder = new File(plugin.getDataFolder(), "schem");
        if (!schemFolder.exists()) schemFolder.mkdirs();

        File outFile = new File(schemFolder, nameSchem + ".schem");
        if (outFile.exists()) return;

        try (InputStream in = plugin.getResource("schem/" + nameSchem + ".schem")) {
            if (in == null) {
                plugin.getSLF4JLogger().warn("Le fichier '" + nameSchem + ".schem' est introuvable dans les ressources.");
                return;
            }
            Files.copy(in, outFile.toPath());
            plugin.getSLF4JLogger().info("Fichier '" + nameSchem + ".schem' extrait dans plugins/OpenMC/schem/.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CachedSchematic preload(String group, String name, File file) {
        if (file == null || !file.exists()) return null;
        return CACHE.computeIfAbsent(group + ":" + name, f -> {
            try {
                var format = ClipboardFormats.findByFile(file);
                if (format == null) return null;

                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    Clipboard clipboard = reader.read();

                    Region region = clipboard.getRegion();
                    BlockVector3 min = region.getMinimumPoint();
                    BlockVector3 max = region.getMaximumPoint();

                    //pb vient d'ici
                    int width = min.x() - max.y() + 1;
                    int height = min.y() - max.y() + 1;
                    int length = min.z() - max.z() + 1;

                    List<BlockVector3> baseBlocks = new ArrayList<>();
                    for (BlockVector3 pos : region) {
                        if (pos.y() == min.y()) {
                            var block = clipboard.getBlock(pos);
                            if (block.getBlockType().getMaterial().isSolid()) {
                                baseBlocks.add(pos.subtract(min));
                            }
                        }
                    }

                    return new CachedSchematic(clipboard, file, width, height, length, baseBlocks);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static boolean pasteSchem(World bukkitWorld, CachedSchematic schematic, Location loc, boolean checkFloating) {
        if (schematic == null || schematic.clipboard() == null) return false;

        int baseX = loc.getBlockX();
        int baseY = loc.getBlockY();
        int baseZ = loc.getBlockZ();

        if (checkFloating) {
            int floating = 0, checked = 0;

            for (int i = 0; i < schematic.baseBlocks().size(); i += 3) {
                BlockVector3 rel = schematic.baseBlocks().get(i);
                int worldX = baseX + rel.x();
                int worldZ = baseZ + rel.z();

                Material below = bukkitWorld.getBlockAt(worldX, baseY - 1, worldZ).getType();
                if (below.isAir() || !below.isSolid()) floating++;
                checked++;
            }

            if (checked > 0 && ((double) floating / checked) > 0.4D) {
                return false;
            }
        }

        var weWorld = BukkitAdapter.adapt(bukkitWorld);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            try (EditSession session = WorldEdit.getInstance().newEditSession(weWorld)) {
                Operation op = new ClipboardHolder(schematic.clipboard())
                        .createPaste(session)
                        .to(BlockVector3.at(baseX, baseY, baseZ))
                        .ignoreAirBlocks(true)
                        .build();

                Operations.complete(op);
                session.flushSession();
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    public static void preloadSchematics(Map<String, List<String>> schematicsGroups) {
        for (Map.Entry<String, List<String>> entry : schematicsGroups.entrySet()) {
            String group = entry.getKey();
            for (String name : entry.getValue()) {
                File file = new File(OMCPlugin.getInstance().getDataFolder(), "schem/" + name + ".schem");
                CachedSchematic cached = preload(group, name, file);
                if (cached != null) {
                    CACHE.put(group + ":" + name, cached);
                }
            }
        }
    }

    public static CachedSchematic getCachedSchematic(String group, String name) {
        return CACHE.get(group + ":" + name);
    }

    public record CachedSchematic(Clipboard clipboard, File file, int width, int height, int length,
                                  List<BlockVector3> baseBlocks) {
    }
}
