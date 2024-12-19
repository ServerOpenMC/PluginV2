package fr.openmc.core.commands.debug;

import fr.openmc.core.utils.cooldown.DynamicCooldown;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CooldownCommand {
    private static final DynamicCooldownManager cooldown = new DynamicCooldownManager();

    @Command("debug cooldown")
    @CommandPermission("omc.debug.cooldown")
    @AutoComplete("success|error")
    @Description("Test de cooldown")
    @DynamicCooldown(group="test", message = "§c%ms% (%sec%s)")
    public void cooldown(Player player, @Named("isSuccess") String isSuccess) {
        if (isSuccess.equals("success")) {
            player.sendMessage(Component.text("Succès, le cooldown est activé").color(NamedTextColor.GREEN));
            DynamicCooldownManager.use(player.getUniqueId(), "test" ,5000);
        } else {
            player.sendMessage(Component.text("Erreur, vous pouvez refaire la commande").color(NamedTextColor.RED));
        }
    }
}
