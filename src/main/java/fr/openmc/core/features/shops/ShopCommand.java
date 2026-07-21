package fr.openmc.core.features.shops;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.managers.PlayerShopManager;
import fr.openmc.core.features.shops.managers.ShopManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("shop")
public class ShopCommand {
    
    @CommandPlaceholder
    @CommandPermission("omc.commands.shop")
    @Description("Create a shop")
    public void createShop(Player player) {
        if (!EconomyManager.hasEnoughMoney(player.getUniqueId(), 500)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.not_enough_money",
                    Component.text("500 " + EconomyManager.getEconomyIcon(), NamedTextColor.RED)), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        PlayerShopManager.startCreatingShop(player);
    }
    
    @Subcommand("bypass")
    @CommandPermission("omc.admins.commands.shop.bypass")
    public void bypass(Player player) {
        if (!ShopManager.shopBypass.contains(player.getUniqueId())) ShopManager.shopBypass.add(player.getUniqueId());
        else ShopManager.shopBypass.remove(player.getUniqueId());
    }
}
