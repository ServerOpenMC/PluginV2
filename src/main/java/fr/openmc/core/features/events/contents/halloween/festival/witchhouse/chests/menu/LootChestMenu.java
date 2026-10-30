package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.chests.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LootChestMenu extends Menu {

    private final LootReward loot;

    public LootChestMenu(Player owner, LootReward loot) {
        super(owner);
        this.loot = loot;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.halloween.festival.chest_lootbox.menu.name");
    }

    @Override
    public String getTexture() {
        return ""; //il n'a pas besoin de texture
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

        if (event.getReason().equals(InventoryCloseEvent.Reason.PLUGIN))
            return;

        MessagesManager.sendMessage(event.getPlayer(),
                TranslationManager.translation("feature.events.halloween.festival.chest_lootbox.prevention"),
                Prefix.HALLOWEEN,
                MessageType.INFO,
                true
        );
    }

    private static final List<Integer> CHEST_MENU_ITEM_SLOTS =
            IntStream.rangeClosed(0, 26)
                    .boxed()
                    .toList();

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        List<Integer> slots = new ArrayList<>(CHEST_MENU_ITEM_SLOTS);

        for (CustomLoot cloot : loot.loots()) {

            if (cloot instanceof ItemLoot itemLoot) {

                int amount = itemLoot.getRandomAmount();

                while (amount > 0) {
                    int slot = RandomUtils.getRandomObjectFromList(slots);

                    int amountToDepose = Math.min(
                            amount,
                            RandomUtils.randomBetween(1, 3)
                    );

                    ItemStack item = itemLoot.getItem().clone();
                    item.setAmount(amountToDepose);

                    inventory.put(slot, new ItemMenuBuilder(this, item));

                    slots.remove(Integer.valueOf(slot));

                    amount -= amountToDepose;
                }
            }
        }

        inventory.put(31, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.MAILBOX_HOURGLASS, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.halloween.festival.chest_lootbox.menu.button.name"));
            itemMeta.lore(List.of(TranslationManager.translation("feature.events.halloween.festival.chest_lootbox.menu.button.lore")));
        }).setCloseButton());

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return Stream.concat(
                CHEST_MENU_ITEM_SLOTS.stream(),
                MenuUtils.getInventoryItemSlots(this.getInventorySize().getSize()).stream()
        ).toList();
    }
}
