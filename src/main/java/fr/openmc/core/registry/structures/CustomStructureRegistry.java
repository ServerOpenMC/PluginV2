package fr.openmc.core.registry.structures;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.hooks.WorldEditHook;
import fr.openmc.core.utils.FilesUtils;

import java.io.File;

public class CustomStructureRegistry extends Registry<String, CustomStructure>
        implements KeyedRegistry<String, CustomStructure>, LoadIfEnable<WorldEditHook> {

    private static final File structureFolder = new File(OMCPlugin.getInstance().getDataFolder(), "data/omc_structure");

    public static final String structureKey = "omc_structure";

    // ** REGISTER STRUCTURES **
    public final CustomStructure WITCH_SHOP = register("test","pillar.nbt");

    public CustomStructureRegistry() {
        FilesUtils.createDirectoryIfNotExists(structureFolder);
    }

    @Override
    public String key(CustomStructure registryObject) {
        return registryObject.getId();
    }

    public CustomStructure register(String name, String fileName) {
        return register(name, new File(structureFolder, fileName));
    }

    public CustomStructure register(String name, File file) {
        if (file == null || name == null) return null;
        if (!file.exists()) return null;
        return KeyedRegistry.super.register(new CustomStructure((structureKey + name), file));
    }
}