package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.managers.ColorConvertor;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
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
        ChatColor campColor = contestManager.dataPlayer.get(player.getUniqueId().toString()).getColor();
        Material m = ColorConvertor.getMaterialFromColor(campColor);

        List<String> loreinfo = new ArrayList<String>();
        List<String> lore_contribute = new ArrayList<String>();
        List<String> lore_trade = new ArrayList<String>();
        List<String> lore_rang = new ArrayList<String>();

        loreinfo.add("§7Apprenez en plus sur les Contest !");
        loreinfo.add("§7Le déroulement..., Les résultats, ...");
        loreinfo.add("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!");

        Material shell_contest = CustomStack.getInstance("contest:contest_shell").getItemStack().getType();
        lore_contribute.add("§7Donner vos §bCoquillages de Contest");
        lore_contribute.add("§7Pour faire gagner votre"+ campColor +" Team!");
        lore_contribute.add("§e§lCliquez pour verser tout vos Coquillages");

        lore_trade.add("§7Faites des Trades contre des §bCoquillages de Contest");
        lore_trade.add("§7Utile pour faire gagner ta"+ campColor +" Team");
        lore_trade.add("§e§lCliquez pour acceder au Menu des trades");

        lore_rang.add(campColor + contestPlayerManager.getRankContest(player) + campName);
        lore_rang.add("§7Progression §8: " + campColor + contestManager.dataPlayer.get(player.getUniqueId().toString()).getPoints() + "§8/" + campColor + contestPlayerManager.getRepPointsToRank(getOwner()));
        lore_rang.add("§e§lAUGMENTER DE RANG POUR VOIR DES RECOMPENSES MEILLEURES");

        inventory.put(8, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.setDisplayName("§6§lVotre Grade");
            itemMeta.setLore(lore_rang);
        }));


        inventory.put(11, new ItemBuilder(this, shell_contest, itemMeta -> {
            itemMeta.setDisplayName("§7Les Trades");
            itemMeta.setLore(lore_trade);
            itemMeta.setCustomModelData(10000);
        }).setNextMenu(new TradeMenu(getOwner())));

        inventory.put(15, new ItemBuilder(this, m, itemMeta -> {
            itemMeta.setDisplayName("§r§7Contribuer pour la"+ campColor+ " Team " + campName);
            itemMeta.setLore(lore_contribute);
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

                        MessagesManager.sendMessageType(getOwner(), "§7Vous avez déposé§b " + shellCount + " Coquillage(s) de Contest§7 pour votre Team!", Prefix.CONTEST, MessageType.SUCCESS, true);
                    } else {
                        MessagesManager.sendMessageType(getOwner(), "§cVous n'avez pas de Coquillage(s) de Contest§7", Prefix.CONTEST, MessageType.ERROR, true);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }));

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.setDisplayName("§r§aPlus d'info !");
            itemMeta.setLore(loreinfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return inventory;
    }
}
