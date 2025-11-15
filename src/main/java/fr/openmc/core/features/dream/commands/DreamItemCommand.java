package fr.openmc.core.features.dream.commands;

import fr.openmc.core.features.dream.commands.autocomplete.DreamItemAutoComplete;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admdream")
@CommandPermission("omc.admins.commands.admndream")
public class DreamItemCommand {
    @Subcommand("item get")
    @CommandPermission("omc.admins.commands.admndream.item.get")
    public void get(Player player, @SuggestWith(DreamItemAutoComplete.class) String name) {
        DreamItem item = DreamItemRegistry.getByName("omc_dream:" + name);
        if (item == null) {
            player.sendMessage("§cCet item n'existe pas.");
            return;
        }
        player.getInventory().addItem(item.getBest());
    }
}