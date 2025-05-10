package fr.openmc.core.features.city.menu.mayor.npc;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.menu.mayor.MayorElectionMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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

public class MayorNpcMenu extends Menu {
    private final City city;

    public MayorNpcMenu(Player owner, City city) {
        super(owner);
        this.city = city;
    }

    @Override
    public @NotNull String getName() {
        return "Maire - Mandat";
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
            Mayor mayor = city.getMayor();

            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            List<Component> loreMayor = new ArrayList<>(List.of(
                    Component.text("§8§oMaire de " + city.getName())
            ));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());

            inventory.put(4, new ItemBuilder(this, ItemUtils.getPlayerSkull(city.getPlayerWith(CPermission.OWNER)), itemMeta -> {
                itemMeta.displayName(Component.text("§eMaire " + city.getMayor().getName()));
                itemMeta.lore(loreMayor);
            }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme Vide";
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(perk2.getLore()) : null;
            inventory.put(11, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme Vide";
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(perk3.getLore()) : null;
            inventory.put(15, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            if (mayor.getUUID().equals(player.getUniqueId())) {
                inventory.put(18, new ItemBuilder(this, Material.ENDER_PEARL, itemMeta -> {
                    itemMeta.itemName(Component.text("§aDéplacer ce NPC"));
                    itemMeta.lore(List.of(
                            Component.text("§7Vous allez retourner au Menu des Elections"),
                            Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                    ));
                }).setOnClick(inventoryClickEvent -> {
                    MayorElectionMenu menu = new MayorElectionMenu(player);
                    menu.open();
                }));
            }
            return inventory;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return null;
    }
}