package fr.openmc.core.commands.debug;

import fr.openmc.core.commands.autocomplete.CustomItemAutoComplete;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"customitem", "ci"})
@CommandPermission("omc.admins.commands.customitem")
public class CustomItemCommand {
    @Subcommand("get")
    @CommandPermission("omc.admins.commands.customitem.get")
    public void get(
            Player player,
            @SuggestWith(CustomItemAutoComplete.class) String name,
            @Optional Integer amount
    ) {
        CustomItem item = CustomItemRegistry.getByName(name);

        if (item == null) {
            MessagesManager.sendMessage(player, Component.text("Cet item n'existe pas"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        ItemStack finalItem = item.getBest();
        if (amount != null && amount > 1) {
            finalItem.setAmount(amount);
        }

        player.getInventory().addItem(finalItem);
    }
}
