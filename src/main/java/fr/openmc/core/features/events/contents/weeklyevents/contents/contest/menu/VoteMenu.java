package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.models.ContestPlayer;
import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteMenu extends Menu {

    public VoteMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.contest.vote.menu.title");
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
    public void onInventoryClick(InventoryClickEvent event) {
        // empty
    }


    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Player player = getOwner();
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        String camp1Name = ContestManager.data.getCamp1();
        String camp2Name = ContestManager.data.getCamp2();

        String camp1Color = ContestManager.data.getColor1();
        String camp2Color = ContestManager.data.getColor2();

        NamedTextColor color1 = ColorUtils.getNamedTextColor(camp1Color);
        NamedTextColor color2 = ColorUtils.getNamedTextColor(camp2Color);
        Material m1 = ColorUtils.getMaterialFromColor(color1);
        Material m2 = ColorUtils.getMaterialFromColor(color2);

        int camp1Slot = 11;
        int camp2Slot = 15;

        List<Component> lore1 = new ArrayList<>();
        List<Component> lore2 = new ArrayList<>();
        boolean ench1;
        boolean ench2;

        ContestPlayer playerData = ContestManager.dataPlayer.get(player.getUniqueId());
        
        if (playerData == null) {
            ench1 = false;
            ench2 = false;
            lore1.add(TranslationManager.translation(
                    "feature.events.contest.vote.lore.vote_team",
                    Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1)
            ));
            lore1.add(TranslationManager.translation("feature.events.contest.vote.lore.win"));
            lore1.add(TranslationManager.translation("feature.events.contest.vote.lore.warning_choice"));


            lore2.add(TranslationManager.translation(
                    "feature.events.contest.vote.lore.vote_team",
                    Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2)
            ));
            lore2.add(TranslationManager.translation("feature.events.contest.vote.lore.win"));
            lore2.add(TranslationManager.translation("feature.events.contest.vote.lore.warning_choice"));
        } else {
            if (playerData.getCamp() <= 0) {
                ench1 = false;
                ench2 = false;
                lore1.add(TranslationManager.translation(
                        "feature.events.contest.vote.lore.vote_team",
                        Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1)
                ));
                lore1.add(TranslationManager.translation("feature.events.contest.vote.lore.win"));
                lore1.add(TranslationManager.translation("feature.events.contest.vote.lore.warning_choice"));

                lore2.add(TranslationManager.translation(
                        "feature.events.contest.vote.lore.vote_team",
                        Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2)
                ));
                lore2.add(TranslationManager.translation("feature.events.contest.vote.lore.win"));
                lore2.add(TranslationManager.translation("feature.events.contest.vote.lore.warning_choice"));

            } else if (playerData.getCamp() == 1) {
                lore1.add(TranslationManager.translation(
                        "feature.events.contest.vote.lore.voted_for",
                        Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1)
                ));
                lore1.add(TranslationManager.translation("feature.events.contest.vote.lore.win_exclaim"));
                ench1 = true;

                lore2.add(TranslationManager.translation(
                        "feature.events.contest.vote.lore.lose_team",
                        Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2)
                ));
                lore2.add(TranslationManager.translation("feature.events.contest.vote.lore.lose_detail"));
                ench2 = false;
            } else if (playerData.getCamp() == 2) {
                lore1.add(TranslationManager.translation(
                        "feature.events.contest.vote.lore.lose_team",
                        Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1)
                ));
                lore1.add(TranslationManager.translation("feature.events.contest.vote.lore.lose_detail"));
                ench1 = false;

                lore2.add(TranslationManager.translation(
                        "feature.events.contest.vote.lore.voted_for",
                        Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2)
                ));
                lore2.add(TranslationManager.translation("feature.events.contest.vote.lore.win_exclaim"));
                ench2 = true;
            } else {
                ench1 = false;
                ench2 = false;
            }
        }

        List<Component> loreInfo = TranslationManager.translationLore("feature.events.contest.vote.info.lore");

        inventory.put(camp1Slot, new ItemBuilder(this, m1, itemMeta -> {
            itemMeta.displayName(Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(ench1);
        }).setOnClick(inventoryClickEvent -> {
            if (playerData == null || playerData.getCamp() <= 0) {
                String campName = ContestManager.data.getCamp1();
                String campColor = ContestManager.data.getColor1();

                NamedTextColor colorFinal = ColorUtils.getNamedTextColor(campColor);
                Component teamComponent = TranslationManager.translation(
                        "feature.events.contest.team.label",
                        Component.text(campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)
                );
                List<Component> loreAccept = TranslationManager.translationLore(
                        "feature.events.contest.vote.confirm.join.lore",
                        teamComponent
                );

                List<Component> loreDeny = TranslationManager.translationLore(
                        "feature.events.contest.vote.confirm.cancel.lore",
                        teamComponent
                );

                ConfirmMenu menu = new ConfirmMenu(
                        player,
                        () -> {
                            ContestManager.dataPlayer.put(player.getUniqueId(), new ContestPlayer(player.getUniqueId(), 0, 1, colorFinal));
                            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
                            MessagesManager.sendMessage(
                                    player,
                                    TranslationManager.translation(
                                            "feature.events.contest.vote.joined",
                                            teamComponent
                                    ),
                                    Prefix.CONTEST,
                                    MessageType.SUCCESS,
                                    false
                            );

                            player.closeInventory();
                        },
                        () -> new VoteMenu(player).open(),
                        loreAccept,
                        loreDeny,
                        FontImageWrapper.replaceFontImages("§r§f:offset_-48::contest_menu:"),
                        InventorySize.LARGE,
                        15,
                        11
                );
                menu.open();
            }
        }));

        inventory.put(camp2Slot, new ItemBuilder(this, m2, itemMeta -> {
            itemMeta.displayName(Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2));
            itemMeta.lore(lore2);
            itemMeta.setEnchantmentGlintOverride(ench2);
        }).setOnClick(inventoryClickEvent -> {
            if (playerData == null || playerData.getCamp() <= 0) {
                String campName = ContestManager.data.getCamp2();
                String campColor = ContestManager.data.getColor2();

                NamedTextColor colorFinal = ColorUtils.getNamedTextColor(campColor);
                Component teamComponent = TranslationManager.translation(
                        "feature.events.contest.team.label",
                        Component.text(campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)
                );
                List<Component> loreAccept = TranslationManager.translationLore(
                        "feature.events.contest.vote.confirm.join.lore",
                        teamComponent
                );

                List<Component> loreDeny = TranslationManager.translationLore(
                        "feature.events.contest.vote.confirm.cancel.lore",
                        teamComponent
                );

                ConfirmMenu menu = new ConfirmMenu(
                        player,
                        () -> {
                            ContestManager.dataPlayer.put(player.getUniqueId(), new ContestPlayer(player.getUniqueId(), 0, 2, colorFinal));
                            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
                            MessagesManager.sendMessage(
                                    player,
                                    TranslationManager.translation(
                                            "feature.events.contest.vote.joined",
                                            teamComponent
                                    ),
                                    Prefix.CONTEST,
                                    MessageType.SUCCESS,
                                    false
                            );

                            player.closeInventory();
                        },
                        () -> new VoteMenu(player).open(),
                        loreAccept,
                        loreDeny,
                        FontImageWrapper.replaceFontImages("§r§f:offset_-48::contest_menu:"),
                        InventorySize.LARGE,
                        15,
                        11
                );
                menu.open();
            }
        }));

        inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.vote.info.name"));
            itemMeta.lore(loreInfo);
        }).setOnClick(inventoryClickEvent -> new MoreInfoMenu(player).open()));

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
