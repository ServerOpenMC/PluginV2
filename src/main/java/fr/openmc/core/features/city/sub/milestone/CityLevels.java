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
import fr.openmc.core.items.CustomItemRegistry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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
            Component.text("Niveau 1"),
            Component.text("Ere Urbaine"),
            List.of(
                    new CommandRequirement("/city create", 1)
            ),
            List.of(
                    new TemplateRewards(
                            Component.text("§6" + FREE_CLAIMS + " Claims §7Gratuits")
                    ),
                    MascotsSkinUnlockRewards.LEVEL_1,
                    MascotsLevelsRewards.LEVEL_1,
                    MemberLimitRewards.LEVEL_1
            ),
            0
    ),
    LEVEL_2(
            Component.text("Niveau 2"),
            Component.text("Les Fondations"),
            List.of(
                    new CommandRequirement("/city map", 1),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 5 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Claims (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city setwarp")
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
            Component.text("Niveau 3"),
            Component.text("Ville peu développé"),
            List.of(
                    new CommandRequirement("/city bank view", 1),
                    new CommandRequirement("/city chest", 1),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 10,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 10 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 10 Claims (%d/10)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 5000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 5k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 5k dans la banque (%s/5k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMembers().size() >= 2,
                            city -> ItemStack.of(Material.PLAYER_HEAD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 2 Membres");
                                }

                                return Component.text(String.format(
                                        "Avoir 2 Membres (%d/2)",
                                        city.getMembers().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
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
            Component.text("Niveau 4"),
            Component.text("Démocratie"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && !NotationManager.cityNotations.get(city.getUniqueId()).isEmpty(),
                            city -> ItemStack.of(Material.DIAMOND),
                            (city, level) -> Component.text("Recevoir une Notation")
                    ),
                    new TemplateRequirement(
                            city -> city.getRanks().size() >= 2,
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 2 Grades (/city rank)");
                                }

                                return Component.text(String.format(
                                        "Avoir 2 Grades (%d/2)",
                                        city.getRanks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 7500,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 7,5k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 7,5k dans la banque (%s/7,5k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
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

                            (city, level, scope) -> Component.text("Craftez une Urne"),
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
            Component.text("Niveau 5"),
            Component.text("Développement Economique"),
            List.of(
                    new TemplateRequirement(
                            city -> NPCManager.hasNPCS(city.getUniqueId()),
                            city -> CustomItemRegistry.getByName("omc_blocks:urne").getBest(),
                            (city, level) -> Component.text("Poser l'Urne")
                    ),

                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && NotationManager.cityNotations.get(city.getUniqueId()).stream().anyMatch(notation -> notation.getTotalNote() >= 10),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 10 points sur une des Notations")
                    ),
                    new CommandRequirement("/city mayor", 1),
                    new ItemDepositRequirement(Material.GOLD_BLOCK, 32),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 12000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 12k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 12k dans la banque (%s/12k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 20,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 20 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 20 Claims (%d/20)",
                                        city.getChunks().size()
                                ));
                            }
                    )
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
            Component.text("Niveau 6"),
            Component.text("Capitale"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && NotationManager.cityNotations.get(city.getUniqueId()).stream().anyMatch(notation -> notation.getTotalNote() >= 20),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 20 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 20000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 20k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 20k dans la banque (%s/20k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 25,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 25 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 25 Claims (%d/25)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 5,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 5 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(Material.STONE_BRICKS, 400),
                    new ItemDepositRequirement(Material.BLACK_CONCRETE, 184),
                    new ItemDepositRequirement(Material.WHITE_CONCRETE, 64),
                    new ItemDepositRequirement(Material.DIAMOND, 64)
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
            Component.text("Niveau 7"),
            Component.text("Royaume ?"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && NotationManager.cityNotations.get(city.getUniqueId()).stream().anyMatch(notation -> notation.getTotalNote() >= 30),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 30 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 30000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 30k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 30k dans la banque (%s/30k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 30,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 30 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 30 Claims (%d/30)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 6,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 6 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 400),
                    new ItemDepositRequirement(Material.DIAMOND_SWORD, 10),
                    new ItemDepositRequirement(Material.TNT, 10)
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
            Component.text("Niveau 8"),
            Component.text("Empire ?"),
            List.of(
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWar() >= 2,
                            city -> ItemStack.of(Material.IRON_SWORD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir fait 2 guerres");
                                }

                                return Component.text(String.format(
                                        "Avoir fait 2 guerres (%s/2)",
                                        WarManager.warHistory.get(city.getUniqueId()) != null ? WarManager.warHistory.get(city.getUniqueId()).getNumberWar() : 0
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWon() >= 1,
                            city -> ItemStack.of(Material.DIAMOND_SWORD),
                            (city, level) -> Component.text("Gagner une guerre")
                    ),
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && NotationManager.cityNotations.get(city.getUniqueId()).stream().anyMatch(notation -> notation.getTotalNote() >= 40) && NotationManager.cityNotations.get(city.getUniqueId()) != null,
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 40 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 60000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 60k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 60k dans la banque (%s/60k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 50,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 50 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 50 Claims (%d/50)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 7,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 7 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(Material.NETHERITE_INGOT, 4),
                    new ItemDepositRequirement(Material.OBSIDIAN, 128)
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
            Component.text("Niveau 9"),
            Component.text("Puissance militaire"),
            List.of(
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWon() >= 3,
                            city -> ItemStack.of(Material.DIAMOND_SWORD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Gagner 3 guerres");
                                }

                                return Component.text(String.format(
                                        "Gagner 3 guerres (%s/3)",
                                        WarManager.warHistory.get(city.getUniqueId()) != null ? WarManager.warHistory.get(city.getUniqueId()).getNumberWon() : 0
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && NotationManager.cityNotations.get(city.getUniqueId()).stream().anyMatch(notation -> notation.getTotalNote() >= 50),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 50 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 80000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 80k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 80k dans la banque (%s/80k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 8,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 8 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(Material.DIAMOND, 300),
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
            Component.text("Niveau 10"),
            Component.text("Métropole"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.top10Cities.contains(city.getUniqueId()),
                            city -> ItemStack.of(Material.HONEYCOMB),
                            (city, level) -> Component.text("Etre dans le top 10 des notations sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUniqueId()) != null && NotationManager.cityNotations.get(city.getUniqueId()).stream().anyMatch(notation -> notation.getTotalNote() >= 60),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 60 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUniqueId()) != null && WarManager.warHistory.get(city.getUniqueId()).getNumberWar() >= 10,
                            city -> ItemStack.of(Material.NETHERITE_SWORD),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir fait 10 guerres");
                                }

                                return Component.text(String.format(
                                        "Avoir fait 10 guerres (%s/10)",
                                        WarManager.warHistory.get(city.getUniqueId()) != null ? WarManager.warHistory.get(city.getUniqueId()).getNumberWar() : 0
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 125000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() != level.ordinal()) {
                                    return Component.text("Avoir 125k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 125k dans la banque (%s/125k)",
                                        EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 9,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 9 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_blocks:aywenite_block").getBest(), 32),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), 128),
                    new ItemDepositRequirement(Material.SCULK, 1028)
            ),
            List.of(
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

    private final Component name;
    private final Component description;
    private final List<CityRequirement> requirements;
    private final List<CityRewards> rewards;
    private final long upgradeTime;

    /**
     * Constructeur de l'énumération des niveaux de ville.
     *
     * @param name         le nom du niveau sous forme de composant
     * @param description  la description du niveau sous forme de composant
     * @param requirements la liste des exigences à remplir
     * @param rewards      la liste des récompenses obtenues une fois le niveau atteint
     * @param upgradeTime  le temps requis pour la montée de niveau (en secondes)
     */
    CityLevels(Component name, Component description, List<CityRequirement> requirements, List<CityRewards> rewards, long upgradeTime) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.rewards = rewards;
        this.upgradeTime = upgradeTime;
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
