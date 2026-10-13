package fr.openmc.core.features.profile;

import fr.openmc.core.features.profile.command.ProfileCommand;
import fr.openmc.core.features.profile.listeners.ProfileInteractionListener;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.annotations.Credit;

import java.util.Set;

@Credit(developers = {"ar1hurgit"})
public class ProfileManager extends Feature implements HasCommands, HasListeners {
    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new ProfileCommand()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(ProfileInteractionListener::new);
    }
}
