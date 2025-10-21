package fr.openmc.core.commands.debug;

import fr.openmc.api.cooldown.DynamicCooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
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
    @DynamicCooldown(group="test", message = "§c%ms% (%sec%s)")
    public void cooldown(Player player, @Named("isSuccess") @Suggest({"success", "error"}) String isSuccess) {
        if (isSuccess.equals("success")) {
            player.sendMessage(Component.text("Succès, le cooldown est activé").color(NamedTextColor.GREEN));
            DynamicCooldownManager.use(player.getUniqueId(), "test" ,5000);
        } else {
            player.sendMessage(Component.text("Erreur, vous pouvez refaire la commande").color(NamedTextColor.RED));
        }
    }
}
