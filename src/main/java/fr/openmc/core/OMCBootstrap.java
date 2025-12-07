package fr.openmc.core;

import fr.openmc.core.features.dream.registries.DreamEnchantementRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.event.RegistryEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class OMCBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {
                        URI uri = Objects.requireNonNull(getClass().getResource("/datapack")).toURI();

                        event.registrar().discoverPack(uri, "omc");
                    } catch (URISyntaxException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose()
                .newHandler(DreamEnchantementRegistry::loadEnchantmentInBootstrap)
        );
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new OMCPlugin();
    }

}
