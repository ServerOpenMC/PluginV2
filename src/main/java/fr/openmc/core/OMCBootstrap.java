package fr.openmc.core;

import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.utils.bootstrap.DatapackRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.event.RegistryEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class OMCBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        // ** LOAD DATAPACKS **
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> DatapackRegistry.load(event, DatapackRegistry.extractDatapacks(context.getPluginSource()))
        ));

        // ** ENCHANTMENT IMPL **
        CustomEnchantmentRegistry.init();
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose()
                .newHandler(CustomEnchantmentRegistry::loadEnchantmentInBootstrap)
        );
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new OMCPlugin();
    }

}
