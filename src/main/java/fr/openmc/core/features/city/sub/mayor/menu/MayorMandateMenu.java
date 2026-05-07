package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorMandateMenu extends Menu {

    public MayorMandateMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.mandate.name");
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

        City city = CityManager.getPlayerCity(player.getUniqueId());
        Mayor mayor = city.getMayor();

        Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
        Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
        Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

        List<Component> loreMayor = new ArrayList<>(List.of(
                TranslationManager.translation(
                        "feature.city.mayor.menu.mandate.mayor.lore.header",
                        Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
                )
        ));
        loreMayor.add(Component.empty());
        loreMayor.add(perk2 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                TranslationManager.translation(perk2.getNameKey()));
        loreMayor.addAll(perk2 == null ? List.of() : TranslationManager.translationLore(perk2.getLoreKey()));
        loreMayor.add(Component.empty());
        loreMayor.add(perk3 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                TranslationManager.translation(perk3.getNameKey()));
        loreMayor.addAll(perk3 == null ? List.of() : TranslationManager.translationLore(perk3.getLoreKey()));

        inventory.put(3, new ItemBuilder(this, SkullUtils.getPlayerSkull(mayor.getMayorUUID()), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.mandate.mayor.title", mayor.getName())
                    .color(mayor.getMayorColor()).decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(loreMayor);
        }));

        // ACCES DES LOIS
        // - PVP ENTRE MEMBRES (activé/désactiver) - Maire
        // - Annonce Ville (genre de broadcast ds ville) - Maire
        // - /city warp (donc le setspawnpoint de la ville en gros) => baton de set warp - Maire
        // - Evenement Déclanchable - Maire

        // si le joueur est maire

        if (player.getUniqueId().equals(mayor.getMayorUUID())) {
            List<Component> loreLaw = TranslationManager.translationLore(
                    "feature.city.mayor.menu.mandate.law.lore",
                    TranslationManager.translation("feature.city.mayor.label.mayor")
                            .color(mayor.getMayorColor()).decoration(TextDecoration.ITALIC, false)
            );
            inventory.put(4, new ItemBuilder(this, Material.STONE_BUTTON, itemMeta -> {
                itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.mandate.law.name"));
                itemMeta.lore(loreLaw);
            }).setOnClick(event -> new MayorLawMenu(player).open()));
        }

        List<Component> loreOwner = new ArrayList<>(List.of(
                TranslationManager.translation(
                        "feature.city.mayor.menu.mandate.owner.lore.header",
                        Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
                )
        ));
        loreOwner.add(Component.empty());
        loreOwner.add(perk1 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                TranslationManager.translation(perk1.getNameKey()));
        loreOwner.addAll(perk1 == null ? List.of() : TranslationManager.translationLore(perk1.getLoreKey()));

        inventory.put(5, new ItemBuilder(this, SkullUtils.getPlayerSkull(city.getPlayerWithPermission(CityPermission.OWNER)), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation(
                    "feature.city.mayor.menu.mandate.owner.title",
                    Component.text(CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission((CityPermission.OWNER))).getName())
            ).color(NamedTextColor.YELLOW));
            itemMeta.lore(loreOwner);
        }));

        ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	    Component namePerk1 = (perk1 != null) ? TranslationManager.translation(perk1.getNameKey()) :
                TranslationManager.translation("feature.city.mayor.perk.none.name");
        List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(TranslationManager.translationLore(perk1.getLoreKey())) : null;
        inventory.put(29, new ItemBuilder(this, iaPerk1, itemMeta -> {
            itemMeta.customName(namePerk1);
            itemMeta.lore(lorePerk1);
        }).hide((perk1 != null) ? perk1.getToHide() : null));

        ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	    Component namePerk2 = (perk2 != null) ? TranslationManager.translation(perk2.getNameKey()) :
                TranslationManager.translation("feature.city.mayor.perk.none.name");
        List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(TranslationManager.translationLore(perk2.getLoreKey())) : null;
        inventory.put(22, new ItemBuilder(this, iaPerk2, itemMeta -> {
            itemMeta.customName(namePerk2);
            itemMeta.lore(lorePerk2);
        }).hide((perk2 != null) ? perk2.getToHide() : null));

        ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	    Component namePerk3 = (perk3 != null) ? TranslationManager.translation(perk3.getNameKey()) :
                TranslationManager.translation("feature.city.mayor.perk.none.name");
        List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(TranslationManager.translationLore(perk3.getLoreKey())) : null;
        inventory.put(33, new ItemBuilder(this, iaPerk3, itemMeta -> {
            itemMeta.customName(namePerk3);
            itemMeta.lore(lorePerk3);
        }).hide((perk3 != null) ? perk3.getToHide() : null));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.common.back.name").color(NamedTextColor.GREEN));
            itemMeta.lore(TranslationManager.translationLore("feature.city.mayor.menu.common.back.lore"));
        }, true));

        List<Component> loreInfo = TranslationManager.translationLore("feature.city.mayor.menu.common.more_info.lore");

        inventory.put(52, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.common.more_info.name"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
