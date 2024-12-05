package fr.openmc.core.utils.customitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("debug customitems")
@CommandPermission("omc.debug.customitems")
public class CustomItemsDebugCommand {
    private void passTest(Player player, int test, boolean pass) {
        player.sendMessage("Test " + test + ": " + (pass ? "§aPassé" : "§cÉchoué"));
    }

    @Subcommand("is closebutton")
    public void isCloseButton(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        CustomItem closeButton = CustomItemRegistry.getByName("menu:close_button");

        passTest(player, 1, closeButton.equals(item));
        passTest(player, 2, closeButton.equals("menu:close_button"));
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
        CustomItem item = CustomItemRegistry.getByItemStack(mainhand);
        if (item == null) {
            player.sendMessage("§cL'item en main n'est pas un custom item.");
        } else {
            player.sendMessage(item.getName());
        }
    }

    @Subcommand("list")
    public void list(Player player) {
        player.sendMessage("§eListe des custom items:");
        for (String item : CustomItemRegistry.getNames()) {
            player.sendMessage("§e- " + item);
        }
    }

    @Subcommand("get")
    public void get(Player player, String name) {
        CustomItem item = CustomItemRegistry.getByName(name);
        if (item == null) {
            player.sendMessage("§cCet item n'existe pas.");
            return;
        }
        player.getInventory().addItem(item.getBest());
    }
}
