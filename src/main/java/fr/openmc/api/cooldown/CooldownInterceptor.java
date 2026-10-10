package fr.openmc.api.cooldown;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.process.CommandCondition;

public class CooldownInterceptor implements CommandCondition<BukkitCommandActor> {
    @Override
    public void test(@NotNull ExecutionContext<BukkitCommandActor> context) {
        DynamicCooldown cooldown = context.command().annotations().get(DynamicCooldown.class);
        if (cooldown == null) return;

        OMCPlayer player = OMCPlayer.of(context.actor().requirePlayer());

        if (!player.cooldown().isReady(cooldown.group())) {
            long remaining = player.cooldown().getRemaining(cooldown.group());

            Component message = TranslationManager.translation(
                    cooldown.messageKey(),
                    Component.text(remaining / 1000),
                    Component.text(remaining),
                    Component.text(DateUtils.convertSecondToTime(remaining / 1000))
            ).color(NamedTextColor.RED);

            player.sendMessage(message);
        }
    }
}
