package fr.openmc.core.features.adminshop.menus.category;

import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.features.adminshop.menus.AdminShopVariantsMenu;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorVariantsMenu extends AdminShopVariantsMenu {
    private static final List<String> COLORS = List.of(
            "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY",
            "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    private static final Map<String, List<Material>> CACHE = new HashMap<>();

    public ColorVariantsMenu(Player owner, String categoryId, ShopItem item) {
        super(owner, categoryId, item);
    }

    @Override
    public List<Material> getVariants() {
        return CACHE.computeIfAbsent(original.getBaseType(), ColorVariantsMenu::buildVariants);
    }

    @Override
    public AdminShopVariantsMenu methodConstructor() {
        return new ColorVariantsMenu(getOwner(), categoryId, original);
    }

    private static List<Material> buildVariants(String baseType) {
        String suffix = switch (baseType) {
            case "GLASS" -> "STAINED_GLASS";
            case "GLASS_PANE" -> "STAINED_GLASS_PANE";
            default -> baseType;
        };

        List<Material> list = new ArrayList<>();

        Material plain = Material.getMaterial(baseType);
        if (plain != null) list.add(plain);

        for (String color : COLORS) {
            Material m = Material.getMaterial(color + "_" + suffix);
            if (m != null) list.add(m);
        }
        return List.copyOf(list);
    }

    @Override
    public @NonNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.color_variants.name", ItemUtils.getItemTranslation(original.getMaterial()));
    }
}
