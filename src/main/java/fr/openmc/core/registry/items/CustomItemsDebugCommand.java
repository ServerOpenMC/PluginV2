package fr.openmc.core.registry.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        player.sendMessage(TranslationManager.translation("command.registry.custom_items_debug.test",
                Component.text(test),
                TranslationManager.translation(pass ? "command.registry.custom_items_debug.test.passed" : "command.registry.custom_items_debug.test.failed")));
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
            player.sendMessage(TranslationManager.translation("command.registry.custom_items_debug.hand.empty"));
            return;
        }
        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(mainhand);

        if (item.isPresent()) {
            player.sendMessage(item.get().getId());
        } else {
            player.sendMessage(TranslationManager.translation("command.registry.custom_items_debug.hand.not_custom"));
        }
    }

    @Subcommand("list")
    public void list(Player player) {
        player.sendMessage(TranslationManager.translation("command.registry.custom_items_debug.list.title"));
        for (String item : OMCRegistry.CUSTOM_ITEMS.keys()) {
            player.sendMessage(TranslationManager.translation("command.registry.custom_items_debug.list.item",
                    Component.text(item).color(NamedTextColor.YELLOW)));
        }
    }

    @Subcommand("get")
    public void get(Player player, String name) {
        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(name);

        if (item.isPresent()) {
            player.getInventory().addItem(item.get().getBest());
        } else {
            player.sendMessage(TranslationManager.translation("command.registry.custom_items_debug.get.not_found"));
        }
    }
}
