package fr.openmc.core.bootstrap.integration;

import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for generating resource packs for the plugin.
 * This is used for example to generate the resource pack needed for translations.
 * MUST BE LAUNCH IN BOOTSTRAP, else item adder won't be able to load the content of resource pack.
 */
@SuppressWarnings("UnstableApiUsage")
public class ResourcePacksGenerator {

    // https://minecraft.wiki/w/Pack_format
    private static final String FORMAT_VERSION = "75";

    /**
     * Generate the base structure of a resource pack in the plugin's data folder, with the given name.
     * @param folderName the name of the folder to create for the resource pack (ex. "generated-rp-langs")
     * @throws IOException if an error occurs while creating the resource pack folder or writing the metadata file
     */
    public static Path generateBase(BootstrapContext context, String folderName) throws IOException {
        Path resourcePackFolder = context.getDataDirectory().resolve(folderName);

        FilesUtils.deleteDirectory(resourcePackFolder);
        Files.createDirectories(resourcePackFolder);

        generateMetadata(resourcePackFolder, folderName);

        return resourcePackFolder;
    }

    /**
     * Generate the pack.mcmeta file for a resource pack with the given name in the given folder.
     * @param resourcePackFolder the folder where the resource pack is located
     * @param packName the name of the resource pack (used in the description of the metadata)
     * @throws IOException if an error occurs while writing the metadata file
     */
    public static void generateMetadata(Path resourcePackFolder, String packName) throws IOException {
        String metadata = """
                {
                  "pack": {
                    "description": "OMC RESOURCEPACK - %s",
                    "min_format": %s,
                    "max_format": %s
                  }
                }
                """.formatted(packName, FORMAT_VERSION, FORMAT_VERSION);

        Files.writeString(resourcePackFolder.resolve("pack.mcmeta"), metadata);
    }
}
