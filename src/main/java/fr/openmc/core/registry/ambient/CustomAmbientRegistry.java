package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.ambient.contents.DarkAmbient;
import fr.openmc.core.registry.ambient.contents.HellAmbient;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class CustomAmbientRegistry extends Registry<String, CustomAmbient> implements KeyedRegistry<String, CustomAmbient> {
    private final OMCDatapack ambientDatapack = new OMCDatapack("openmc", "omc_ambient");

    // ** REGISTER AMBIENT **
    public final CustomAmbient DARK = register(new DarkAmbient());
    public final CustomAmbient HELL = register(new HellAmbient());

    @Override
    public String key(CustomAmbient registryObject) {
        return registryObject.getId();
    }

    @Override
    public void bootstrap(BootstrapContext context) throws IOException {
        for (CustomAmbient ambient : values()) {
            ambientDatapack.addInjector(ambient.toDimensionTypeInjector());
        }

        ambientDatapack.build(context);
    }
}
