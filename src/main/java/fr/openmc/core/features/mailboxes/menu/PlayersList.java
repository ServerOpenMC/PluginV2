package fr.openmc.core.features.mailboxes.menu;


import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.utils.PaginatedMailbox;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayersList extends PaginatedMailbox<ItemStack> {
    public PlayersList(Player player) {
        super(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) continue;
            if (!MailboxManager.canSend(player, onlinePlayer)) continue;
            pageItems.add(ItemUtils.getPlayerHead(onlinePlayer.getUniqueId()));
        }
        initInventory();
    }

    @Override
    public void openInventory() {
        player.openInventory(this.inventory);
    }
}
