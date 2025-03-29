package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.MayorElector;
import fr.openmc.core.features.city.mayor.PerkType;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MayorVoteMenu extends PaginatedMenu {
    public MayorVoteMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        Player player = getOwner();

        MayorManager mayorManager = MayorManager.getInstance();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        List<ItemStack> items = new ArrayList<>();

        int totalVotes = mayorManager.cityElections.get(city).size();
        for (MayorElector elector : mayorManager.cityElections.get(city)) {
            Perks perk2 = PerkManager.getPerkById(elector.getIdChoicePerk2());
            Perks perk3 = PerkManager.getPerkById(elector.getIdChoicePerk3());
            NamedTextColor color = elector.getElectorColor();
            int vote = elector.getVote();

            List<Component> loreMayor = new ArrayList<>(List.of(
                    Component.text("§8Candidat pour le Maire de " + city.getName())
            ));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text("§7Votes : ").append(Component.text(elector.getVote()).color(color).decoration(TextDecoration.ITALIC, false)));
            loreMayor.add(Component.text(" §8[" + getProgressBar(vote, totalVotes, color) + "§8] §7(" + getVotePercentage(vote, totalVotes) + "%)"));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text("§e§lCLIQUEZ ICI POUR LE VOTER"));

            ItemStack perkItem = new ItemBuilder(this, ItemUtils.getPlayerSkull(elector.getElectorUUID()), itemMeta -> {
                itemMeta.displayName(Component.text("Maire " + player.getName()).color(color).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(loreMayor);
            }).setOnClick(inventoryClickEvent -> {
                if (mayorManager.isPlayerVoted(player)) {
                    MayorElector votePlayer = mayorManager.getPlayerVote(player);
                    if (elector == votePlayer) return;

                    votePlayer.setVote(votePlayer.getVote()-1);
                    mayorManager.removeVotePlayer(player);
                    mayorManager.voteElector(player, elector);
                } else {
                   mayorManager.voteElector(player, elector);
                }
            });

            items.add(perkItem);

        }
        return items;
    }

    private String getProgressBar(int vote, int totalVotes, NamedTextColor color) {
        int progressBars = 20;
        int barFill = (int) (((double) vote / totalVotes) * progressBars);

        StringBuilder bar = new StringBuilder();
        bar.append(ColorUtils.getColorCode(color));
        for (int i = 0; i < barFill; i++) {
            bar.append("|");
        }
        bar.append("§7");
        for (int i = barFill; i < progressBars; i++) {
            bar.append("|");
        }
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
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Membres";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }
}
