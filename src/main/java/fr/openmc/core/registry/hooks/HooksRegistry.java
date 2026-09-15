package fr.openmc.core.registry.hooks;

import fr.openmc.api.entity.player.OMCPlayerImpl;
import fr.openmc.core.hooks.*;
import fr.openmc.core.hooks.github.GitHubHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.integration.DatabaseManager;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HooksRegistry extends Registry<String, Hooks>
        implements KeyedRegistry<String, Hooks> {
    private final HashMap<HookLoadingType, List<Hooks>> hookLoad = new HashMap<>();

    public final ProtocolLibHook PROTOCOL_LIB = registerHooks(new ProtocolLibHook(), HookLoadingType.RUNTIME);
    public final PapiHook PAPI = registerHooks(new PapiHook(), HookLoadingType.RUNTIME);
    public final LuckPermsHook LUCK_PERMS = registerHooks(new LuckPermsHook(), HookLoadingType.RUNTIME);
    public final WorldGuardHook WORLD_GUARD = registerHooks(new WorldGuardHook(), HookLoadingType.RUNTIME);
    public final ItemsAdderHook ITEMS_ADDER = registerHooks(new ItemsAdderHook(), HookLoadingType.RUNTIME);
    public final FancyNpcsHook FANCY_NPCS = registerHooks(new FancyNpcsHook(), HookLoadingType.RUNTIME);
    public final GitHubHook GITHUB = registerHooks(new GitHubHook(), HookLoadingType.RUNTIME);
   // public final BedrockHook BEDROCK = registerHooks(new BedrockHook(), HookLoadingType.AFTER_IA);

    public <T extends Hooks> T registerHooks(T value, HookLoadingType type) {
        register(key(value), value);
        hookLoad.computeIfAbsent(type, id -> new ArrayList<>())
                .add(value);
        return value;
    }

    @Override
    public void init() {
        for (Hooks hook : hookLoad.get(HookLoadingType.RUNTIME)) {
            DatabaseManager.startHookDB(hook);
            hook.startInit();
        }
    }

    @Override
    public void postInit() {
        for (Hooks hook : hookLoad.get(HookLoadingType.AFTER_IA)) {
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
