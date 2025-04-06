package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
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
    public @NotNull String getName() {
        return "Menu des villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();
        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            Mayor mayor = city.getMayor();

            Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            List<Component> loreMayor =  new ArrayList<>(List.of(
                    Component.text("§8§oMaire de " + city.getName())
            ));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());


            inventory.put(3, new ItemBuilder(this, ItemUtils.getPlayerSkull(mayor.getUUID()),itemMeta -> {
                itemMeta.displayName(Component.text("Maire " + mayor.getName()).color(mayor.getMayorColor()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(loreMayor);
            }));

            List<Component> loreOwner =  new ArrayList<>(List.of(
                    Component.text("§8§oPropriétaire de " + city.getName())
            ));
            loreOwner.add(Component.text(""));
            loreOwner.add(Component.text(perk1.getName()));
            loreOwner.addAll(perk1.getLore());

            inventory.put(5, new ItemBuilder(this, ItemUtils.getPlayerSkull(city.getPlayerWith(CPermission.OWNER)),itemMeta -> {
                itemMeta.displayName(Component.text("§ePropriétaire " + Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()));
                itemMeta.lore(loreOwner);
            }));

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme Vide";
            List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(perk1.getLore()) : null;
            inventory.put(10, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.itemName(Component.text(namePerk1));
                itemMeta.lore(lorePerk1);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme Vide";
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(perk2.getLore()) : null;
            inventory.put(13, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.itemName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme Vide";
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(perk3.getLore()) : null;
            inventory.put(16, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.itemName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));
            return inventory;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return inventory;
    }
}
