package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreInfoMenu extends Menu {

    public MoreInfoMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.more_info.name");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-38::mayor:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        List<Component> lore0 = TranslationManager.translationLore(
                "feature.city.mayor.menu.more_info.elections.lore",
                Component.text(MayorManager.MEMBER_REQUEST_ELECTION).color(NamedTextColor.GOLD)
        );

        List<Component> lore1 = TranslationManager.translationLore("feature.city.mayor.menu.more_info.reforms.lore");


        int phase = MayorManager.phaseMayor;

        inventory.put(11, new ItemBuilder(this, Material.ORANGE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.more_info.elections.title"));
            itemMeta.lore(lore0);
            itemMeta.setEnchantmentGlintOverride(phase != 2);
        }));

        inventory.put(15, new ItemBuilder(this, Material.CYAN_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.more_info.reforms.title"));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(phase == 2);
        }));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.common.back.name")), true));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
