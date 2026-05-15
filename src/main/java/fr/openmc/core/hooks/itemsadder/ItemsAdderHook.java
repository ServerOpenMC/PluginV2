package fr.openmc.core.hooks.itemsadder;

import dev.lone.itemsadder.api.ItemsAdder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.hooks.ApiHook;
import fr.openmc.core.bootstrap.hooks.Hooks;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.hooks.itemsadder.behaviours.BehaviourUpBlock;
import fr.openmc.core.hooks.itemsadder.events.IAItemLoadEvent;
import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholderRegistry;
import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/**
 * Gère toutes les actions spéciales avec items adder (injecteur de namespaces)
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemsAdderHook extends Hooks implements ApiHook<ItemsAdder> {
    @Getter
    private static ItemsAdder api;

    private static final String CONTENTS_FOLDER_NAME = "contents";

    public static boolean isEnable() {
        return Hooks.isEnabled(ItemsAdderHook.class);
    }

    @Override
    protected String getPluginName() {
        return "ItemsAdder";
    }

    @Override
    public void init() {
        api = ApiHook.super.api();

        OMCPlugin.registerEvents(
                new BehaviourUpBlock()
        );
    }

    /**
     * Charge les contents qui sont chargé dans ItemsAdder
     * Appelle {@code IAItemLoadEvent} et donne en meme temps le Yaml de l'item.
     * DOIT ETRE LANCE APRES QUE ITEMS ADDER SOIT COMPLETEMENT CHARGE
     */
    public static void loadContents() {
        File pluginsDir = OMCPlugin.getInstance().getDataFolder().getParentFile(); // * root/pluigns
        File itemsAdderDir = new File(pluginsDir, "ItemsAdder"); // * root/pluigns/ItemsAdder
        File contentDir = new File(itemsAdderDir, CONTENTS_FOLDER_NAME); // * root/pluigns/ItemsAdder/contents

        List<String> contentFoldersName = FilesUtils.listFolderNames(contentDir.getAbsolutePath());

        for (String content : contentFoldersName) {
            File inContentDir = new File(contentDir, content);

            List<File> ymlFiles = FilesUtils.getAllFiles(inContentDir, "yml");

            for (File ymlFile : ymlFiles) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(ymlFile);

                String namespace = config.getString("info.namespace");
                ConfigurationSection itemsSection = config.getConfigurationSection("items");

                if (itemsSection == null) continue;

                for (String itemId : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemId);

                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(),
                            () -> Bukkit.getPluginManager().callEvent(new IAItemLoadEvent(namespace, itemId, itemSection)));
                }
            }
        }
    }

    /**
     * Copie tous les dossiers de contents depuis les ressources du plugin
     * vers plugins/ItemsAdder/CONTENTS_FOLDER_NAME
     */
    public static void copyContentsToItemsAdder(BootstrapContext context, String contentsName) {
        try {
            File pluginsDir = context.getDataDirectory().toFile().getParentFile(); // * root/pluigns
            File itemsAdderDir = new File(pluginsDir, "ItemsAdder"); // * root/pluigns/ItemsAdder
            File contentDir = new File(itemsAdderDir, CONTENTS_FOLDER_NAME); // * root/pluigns/ItemsAdder/contents

            if (!FilesUtils.createDirectoryIfNotExists(contentDir)) {
                OMCLogger.error("Impossible de créer le dossier {}", contentDir.getAbsolutePath());
                return;
            }

            // * Recupere la liste des namespaces qu'il y a dans contents
            List<String> contentFolders = FilesUtils.listFolderNamesInResource(contentsName);

            if (contentFolders.isEmpty()) return;

            // * Charge les registres de nos placeholders
            IAPlaceholderRegistry placeholderRegistry = IAPlaceholderRegistry.loadDefault();
            OMCLogger.successFormatted("{} placeholders ItemsAdder chargés", placeholderRegistry.getPlaceholdersCount());

            // * Copie chaque dossier de contenu
            for (String folder : contentFolders) {
                copyContentFolder(folder, contentDir, placeholderRegistry);
            }

            OMCLogger.successFormatted("Contenus ItemsAdder copiés avec succès");
        } catch (Exception e) {
            OMCLogger.error("Erreur lors de la copie des contenus ItemsAdder", e);
        }
    }

    /**
     * Copie un dossier de contenu depuis les ressources vers le dossier ItemsAdder
     *
     * @param folderName Nom du dossier à copier
     * @param targetDir Dossier destination
     */
    private static void copyContentFolder(String folderName, File targetDir,
                                          IAPlaceholderRegistry placeholderRegistry) {
        try {
            File destFolder = new File(targetDir, folderName);

            // * On supprime le dossier qui se trouve déjà ds contents
            if (destFolder.exists()) {
                FilesUtils.deleteDirectory(destFolder);
            }

            // * On crée le dossier si il n'est pas fait
            if (!FilesUtils.createDirectoryIfNotExists(destFolder)) {
                OMCLogger.warn("Impossible de créer le dossier {}", destFolder.getAbsolutePath());
                return;
            }

            // * On copie les resources contents vers la plugins/ItemAdder/contents
            FilesUtils.copyResourceFolder(CONTENTS_FOLDER_NAME + "/" + folderName, destFolder,
                    placeholderRegistry::applyPlaceholders);

            OMCLogger.debug("Dossier {} copié avec succès", folderName);
        } catch (Exception e) {
            OMCLogger.warn("Erreur lors de la copie du dossier {}: {}", folderName, e.getMessage());
        }
    }

    @Override
    public Class<ItemsAdder> apiClass() {
        return ItemsAdder.class;
    }
}
