package fr.openmc.core.hooks.itemsadder;

import fr.openmc.core.bootstrap.hooks.Hooks;
import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholderRegistry;
import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.File;
import java.util.List;

/**
 * Gère toutes les actions spéciales avec items adder (injecteur de namespaces)
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemsAdderHook extends Hooks {
    private static final String CONTENTS_FOLDER_NAME = "contents";

    public static boolean isEnable() {
        return Hooks.isEnabled(ItemsAdderHook.class);
    }

    @Override
    protected String getPluginName() {
        return "ItemsAdder";
    }

    /**
     * Copie tous les dossiers de contents depuis les ressources du plugin
     * vers plugins/ItemsAdder/CONTENTS_FOLDER_NAME
     */
    public static void copyContentsToItemsAdder(BootstrapContext context, String contentsName) {
        ComponentLogger logger = context.getLogger();
        try {
            File pluginsDir = context.getDataDirectory().toFile().getParentFile(); // * root/pluigns
            File itemsAdderDir = new File(pluginsDir, "ItemsAdder"); // * root/pluigns/ItemsAdder
            File contentDir = new File(itemsAdderDir, CONTENTS_FOLDER_NAME); // * root/pluigns/ItemsAdder/contents

            if (!FilesUtils.createDirectoryIfNotExists(contentDir)) {
                logger.error("Impossible de créer le dossier {}", contentDir.getAbsolutePath());
                return;
            }

            // * Recupere la liste des namespaces qu'il y a dans contents
            List<String> contentFolders = FilesUtils.listFolderNamesInResource(logger, contentsName);

            if (contentFolders.isEmpty()) return;

            // * Charge les registres de nos placeholders
            IAPlaceholderRegistry placeholderRegistry = IAPlaceholderRegistry.loadDefault();
            logger.info("\u001B[32m{} placeholders ItemsAdder chargés\u001B[0m", placeholderRegistry.getPlaceholdersCount());

            // * Copie chaque dossier de contenu
            for (String folder : contentFolders) {
                copyContentFolder(context, folder, contentDir, placeholderRegistry);
            }

            logger.info("\u001B[32m✔ Contenus ItemsAdder copiés avec succès\u001B[0m");
        } catch (Exception e) {
            logger.error("Erreur lors de la copie des contenus ItemsAdder", e);
        }
    }

    /**
     * Copie un dossier de contenu depuis les ressources vers le dossier ItemsAdder
     *
     * @param folderName Nom du dossier à copier
     * @param targetDir Dossier destination
     */
    private static void copyContentFolder(BootstrapContext context, String folderName, File targetDir,
                                          IAPlaceholderRegistry placeholderRegistry) {
        ComponentLogger logger = context.getLogger();
        try {
            File destFolder = new File(targetDir, folderName);

            // * On supprime le dossier qui se trouve déjà ds contents
            if (destFolder.exists()) {
                FilesUtils.deleteDirectory(logger, destFolder);
            }

            // * On crée le dossier si il n'est pas fait
            if (!FilesUtils.createDirectoryIfNotExists(destFolder)) {
                logger.warn("Impossible de créer le dossier {}", destFolder.getAbsolutePath());
                return;
            }

            // * On copie les resources contents vers la plugins/ItemAdder/contents
            FilesUtils.copyResourceFolder(logger, CONTENTS_FOLDER_NAME + "/" + folderName, destFolder,
                    content -> placeholderRegistry.applyPlaceholders(content, logger));

            logger.debug("Dossier {} copié avec succès", folderName);
        } catch (Exception e) {
            logger.warn("Erreur lors de la copie du dossier {}: {}", folderName, e.getMessage());
        }
    }
}
