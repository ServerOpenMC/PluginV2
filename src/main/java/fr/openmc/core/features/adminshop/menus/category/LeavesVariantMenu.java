package fr.openmc.core.features.adminshop.menus.category;

import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.features.adminshop.menus.AdminShopVariantsMenu;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeavesVariantMenu extends AdminShopVariantsMenu {
    private static final List<Material> VARIANTS = List.of(
            Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES, Material.JUNGLE_LEAVES,
            Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES,
            Material.PALE_OAK_LEAVES, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES,
            Material.ORANGE_POPLAR_LEAVES, Material.YELLOW_POPLAR_LEAVES, Material.RED_POPLAR_LEAVES, Material.DIAMOND,
            Material.ABANDONED_CAMP_MAP, Material.MAP, Material.AMETHYST_BLOCK, Material.ARMS_UP_POTTERY_SHERD, Material.MELON, Material.OPEN_EYEBLOSSOM);

    public LeavesVariantMenu(Player owner, String categoryId, ShopItem item) {
        super(owner, categoryId, item);
    }

    @Override
    public List<Material> getVariants() {
        return VARIANTS;
    }

    @Override
    public AdminShopVariantsMenu methodConstructor() {
        return new LeavesVariantMenu(getOwner(), categoryId, original);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.leaves_variants.name");
    }
}