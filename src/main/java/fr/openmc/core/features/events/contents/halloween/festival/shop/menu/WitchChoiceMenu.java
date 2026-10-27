package fr.openmc.core.features.events.contents.halloween.festival.shop.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.buttons.DepositButton;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.buttons.TerrorHouseButton;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WitchChoiceMenu extends Menu {

    private static final int[] WITCH_HOUSE_SLOT = {11};
    private static final int[] DEPOSIT_SLOTS = {15};
    private static final Sound WITCH_MENU_CLOSE = Sound.ENTITY_WITCH_AMBIENT;

    public WitchChoiceMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.halloween.festival.menu.choice.name");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_choice_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        TerrorHouseButton.init(this, inventory, WITCH_HOUSE_SLOT);

        DepositButton.init(this, inventory, DEPOSIT_SLOTS);

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = getOwner();
        player.playSound(player, WITCH_MENU_CLOSE, 1f, 1f);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }
}
