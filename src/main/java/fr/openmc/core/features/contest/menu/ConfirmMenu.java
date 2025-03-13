package fr.openmc.core.features.contest.menu;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.ContestPlayer;
import fr.openmc.core.features.contest.managers.ColorUtils;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import dev.xernas.menulib.Menu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConfirmMenu extends Menu {
    private final String getCampName;
    private final String getColor;
    private final ContestManager contestManager;

    public ConfirmMenu(Player owner, String camp, String color) {
        super(owner);
        this.contestManager = ContestManager.getInstance();
        this.getCampName = camp;
        this.getColor = color;
    }

    @Override
    public @NotNull String getName() {
        if (PapiAPI.hasPAPI() && CustomItemRegistry.hasItemsAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-48%%img_contest_menu%");
        } else {
            return "Menu des Contests";
        }
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
    public @NotNull Map<Integer, ItemStack> getContent() {
        Player player = getOwner();
        Map<Integer, ItemStack> inventory = new HashMap<>();

        String messageTeam = "La Team ";

        String campName = contestManager.data.get(getCampName);
        String campColor = contestManager.data.get(getColor);

        NamedTextColor colorFinal = ColorUtils.getNamedTextColor(campColor);
        List<Component> lore1 = Arrays.asList(
                Component.text("§7Vous allez rejoindre ").append(Component.text( messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                Component.text("§c§lATTENTION! Vous ne pourrez changer de choix !")
        );

        List<Component> lore0 = Arrays.asList(
                Component.text("§7Vous allez annuler votre choix : ").append(Component.text( messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
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
            String color = contestManager.data.get("color" + Integer.valueOf(substring));
            NamedTextColor campColorF = ColorUtils.getNamedTextColor(color);

            contestManager.dataPlayer.put(player.getUniqueId().toString(), new ContestPlayer(player.getName(), 0, Integer.valueOf(substring), campColorF));
            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
            MessagesManager.sendMessage(player, Component.text("§7Vous avez bien rejoint : ").append(Component.text("La Team " + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)), Prefix.CONTEST, MessageType.SUCCESS, false);

            player.closeInventory();
        }));
        player.openInventory(getInventory());
        return inventory;

    }
}