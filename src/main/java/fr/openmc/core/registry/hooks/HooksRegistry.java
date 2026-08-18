package fr.openmc.core.registry.hooks;

import fr.openmc.core.hooks.*;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.integration.DatabaseManager;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.Registry;

public class HooksRegistry extends Registry<String, Hooks>
        implements KeyedRegistry<String, Hooks> {

    public final ProtocolLibHook PROTOCOL_LIB = register(new ProtocolLibHook());
    public final PapiHook PAPI = register(new PapiHook());
    public final LuckPermsHook LUCK_PERMS = register(new LuckPermsHook());
    public final WorldGuardHook WORLD_GUARD = register(new WorldGuardHook());
    public final ItemsAdderHook ITEMS_ADDER = register(new ItemsAdderHook());
    public final FancyNpcsHook FANCY_NPCS = register(new FancyNpcsHook());
    public final GitHubHook GITHUB = register(new GitHubHook());

    @Override
    public void init() {
        for (Hooks hook : values()) {
            DatabaseManager.startHookDB(hook);
            hook.startInit();
        }
    }

    @Override
    public void stop() {
        for (Hooks hook : values()) {
            hook.startSave();
        }
    }

    @Override
    public String key(Hooks registryObject) {
        return registryObject.getClass().getSimpleName();
    }
}
