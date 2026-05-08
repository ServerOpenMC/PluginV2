package fr.openmc.core.features.city.sub.mayor.menu.npc;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.bukkit.SkullUtils;
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
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.mayor.name");
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

            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            List<Component> loreMayor = new ArrayList<>(List.of(
                    TranslationManager.translation(
                            "feature.city.mayor.menu.mayor.lore.header",
                            Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
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

        inventory.put(4, new ItemBuilder(this, SkullUtils.getPlayerSkull(city.getPlayerWithPermission(CityPermission.OWNER)), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.mayor.title", city.getMayor().getName()).color(NamedTextColor.YELLOW));
                itemMeta.lore(loreMayor);
            }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	    Component namePerk2 = (perk2 != null) ? TranslationManager.translation(perk2.getNameKey()) :
                TranslationManager.translation("feature.city.mayor.perk.none.name");
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(TranslationManager.translationLore(perk2.getLoreKey())) : null;
        inventory.put(20, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(namePerk2);
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

                                NPCManager.moveNPC("mayor", locationClick, city.getUniqueId());
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
