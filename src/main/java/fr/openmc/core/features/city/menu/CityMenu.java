package fr.openmc.core.features.city.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.actions.CityLeaveAction;
import fr.openmc.core.features.city.conditions.CityChestConditions;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.features.city.menu.playerlist.CityPlayerListMenu;
import fr.openmc.core.features.city.sub.bank.conditions.CityBankConditions;
import fr.openmc.core.features.city.sub.bank.menu.CityBankMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotsDeadMenu;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.actions.MayorCommandAction;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.menu.CityMilestoneMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.features.city.sub.notation.NotationNote;
import fr.openmc.core.features.city.sub.notation.menu.NotationDialog;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.features.city.sub.rank.menus.CityRanksMenu;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_1_DAY;
import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_2_DAY;

public class CityMenu extends Menu {

    public CityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes";
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

		City city = CityManager.getPlayerCity(player.getUniqueId());
		assert city != null;

        List<Component> loreRanks;
        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
            loreRanks = List.of(
                    Component.text("§7Gérer les grades de votre ville"),
                    Component.text("§7Votre Grade : §d" + city.getRankName(player.getUniqueId())),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX GRADES")
            );
        } else {
            loreRanks = List.of(
                    Component.text("§7Gérer les grades de votre ville"),
                    Component.empty(),
                    Component.text("§cVous devez etre Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + " pour débloquer ceci")
            );
        }

        inventory.put(0, new ItemBuilder(this, Material.COMMAND_BLOCK, itemMeta -> {
            itemMeta.displayName(Component.text("§6Grades de la Ville"));
            itemMeta.lore(loreRanks);
        }).setOnClick(inventoryClickEvent -> {
            if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
                MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette Feature ! Veuillez Améliorer votre Ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            new CityRanksMenu(getOwner(), city).open();
        }));

		boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CityPermission.RENAME);
		boolean hasPermissionChest = city.hasPermission(player.getUniqueId(), CityPermission.CHEST);
		boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CityPermission.OWNER);
		boolean hasPermissionChunkSee = city.hasPermission(player.getUniqueId(), CityPermission.SEE_CHUNKS);
		boolean hasPermissionChangeType = city.hasPermission(player.getUniqueId(), CityPermission.TYPE);

        String mayorName = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getName() : "§7Aucun";
        NamedTextColor mayorColor = (city.getMayor() != null && city.getMayor().getName() != null) ? city.getMayor().getMayorColor() : NamedTextColor.DARK_GRAY;

        List<Component> loreMillestoneCity;

        loreMillestoneCity = List.of(
                Component.text("§8§oAcceder à votre route de progression de la ville !"),
                Component.text("§8§oImportant pour débloquer les différentes features des Villes !"),
                Component.empty(),
                Component.text("§7Level : §3" + city.getLevel()),
                Component.empty(),
                Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MILESTONE")
        );

        inventory.put(3, new ItemBuilder(this, Material.NETHER_STAR, itemMeta -> {
            itemMeta.itemName(Component.text("§3Milestone de votre ville"));
            itemMeta.lore(loreMillestoneCity);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            new CityMilestoneMenu(player, cityCheck).open();
        }));

        List<Component> loreModifyCity;

        if (hasPermissionRenameCity || hasPermissionOwner) {
            loreModifyCity = List.of(
                    Component.text("§7Propriétaire de la Ville : " + CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER)).getName()),
                    Component.text("§dMaire de la Ville §7: ").append(Component.text(mayorName).color(mayorColor).decoration(TextDecoration.ITALIC, false)),
                    Component.text("§7Membre(s) : §d" + city.getMembers().size() + "§7/§d" + MemberLimitRewards.getMemberLimit(city.getLevel())),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR MODIFIER LA VILLE")
            );
        } else {
            loreModifyCity = List.of(
                    Component.text("§7Propriétaire de la Ville : " + CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER)).getName()),
                    Component.text("§dMaire de la Ville §7: ").append(Component.text(mayorName).color(mayorColor).decoration(TextDecoration.ITALIC, false)),
                    Component.text("§7Membre(s) : §d" + city.getMembers().size() + "§7/§d" + MemberLimitRewards.getMemberLimit(city.getLevel()))
            );
        }

        inventory.put(4, new ItemBuilder(this, Material.BOOKSHELF, itemMeta -> {
            itemMeta.itemName(Component.text("§d" + city.getName()));
            itemMeta.lore(loreModifyCity);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (hasPermissionOwner) {
                CityModifyMenu menu = new CityModifyMenu(player);
                menu.open();
            }
        }));

        CityNotation notation = city.getNotationOfWeek(DateUtils.getWeekFormat());
        if (notation != null) {
            List<Component> loreNotation = new ArrayList<>() {
                {
                    add(Component.text("§7Notation de la Ville : §9" + Math.floor(notation.getTotalNote()) + "§7/§9" + NotationNote.getMaxTotalNote()));
                    add(Component.text("§7Argent remporté : §6" + EconomyManager.getFormattedSimplifiedNumber(notation.getMoney()) + EconomyManager.getEconomyIcon()));
                    add(Component.empty());
                    add(Component.text("§e§lCLIQUEZ ICI POUR VOIR LA NOTATION"));
                }
            };

            inventory.put(5, new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
                itemMeta.itemName(Component.text("§3La Notation de Votre Ville"));
                itemMeta.lore(loreNotation);
            }).setOnClick(inventoryClickEvent -> {
                NotationDialog.send(player, DateUtils.getWeekFormat());
            }));
        }

        Mascot mascot = city.getMascot();

        Supplier<ItemBuilder> mascotItemSupplier = () -> {
            LivingEntity mob;
            List<Component> loreMascots;
            if (mascot != null) {
                mob = (LivingEntity) mascot.getEntity();

                if (mob != null) {
                    double maxHealth = mob.getAttribute(Attribute.MAX_HEALTH).getValue();
                    if (!mascot.isAlive()) {
		                loreMascots = List.of(
				                Component.text("§7Vie : §c" + Math.floor(mob.getHealth()) + "§4/§c" + maxHealth),
				                Component.text("§7Status : §cMorte"),
				                Component.text("§7Réapparition dans : " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:immunity"))),
				                Component.text("§7Niveau : §c" + mascot.getLevel()),
                                Component.empty(),
				                Component.text("§e§lCLIQUEZ ICI POUR INTERAGIR AVEC")
		                );
	                } else {
		                loreMascots = List.of(
				                Component.text("§7Vie : §c" + Math.floor(mob.getHealth()) + "§4/§c" + maxHealth),
				                Component.text("§7Status : §aEn Vie"),
				                Component.text("§7Niveau : §c" + mascot.getLevel()),
                                Component.empty(),
				                Component.text("§e§lCLIQUEZ ICI POUR INTERAGIR AVEC")
		                );
	                }
                } else {
	                loreMascots = List.of(
			                Component.text("§cMascotte non trouvée")
	                );
				}
            } else {
                mob = null;
                loreMascots = List.of(
                        Component.text("§cMascotte Inexistante")
                );
            }

            return new ItemBuilder(this, mascot != null ? mascot.getMascotEgg() : Material.BARRIER, itemMeta -> {
                itemMeta.itemName(Component.text("§cVotre Mascotte"));
                itemMeta.lore(loreMascots);
            }).setOnClick(inventoryClickEvent -> {
                if (mascot == null) return;
                if (mob == null) return;

                if (!mascot.isAlive()) {
                    MascotsDeadMenu menu = new MascotsDeadMenu(player, city.getUniqueId());
                    menu.open();
                    return;
                }

                MascotMenu menu = new MascotMenu(player, mascot);
                menu.open();
            });
        };

        if (!mascot.isAlive()) {
            MenuUtils.runDynamicItem(player, this, 8, mascotItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            inventory.put(8, mascotItemSupplier.get());
        }

        List<Component> loreChunkCity;

        if (hasPermissionChunkSee) {
            loreChunkCity = List.of(
                    Component.text("§7Votre ville a une superficie de §6" + city.getChunks().size()),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER A LA CARTE")
            );
        } else {
            loreChunkCity = List.of(
                    Component.text("§7Votre ville a une superficie de §6" + city.getChunks().size())
            );
        }

        inventory.put(19, new ItemBuilder(this, Material.OAK_FENCE, itemMeta -> {
            itemMeta.itemName(Component.text("§6Taille de votre Ville"));
            itemMeta.lore(loreChunkCity);
        }).setOnClick(inventoryClickEvent -> {
            if (!hasPermissionChunkSee) {
                MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions de voir les claims"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            CityChunkMenu menu = new CityChunkMenu(player);
            menu.open();
        }));

        ItemStack playerHead = ItemUtils.getPlayerSkull(player.getUniqueId());

        inventory.put(22, new ItemBuilder(this, playerHead, itemMeta -> {
            itemMeta.displayName(Component.text("§dListe des Membres"));
            itemMeta.lore(List.of(
                    Component.text("§7Il y a actuellement §d" + city.getMembers().size() + "§7 membre(s) dans votre ville"),
                    Component.text("§7Vous avez une limite de membre de §d" + MemberLimitRewards.getMemberLimit(city.getLevel()) + "§7 membre(s)"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR VOIR LA LISTE DES JOUEURS")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityPlayerListMenu menu = new CityPlayerListMenu(player);
            menu.open();
        }));

        Supplier<ItemBuilder> electionItemSupplier = () -> {
                List<Component> loreElections = List.of();
            if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
                if (MayorManager.phaseMayor == 2) {
                    loreElections = List.of(
                            Component.text("§7En ce moment, les Maires sont tous appliqués dans les Villes !"),
                            Component.text("§7Sauf la votre !"),
                            Component.empty(),
                            Component.text("§cVous devez etre Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR) + " pour débloquer ceci")
                    );
                } else if (MayorManager.phaseMayor == 1) {
                    loreElections = List.of(
                            Component.text("§7Les Elections sont actuellement §6ouverte"),
                            Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                            Component.text("§7Mais vous ne pouvez pas y acceder !"),
                            Component.empty(),
                            Component.text("§cVous devez etre Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR) + " pour débloquer ceci")
                    );
                } else {
                    loreElections = List.of(
                            Component.text("§cErreur")
                    );
                }
            } else {
                if (city.getElectionType() == ElectionType.ELECTION) {
                    if (MayorManager.phaseMayor == 2) {
                        loreElections = List.of(
                                Component.text("§7Votre ville a un §6Maire !"),
                                Component.text("§7Maire : ").append(Component.text(mayorName)).color(mayorColor).decoration(TextDecoration.ITALIC, false),
                                Component.empty(),
                                Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX INFORMATIONS")
                        );
                    } else if (MayorManager.phaseMayor == 1) {
                        loreElections = List.of(
                                Component.text("§7Les Elections sont actuellement §6ouverte"),
                                Component.empty(),
                                Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                                Component.empty(),
                                Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX ELECTIONS")

                        );
                    } else {
                        loreElections = List.of(
                                Component.text("§cErreur")
                        );
                    }
                } else {
                    if (MayorManager.phaseMayor == 2) {
                        loreElections = List.of(
                                Component.text("§7Votre ville a un §6Maire !"),
                                Component.text("§7Maire §7: ").append(Component.text(mayorName)).color(mayorColor).decoration(TextDecoration.ITALIC, false),
                                Component.text("§cOuverture des Elections dans " + DateUtils.getTimeUntilNextDay(PHASE_1_DAY)),
                                Component.empty(),
                                Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX INFORMATIONS")
                        );
                    } else if (MayorManager.phaseMayor == 1) {
                        if (hasPermissionOwner) {
                            if (city.hasMayor()) {
                                loreElections = List.of(
                                        Component.text("§7Les Elections sont §6désactivées"),
                                        Component.text("§cIl vous faut au moins §6" + MayorManager.MEMBER_REQUEST_ELECTION + " §cmembres"),
                                        Component.empty(),
                                        Component.text("§7Vous avez déjà choisis vos §3Réformes §7!"),
                                        Component.text("§7Cependant vous pouvez changer votre couleur !"),
                                        Component.empty(),
                                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY))
                                );
                            } else {
                                loreElections = List.of(
                                        Component.text("§7Les Elections sont §6désactivées"),
                                        Component.text("§cIl vous faut au moins §6" + MayorManager.MEMBER_REQUEST_ELECTION + " §cmembres"),
                                        Component.empty(),
                                        Component.text("§7Seul le Propriétaire peut choisir §3les Réformes §7qu'il veut."),
                                        Component.empty(),
                                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                                        Component.empty(),
                                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR VOS REFORMES")
                                );
                            }
                        } else {
                            loreElections = List.of(
                                    Component.text("§7Les Elections sont §6désactivées"),
                                    Component.text("§cIl vous faut au moins §6" + MayorManager.MEMBER_REQUEST_ELECTION + " §cmembres"),
                                    Component.empty(),
                                    Component.text("§7Seul le Propriétaire peut choisir §3les Réformes §7qu'il veut."),
                                    Component.empty(),
                                    Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY))
                            );
                        }
                        }
                    }
                }

                List<Component> finalLoreElections = loreElections;
                return new ItemBuilder(this, Material.JUKEBOX, itemMeta -> {
                    itemMeta.displayName(Component.text("§6Les Elections"));
                    itemMeta.lore(finalLoreElections);
                }).setOnClick(inventoryClickEvent -> MayorCommandAction.launchInteractionMenu(player));
        };

        MenuUtils.runDynamicItem(player, this, 23, electionItemSupplier)
		        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L * 60); //ici, je n'ai pas besoin d'attendre 1 sec pour update le menu

        Supplier<ItemBuilder> typeItemSupplier = () -> {

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Votre ville est en §5" + city.getType().getDisplayName().toLowerCase(Locale.ROOT)));

            if (city.getType().equals(CityType.WAR) && city.hasPermission(player.getUniqueId(), CityPermission.LAUNCH_WAR)) {
                lore.add(Component.empty());
                lore.add(Component.text("§7Vous pouvez lancer une guerre avec §c/war"));
            }

            if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
                lore.add(Component.empty());
                lore.add(Component.text("§cCooldown §7: " +
                        DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type"))));
            }

            if (hasPermissionChangeType) {
                lore.add(Component.empty());
                lore.add(Component.text("§e§lCLIQUEZ ICI POUR LE CHANGER"));
            }

            return new ItemBuilder(CityMenu.this, Material.NETHERITE_SWORD, meta -> {
                meta.itemName(Component.text("§5Le Statut de votre Ville"));
                meta.lore(lore);
            }).setOnClick(inventoryClickEvent -> {
                if (!(city.hasPermission(player.getUniqueId(), CityPermission.TYPE))) return;

                new CityTypeMenu(player).open();
            });
        };

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
            MenuUtils.runDynamicItem(player, this, 25, typeItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            inventory.put(25, typeItemSupplier.get());
        }

        List<Component> loreChestCity;

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CHEST)) {
            loreChestCity = List.of(
                    Component.text("§7Acceder au Coffre de votre Ville pour"),
                    Component.text("§7stocker des items en commun"),
                    Component.empty(),
                    Component.text("§cVous devez etre Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CHEST) + " pour débloquer ceci")
            );
        } else {
            if (hasPermissionChest) {
                if (city.getChestWatcher() != null) {
                    loreChestCity = List.of(
                            Component.text("§7Acceder au Coffre de votre Ville pour"),
                            Component.text("§7stocker des items en commun"),
                            Component.empty(),
                            Component.text("§7Ce coffre est déjà ouvert par §c" + Bukkit.getPlayer(city.getChestWatcher()).getName())
                    );
                } else {
                    loreChestCity = List.of(
                            Component.text("§7Acceder au Coffre de votre Ville pour"),
                            Component.text("§7stocker des items en commun"),
                            Component.empty(),
                            Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU COFFRE")
                    );
                }
            } else {
                loreChestCity = List.of(
                        Component.text("§7Vous n'avez pas le §cdroit de visionner le coffre !")
                );
            }
        }

            inventory.put(36, new ItemBuilder(this, Material.CHEST, itemMeta -> {
                itemMeta.itemName(Component.text("§aLe Coffre de la Ville"));
                itemMeta.lore(loreChestCity);
            }).setOnClick(inventoryClickEvent -> {
                City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

                if (!CityChestConditions.canCityChestOpen(cityCheck, player)) return;

            new CityChestMenu(player, city, 1).open();
        }));

        List<Component> loreBankCity;

        if (FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CITY_BANK)) {
            loreBankCity = List.of(
                    Component.text("§7Stocker votre argent et celle de votre ville"),
                    Component.text("§7Contribuer au développement de votre ville"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX COMPTES")
            );
        } else {
            loreBankCity = List.of(
                    Component.text("§7Stocker votre argent et celle de votre ville"),
                    Component.text("§7Contribuer au développement de votre ville"),
                    Component.empty(),
                    Component.text("§cVous devez être Niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CITY_BANK) + " pour débloquer ceci")
            );
        }

        inventory.put(40, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
            itemMeta.itemName(Component.text("§6La Banque"));
            itemMeta.lore(loreBankCity);
        }).setOnClick(inventoryClickEvent -> {
            City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
            if (cityCheck == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            if (!CityBankConditions.canOpenCityBank(cityCheck, player)) return;

            new CityBankMenu(player).open();
        }));


        if (!hasPermissionOwner) {
            inventory.put(44, new ItemBuilder(this, Material.OAK_DOOR, itemMeta -> {
                itemMeta.itemName(Component.text("§cPartir de la Ville"));
                itemMeta.lore(List.of(
                        Component.text("§7Vous allez §cquitter §7" + city.getName()),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR PARTIR")
                ));
            }).setOnClick(inventoryClickEvent -> {
                City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
                if (!CityLeaveCondition.canCityLeave(cityCheck, player)) return;

                ConfirmMenu menu = new ConfirmMenu(player,
                        () -> {
                            CityLeaveAction.startLeave(player);
                            player.closeInventory();
                        },
                        player::closeInventory,
                        List.of(Component.text("§7Voulez vous vraiment partir de " + city.getName() + " ?")),
                        List.of(Component.text("§7Rester dans la ville " + city.getName()))
                );
                menu.open();
            }));
        }

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
