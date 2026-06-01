package fr.openmc.core;

import fr.openmc.core.bootstrap.integration.DatapackLoader;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Bootstrap Paper du plugin OpenMC.
 * Enregistre le datapack et les registres avant la creation du plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public class OMCBootstrap implements PluginBootstrap {
    /**
     * Configure les handlers de cycle de vie necessaires avant l'activation du plugin.
     *
     * @param context Contexte de bootstrap Paper
     * @throws RuntimeException Si le datapack ne peut pas etre chargé
     */
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        OMCLogger.setBootstrapLogger(context.getLogger());

        // ** LOAD DATAPACKS **
        DatapackLoader.loadAllInResource(context);

        // ** LOAD ITEMS ADDER NAMESPACES **
        ItemsAdderHook.copyContentsToItemsAdder(context, "contents");

        // ** REGISTRY MANAGER **
        OMCRegistry.bootstrapAll(context);

        // ** LOAD TRANSLATION **
        // this creates resource pack who is needed for item adder
        TranslationManager.init(
                context,
                Locale.FRANCE,
                Locale.US,
                Locale.UK
        );
    }

    /**
     * Construit l'instance principale du plugin.
     *
     * @param context Contexte du provider Paper
     * @return Instance du plugin principal
     */
    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new OMCPlugin();
    }

}
