package fr.openmc.core.utils.cooldown;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.process.CommandCondition;

import java.util.List;

public class CooldownInterceptor implements CommandCondition {
    @Override
    public void test(@NotNull CommandActor actor, @NotNull ExecutableCommand command, @NotNull @Unmodifiable List<String> arguments) {

        DynamicCooldown cooldown = command.getAnnotation(DynamicCooldown.class);
        if (cooldown == null) {
            return;
        }

        if (!DynamicCooldownManager.isReady(actor.getUniqueId(), cooldown.group())) {
            long remaining = DynamicCooldownManager.getRemaining(actor.getUniqueId(), cooldown.group());
            String message = cooldown.message();
            message = message.replace("%sec%", String.valueOf(remaining / 1000));
            message = message.replace("%ms%", String.valueOf(remaining));
            actor.reply(message);
            throw new IllegalStateException("Cooldown active");
        }
    }
}
