package fr.openmc.core.features.dream.items;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("dream item")
@CommandPermission("omc.admins.commands.dream.item")
public class DreamItemCommand {
    @Subcommand("get")
    public void get(Player player, @SuggestWith(DreamItemAutoComplete.class) String name) {
        DreamItem item = DreamItemRegister.getByName(name);
        if (item == null) {
            player.sendMessage("§cCet item n'existe pas.");
            return;
        }
        player.getInventory().addItem(item.getBest());
    }
}