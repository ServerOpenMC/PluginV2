package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ColorUtils;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.utils.ItemUtils;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class ContributionMenu extends Menu {
    private final ContestManager contestManager;
    private final ContestPlayerManager contestPlayerManager;

    public ContributionMenu(Player owner) {
        super(owner);
        this.contestManager = ContestManager.getInstance();
        this.contestPlayerManager = ContestPlayerManager.getInstance();
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
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Player player = getOwner();
        Map<Integer, ItemStack> inventory = new HashMap<>();

        String campName = contestPlayerManager.getPlayerCampName(player);
        NamedTextColor campColor = contestManager.dataPlayer.get(player.getUniqueId().toString()).getColor();
        Material m = ColorUtils.getMaterialFromColor(campColor);

        List<Component> loreinfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Contest !"),
                Component.text("§7Le déroulement..., Les résultats, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        List<Component> lore_contribute = Arrays.asList(
                Component.text("§7Donner vos §bCoquillages de Contest"),
                Component.text("§7Pour faire gagner votre ")
                        .append(Component.text("Team").decoration(TextDecoration.ITALIC, false).color(campColor)),
                Component.text("§e§lCliquez pour verser tout vos Coquillages")
        );

        List<Component> lore_trade = Arrays.asList(
                Component.text("§7Faites des Trades contre des §bCoquillages de Contest"),
                Component.text("§7Utile pour faire gagner ta ")
                        .append(Component.text("Team").decoration(TextDecoration.ITALIC, false).color(campColor)),
                Component.text("§e§lCliquez pour acceder au Menu des trades")
        );

        List<Component> lore_rang = Arrays.asList(
                Component.text(contestPlayerManager.getRankContest(player) + campName).decoration(TextDecoration.ITALIC, false).color(campColor),
                Component.text("§7Progression §8: ")
                        .append(Component.text(contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints()).decoration(TextDecoration.ITALIC, false).color(campColor))
                        .append(Component.text("§8/"))
                        .append(Component.text(contestPlayerManager.getRepPointsToRank(getOwner())).decoration(TextDecoration.ITALIC, false).color(campColor)),
                Component.text("§e§lAUGMENTER DE RANG POUR VOIR DES RECOMPENSES MEILLEURES")
        );

        //TODO: itemadder dependency
        Material shell_contest = CustomItemRegistry.getByName("omc_contest:contest_shell").getBest().getType();

        inventory.put(8, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.displayName(Component.text("§6§lVotre Grade"));
            itemMeta.lore(lore_rang);
        }));


        inventory.put(11, new ItemBuilder(this, shell_contest, itemMeta -> {
            itemMeta.displayName(Component.text("§7Les Trades"));
            itemMeta.lore(lore_trade);
            itemMeta.setCustomModelData(10000);
        }).setNextMenu(new TradeMenu(getOwner())));

        inventory.put(15, new ItemBuilder(this, m, itemMeta -> {
            itemMeta.displayName(Component.text("§r§7Contribuer pour la§r ").append(Component.text("Team " + campName).decoration(TextDecoration.ITALIC, false).color(campColor)));
            itemMeta.lore(lore_contribute);
        }).setOnClick(inventoryClickEvent -> {
                try {
                    ItemStack shell_contestItem = CustomStack.getInstance("contest:contest_shell").getItemStack();
                    int shellCount = Arrays.stream(player.getInventory().getContents()).filter(is -> is != null && is.isSimilar(shell_contestItem)).mapToInt(ItemStack::getAmount).sum();

                    if (ItemUtils.hasEnoughItems(player, shell_contestItem.getType(), shellCount)) {
                        ItemUtils.removeItemsFromInventory(player, shell_contestItem.getType(), shellCount);

                        int newPlayerPoints = shellCount + contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints();
                        int updatedCampPoints = shellCount + contestManager.data.getInteger("points" + contestManager.dataPlayer.get(player.getUniqueId().toString()).getCamp());

                        contestPlayerManager.setPointsPlayer(player, newPlayerPoints);
                        String pointCamp = "points" + contestManager.dataPlayer.get(player.getUniqueId().toString()).getCamp();
                        if (Objects.equals(pointCamp, "points1")) {
                            contestManager.data.setPointsCamp1(updatedCampPoints);
                        } else if (Objects.equals(pointCamp, "points2")) {
                            contestManager.data.setPointsCamp2(updatedCampPoints);
                        }

                        MessagesManager.sendMessageType(getOwner(), Component.text("§7Vous avez déposé§b " + shellCount + " Coquillage(s) de Contest§7 pour votre Team!"), Prefix.CONTEST, MessageType.SUCCESS, true);
                    } else {
                        MessagesManager.sendMessageType(getOwner(), Component.text("§cVous n'avez pas de Coquillage(s) de Contest§7"), Prefix.CONTEST, MessageType.ERROR, true);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }));

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreinfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return inventory;
    }
}
