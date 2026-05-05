package fr.openmc.core.features.city.sub.mayor.menu.npc;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OwnerNpcMenu extends Menu {

    private final ElectionType electionType;
    private final City city;

    public OwnerNpcMenu(Player owner, City city, ElectionType electionType) {
        super(owner);
        this.city = city;
        this.electionType = electionType;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.owner.name");
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

        Mayor mayor = city.getMayor();
        UUID uuidOwner = city.getPlayerWithPermission((CityPermission.OWNER));

        String nameOwner = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission((CityPermission.OWNER))).getName();

        Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
        if (electionType == ElectionType.ELECTION) {
            List<Component> loreOwner = new ArrayList<>(List.of(
                    TranslationManager.translation(
                            "feature.city.mayor.menu.owner.lore.header",
                            Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
                    )
            ));
            loreOwner.add(Component.empty());
	        loreOwner.add(perk1 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                    TranslationManager.translation(perk1.getNameKey()));
            loreOwner.addAll(perk1 == null ? List.of() : TranslationManager.translationLore(perk1.getLoreKey()));

            inventory.put(4, new ItemBuilder(this, SkullUtils.getPlayerSkull(uuidOwner), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.owner.title", Component.text(nameOwner)).color(NamedTextColor.YELLOW));
                itemMeta.lore(loreOwner);
            }));

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        Component namePerk1 = (perk1 != null) ? TranslationManager.translation(perk1.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(TranslationManager.translationLore(perk1.getLoreKey())) : null;
            inventory.put(31, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.itemName(namePerk1);
                itemMeta.lore(lorePerk1);
            }).hide(perk1 == null ? null : perk1.getToHide()));
        } else {
            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            List<Component> loreOwner = new ArrayList<>(List.of(
                    TranslationManager.translation(
                            "feature.city.mayor.menu.owner.lore.header",
                            Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
                    )
            ));
            loreOwner.add(Component.empty());
	        loreOwner.add(perk1 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                    TranslationManager.translation(perk1.getNameKey()));
            loreOwner.addAll(perk1 == null ? List.of() : TranslationManager.translationLore(perk1.getLoreKey()));
            loreOwner.add(Component.empty());
	        loreOwner.add(perk2 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                    TranslationManager.translation(perk2.getNameKey()));
            loreOwner.addAll(perk2 == null ? List.of() : TranslationManager.translationLore(perk2.getLoreKey()));
            loreOwner.add(Component.empty());
	        loreOwner.add(perk3 == null ? TranslationManager.translation("feature.city.menus.common.error") :
                    TranslationManager.translation(perk3.getNameKey()));
            loreOwner.addAll(perk3 == null ? List.of() : TranslationManager.translationLore(perk3.getLoreKey()));

            inventory.put(4, new ItemBuilder(this, SkullUtils.getPlayerSkull(uuidOwner), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.owner.title", Component.text(nameOwner)).color(NamedTextColor.YELLOW));
                itemMeta.lore(loreOwner);
            }));

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        Component namePerk1 = (perk1 != null) ? TranslationManager.translation(perk1.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(TranslationManager.translationLore(perk1.getLoreKey())) : null;
            inventory.put(20, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.itemName(namePerk1);
                itemMeta.lore(lorePerk1);
            }).hide(perk1 == null ? null : perk1.getToHide()));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        Component namePerk2 = (perk2 != null) ? TranslationManager.translation(perk2.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(TranslationManager.translationLore(perk2.getLoreKey())) : null;
            inventory.put(22, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.itemName(namePerk2);
                itemMeta.lore(lorePerk2);
            }).hide(perk2 == null ? null : perk2.getToHide()));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        Component namePerk3 = (perk3 != null) ? TranslationManager.translation(perk3.getNameKey()) :
                    TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(TranslationManager.translationLore(perk3.getLoreKey())) : null;
            inventory.put(24, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(namePerk3);
                itemMeta.lore(lorePerk3);
            }).hide(perk3 == null ? null : perk3.getToHide()));
        }

        if (mayor.getMayorUUID().equals(player.getUniqueId())) {
            inventory.put(46, new ItemBuilder(this, Material.ENDER_PEARL, itemMeta -> {
                itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.npc.move.name").color(NamedTextColor.GREEN));
                itemMeta.lore(TranslationManager.translationLore("feature.city.mayor.menu.npc.move.lore"));
            }).setOnClick(inventoryClickEvent -> {
                List<Component> loreItemNPC = List.of(
                        TranslationManager.translation("feature.city.mayor.npc.move.item.lore")
                );
                ItemStack itemToGive = new ItemStack(Material.STICK);
                ItemMeta itemMeta = itemToGive.getItemMeta();

                itemMeta.displayName(TranslationManager.translation("feature.city.mayor.npc.move.item.name"));
                itemMeta.lore(loreItemNPC);
                itemToGive.setItemMeta(itemMeta);
                ItemInteraction.runLocationInteraction(
                        player,
                        itemToGive,
                        "mayor:owner-npc-move",
                        300,
                        TranslationManager.translation("feature.city.mayor.npc.move.interaction.remaining", Component.text("300s").color(NamedTextColor.GRAY)),
                        TranslationManager.translation("feature.city.mayor.npc.move.interaction.timeout"),
                        locationClick -> {
                            if (locationClick == null) return true;

                            Chunk chunk = locationClick.getChunk();

                            City cityByChunk = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());
                            if (cityByChunk == null) {
                                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.move.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                                return false;
                            }

                            City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                            if (playerCity == null) {
                                return false;
                            }

                            if (!cityByChunk.getUniqueId().equals(playerCity.getUniqueId())) {
                                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.npc.move.error.outside_city"), Prefix.CITY, MessageType.ERROR, false);
                                return false;
                            }

                            NPCManager.moveNPC("owner", locationClick, city.getUniqueId());
                            NPCManager.updateNPCS(city.getUniqueId());
                            return true;
                        },
                        null
                );
            }));
        }
        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
