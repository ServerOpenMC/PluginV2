package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorCandidate;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.api.menulib.utils.StaticSlots.combine;

public class MayorVoteMenu extends PaginatedMenu {
    public MayorVoteMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return combine(combine(StaticSlots.getRightSlots(getInventorySize()), StaticSlots.getLeftSlots(getInventorySize())), StaticSlots.getBottomSlots(getInventorySize()));
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        int totalVotes = city.getMembers().size();
        for (MayorCandidate candidate : MayorManager.cityElections.get(city.getUniqueId())) {
            Perks perk2 = PerkManager.getPerkById(candidate.getIdChoicePerk2());
            Perks perk3 = PerkManager.getPerkById(candidate.getIdChoicePerk3());
            NamedTextColor color = candidate.getCandidateColor();
            int vote = candidate.getVote();

            List<Component> loreMayor = new ArrayList<>(List.of(
                    TranslationManager.translation(
                            "feature.city.mayor.menu.vote.lore.header",
                            Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE)
                    )
            ));
            if (perk2 == null || perk3 == null) return List.of();
            loreMayor.add(Component.empty());
            loreMayor.add(TranslationManager.translation(
                    "feature.city.mayor.menu.vote.lore.votes",
                    Component.text(vote).color(color).decoration(TextDecoration.ITALIC, false)
            ));
            loreMayor.add(TranslationManager.translation(
                    "feature.city.mayor.menu.vote.lore.progress",
                    getProgressBarComponent(vote, totalVotes, color),
                    Component.text(getVotePercentage(vote, totalVotes)).color(NamedTextColor.GRAY)
            ));
            loreMayor.add(Component.empty());
            loreMayor.add(TranslationManager.translation(perk2.getNameKey()));
            loreMayor.addAll(TranslationManager.translationLore(perk2.getLoreKey()));
            loreMayor.add(Component.empty());
            loreMayor.add(TranslationManager.translation(perk3.getNameKey()));
            loreMayor.addAll(TranslationManager.translationLore(perk3.getLoreKey()));
            loreMayor.add(Component.empty());
            loreMayor.add(TranslationManager.translation("feature.city.mayor.menu.vote.lore.click"));

            MayorCandidate playerVote = MayorManager.getPlayerVote(player);
            boolean ench = playerVote != null && candidate == playerVote;


            ItemStack mayorItem = new ItemBuilder(this, SkullUtils.getPlayerSkull(candidate.getCandidateUUID()), itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation(
                            "feature.city.mayor.menu.vote.mayor.title",
                            Component.text(candidate.getName()).color(color).decoration(TextDecoration.ITALIC, false)
                    ).color(color).decoration(TextDecoration.ITALIC, false));
                    itemMeta.lore(loreMayor);
                    itemMeta.setEnchantmentGlintOverride(ench);
                }).setOnClick(inventoryClickEvent -> {
                    if (MayorManager.hasVoted(player) && playerVote != null) {
                        if (candidate.getCandidateUUID().equals(playerVote.getCandidateUUID())) {
                            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.menu.vote.message.already_voted"), Prefix.MAYOR, MessageType.ERROR, false);
                            return;
                        }

                    playerVote.setVote(playerVote.getVote() - 1);
                    MayorManager.removeVotePlayer(player);
                    MayorManager.voteCandidate(city, player, candidate);
                } else {
                    MayorManager.voteCandidate(city, player, candidate);
                }
                MessagesManager.sendMessage(player, TranslationManager.translation(
                        "feature.city.mayor.menu.vote.message.voted",
                        TranslationManager.translation(
                                "feature.city.mayor.menu.vote.mayor.title",
                                Component.text(candidate.getName()).color(color).decoration(TextDecoration.ITALIC, false)
                        ).color(color).decoration(TextDecoration.ITALIC, false)
                ), Prefix.MAYOR, MessageType.SUCCESS, true);

                new MayorVoteMenu(player).open();
            });

            items.add(mayorItem);

        }

        return items;
    }

    private Component getProgressBarComponent(int vote, int totalVotes, NamedTextColor color) {
        int progressBars = 20;
        int barFill = (int) (((double) vote / totalVotes) * progressBars);

        Component filled = Component.text("|".repeat(Math.max(0, barFill)))
                .color(color)
                .decoration(TextDecoration.ITALIC, false);
        Component empty = Component.text("|".repeat(Math.max(0, progressBars - barFill)))
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);

        return Component.empty().append(filled).append(empty);
    }

    private int getVotePercentage(int vote, int totalVotes) {
        if (totalVotes == 0) return 0;
        return (int) (((double) vote / totalVotes) * 100);
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.vote.button.close"));
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.vote.button.prev"));
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.vote.button.next"));
        }).setNextPageButton());

        List<Component> loreInfo = TranslationManager.translationLore("feature.city.mayor.menu.common.more_info.lore");

        map.put(54, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.common.more_info.name"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));
        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.vote.name");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-38::mayor:");
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
