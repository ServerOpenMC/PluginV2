package fr.openmc.core.features.city.menu.mayor;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.MayorCandidate;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.utils.ColorUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            assert city != null;


            int totalVotes = city.getMembers().size();
            for (MayorCandidate candidate : MayorManager.cityElections.get(city)) {
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

                boolean ench;
                if (candidate == playerVote) {
                    ench = true;
                } else {
                    ench = false;
                }


                ItemStack mayorItem = new ItemBuilder(this, ItemUtils.getPlayerSkull(candidate.getUUID()), itemMeta -> {
                    itemMeta.displayName(Component.text("Maire " + candidate.getName()).color(color).decoration(TextDecoration.ITALIC, false));
                    itemMeta.lore(loreMayor);
                    itemMeta.setEnchantmentGlintOverride(ench);
                }).setOnClick(inventoryClickEvent -> {
                    if (MayorManager.hasVoted(player)) {
                        if (candidate == playerVote) {
                            MessagesManager.sendMessage(player, Component.text("§7Vous avez déjà voté pour ce §6Maire"), Prefix.MAYOR, MessageType.ERROR, false);
                            return;
                        };

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
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
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
        return "Menu des Maires - Votes";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }
}
