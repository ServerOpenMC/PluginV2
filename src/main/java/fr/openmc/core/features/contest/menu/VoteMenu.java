package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.models.ContestPlayer;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VoteMenu extends Menu {

    public VoteMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Contests - Vote";
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
        
        String voteTeamMsg = "§7Votez pour la team ";
        String winMsg = "§7Faites la gagner en déposant le plus de points";
        String clickMsg = "§c§lATTENTION ! Le choix est définitif!";


        if (playerData == null) {
            ench1 = false;
            ench2 = false;
            lore1.add(Component.text(voteTeamMsg)
                    .append(Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1))
            );
            lore1.add(Component.text(winMsg));
            lore1.add(Component.text(clickMsg));


            lore2.add(Component.text(voteTeamMsg)
                    .append(Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2))
            );
            lore2.add(Component.text(winMsg));
            lore2.add(Component.text(clickMsg));
        } else {
            if (playerData.getCamp() <= 0) {
                ench1 = false;
                ench2 = false;
                lore1.add(Component.text(voteTeamMsg)
                        .append(Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1))
                );
                lore1.add(Component.text(winMsg));
                lore1.add(Component.text(clickMsg));

                lore2.add(Component.text(voteTeamMsg)
                        .append(Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2))
                );
                lore2.add(Component.text(winMsg));
                lore2.add(Component.text(clickMsg));

            } else if (playerData.getCamp() == 1) {
                lore1.add(
                        Component.text("§7Vous avez votez pour la team ")
                                .append(Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1))
                );
                lore1.add(Component.text("§7Faites la gagner en déposant le plus de points !"));
                ench1 = true;

                lore2.add(
                        Component.text("§7Faites perdre la team ")
                                .append(Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2))
                );
                lore2.add(Component.text("§7En apportant le plus de points que vous pouvez !"));
                ench2 = false;
            } else if (playerData.getCamp() == 2) {
                lore1.add(
                        Component.text("§7Faites perdre la team ")
                                .append(Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1))
                );
                lore1.add(Component.text("§7En apportant le plus de points que vous pouvez !"));
                ench1 = false;

                lore2.add(
                        Component.text("§7Vous avez votez pour la team ")
                                .append(Component.text(camp2Name).decoration(TextDecoration.ITALIC, false).color(color2))
                );
                lore2.add(Component.text("§7Faites la gagner en déposant le plus de points !"));
                ench2 = true;
            } else {
                ench1 = false;
                ench2 = false;
            }
        }

        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les contests !"),
                Component.text("§7Le déroulement, les résultats, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        inventory.put(camp1Slot, new ItemBuilder(this, m1, itemMeta -> {
            itemMeta.displayName(Component.text(camp1Name).decoration(TextDecoration.ITALIC, false).color(color1));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(ench1);
        }).setOnClick(inventoryClickEvent -> {
            if (playerData == null || playerData.getCamp() <= 0) {
                String messageTeam = "La Team ";

                String campName = ContestManager.data.getCamp1();
                String campColor = ContestManager.data.getColor1();

                NamedTextColor colorFinal = ColorUtils.getNamedTextColor(campColor);
                List<Component> loreAccept = Arrays.asList(
                        Component.text("§7Vous allez rejoindre ").append(Component.text(messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                        Component.text("§c§lATTENTION ! Vous ne pourrez changer de choix !")
                );

                List<Component> loreDeny = Arrays.asList(
                        Component.text("§7Vous allez annuler votre choix : ").append(Component.text(messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                        Component.text("§c§lATTENTION ! Vous ne pourrez changer de choix !")
                );

                ConfirmMenu menu = new ConfirmMenu(
                        player,
                        () -> {
                            ContestManager.dataPlayer.put(player.getUniqueId(), new ContestPlayer(player.getUniqueId(), 0, 1, colorFinal));
                            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
                            MessagesManager.sendMessage(player, Component.text("§7Vous avez bien rejoint : ").append(Component.text("La team " + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)), Prefix.CONTEST, MessageType.SUCCESS, false);

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
                String messageTeam = "La Team ";

                String campName = ContestManager.data.getCamp2();
                String campColor = ContestManager.data.getColor2();

                NamedTextColor colorFinal = ColorUtils.getNamedTextColor(campColor);
                List<Component> loreAccept = Arrays.asList(
                        Component.text("§7Vous allez rejoindre ").append(Component.text(messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                        Component.text("§c§lATTENTION ! Vous ne pourrez changer de choix !")
                );

                List<Component> loreDeny = Arrays.asList(
                        Component.text("§7Vous allez annuler votre choix : ").append(Component.text(messageTeam + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)),
                        Component.text("§c§lATTENTION ! Vous ne pourrez changer de choix !")
                );

                ConfirmMenu menu = new ConfirmMenu(
                        player,
                        () -> {
                            ContestManager.dataPlayer.put(player.getUniqueId(), new ContestPlayer(player.getUniqueId(), 0, 1, colorFinal));
                            player.playSound(player.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 0.2F);
                            MessagesManager.sendMessage(player, Component.text("§7Vous avez bien rejoint : ").append(Component.text("La team " + campName).decoration(TextDecoration.ITALIC, false).color(colorFinal)), Prefix.CONTEST, MessageType.SUCCESS, false);

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
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
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
