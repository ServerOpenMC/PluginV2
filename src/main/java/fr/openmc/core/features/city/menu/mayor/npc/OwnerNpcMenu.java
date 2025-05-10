package fr.openmc.core.features.city.menu.mayor.npc;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.menu.mayor.MayorElectionMenu;
import fr.openmc.core.utils.CacheOfflinePlayer;
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

import java.util.*;

public class OwnerNpcMenu extends Menu {

    private final boolean isMayor;
    private final ElectionType electionType;
    private final City city;

    public OwnerNpcMenu(Player owner, City city, ElectionType electionType, boolean isMayor) {
        super(owner);
        this.city = city;
        this.electionType = electionType;
        this.isMayor = isMayor;
    }

    @Override
    public @NotNull String getName() {
        return "Propriétaire - Mandat";
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
            UUID uuidOwner = city.getPlayerWith(CPermission.OWNER);

            String nameOwner = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName();

            if (electionType == ElectionType.ELECTION) {
                Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                List<Component> loreOwner = new ArrayList<>(List.of(
                        Component.text("§8§oPropriétaire de " + city.getName())
                ));
                loreOwner.add(Component.text(""));
                loreOwner.add(Component.text(perk1.getName()));
                loreOwner.addAll(perk1.getLore());

                inventory.put(4, new ItemBuilder(this, ItemUtils.getPlayerSkull(uuidOwner), itemMeta -> {
                    itemMeta.displayName(Component.text("§ePropriétaire " + nameOwner));
                    itemMeta.lore(loreOwner);
                }));

                ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
                String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme Vide";
                List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(perk1.getLore()) : null;
                inventory.put(13, new ItemBuilder(this, iaPerk1, itemMeta -> {
                    itemMeta.itemName(Component.text(namePerk1));
                    itemMeta.lore(lorePerk1);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }));
            } else {
                Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
                Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
                Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

                List<Component> loreOwner = new ArrayList<>(List.of(
                        Component.text("§8§oPropriétaire de " + city.getName())
                ));
                loreOwner.add(Component.text(""));
                loreOwner.add(Component.text(perk1.getName()));
                loreOwner.addAll(perk1.getLore());
                loreOwner.add(Component.text(""));
                loreOwner.add(Component.text(perk2.getName()));
                loreOwner.addAll(perk2.getLore());
                loreOwner.add(Component.text(""));
                loreOwner.add(Component.text(perk3.getName()));
                loreOwner.addAll(perk3.getLore());

                inventory.put(4, new ItemBuilder(this, ItemUtils.getPlayerSkull(uuidOwner), itemMeta -> {
                    itemMeta.displayName(Component.text("§ePropriétaire " + nameOwner));
                    itemMeta.lore(loreOwner);
                }));

                ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
                String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme Vide";
                List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(perk1.getLore()) : null;
                inventory.put(13, new ItemBuilder(this, iaPerk1, itemMeta -> {
                    itemMeta.itemName(Component.text(namePerk1));
                    itemMeta.lore(lorePerk1);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }));
            }


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