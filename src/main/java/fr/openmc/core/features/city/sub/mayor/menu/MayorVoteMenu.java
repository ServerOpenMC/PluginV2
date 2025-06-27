package fr.openmc.core.features.city.sub.mayor.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.MayorCandidate;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.api.PapiApi;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.openmc.api.menulib.utils.StaticSlots.*;

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
        return combine(combine(RIGHT, LEFT), BOTTOM);
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

            int totalVotes = city.getMembers().size();
        for (MayorCandidate candidate : MayorManager.cityElections.get(city.getUUID())) {
                Perks perk2 = PerkManager.getPerkById(candidate.getIdChoicePerk2());
                Perks perk3 = PerkManager.getPerkById(candidate.getIdChoicePerk3());
                NamedTextColor color = candidate.getCandidateColor();
                int vote = candidate.getVote();
                MayorCandidate playerVote = MayorManager.getPlayerVote(player);

            List<Component> loreMayor = new ArrayList<>(List.of(
                    Component.text("§8Candidat pour le Maire de " + city.getName())
            ));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text("§7Votes : ").append(Component.text(vote).color(color).decoration(TextDecoration.ITALIC, false)));
            loreMayor.add(Component.text(" §8[" + getProgressBar(vote, totalVotes, color) + "§8] §7(" + getVotePercentage(vote, totalVotes) + "%)"));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text("§e§lCLIQUEZ ICI POUR LE VOTER"));

            boolean ench = candidate == playerVote;


                ItemStack mayorItem = new ItemBuilder(this, ItemUtils.getPlayerSkull(candidate.getUUID()), itemMeta -> {
                    itemMeta.displayName(Component.text("Maire " + candidate.getName()).color(color).decoration(TextDecoration.ITALIC, false));
                    itemMeta.lore(loreMayor);
                    itemMeta.setEnchantmentGlintOverride(ench);
                }).setOnClick(inventoryClickEvent -> {
                    if (MayorManager.hasVoted(player)) {
                        if (candidate == playerVote) {
                            MessagesManager.sendMessage(player, Component.text("§7Vous avez déjà voté pour ce §6Maire"), Prefix.MAYOR, MessageType.ERROR, false);
                            return;
                        }

                        playerVote.setVote(playerVote.getVote()-1);
                        MayorManager.removeVotePlayer(player);
                        MayorManager.voteCandidate(city, player, candidate);
                    } else {
                       MayorManager.voteCandidate(city, player, candidate);
                    }
                    MessagesManager.sendMessage(player, Component.text("§7Vous avez voté pour le ").append(Component.text("Maire " + candidate.getName()).color(color)), Prefix.MAYOR, MessageType.SUCCESS, true);

                new MayorVoteMenu(player).open();
            });

            items.add(mayorItem);

        }

        return items;
    }

    private String getProgressBar(int vote, int totalVotes, NamedTextColor color) {
        int progressBars = 20;
        int barFill = (int) (((double) vote / totalVotes) * progressBars);

        StringBuilder bar = new StringBuilder();
        bar.append(ColorUtils.getColorCode(color));
        bar.append("|".repeat(Math.max(0, barFill)));
        bar.append("§7");
        bar.append("|".repeat(Math.max(0, progressBars - barFill)));
        return bar.toString();
    }

    private int getVotePercentage(int vote, int totalVotes) {
        if (totalVotes == 0) return 0;
        return (int) (((double) vote / totalVotes) * 100);
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cFermer"));
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cPage précédente"));
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("menu:next_page").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
        }).setNextPageButton());

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Maires !"),
                Component.text("§7Le déroulement..., Les éléctions, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        map.put(54, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));
        return map;
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemsAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-38%%img_mayor%");
        } else {
            return "Menu des Maires - Votes";
        }
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
