package fr.openmc.core.features.events.contents.deposit;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.deposit.events.GingerbreadDepositEvent;
import fr.openmc.core.features.events.contents.deposit.events.HalloweenPumpkinDepositEvent;

public class DepositEventsRegistry extends Registry<String, DepositEvent>
        implements KeyedRegistry<String, DepositEvent> {

    public final DepositEvent HALLOWEEN = register(new HalloweenPumpkinDepositEvent());
    public final DepositEvent CHRISTMAS = register(new GingerbreadDepositEvent());

    @Override
    public String key(DepositEvent registryObject) {
        return registryObject.getId();
    }

    public DepositEvent getEvent(Class<? extends DepositEvent> eventClass) {
        return entries.values().stream()
                .filter(weeklyEvent -> weeklyEvent.getClass().equals(eventClass))
                .findFirst()
                .orElse(null);
    }
}