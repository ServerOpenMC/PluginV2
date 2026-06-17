package fr.openmc.core.features.dungeons;

import com.mojang.brigadier.Message;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dungeons.db.DBKeyVault;
import fr.openmc.core.features.dungeons.registry.items.Key;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DungeonsMenu extends Menu {

    private final List<DBKeyVault> playerKeyVaults;

    protected DungeonsMenu(Player owner) {
        super(owner);
        playerKeyVaults = DungeonsManager.getDataKeyVault().get(owner.getUniqueId());
    }

    @Override
    public @NotNull Component getName() {
        return Component.text("Dungeon Vault");
    }

    @Override
    public String getTexture() {
        return "";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map <Integer, ItemMenuBuilder> map = new HashMap<>();

        for (int i = 1; i <= 4; i++) {
            final int level = i;
            for (Rarity rarity : Rarity.values()) {
                if (rarity == Rarity.ABSOLUTE_MASTER) continue;
                Key key = (Key) OMCRegistry.CUSTOM_ITEMS.getOrThrow("omc_dungeons:key_level_" + i + "_" + rarity.name().toLowerCase());
                map.put(2 * i + rarity.ordinal() * 9 - 1, new ItemMenuBuilder(this, key.getBest(), itemMeta -> {
                    itemMeta.lore(List.of(
                            Component.text("§fLevel " + level),
                            Component.text(rarity.getName()).color(rarity.getColor()).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).decorate(TextDecoration.BOLD)
                    ));

                }).setOnClick(keyOnClick(key)));


            }
        }

        map.put(45, new ItemMenuBuilder(this, Material.SMITHING_TABLE, itemMeta -> {
            itemMeta.displayName(Component.text("Collisionneur de Hadrons"));
            itemMeta.lore(List.of(
                    Component.text("Permet de fusionner deux clé de niveau identique pour obtenir une clé de rareté supérieure."),
                    Component.text("Fusionnez deux exemplaires de chaque clé de niveau Maître pour obtenir la clé de Maître Absolu.")
            ));
        }));

        map.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.KEY_L5_R6.getBest(), itemMeta -> {
            itemMeta.lore(List.of(
               Component.text(Rarity.ABSOLUTE_MASTER.getName()).color(Rarity.ABSOLUTE_MASTER.getColor()).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).decorate(TextDecoration.BOLD)
            ));
        }).setOnClick(keyOnClick((Key) OMCRegistry.CUSTOM_ITEMS.KEY_L5_R6)));

        return map;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private Consumer<InventoryClickEvent> keyOnClick(Key key)  {
        return event -> {

                if (event.isLeftClick()) {
                    if (ItemUtils.hasEnoughItems(getOwner(), key.getBest(), 1)) {
                        MessagesManager.sendMessage(getOwner(), Component.text("Vous ne possédez pas assez de cet item"), Prefix.DUNGEONS, MessageType.ERROR, true);
                        return;
                    };
                    ItemUtils.removeItemsFromInventory(getOwner(), key.getBest(), 1);
                } else if (event.isRightClick()) {
                    if (!ItemUtils.hasEnoughItems(getOwner(), key.getBest(), 1)) {
                        MessagesManager.sendMessage(getOwner(), Component.text("Vous n'avez pas assez de de cet item dans votre vault"), Prefix.DUNGEONS, MessageType.ERROR, true);
                        return;
                    }
                    if (!ItemUtils.hasEnoughSpace(getOwner(), key.getBest())) {
                        MessagesManager.sendMessage(getOwner(), Component.text("Vous n'avez pas assez de place dans votre inventaire."),  Prefix.DUNGEONS, MessageType.ERROR, true);
                        return;
                    }

                }
        };

    };

}
