package fr.openmc.core.commands.debug;

import fr.openmc.api.cooldown.DynamicCooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Suggest;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CooldownCommand {
    @Command("debug cooldown")
    @CommandPermission("omc.debug.cooldown")
    @Description("Test de cooldown")
    @DynamicCooldown(group="test", messageKey = "command.api.cooldown.debug.must_wait")
    public void cooldown(Player player, @Named("isSuccess") @Suggest({"success", "error"}) String isSuccess) {
        if (isSuccess.equals("success")) {
            player.sendMessage(TranslationManager.translation("command.debug.cooldown.success"));
            DynamicCooldownManager.use(player.getUniqueId(), "test" ,5000);
        } else {
            player.sendMessage(TranslationManager.translation("command.debug.cooldown.error"));
        }
    }
}
