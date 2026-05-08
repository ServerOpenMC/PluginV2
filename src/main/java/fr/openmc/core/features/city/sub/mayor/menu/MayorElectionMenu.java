package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorCreateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorModifyMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MenuType;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_2_DAY;

public class MayorElectionMenu extends Menu {

    public MayorElectionMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.election.name");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-38::mayor:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());

        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);

        Supplier<ItemBuilder> electionItemSupplier = () -> {
            List<Component> loreElection;
            if (MayorManager.hasVoted(player)) {
                loreElection = TranslationManager.translationLore(
                        "feature.city.mayor.menu.election.item.lore.voted",
                        Component.text(MayorManager.getPlayerVote(player).getName())
                                .decoration(TextDecoration.ITALIC, false)
                                .color(MayorManager.getPlayerVote(player).getCandidateColor()),
                        Component.text(DateUtils.getTimeUntilNextDay(PHASE_2_DAY)).color(NamedTextColor.RED)
                );
            } else {
                loreElection = TranslationManager.translationLore(
                        "feature.city.mayor.menu.election.item.lore.not_voted",
                        Component.text(DateUtils.getTimeUntilNextDay(PHASE_2_DAY)).color(NamedTextColor.RED)
                );
            }

            return new ItemBuilder(this, Material.JUKEBOX, itemMeta -> {
                itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.election.item.name"));
                itemMeta.lore(loreElection);
            }).setOnClick(inventoryClickEvent -> {
                if (MayorManager.cityElections.get(city.getUniqueId()) == null) {
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.menu.election.error.no_candidate"), Prefix.MAYOR, MessageType.ERROR, true);
                    return;
                }
                new MayorVoteMenu(player).open();
            });
        };

        MenuUtils.runDynamicItem(player, this, 29, electionItemSupplier)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

        List<Component> loreCandidature;
        if (MayorManager.hasCandidated(player)) {
            loreCandidature = TranslationManager.translationLore("feature.city.mayor.menu.election.candidature.lore.already");
        } else {
            loreCandidature = TranslationManager.translationLore("feature.city.mayor.menu.election.candidature.lore.new");
        }

        if (hasPermissionOwner) {
            List<Component> lorePerkOwner;
            if (MayorManager.hasChoicePerkOwner(player)) {
                Perks perk1 = PerkManager.getPerkById(city.getMayor().getIdPerk1());
                if (perk1 == null) return Map.of();
                lorePerkOwner = new ArrayList<>(List.of(
                        TranslationManager.translation("feature.city.mayor.menu.election.owner_reform.lore.chosen")
                ));
                lorePerkOwner.add(Component.empty());
                lorePerkOwner.add(TranslationManager.translation(perk1.getNameKey()));
                lorePerkOwner.addAll(TranslationManager.translationLore(perk1.getLoreKey()));
            } else {
                lorePerkOwner = TranslationManager.translationLore("feature.city.mayor.menu.election.owner_reform.lore.choice");
            }

            inventory.put(22, new ItemBuilder(this, SkullUtils.getPlayerSkull(player.getUniqueId()), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.election.owner_reform.name"));
                itemMeta.lore(lorePerkOwner);
            }).setOnClick(inventoryClickEvent -> {
                if (!MayorManager.hasChoicePerkOwner(player)) {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> new MayorCreateMenu(player, null, null, null, MenuType.OWNER_1).open());
                }
            }));
        }

        inventory.put(33, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.election.candidature.name"));
            itemMeta.lore(loreCandidature);
        }).setOnClick(inventoryClickEvent -> {
            if (MayorManager.hasCandidated(player)) {
                new MayorModifyMenu(player).open();
            } else {
                new MayorCreateMenu(player, null, null, null, MenuType.CANDIDATE).open();
            }
        }));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.common.back.name").color(NamedTextColor.GREEN));
            itemMeta.lore(TranslationManager.translationLore("feature.city.mayor.menu.common.back.lore"));
        }, true));


        List<Component> loreInfo = TranslationManager.translationLore("feature.city.mayor.menu.common.more_info.lore");

        inventory.put(52, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mayor.menu.common.more_info.name"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(getOwner()).open()));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
