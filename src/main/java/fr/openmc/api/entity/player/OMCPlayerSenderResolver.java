package fr.openmc.api.entity.player;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.process.SenderResolver;

public class OMCPlayerSenderResolver implements SenderResolver<BukkitCommandActor> {

    @Override
    public boolean isSenderType(@NotNull CommandParameter parameter) {
        return parameter.type() == OMCPlayer.class;
    }

    @Override
    public @NotNull Object getSender(@NotNull Class<?> customSenderType, @NotNull BukkitCommandActor actor, @NotNull ExecutableCommand<BukkitCommandActor> command) {
        return OMCPlayer.of(actor.requirePlayer());
    }
}
