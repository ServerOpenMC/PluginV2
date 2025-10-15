package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorCandidate;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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

import java.util.*;

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
		            Component.text("§8Candidat pour le maire de " + city.getName())
            ));
            loreMayor.add(Component.empty());
            loreMayor.add(Component.text("§7Votes : ").append(Component.text(vote).color(color).decoration(TextDecoration.ITALIC, false)));
            loreMayor.add(Component.text(" §8[" + getProgressBar(vote, totalVotes, color) + "§8] §7(" + getVotePercentage(vote, totalVotes) + "%)"));
            loreMayor.add(Component.empty());
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
            loreMayor.add(Component.empty());
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());
            loreMayor.add(Component.empty());
            loreMayor.add(Component.text("§e§lCLIQUEZ ICI POUR LE VOTER"));

            MayorCandidate playerVote = MayorManager.getPlayerVote(player);
            boolean ench = playerVote != null && candidate == playerVote;


            ItemStack mayorItem = new ItemBuilder(this, ItemUtils.getPlayerSkull(candidate.getCandidateUUID()), itemMeta -> {
                itemMeta.displayName(Component.text("Maire " + candidate.getName()).color(color).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(loreMayor);
                itemMeta.setEnchantmentGlintOverride(ench);
            }).setOnClick(inventoryClickEvent -> {
                if (MayorManager.hasVoted(player) && playerVote != null) {
                    if (candidate.getCandidateUUID().equals(playerVote.getCandidateUUID())) {
	                    MessagesManager.sendMessage(player, Component.text("§7Vous avez déjà voté pour ce §6maire"), Prefix.MAYOR, MessageType.ERROR, false);
                        return;
                    }

                    playerVote.setVote(playerVote.getVote() - 1);
                    MayorManager.removeVotePlayer(player);
                    MayorManager.voteCandidate(city, player, candidate);
                } else {
                    MayorManager.voteCandidate(city, player, candidate);
                }
	            MessagesManager.sendMessage(player, Component.text("§7Vous avez voté pour le ").append(Component.text("maire " + candidate.getName()).color(color)), Prefix.MAYOR, MessageType.SUCCESS, true);

                new MayorVoteMenu(player).open();
            });

            items.add(mayorItem);

        }

        return items;
    }

    private String getProgressBar(int vote, int totalVotes, NamedTextColor color) {
        int progressBars = 20;
        int barFill = (int) (((double) vote / totalVotes) * progressBars);

        return ColorUtils.getColorCode(color) +
                "|".repeat(Math.max(0, barFill)) +
                "§7" +
                "|".repeat(Math.max(0, progressBars - barFill));
    }

    private int getVotePercentage(int vote, int totalVotes) {
        if (totalVotes == 0) return 0;
        return (int) (((double) vote / totalVotes) * 100);
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cFermer"));
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cPage précédente"));
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
        }).setNextPageButton());

        List<Component> loreInfo = Arrays.asList(
		        Component.text("§7Apprenez en plus sur les maires !"),
		        Component.text("§7Le déroulement..., les éléctions, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        map.put(54, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));
        return map;
    }

    @Override
    public @NotNull String getName() {
	    return "Menu des maires - Votes";
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
