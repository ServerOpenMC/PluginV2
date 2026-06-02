package fr.openmc.core.registry.mobs;

import java.util.function.Function;

public record CustomMobEntry(
        String id,
        Function<String, CustomMob<?>> factory
) { }
