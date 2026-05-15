package fr.openmc.core.features.city.sub.mascots.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotType;
import fr.openmc.core.features.city.sub.milestone.rewards.MascotsSkinUnlockRewards;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.changeMascotsSkin;

public class MascotsSkinMenu extends Menu {

    private final Sound selectSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    private final Sound deniedSound = Sound.BLOCK_NOTE_BLOCK_BASS;
    private final Material egg;
    private final Mascot mascots;

    public MascotsSkinMenu(Player owner, Material egg, Mascot mascots) {
        super(owner);
        this.egg = egg;
        this.mascots = mascots;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mascots.menu.skin.name");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> map = new HashMap<>();

        City playerCity = CityManager.getPlayerCity(getOwner().getUniqueId());

        if (playerCity == null) return map;

        for (MascotType mascotType : MascotType.values()) {
            map.put(mascotType.getSlot(), createMascotButton(playerCity, mascotType));
        }

        map.put(18, new ItemBuilder(this, Material.ARROW, meta -> {
            meta.displayName(TranslationManager.translation("messages.menus.back"));
            meta.lore(TranslationManager.translationLore("messages.menus.back_lore"));
        }, true));

        return map;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private ItemBuilder createMascotButton(City city, MascotType type) {
        List<Component> loreMascots = new ArrayList<>();

        if (city.getLevel() < MascotsSkinUnlockRewards.getLevelRequiredSkin(type)) {
            loreMascots.add(TranslationManager.translation(
                    "feature.city.mascots.menu.skin.lore.level_required",
                    Component.text(MascotsSkinUnlockRewards.getLevelRequiredSkin(type)).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
            ));
        } else {
            loreMascots.add(TranslationManager.translation(
                    "feature.city.mascots.menu.skin.lore.price_required",
                    Component.text(type.getPrice()).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
            ));
        }

        return new ItemBuilder(this, type.getMascotItem(egg.equals(type.getSpawnEgg())),
                meta -> meta.lore(loreMascots))
                .setOnClick(event -> {
                    if (city.getLevel() < MascotsSkinUnlockRewards.getLevelRequiredSkin(type)) {
                        MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.city.mascots.menu.skin.error.level_required"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }
                    if (!egg.equals(type.getSpawnEgg())) {
                        int aywenite = type.getPrice();
                        ItemStack ISAywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
                        if (ItemUtils.hasEnoughItems(getOwner(), ISAywenite, aywenite)) {
                            changeMascotsSkin(mascots, type.getEntityType(), getOwner(), aywenite);
                            getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                            getOwner().closeInventory();
                        } else {
                            MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.city.mascots.menu.skin.error.not_enough_aywenite"), Prefix.CITY, MessageType.ERROR, false);
                            getOwner().closeInventory();
                        }
                    } else {
                        getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
                    }
        });
    }
}