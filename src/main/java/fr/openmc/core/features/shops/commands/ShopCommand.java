package fr.openmc.core.features.shops.commands;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.manager.PlayerShopManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("shop")
@Description("Manage shops")
@CommandPermission("omc.commands.shop")
public class ShopCommand {
    
    @Subcommand("create")
    @Description("Create a shop")
    public void createShop(Player player) {
        if (!EconomyManager.hasEnoughMoney(player.getUniqueId(), 500)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.not_enough_money", Component.text("500 " + EconomyManager.getEconomyIcon())), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        PlayerShopManager.startCreatingShop(player);
    }
}
