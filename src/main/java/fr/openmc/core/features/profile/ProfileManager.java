package fr.openmc.core.features.profile;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.profile.command.ProfileCommand;
import fr.openmc.core.features.profile.listeners.ProfileInteractionListener;
import org.bukkit.event.Listener;

import java.util.Set;

@Credit(developers = {"ar1hurgit"})
public class ProfileManager extends Feature implements LoadAfterItemsAdder, HasCommands, HasListeners {
    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new ProfileCommand()
        );
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new ProfileInteractionListener()
        );
    }
}
