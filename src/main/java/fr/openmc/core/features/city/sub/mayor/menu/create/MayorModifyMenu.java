package fr.openmc.core.features.city.sub.mayor.menu.create;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorCandidate;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.text.ColorUtils;
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

public class MayorModifyMenu extends Menu {
    public MayorModifyMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.modify.name");
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
        Player player = getOwner();

        MayorCandidate mayorCandidate = MayorManager.getCandidate(player.getUniqueId());
        Perks perk2 = PerkManager.getPerkById(mayorCandidate.getIdChoicePerk2());
        Perks perk3 = PerkManager.getPerkById(mayorCandidate.getIdChoicePerk3());

        assert perk2 != null;
        inventory.put(20, new ItemBuilder(this, perk2.getItemStack(), itemMeta -> {
            itemMeta.customName(TranslationManager.translation(perk2.getNameKey()));
            itemMeta.lore(TranslationManager.translationLore(perk2.getLoreKey()));
        }));

        assert perk3 != null;
        inventory.put(22, new ItemBuilder(this, perk3.getItemStack(), itemMeta -> {
            itemMeta.customName(TranslationManager.translation(perk3.getNameKey()));
            itemMeta.lore(TranslationManager.translationLore(perk3.getLoreKey()));
        }));

        List<Component> loreColor = TranslationManager.translationLore("feature.city.mayor.menu.modify.color.lore");
        inventory.put(24, new ItemBuilder(this, ColorUtils.getMaterialFromColor(mayorCandidate.getCandidateColor()), itemMeta -> {
            itemMeta.itemName(TranslationManager.translation(
                    "feature.city.mayor.menu.modify.color.name",
                    TranslationManager.translation("feature.city.mayor.label.color").color(mayorCandidate.getCandidateColor())
            ));
            itemMeta.lore(loreColor);
        }).setOnClick(inventoryClickEvent -> {
            new MayorColorMenu(player, null, null, null, "change", null).open();
        }));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.common.back.name").color(NamedTextColor.GREEN));
            itemMeta.lore(TranslationManager.translationLore("feature.city.mayor.menu.modify.back.lore"));
        }, true));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
