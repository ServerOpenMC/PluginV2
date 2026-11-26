package fr.openmc.core.features.events.contents.deposit.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.deposit.DepositEvent;
import fr.openmc.core.features.events.contents.deposit.DepositEventManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepositMenu extends Menu {
    private final DepositEvent event;

    public DepositMenu(OMCPlayer owner, DepositEvent event) {
        super(owner);
        this.event = event;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(event.key("menu.deposit.title"));
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        TextColor mainColor = TextColor.color(event.getMainColor());
        TextColor secondaryColor = TextColor.color(event.getSecondaryColor());

        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        content.put(13, new ItemMenuBuilder(this, event.getRequiredItem().clone(), meta -> {
            meta.customName(TranslationManager.translation(event.key("menu.deposit.button.name")).color(mainColor));
            meta.lore(TranslationManager.translationLore(event.key("menu.deposit.button.lore")));
            meta.setEnchantmentGlintOverride(true);
        }).setOnClick(click -> {
            Player player = (Player) click.getWhoClicked();
            int count = ItemUtils.removeItemsFromPlayerInventory(player, event.getRequiredItem(), Integer.MAX_VALUE);

            if (count == 0) {
                MessagesManager.sendMessage(player,
                        TranslationManager.translation(event.key("menu.deposit.no_item")),
                        event.getPrefix(), MessageType.ERROR, false);
                player.closeInventory();
                return;
            }

            DepositEventManager.deposit(event, player.getUniqueId(), count);
            MessagesManager.sendMessage(player,
                    TranslationManager.translation(event.key("menu.deposit.success"),
                            Component.text(count).color(TextColor.color(event.getMainColor())).decorate(TextDecoration.BOLD)
                    ).color(secondaryColor),
                    event.getPrefix(), MessageType.SUCCESS, false);
            player.closeInventory();
        }));

        if (event.getRewards() != null) {
            content.put(18, new ItemMenuBuilder(this, event.getRequiredItem().clone(), meta -> {
                meta.customName(TranslationManager.translation("feature.events.deposit.rewards").color(mainColor));
                meta.lore(TranslationManager.translationLore("feature.events.deposit.rewards.lore"));
                meta.setEnchantmentGlintOverride(true);
            }).setOnClick(_ ->
                    event.getRewards().openMenu(getOwner())));
        }

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return Collections.emptyList();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}