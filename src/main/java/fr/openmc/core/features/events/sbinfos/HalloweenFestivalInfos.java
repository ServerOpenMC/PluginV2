package fr.openmc.core.features.events.sbinfos;

import fr.openmc.core.features.displays.scoreboards.sb.InfoSwapper;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.PointRewards;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floors;
import fr.openmc.core.utils.bedrock.CharRemplacementUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class HalloweenFestivalInfos implements InfoSwapper {

    @Override
    public boolean isActive() {
        return HalloweenFestivalManager.isActive();
    }

    @Override
    public void addInfosTo(Player player, List<Component> lines) {

        lines.add(MiniMessage.miniMessage().deserialize(
                "<gradient:#F82C5D:#F64545><title></gradient>",
                Placeholder.component("title", TranslationManager.translation("feature.displays.scoreboard.corpse.title", true))
        ).decoration(TextDecoration.BOLD, true)); //TODO

        PointRewards reward = TerrorHouseManager.currentRewardToReach.getNextReward();
        Floors floorToReach = TerrorHouseManager.currentFloorToReach;

        double globalAdvancement = TerrorHouseManager.globalAdvancement;
        double floorAdvancement = TerrorHouseManager.floorAdvancement;

        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(reward.equals(PointRewards.NULL)
                                ? TranslationManager.translation("",
                                text(EconomyManager.getFormattedSimplifiedNumber(globalAdvancement))
                        )

                                : TranslationManager.translation("",
                                text(EconomyManager.getFormattedSimplifiedNumber(globalAdvancement)),
                                text(EconomyManager.getFormattedSimplifiedNumber(reward.getGlobalPointsRequire()))
                        )
                )
                .append(text(WitchShopManager.getHalloweenPointIcon()))
        );

        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .append(TerrorHouseManager.finalFloorReach
                        ? TranslationManager.translation("last floor reached")//TODO
                        : TranslationManager.translation("goal {étage} : xx / xx",//TODO
                        text(floorToReach.getSimplifyFloor()),
                        text(EconomyManager.getFormattedSimplifiedNumber(floorAdvancement)),
                        text(EconomyManager.getFormattedSimplifiedNumber(floorToReach.getRequire())))
                )
        );

        lines.add(text("  " + CharRemplacementUtils.getPointChar(player) + " ", NamedTextColor.DARK_GRAY)
                .appendSpace()
                .append(text(DateUtils.convertMillisToTime(HalloweenFestivalManager.getRemainingTime()), TextColor.color(0xFF8F06)))
        );
    }
}
