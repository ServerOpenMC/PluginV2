package fr.openmc.core.features.city.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.actions.CityCreateAction;
import fr.openmc.core.features.city.commands.CityInviteCommands;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.utils.text.InputUtils.MAX_LENGTH_CITY;

public class NoCityMenu extends Menu {

    public NoCityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.no_city.name");
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
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();


            Component nameNotif;
            List<Component> loreNotif = new ArrayList<>();
        if (!CityInviteCommands.invitations.containsKey(player)) {
                nameNotif = TranslationManager.translation("feature.city.menus.no_city.invitations.none.title");
	            loreNotif.addAll(TranslationManager.translationLore("feature.city.menus.no_city.invitations.none.lore"));

            inventory.put(15, new ItemBuilder(this, Material.CHISELED_BOOKSHELF, itemMeta -> {
                itemMeta.itemName(nameNotif);
                itemMeta.lore(loreNotif);
            }).setOnClick(inventoryClickEvent -> MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.invite.commands.accept.none_pending"), Prefix.CITY, MessageType.ERROR, false)));
        } else {
            List<Player> invitations = CityInviteCommands.invitations.get(player);
            nameNotif = TranslationManager.translation(
                    "feature.city.menus.no_city.invitations.count.title",
                    Component.text(invitations.size()),
                    Component.text(invitations.size() > 1 ? "s" : "")
            );

            loreNotif.addAll(TranslationManager.translationLore("feature.city.menus.no_city.invitations.count.lore"));

            inventory.put(15, new ItemBuilder(this, Material.BOOKSHELF, itemMeta -> {
                itemMeta.itemName(nameNotif);
                itemMeta.lore(loreNotif);
            }).setOnClick(inventoryClickEvent -> {
                new InvitationsMenu(player).open();
            }));
        }

        Supplier<ItemBuilder> createItemSupplier = () -> {
                List<Component> loreCreate;
                if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
                    loreCreate = TranslationManager.translationLore(
                            "feature.city.menus.no_city.create.lore.cooldown",
                            Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big"))).color(NamedTextColor.RED)
                    );
                } else {
                    loreCreate = TranslationManager.translationLore(
                            "feature.city.menus.no_city.create.lore.ready",
                            Component.text(CityCreateConditions.MONEY_CREATE).color(NamedTextColor.GOLD),
                            Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD),
                            Component.text(CityCreateConditions.AYWENITE_CREATE).color(NamedTextColor.LIGHT_PURPLE)
                    );
                }

                return new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
                    itemMeta.itemName(TranslationManager.translation("feature.city.menus.no_city.create.title"));
                    itemMeta.lore(loreCreate);
                }).setOnClick(inventoryClickEvent -> {
                    if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) return;

                    DialogInput.send(player, TranslationManager.translation("feature.city.commands.create.enter_city_name"), MAX_LENGTH_CITY, input -> {
                                if (input == null) return;
                                CityCreateAction.beginCreateCity(player, input);
                            }
                    );
                });
            };

            if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
                MenuUtils.runDynamicItem(player, this, 11, createItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                inventory.put(11, createItemSupplier.get());
            }


        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
