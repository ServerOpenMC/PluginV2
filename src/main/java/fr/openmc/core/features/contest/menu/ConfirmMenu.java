package fr.openmc.core.features.contest.menu;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.ContestPlayer;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
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

    private Map<Integer, ItemStack> inventory;

    public ConfirmMenu(Player owner, String camp, String color) {
        super(owner);
        this.contestManager = ContestManager.getInstance();
        this.getCampName = camp;
        this.getColor = color;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-48%%img_contest_menu%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {}


    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Player player = getOwner();
        inventory = new HashMap<>();

        String campName = contestManager.data.get(getCampName);
        String campColor = contestManager.data.get(getColor);

        ChatColor colorFinal = ChatColor.valueOf(campColor);
        List<String> lore1 = new ArrayList<>();
        lore1.add("§7Vous allez rejoindre " + colorFinal + "La Team " + campName);
        lore1.add("§c§lATTENTION! Vous ne pourrez changer de choix !");

        List<String> lore0 = new ArrayList<>();
        lore0.add("§7Vous allez annuler votre choix : " + colorFinal + "La Team " + campName);

        inventory.put(11, new ItemBuilder(this, Material.RED_CONCRETE, itemMeta -> {
            itemMeta.setDisplayName("§r§cAnnuler");
            itemMeta.setLore(lore0);
        }).setOnClick(inventoryClickEvent -> {
            VoteMenu menu = new VoteMenu(player);
            menu.open();
        }));

        inventory.put(15, new ItemBuilder(this, Material.GREEN_CONCRETE, itemMeta -> {
            itemMeta.setDisplayName("§r§aConfirmer");
            itemMeta.setLore(lore1);
        }).setOnClick(inventoryClickEvent -> {
            String substring = this.getCampName.substring(this.getCampName.length() - 1);
            String color = contestManager.data.get("color" + Integer.valueOf(substring));
            ChatColor campColorF = ChatColor.valueOf(color);
            contestManager.dataPlayer.put(player.getUniqueId().toString(), new ContestPlayer(player.getName(), 0, Integer.valueOf(substring), campColorF));
            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
            MessagesManager.sendMessageType(player, "§7Vous avez bien rejoint : " + colorFinal + "La Team " + campName, Prefix.CONTEST, MessageType.SUCCESS, false);
            player.closeInventory();
        }));
        player.openInventory(getInventory());
        return inventory;

    }
}