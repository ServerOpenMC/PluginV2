package fr.openmc.core.features.adminshop;

import fr.openmc.core.features.adminshop.autocomplete.AdminShopCategoryAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class AdminShopCommand {
    @Command("adminshop")
    @Description("Ouvrir le menu du shop admin, ou directement une de ses catégories")
    @CommandPermission("omc.commands.adminshop")
    public void openAdminShop(
            Player player,
            @Named("category") @Optional @SuggestWith(AdminShopCategoryAutoComplete.class) String categoryName
    ) {
        if (categoryName == null || categoryName.isBlank()) {
            AdminShopManager.openMainMenu(player);
            return;
        }

        ShopCategory category = AdminShopManager.findCategory(categoryName);

        if (category == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.adminshop.command.category_not_found",
                            Component.text(categoryName).color(NamedTextColor.YELLOW)
                    ),
                    Prefix.ADMINSHOP, MessageType.ERROR, true);
            return;
        }

        AdminShopManager.openCategoryMenu(player, category.id());
    }
}
