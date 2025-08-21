package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.models.ContestPlayer;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmMenu extends Menu {
    private final String getCampName;
    private final String getColor;

    public ConfirmMenu(Player owner, String camp, String color) {
        super(owner);
        this.getCampName = camp;
        this.getColor = color;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Contests - Confirmation";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::contest_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }


    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Player player = getOwner();
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        String messageTeam = "La Team ";

            String campName = ContestManager.data.get(getCampName);
            String campColor = ContestManager.data.get(getColor);

        NamedTextColor colorFinal = ColorUtils.getNamedTextColor(campColor);
        List<Component> lore1 = Arrays.asList(
                Component.text("§7Vous allez rejoindre ").append(Component.text(messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                Component.text("§c§lATTENTION! Vous ne pourrez changer de choix !")
        );

        List<Component> lore0 = Arrays.asList(
                Component.text("§7Vous allez annuler votre choix : ").append(Component.text(messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                Component.text("§c§lATTENTION! Vous ne pourrez changer de choix !")
        );


        inventory.put(11, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§cAnnuler"));
            itemMeta.lore(lore0);
        }).setOnClick(inventoryClickEvent -> {
            VoteMenu menu = new VoteMenu(player);
            menu.open();
        }));

            inventory.put(15, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> {
                itemMeta.displayName(Component.text("§r§aConfirmer"));
                itemMeta.lore(lore1);
            }).setOnClick(inventoryClickEvent -> {
                String substring = this.getCampName.substring(this.getCampName.length() - 1);
                String color = ContestManager.data.get("color" + Integer.valueOf(substring));
                NamedTextColor campColorF = ColorUtils.getNamedTextColor(color);

                ContestManager.dataPlayer.put(player.getUniqueId(), new ContestPlayer(player.getUniqueId(), 0, Integer.parseInt(substring), campColorF));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
                MessagesManager.sendMessage(player, Component.text("§7Vous avez bien rejoint : ").append(Component.text("La Team " + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)), Prefix.CONTEST, MessageType.SUCCESS, false);

            player.closeInventory();
        }));
        player.openInventory(getInventory());

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
