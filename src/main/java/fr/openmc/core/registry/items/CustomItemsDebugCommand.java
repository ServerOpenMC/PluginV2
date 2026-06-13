package fr.openmc.core.registry.items;

import fr.openmc.core.OMCRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Optional;

@Command("debug customitems")
@CommandPermission("omc.debug.customitems")
public class CustomItemsDebugCommand {
    private void passTest(Player player, int test, boolean pass) {
        player.sendMessage("Test " + test + ": " + (pass ? "§apassé" : "§céchoué"));
    }

    @Subcommand("is closebutton")
    public void isCloseButton(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        CustomItem closeButton = OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL;

        passTest(player, 1, closeButton.equals(item));
        passTest(player, 2, closeButton.equals("_iainternal:icon_cancel"));
        passTest(player, 3, closeButton.equals(closeButton));
    }

    @Subcommand("hand")
    public void hand(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack mainhand = inv.getItemInMainHand();

        if (mainhand.getAmount() == 0) {
            player.sendMessage("§cVous ne tenez rien en main.");
            return;
        }
        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(mainhand);

        if (item.isPresent()) {
            player.sendMessage(item.get().getId());
        } else {
            player.sendMessage("§cL'item en main n'est pas un custom item.");
        }
    }

    @Subcommand("list")
    public void list(Player player) {
        player.sendMessage("§eListe des custom items :");
        for (String item : OMCRegistry.CUSTOM_ITEMS.keys()) {
            player.sendMessage("§e- " + item);
        }
    }

    @Subcommand("get")
    public void get(Player player, String name) {
        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(name);

        if (item.isPresent()) {
            player.getInventory().addItem(item.get().getBest());
        } else {
            player.sendMessage("§cCet item n'existe pas.");
        }
    }
}
