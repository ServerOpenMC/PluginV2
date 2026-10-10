package fr.openmc.core.commands.debug;

import fr.openmc.api.cooldown.DynamicCooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Suggest;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class DebugCooldownCommand {
    @Command("debug cooldown")
    @CommandPermission("omc.debug.cooldown")
    @Description("Test de cooldown")
    @DynamicCooldown(group="test", messageKey = "command.api.cooldown.debug.must_wait")
    public void cooldown(OMCPlayer player, @Named("isSuccess") @Suggest({"success", "error"}) String isSuccess) {
        if (isSuccess.equals("success")) {
            player.sendMessage(TranslationManager.translation("command.debug.cooldown.success"));
            player.cooldown().use("test" ,5000);
        } else {
            player.sendMessage(TranslationManager.translation("command.debug.cooldown.error"));
        }
    }
}
