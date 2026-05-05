package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.milestone.requirements.CommandRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.EventTemplateRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.ItemDepositRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.TemplateRequirement;
import fr.openmc.core.features.city.sub.milestone.rewards.*;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

import static fr.openmc.core.features.city.actions.CityCreateAction.FREE_CLAIMS;

@Getter
public enum CityLevels {
    LEVEL_1(
            "feature.city.levels.level_1.name",
            "feature.city.levels.level_1.description",
            List.of(
                    new CommandRequirement("/city create", 1)
            ),
            List.of(
                    new TemplateRewards(
                            "feature.city.levels.rewards.free_claims",
                            Component.text(FREE_CLAIMS).color(NamedTextColor.GOLD)
                    ),
                    MascotsSkinUnlockRewards.LEVEL_1,
                    MascotsLevelsRewards.LEVEL_1,
                    MemberLimitRewards.LEVEL_1
            ),
            0
    ),
    LEVEL_2(
            "feature.city.levels.level_2.name",
            "feature.city.levels.level_2.description",
            List.of(
                    new CommandRequirement("/city map", 1),
                    new CommandRequirement("/city claim view", 1),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.claims",
                                            Component.text(5)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.claims.progress",
                                        Component.text(5),
                                        Component.text(city.getChunks().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> TranslationManager.translation("feature.city.levels.requirements.setwarp")
                    ),
                    new ItemDepositRequirement(Material.GOLD_INGOT, 128)
            ),
            List.of(
                    FeaturesRewards.LEVEL_2,
                    PlayerBankLimitRewards.LEVEL_2,
                    MascotsLevelsRewards.LEVEL_2,
                    MascotsSkinUnlockRewards.LEVEL_2,
                    ChestPageLimitRewards.LEVEL_2,
                    MemberLimitRewards.LEVEL_2
            ),
            60 * 10
    ),
    LEVEL_3(
            "feature.city.levels.level_3.name",
            "feature.city.levels.level_3.description",
            List.of(
                    new CommandRequirement("/city bank", 1),
                    new CommandRequirement("/city chest", 1),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 10,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.claims",
                                            Component.text(10)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.claims.progress",
                                        Component.text(10),
                                        Component.text(city.getChunks().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 5000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("5k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("5k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMembers().size() >= 2,
                            city -> ItemStack.of(Material.PLAYER_HEAD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.members",
                                            Component.text(2)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.members.progress",
                                        Component.text(2),
                                        Component.text(city.getMembers().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.mascot_level",
                                    Component.text(2)
                            )
                    ),
                    new ItemDepositRequirement(Material.SPIDER_EYE, 8),
                    new ItemDepositRequirement(Material.BONE, 16),
                    new ItemDepositRequirement(Material.CHEST, 16),
                    new ItemDepositRequirement(Material.DIAMOND, 16)
            ),
            List.of(
                    FeaturesRewards.LEVEL_3,
                    PlayerBankLimitRewards.LEVEL_3,
                    MascotsLevelsRewards.LEVEL_3,
                    MascotsSkinUnlockRewards.LEVEL_3,
                    MemberLimitRewards.LEVEL_3,
                    ChestPageLimitRewards.LEVEL_3,
                    RankLimitRewards.LEVEL_3
            ),
            60 * 30
    ),
    LEVEL_4(
            "feature.city.levels.level_4.name",
            "feature.city.levels.level_4.description",
            List.of(
                    new TemplateRequirement(
                            city -> !city.getAvailableNotation().isEmpty(),
                            city -> ItemStack.of(Material.DIAMOND),
                            (city, level) -> TranslationManager.translation("feature.city.levels.requirements.notation.receive")
                    ),
                    new TemplateRequirement(
                            city -> city.getRanks().size() >= 2,
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.ranks",
                                            Component.text(2)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.ranks.progress",
                                        Component.text(2),
                                        Component.text(city.getRanks().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 15000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("15k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("15k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 128),
                    new ItemDepositRequirement(Material.GRAY_WOOL, 32),
                    new ItemDepositRequirement(Material.GLASS, 128),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_foods:courgette").getBest(), 8),
                    new EventTemplateRequirement(
                            (city, scope) -> Objects.requireNonNull(CityStatisticsManager
                                            .getOrCreateStat(city.getUniqueId(), scope))
                                    .asInt() >= 1,

                            city -> CustomItemRegistry.getByName("omc_blocks:urne").getBest(),
                            
                            (city, level, scope) -> TranslationManager.translation("feature.city.levels.requirements.craft_urne"),
                            "craft_urne",
                            CraftItemEvent.class,
                            (event, scope) -> {
                                CraftItemEvent eventCraft = (CraftItemEvent) event;
                                ItemStack item = eventCraft.getCurrentItem();
                                if (item == null || !item.isSimilar(CustomItemRegistry.getByName("omc_blocks:urne").getBest()))
                                    return;

                                Player player = (Player) eventCraft.getWhoClicked();
                                City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                                if (Objects.requireNonNull(CityStatisticsManager.getOrCreateStat(playerCity.getUniqueId(), scope)).asInt() >= 1)
                                    return;

                                CityStatisticsManager.increment(playerCity.getUniqueId(), scope, 1);
                            }
                    )
            ),
            List.of(
                    FeaturesRewards.LEVEL_4,
                    PlayerBankLimitRewards.LEVEL_4,
                    InterestRewards.LEVEL_4,
                    MascotsLevelsRewards.LEVEL_4,
                    MascotsSkinUnlockRewards.LEVEL_4,
                    ChestPageLimitRewards.LEVEL_4,
                    RankLimitRewards.LEVEL_4
            ),
            60 * 90
    ),
    LEVEL_5(
            "feature.city.levels.level_5.name",
            "feature.city.levels.level_5.description",
            List.of(
                    new TemplateRequirement(
                            city -> NPCManager.hasNPCS(city.getUniqueId()),
                            city -> CustomItemRegistry.getByName("omc_blocks:urne").getBest(),
                            (city, level) -> TranslationManager.translation("feature.city.levels.requirements.place_urne")
                    ),

                    new TemplateRequirement(
                            city -> city.getAvailableNotation().stream().anyMatch(notation -> notation.getTotalNote() >= 10),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.notation.points",
                                    Component.text(10)
                            )
                    ),
                    new CommandRequirement("/city mayor", 1),
                    new ItemDepositRequirement(Material.GOLD_BLOCK, 32),
                    new ItemDepositRequirement(Material.STONE_BRICKS, 200),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 20000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("20k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("20k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 23,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.claims",
                                            Component.text(23)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.claims.progress",
                                        Component.text(23),
                                        Component.text(city.getChunks().size())
                                );
                            }
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_foods:the_mixture").getBest(), 32)
            ),
            List.of(
                    FeaturesRewards.LEVEL_5,
                    PlayerBankLimitRewards.LEVEL_5,
                    InterestRewards.LEVEL_5,
                    MascotsLevelsRewards.LEVEL_5,
                    MascotsSkinUnlockRewards.LEVEL_5,
                    MemberLimitRewards.LEVEL_5,
                    ChestPageLimitRewards.LEVEL_5,
                    RankLimitRewards.LEVEL_5
            ),
            60 * 60 * 3
    ),
    LEVEL_6(
            "feature.city.levels.level_6.name",
            "feature.city.levels.level_6.description",
            List.of(
                    new TemplateRequirement(
                            city -> city.getAvailableNotation().stream().anyMatch(notation -> notation.getTotalNote() >= 20),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.notation.points",
                                    Component.text(20)
                            )
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 30000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("30k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("30k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 27,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.claims",
                                            Component.text(27)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.claims.progress",
                                        Component.text(27),
                                        Component.text(city.getChunks().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 5,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.mascot_level",
                                    Component.text(5)
                            )
                    ),
                    new ItemDepositRequirement(Material.STONE_BRICKS, 400),
                    new ItemDepositRequirement(Material.BLACK_CONCRETE, 184),
                    new ItemDepositRequirement(Material.WHITE_CONCRETE, 64),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_foods:courgette").getBest(), 98),
                    new ItemDepositRequirement(Material.DIAMOND, 128)
            ),
            List.of(
                    PlayerBankLimitRewards.LEVEL_6,
                    InterestRewards.LEVEL_6,
                    MascotsLevelsRewards.LEVEL_6,
                    MascotsSkinUnlockRewards.LEVEL_6,
                    MemberLimitRewards.LEVEL_6,
                    ChestPageLimitRewards.LEVEL_6,
                    RankLimitRewards.LEVEL_6
            ),
            60 * 60 * 5
    ),
    LEVEL_7(
            "feature.city.levels.level_7.name",
            "feature.city.levels.level_7.description",
            List.of(
                    new TemplateRequirement(
                            city -> city.getAvailableNotation().stream().anyMatch(notation -> notation.getTotalNote() >= 30),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.notation.points",
                                    Component.text(30)
                            )
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 40000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("40k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("40k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 30,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.claims",
                                            Component.text(30)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.claims.progress",
                                        Component.text(30),
                                        Component.text(city.getChunks().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 6,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.mascot_level",
                                    Component.text(6)
                            )
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 400),
                    new ItemDepositRequirement(Material.DIAMOND_SWORD, 10),
                    new ItemDepositRequirement(Material.TNT, 64),
                    new ItemDepositRequirement(Material.FLINT_AND_STEEL, 16),
                    new ItemDepositRequirement(Material.NETHERITE_INGOT, 2)
            ),
            List.of(
                    FeaturesRewards.LEVEL_7,
                    PlayerBankLimitRewards.LEVEL_7,
                    InterestRewards.LEVEL_7,
                    MascotsLevelsRewards.LEVEL_7,
                    MascotsSkinUnlockRewards.LEVEL_7,
                    MemberLimitRewards.LEVEL_7,
                    ChestPageLimitRewards.LEVEL_7,
                    RankLimitRewards.LEVEL_7
            ),
            60 * 60 * 10
    ),
    LEVEL_8(
            "feature.city.levels.level_8.name",
            "feature.city.levels.level_8.description",
            List.of(
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWar() >= 2,
                            city -> ItemStack.of(Material.IRON_SWORD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.war.count",
                                            Component.text(2)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.war.count.progress",
                                        Component.text(2),
                                        Component.text(WarManager.warHistory.get(city.getUniqueId()) != null ? WarManager.warHistory.get(city.getUniqueId()).getNumberWar() : 0)
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWon() >= 1,
                            city -> ItemStack.of(Material.DIAMOND_SWORD),
                            (city, level) -> TranslationManager.translation("feature.city.levels.requirements.war.win")
                    ),
                    new TemplateRequirement(
                            city -> city.getAvailableNotation().stream().anyMatch(notation -> notation.getTotalNote() >= 40),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.notation.points",
                                    Component.text(40)
                            )
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 60000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("60k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("60k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 50,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.claims",
                                            Component.text(50)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.claims.progress",
                                        Component.text(50),
                                        Component.text(city.getChunks().size())
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 7,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.mascot_level",
                                    Component.text(7)
                            )
                    ),
                    new ItemDepositRequirement(Material.NETHERITE_INGOT, 4),
                    new ItemDepositRequirement(Material.OBSIDIAN, 128),
                    new ItemDepositRequirement(Material.WATER_BUCKET, 16)
            ),
            List.of(
                    FeaturesRewards.LEVEL_8,
                    PlayerBankLimitRewards.LEVEL_8,
                    InterestRewards.LEVEL_8,
                    MascotsLevelsRewards.LEVEL_8,
                    MascotsSkinUnlockRewards.LEVEL_8,
                    MemberLimitRewards.LEVEL_8,
                    ChestPageLimitRewards.LEVEL_8,
                    RankLimitRewards.LEVEL_8
            ),
            60 * 60 * 16
    ),
    LEVEL_9(
            "feature.city.levels.level_9.name",
            "feature.city.levels.level_9.description",
            List.of(
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWon() >= 3,
                            city -> ItemStack.of(Material.DIAMOND_SWORD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.war.win.count",
                                            Component.text(3)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.war.win.count.progress",
                                        Component.text(3),
                                        Component.text(WarManager.warHistory.get(city.getUniqueId()) != null ? WarManager.warHistory.get(city.getUniqueId()).getNumberWon() : 0)
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getAvailableNotation().stream().anyMatch(notation -> notation.getTotalNote() >= 50),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.notation.points",
                                    Component.text(50)
                            )
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 80000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("80k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("80k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 8,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.mascot_level",
                                    Component.text(8)
                            )
                    ),
                    new ItemDepositRequirement(Material.DIAMOND, 300),
                    new ItemDepositRequirement(Material.CYAN_CONCRETE, 200),
                    new ItemDepositRequirement(Material.DRIED_GHAST, 5),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_foods:kebab").getBest(), 128)
            ),
            List.of(
                    FeaturesRewards.LEVEL_9,
                    PlayerBankLimitRewards.LEVEL_9,
                    InterestRewards.LEVEL_9,
                    MascotsLevelsRewards.LEVEL_9,
                    MascotsSkinUnlockRewards.LEVEL_9,
                    ChestPageLimitRewards.LEVEL_9,
                    RankLimitRewards.LEVEL_9
            ),
            60 * 60 * 24
    ),
    LEVEL_10(
            "feature.city.levels.level_10.name",
            "feature.city.levels.level_10.description",
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.top10Cities.contains(city.getUniqueId()),
                            city -> ItemStack.of(Material.HONEYCOMB),
                            (city, level) -> TranslationManager.translation("feature.city.levels.requirements.notation.top10")
                    ),
                    new TemplateRequirement(
                            city -> city.getAvailableNotation().stream().anyMatch(notation -> notation.getTotalNote() >= 60),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.notation.points",
                                    Component.text(60)
                            )
                    ),
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWar() >= 10,
                            city -> ItemStack.of(Material.NETHERITE_SWORD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.war.count",
                                            Component.text(10)
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.war.count.progress",
                                        Component.text(10),
                                        Component.text(WarManager.warHistory.get(city.getUniqueId()) != null ? WarManager.warHistory.get(city.getUniqueId()).getNumberWar() : 0)
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 200000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return TranslationManager.translation(
                                            "feature.city.levels.requirements.bank",
                                            Component.text("200k")
                                    );
                                }

                                return TranslationManager.translation(
                                        "feature.city.levels.requirements.bank.progress",
                                        Component.text("200k"),
                                        Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance()))
                                );
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 9,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> TranslationManager.translation(
                                    "feature.city.levels.requirements.mascot_level",
                                    Component.text(9)
                            )
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_blocks:aywenite_block").getBest(), 64),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), 128),
                    new ItemDepositRequirement(Material.SCULK, 1028)
            ),
            List.of(
                    FeaturesRewards.LEVEL_10,
                    PlayerBankLimitRewards.LEVEL_10,
                    InterestRewards.LEVEL_10,
                    MascotsLevelsRewards.LEVEL_10,
                    MascotsSkinUnlockRewards.LEVEL_10,
                    MemberLimitRewards.LEVEL_10,
                    ChestPageLimitRewards.LEVEL_10,
                    RankLimitRewards.LEVEL_10
            ),
            60 * 60 * 24 * 2
    ),
    ;

    private final String nameKey;
    private final String descriptionKey;
    private final List<CityRequirement> requirements;
    private final List<CityRewards> rewards;
    private final long upgradeTime;

    /**
     * Constructeur de l'énumération des niveaux de ville.
     *
     * @param nameKey      la clé du nom du niveau
     * @param descriptionKey la clé de la description du niveau
     * @param requirements la liste des exigences à remplir
     * @param rewards      la liste des récompenses obtenues une fois le niveau atteint
     * @param upgradeTime  le temps requis pour la montée de niveau (en secondes)
     */
    CityLevels(String nameKey, String descriptionKey, List<CityRequirement> requirements, List<CityRewards> rewards, long upgradeTime) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.requirements = requirements;
        this.rewards = rewards;
        this.upgradeTime = upgradeTime;
    }

    public Component getName() {
        return TranslationManager.translation(nameKey);
    }

    public Component getDescription() {
        return TranslationManager.translation(descriptionKey);
    }

    /**
     * Vérifie si toutes les exigences de la ville sont satisfaites pour ce niveau.
     *
     * @param city la ville à vérifier
     * @return {@code true} si toutes les exigences sont remplies, {@code false} sinon
     */
    public boolean isCompleted(City city) {
        for (CityRequirement requirement : requirements) {
            if (!requirement.isDone(city, this)) return false;
        }
        return true;
    }

    /**
     * Lance le cooldown pour la montée de niveau de la ville.
     *
     * @param city la ville concernée
     */
    public void runUpgradeTime(City city) {
        DynamicCooldownManager.use(city.getUniqueId(), "city:upgrade-level", upgradeTime * 1000);
    }
}
