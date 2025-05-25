package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SchematicsUtils {

    /**
     * /!\ must be put in ressources folder
     *
     * @param nameSchem Just name of file ex. limbo without .schem
     */
    public static void extractSchematic(String nameSchem) {
        OMCPlugin plugin = OMCPlugin.getInstance();
        File schemFolder = new File(plugin.getDataFolder(), "schem");
        if (!schemFolder.exists()) {
            schemFolder.mkdirs();
        }

        File outFile = new File(schemFolder, nameSchem + ".schem");
        if (!outFile.exists()) {
            try (InputStream in = plugin.getResource(nameSchem + ".schem")) {
                if (in == null) {
                    plugin.getLogger().warning("Le fichier '" + nameSchem + ".schem' est introuvable dans les ressources.");
                    return;
                }

                Files.copy(in, outFile.toPath());
                plugin.getLogger().info("Fichier '" + nameSchem + ".schem' extrait dans plugins/OpenMC/schem/.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
