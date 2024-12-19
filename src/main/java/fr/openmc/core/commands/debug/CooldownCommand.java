package fr.openmc.core.commands.debug;

import fr.openmc.core.utils.DynamicCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CooldownCommand {
    private static final DynamicCooldown cooldown = new DynamicCooldown();

    @Command("debug cooldown")
    @CommandPermission("omc.debug.cooldown")
    @AutoComplete("success|error")
    @Description("Test de cooldown")
    public void cooldown(Player player, @Named("isSuccess") String isSuccess) {
        if (!cooldown.isReady(player.getUniqueId())) {
            player.sendMessage(Component.text("Erreur, vous devez attendre "+cooldown.getRemainingTime(player.getUniqueId())+"ms").color(NamedTextColor.RED));
            return;
        }

        if (isSuccess.equals("success")) {
            cooldown.use(player.getUniqueId(), 5000);
            player.sendMessage(Component.text("Succès, le cooldown est activé").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Erreur, vous pouvez refaire la commande").color(NamedTextColor.RED));
        }
    }
}
