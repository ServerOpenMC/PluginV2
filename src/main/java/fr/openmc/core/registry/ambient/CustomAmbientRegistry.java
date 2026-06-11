package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.ambient.contents.DarkAmbient;
import fr.openmc.core.registry.ambient.contents.GoldenAmbient;
import fr.openmc.core.registry.ambient.contents.HellAmbient;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class CustomAmbientRegistry extends Registry<String, CustomAmbient> implements KeyedRegistry<String, CustomAmbient> {
    private final OMCDatapack ambientDatapack = new OMCDatapack("openmc", "omc_ambient");

    // ** REGISTER AMBIENT **
    public final CustomAmbient DARK = register(new DarkAmbient());
    public final CustomAmbient HELL = register(new HellAmbient());
    public final CustomAmbient GOLDEN = register(new GoldenAmbient());

    @Override
    public String key(CustomAmbient registryObject) {
        return registryObject.getId();
    }

    @Override
    public void bootstrap(BootstrapContext context) throws IOException {
        for (CustomAmbient ambient : values()) {
            ambientDatapack.addInjector(ambient.toDimensionTypeInjector());
            if (ambient instanceof TimelineAmbient timelineAmbient) {
                ambientDatapack.addInjector(timelineAmbient.toTimelineInjector(
                        ambientDatapack.getNamespace(), ambient.getId()));
            }
        }

        ambientDatapack.addInjector(new BiomesInjector("omc_ambient").add("empty",
                new BiomesInjector.BiomeBuilder()
                        .grassColor("#FFD700")));

        ambientDatapack.build(context, true); //todo: remettre ça en false
    }
}
