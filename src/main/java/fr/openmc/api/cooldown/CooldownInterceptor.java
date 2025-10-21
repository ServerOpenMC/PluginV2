package fr.openmc.api.cooldown;

import fr.openmc.core.utils.DateUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.process.CommandCondition;

public class CooldownInterceptor implements CommandCondition<BukkitCommandActor> {
    @Override
    public void test(@NotNull ExecutionContext<BukkitCommandActor> context) {
        DynamicCooldown cooldown = context.command().annotations().get(DynamicCooldown.class);
        if (cooldown == null) {
            return;
        }

        Player player = context.actor().requirePlayer();

        if (!DynamicCooldownManager.isReady(player.getUniqueId(), cooldown.group())) {
            long remaining = DynamicCooldownManager.getRemaining(player.getUniqueId(), cooldown.group());
            String message = cooldown.message();
            message = message.replace("%formatTime%", DateUtils.convertSecondToTime(remaining / 1000));
            message = message.replace("%sec%", String.valueOf(remaining / 1000));
            message = message.replace("%ms%", String.valueOf(remaining));
            player.sendMessage(message);
        }
    }
}
