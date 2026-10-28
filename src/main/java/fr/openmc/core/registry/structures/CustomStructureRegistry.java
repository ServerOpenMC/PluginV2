package fr.openmc.core.registry.structures;

import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.hooks.WorldEditHook;

import java.io.File;

import static fr.openmc.core.registry.structures.model.CustomStructureManager.STRUCTURE_FOLDER;

public class CustomStructureRegistry extends Registry<String, CustomStructure>
        implements KeyedRegistry<String, CustomStructure>, LoadIfEnable<WorldEditHook> {

    public static final String STRUCTURE_KEY = "omc_structure";

    public final CustomStructure WITCH_SHOP = register("test", "pillar.nbt");
    public final CustomStructure TERROR_HOUSE = register("terror house", "Terrorhouse.schem");

    @Override
    public String key(CustomStructure structure) {
        return structure.getId();
    }

    public CustomStructure register(String name, String fileName) {
        return register(name, new File(STRUCTURE_FOLDER, fileName));
    }

    public CustomStructure register(String name, File file) {
        if (name == null || file == null) {
            throw new IllegalArgumentException("Structure name and file must not be null.");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Structure file not found: " + file.getAbsolutePath());
        }

        return KeyedRegistry.super.register(new CustomStructure(STRUCTURE_KEY + name, file));
    }
}
