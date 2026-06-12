package fr.openmc.core.registry.ambient;

import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.ambient.contents.DarkAmbient;
import fr.openmc.core.registry.ambient.contents.GoldenAmbient;
import fr.openmc.core.registry.ambient.contents.HellAmbient;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;

import java.io.IOException;

public class CustomAmbientRegistry extends Registry<String, CustomAmbient> implements KeyedRegistry<String, CustomAmbient> {
    public static final String NAMESPACE = "omc_ambient";
    private final OMCDatapack ambientDatapack = new OMCDatapack("openmc", NAMESPACE);

    // ** REGISTER AMBIENT **
    public final CustomAmbient DARK = register(new DarkAmbient());
    public final CustomAmbient HELL = register(new HellAmbient());
    public final CustomAmbient GOLDEN = register(new GoldenAmbient());

    @Override
    public String key(CustomAmbient registryObject) {
        return registryObject.getId();
    }

    @Override
    public void init() {
        RegistriesLoadConfig.init();

        for (CustomAmbient ambient : values()) {
            ambient.getAmbientBuilder().runInjectors(ambient, ambientDatapack);
        }

        if (RegistriesLoadConfig.isMustRestart() || checkIfAmbientChange()) {
            try {
                ambientDatapack.buildRuntime(() -> {
                    OMCLogger.warnFormatted("ATTENTION! Restart du serveur afin d'appliquer les changements dans les registres");
                    Bukkit.restart();
                });
            } catch (IOException e) {
                OMCLogger.error("Erreur survenue durant le build du datapack lors du runtime {}", e.getMessage());
            }
        } else {
            OMCLogger.infoFormatted("Aucun ambient rajouté, innutile de redémarrer le serveur");
        }
    }

    private boolean checkIfAmbientChange() {
        net.minecraft.core.Registry<Biome> biomeRegistry =
                MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.BIOME);

        int numberOfAmbient = values().stream()
                .filter(ambient -> ambient.getAmbientBuilder().utilizeBiome())
                .toList().size();
        int numberOfVanillaBiome = biomeRegistry.keySet().stream()
                .filter(key -> !key.getNamespace().equals(NAMESPACE))
                .toList().size();

        int numberOfBiomeVariant = numberOfAmbient * numberOfVanillaBiome;
        int numberOfBiomeVarientInDatapack = ambientDatapack.getInjectors().stream()
                .filter(injector -> injector instanceof BiomesInjector).toList().size();

        // * On regarde si notre nombre de variante de biome, est pas égale aux nombres de variente de biome dans le datapack
        // si ça a changé ça veut dire qu'il y a des nouveaux biomes ou des nouvelles ambiences
        return numberOfBiomeVariant != numberOfBiomeVarientInDatapack;
    }
}
