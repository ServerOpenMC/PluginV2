package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.items.CustomItemRegistry;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ContributionMenu extends Menu {

    public ContributionMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemsAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-48%%img_contest_menu%");
        } else {
            return "Menu des Contests - Contribution";
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

            String campName = ContestPlayerManager.getPlayerCampName(player);
            NamedTextColor campColor = ContestManager.dataPlayer.get(player.getUniqueId()).getColor();
            Material m = ColorUtils.getMaterialFromColor(campColor);

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Contest !"),
                Component.text("§7Le déroulement..., Les résultats, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        List<Component> loreContribute = Arrays.asList(
                Component.text("§7Donner vos §bCoquillages de Contest"),
                Component.text("§7Pour faire gagner votre ")
                        .append(Component.text("Team").decoration(TextDecoration.ITALIC, false).color(campColor)),
                Component.text("§e§lCliquez pour verser tout vos Coquillages")
        );

        List<Component> loreTrade = Arrays.asList(
                Component.text("§7Faites des Trades contre des §bCoquillages de Contest"),
                Component.text("§7Utile pour faire gagner ta ")
                        .append(Component.text("Team").decoration(TextDecoration.ITALIC, false).color(campColor)),
                Component.text("§e§lCliquez pour acceder au Menu des trades")
        );

            List<Component> loreRang = Arrays.asList(
                    Component.text(ContestPlayerManager.getTitleContest(player) + campName).decoration(TextDecoration.ITALIC, false).color(campColor),
                    Component.text("§7Progression §8: ")
                            .append(Component.text(ContestManager.dataPlayer.get(player.getUniqueId()).getPoints()).decoration(TextDecoration.ITALIC, false).color(campColor))
                            .append(Component.text("§8/"))
                            .append(Component.text(ContestPlayerManager.getGoalPointsToRankUp(getOwner())).decoration(TextDecoration.ITALIC, false).color(campColor)),
                    Component.text("§e§lAUGMENTER DE TITRE POUR AVOIR DES RECOMPENSES MEILLEURES")
            );

        //ITEMADDER
        String namespaceShellContest = "omc_contest:contest_shell";
        ItemStack shellContest = CustomItemRegistry.getByName(namespaceShellContest).getBest();

        inventory.put(8, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.displayName(Component.text("§6§lVotre Titre"));
            itemMeta.lore(loreRang);
        }));

        inventory.put(11, new ItemBuilder(this, shellContest, itemMeta -> {
            itemMeta.displayName(Component.text("§7Les Trades"));
            itemMeta.lore(loreTrade);
        }).setNextMenu(new TradeMenu(getOwner())));

        inventory.put(15, new ItemBuilder(this, m, itemMeta -> {
            itemMeta.displayName(Component.text("§r§7Contribuer pour la§r ").append(Component.text("Team " + campName).decoration(TextDecoration.ITALIC, false).color(campColor)));
            itemMeta.lore(loreContribute);
        }).setOnClick(inventoryClickEvent -> {
            if (!ItemsAdderApi.hasItemAdder()) {
                MessagesManager.sendMessage(player, Component.text("§cFonctionnalité bloqué. Veuillez contactez l'administration"), Prefix.CONTEST, MessageType.ERROR, true);
                return;
            }

            try {
                ItemStack shellContestItem = CustomStack.getInstance(namespaceShellContest).getItemStack();
                int shellCount = Arrays.stream(player.getInventory().getContents()).filter(is -> is != null && is.isSimilar(shellContestItem)).mapToInt(ItemStack::getAmount).sum();

                if (ItemUtils.hasEnoughItems(player, shellContestItem, shellCount)) {
                    ItemUtils.removeItemsFromInventory(player, shellContestItem, shellCount);

                        int newPlayerPoints = shellCount + ContestManager.dataPlayer.get(player.getUniqueId()).getPoints();
                        int updatedCampPoints = shellCount + ContestManager.data.getInteger("points" + ContestManager.dataPlayer.get(player.getUniqueId()).getCamp());

                        ContestPlayerManager.setPointsPlayer(player.getUniqueId(), newPlayerPoints);
                        String pointCamp = "points" + ContestManager.dataPlayer.get(player.getUniqueId()).getCamp();
                        if (Objects.equals(pointCamp, "points1")) {
                            ContestManager.data.setPoints1(updatedCampPoints);
                        } else if (Objects.equals(pointCamp, "points2")) {
                            ContestManager.data.setPoints2(updatedCampPoints);
                        }

                    MessagesManager.sendMessage(getOwner(), Component.text("§7Vous avez déposé§b " + shellCount + " Coquillage(s) de Contest§7 pour votre Team!"), Prefix.CONTEST, MessageType.SUCCESS, true);
                } else {
                    MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas de Coquillage(s) de Contest§7"), Prefix.CONTEST, MessageType.ERROR, true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

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
