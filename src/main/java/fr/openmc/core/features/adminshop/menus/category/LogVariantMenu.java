package fr.openmc.core.features.adminshop.menus.category;

import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.features.adminshop.menus.AdminShopVariantsMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogVariantMenu extends AdminShopVariantsMenu {
    private static final List<Material> VARIANTS = List.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG,
            Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG,
            Material.PALE_OAK_LOG, Material.POPLAR_LOG);

    public LogVariantMenu(Player owner, String categoryId, ShopItem item) {
        super(owner, categoryId, item);
    }

    @Override
    public List<Material> getVariants() {
        return VARIANTS;
    }

    @Override
    public AdminShopVariantsMenu methodConstructor() {
        return new LogVariantMenu(getOwner(), categoryId, original);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.log_variants.name");
    }
}