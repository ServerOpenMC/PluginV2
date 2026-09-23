package fr.openmc.core.features.shops;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.managers.PlayerShopManager;
import fr.openmc.core.features.shops.managers.ShopDatabaseManager;
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
    private final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    private final ShopManager shopManager = OMCRegistry.FEATURES.SHOP.get();
    private final PlayerShopManager playerShopManager = OMCRegistry.SHOP_FEATURES.PLAYER_SHOP;
    
    @CommandPlaceholder
    @CommandPermission("omc.commands.shop")
    @Description("Create a shop")
    public void createShop(Player player) {
        if (!economyManager.hasEnoughMoney(player.getUniqueId(), 500)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.shop.player.not_enough_money",
                    Component.text("500 " + economyManager.getEconomyIcon(), NamedTextColor.RED)), Prefix.SHOP, MessageType.ERROR, false);
            return;
        }
        playerShopManager.startCreatingShop(player);
    }
    
    @Subcommand("bypass")
    @CommandPermission("omc.admins.commands.shop.bypass")
    public void bypass(Player player) {
        if (!shopManager.shopBypass.contains(player.getUniqueId())) shopManager.shopBypass.add(player.getUniqueId());
        else shopManager.shopBypass.remove(player.getUniqueId());
    }
}
