package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestPlayerManager;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.openmc.core.utils.bukkit.ItemUtils.isSimilar;

public class ContributionMenu extends Menu {

    private final ContestManager contestManager = OMCRegistry.FEATURES.CONTEST.get();
    private final ContestPlayerManager contestPlayerManager = OMCRegistry.CONTEST_FEATURES.CONTEST_PLAYER;

    public ContributionMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.contest.contribution.title");
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
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Player player = getOwner();
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        Component campName = contestPlayerManager.getPlayerCampComponent(player);
        NamedTextColor campColor = contestManager.getDataPlayer().get(player.getUniqueId()).getColor();
        Material m = ColorUtils.getMaterialFromColor(campColor);

        List<Component> loreInfo = TranslationManager.translationLore("feature.events.contest.trade.info.lore");

        List<Component> loreContribute = TranslationManager.translationLore(
                "feature.events.contest.contribution.lore.contribute",
                TranslationManager.translation("feature.events.contest.team.label", campName).color(campColor)
        );

        List<Component> loreTrade = TranslationManager.translationLore(
                "feature.events.contest.contribution.lore.trade",
                TranslationManager.translation("feature.events.contest.team.label", campName).color(campColor)
        );

        List<Component> loreRang = TranslationManager.translationLore(
                "feature.events.contest.contribution.lore.rank",
                contestPlayerManager.getTitleContest(player).append(campName).colorIfAbsent(campColor),
                Component.text(contestManager.getDataPlayer().get(player.getUniqueId()).getPoints()).color(campColor),
                Component.text(contestPlayerManager.getGoalPointsToRankUp(getOwner())).color(campColor)
        );

        inventory.put(8, new ItemMenuBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.contribution.title.name"));
            itemMeta.lore(loreRang);
        }));

        inventory.put(11, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.trade.main.name"));
            itemMeta.lore(loreTrade);
        }).setOnClick(inventoryClickEvent -> new TradeMenu(getOwner()).open()));

        inventory.put(15, new ItemMenuBuilder(this, m, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation(
                    "feature.events.contest.contribution.button.name",
                    TranslationManager.translation("feature.events.contest.team.label", campName.colorIfAbsent(campColor))
            ));
            itemMeta.lore(loreContribute);
        }).setOnClick(inventoryClickEvent -> {
            if (!OMCRegistry.HOOKS.ITEMS_ADDER.isEnable()) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.events.contest.contribution.unavailable"), Prefix.CONTEST, MessageType.ERROR, true);
                return;
            }

            try {
                ItemStack shellContestItem = OMCRegistry.CUSTOM_ITEMS.CONTEST_SHELL.getBest();
                int shellCount = Arrays.stream(player.getInventory().getContents()).filter(is -> is != null && isSimilar(shellContestItem, is)).mapToInt(ItemStack::getAmount).sum();

                if (ItemUtils.hasEnoughItems(player, shellContestItem, shellCount)) {
                    ItemUtils.removeItemsFromPlayerInventory(player, shellContestItem, shellCount);

                    int newPlayerPoints = shellCount + contestManager.getDataPlayer().get(player.getUniqueId()).getPoints();
                    int updatedCampPoints = shellCount + contestManager.getData().getInteger("points" + contestManager.getDataPlayer().get(player.getUniqueId()).getCamp());

                    contestPlayerManager.setPointsPlayer(player.getUniqueId(), newPlayerPoints);
                    String pointCamp = "points" + contestManager.getDataPlayer().get(player.getUniqueId()).getCamp();
                    if (Objects.equals(pointCamp, "points1")) {
                        contestManager.getData().setPoints1(updatedCampPoints);
                    } else if (Objects.equals(pointCamp, "points2")) {
                        contestManager.getData().setPoints2(updatedCampPoints);
                    }
                    
                    MessagesManager.sendMessage(getOwner(), TranslationManager.translation(
                            "feature.events.contest.contribution.success",
                            Component.text(shellCount).color(NamedTextColor.AQUA)
                    ), Prefix.CONTEST, MessageType.SUCCESS, true);
                } else {
                    MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.events.contest.contribution.no_shells"), Prefix.CONTEST, MessageType.ERROR, true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        inventory.put(35, new ItemMenuBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.vote.info.name"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));

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
