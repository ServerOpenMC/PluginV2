package fr.openmc.core.features.dream.commands;

import fr.openmc.core.features.dream.commands.autocomplete.DreamItemAutoComplete;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admdream")
@CommandPermission("omc.admins.commands.admindream")
public class DreamItemCommand {
    @Subcommand("item get")
    @CommandPermission("omc.admins.commands.admindream.item.get")
    public void get(
            Player player,
            @SuggestWith(DreamItemAutoComplete.class) String name,
            @Optional Integer amount
    ) {
        DreamItem item = DreamItemRegistry.getByName("omc_dream:" + name);

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