package fr.openmc.core.features.city.sub.mayor.menu.npc;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
    public @NotNull String getName() {
        return "Menu des Maires - Mandat du Maire";
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
                    Component.text("§8§oMaire de " + city.getName())
            ));
        loreMayor.add(Component.empty());
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
        loreMayor.add(Component.empty());
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());

            inventory.put(4, new ItemBuilder(this, ItemUtils.getPlayerSkull(city.getPlayerWithPermission(CPermission.OWNER)), itemMeta -> {
                itemMeta.displayName(Component.text("§eMaire " + city.getMayor().getName()));
                itemMeta.lore(loreMayor);
            }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme Vide";
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(perk2.getLore()) : null;
        inventory.put(20, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
            }).hide(perk2.getToHide()));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme Vide";
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(perk3.getLore()) : null;
        inventory.put(24, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
            }).hide(perk3.getToHide()));

            if (mayor.getUUID().equals(player.getUniqueId())) {
                inventory.put(46, new ItemBuilder(this, Material.ENDER_PEARL, itemMeta -> {
                    itemMeta.itemName(Component.text("§aDéplacer ce NPC"));
                    itemMeta.lore(List.of(
                            Component.text("§7Vous allez pouvoir déplacer ce NPC"),
                            Component.text("§e§lCLIQUEZ ICI POUR CONTINUER")
                    ));
                }).setOnClick(inventoryClickEvent -> {
                    List<Component> loreItemNPC = List.of(
                            Component.text("§7Cliquez sur l'endroit où vous voulez déplacer le §9NPC")
                    );
                    ItemStack itemToGive = new ItemStack(Material.STICK);
                    ItemMeta itemMeta = itemToGive.getItemMeta();

                    itemMeta.displayName(Component.text("§7Emplacement du §9NPC"));
                    itemMeta.lore(loreItemNPC);
                    itemToGive.setItemMeta(itemMeta);
                    ItemInteraction.runLocationInteraction(
                            player,
                            itemToGive,
                            "mayor:owner-npc-move",
                            300,
                            "§7Vous avez 300s pour séléctionner votre emplacement",
                            "§7Vous n'avez pas eu le temps de déplacer votre NPC",
                            locationClick -> {
                                if (locationClick == null) return true;

                                Chunk chunk = locationClick.getChunk();

                                City cityByChunk = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());
                                if (cityByChunk == null) {
                                    MessagesManager.sendMessage(player, Component.text("§cImpossible de mettre le NPC en dehors de votre ville"), Prefix.CITY, MessageType.ERROR, false);
                                    return false;
                                }

                                City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                                if (playerCity == null) {
                                    return false;
                                }

                                if (!cityByChunk.getUUID().equals(playerCity.getUUID())) {
                                    MessagesManager.sendMessage(player, Component.text("§cImpossible de mettre le NPC en dehors de votre ville"), Prefix.CITY, MessageType.ERROR, false);
                                    return false;
                                }

                                NPCManager.moveNPC("mayor", locationClick, city.getUUID());
                                NPCManager.updateNPCS(city.getUUID());
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
