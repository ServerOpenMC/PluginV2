package fr.openmc.core.features.city.menu.mayor;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.ChatInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.mayor.CityLaw;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.mayor.perks.event.IdyllicRain;
import fr.openmc.core.features.city.mayor.perks.event.ImpotCollection;
import fr.openmc.core.features.city.mayor.perks.event.MilitaryDissuasion;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class MayorLawMenu extends Menu {

    private static final long COOLDOWN_TIME_ANNOUNCE = 3 * 60 * 60 * 1000L; // 3 heures en ms
    public static final long COOLDOWN_TIME_WARP = 60 * 60 * 1000L; // 1 heure en ms
    private static final long COOLDOWN_TIME_PVP = 4 * 60 * 60 * 1000L; // 4 heures en ms

    public MayorLawMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Lois";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            Mayor mayor = city.getMayor();

            CityLaw law = city.getLaw();

            Supplier<ItemStack> pvpItemSupplier = () -> {
                String nameLawPVP = law.isPvp() ? "§cDésactiver §7le PVP" : "§4Activer §7le PVP";
                List<Component> loreLawPVP = new ArrayList<>(List.of(
                        Component.text("§7Cette §1loi "+(law.isPvp()?"§4active":"§cdésactive")+" §7le PVP dans toute la §dVille"),
                                Component.text("§7entre les membres !")
                        ));

                if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-pvp")) {
                    loreLawPVP.addAll(
                            List.of(
                                    Component.text(""),
                                    Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mayor.getUUID().toString(), "mayor:law-pvp")))
                            )
                    );
                } else {
                    loreLawPVP.addAll(List.of(
                                    Component.text(""),
                                    Component.text("§e§lCLIQUEZ ICI POUR " + (law.isPvp() ? "ACTIVER" : "DESACTIVER") + " LE PVP")
                            )
                    );
                }

                return new ItemBuilder(this, Material.IRON_SWORD, itemMeta -> {
                    itemMeta.itemName(Component.text(nameLawPVP));
                    itemMeta.lore(loreLawPVP);
                }).setOnClick(inventoryClickEvent -> {
                    if (DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-pvp")) {
                        DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-pvp", COOLDOWN_TIME_PVP);

                        law.setPvp(!law.isPvp());
                        String messageLawPVP = law.isPvp() ? "§7Vous avez §cdésactivé §7le PVP dans votre ville" : "§7Vous avez §4activé §7le PVP dans votre ville";
                        MessagesManager.sendMessage(player, Component.text(messageLawPVP), Prefix.MAYOR, MessageType.SUCCESS, false);

                        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                            new MayorLawMenu(player).open();
                        }, 2);
                    }
                });
            };

            if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-pvp")) {
                MenuUtils.runDynamicItem(player, this, 10, pvpItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                inventory.put(10, pvpItemSupplier.get());
            }

            Supplier<ItemStack> warpItemSupplier = () -> {
                Location warpLoc = law.getWarp();

                List<Component> loreLawWarp;

                if (warpLoc == null) {
                    loreLawWarp = new ArrayList<>(List.of(
                            Component.text("§7Cette §1loi §7n'est pas effective!"),
                            Component.text("§7Vous devez choisir un endroit où les membres pourront"),
                            Component.text("§7arriver")
                    ));
                } else {
                    loreLawWarp = new ArrayList<>(List.of(
                            Component.text("§7Les membres peuvent se téléporter à votre §9warp§7!"),
                            Component.text("§7Voici la position du §9warp §7: "),
                            Component.text("§8- §7x=§6" + warpLoc.getX()),
                            Component.text("§8- §7y=§6" + warpLoc.getY()),
                            Component.text("§8- §7z=§6" + warpLoc.getZ())
                    ));
                }

                if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-move-warp")) {
                    loreLawWarp.addAll(
                            List.of(
                                    Component.text(""),
                                    Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mayor.getUUID().toString(), "mayor:law-move-warp")))
                            )
                    );
                } else {
                    loreLawWarp.addAll(
                            List.of(
                                    Component.text(""),
                                    Component.text("§e§lCLIQUEZ ICI POUR CHOISIR UN ENDROIT")
                            )
                    );
                }


                return new ItemBuilder(this, Material.ENDER_PEARL, itemMeta -> {
                    itemMeta.itemName(Component.text("§7Changer son §9warp"));
                    itemMeta.lore(loreLawWarp);
                }).setOnClick(inventoryClickEvent -> {
                    CityCommands.setWarp(player);
                });
            };

            if (law.getWarp() == null) {
                inventory.put(12, warpItemSupplier.get());
            } else {
                MenuUtils.runDynamicItem(player, this, 12, warpItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            }

            Supplier<ItemStack> announceItemSupplier = () -> {
                List<Component> loreLawAnnounce = new ArrayList<>(List.of(
                        Component.text("§7Cette §1loi §7permet d'émettre un message dans toute la ville!")
                ));

                if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-announce")) {
                    loreLawAnnounce.addAll(
                            List.of(
                                    Component.text(""),
                                    Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mayor.getUUID().toString(), "mayor:law-announce")))
                            )
                    );
                } else {
                    loreLawAnnounce.addAll(
                            List.of(
                                    Component.text(""),
                                    Component.text("§e§lCLIQUEZ ICI POUR ECRIRE LE MESSAGE")
                            )
                    );
                }

                return new ItemBuilder(this, Material.BELL, itemMeta -> {
                    itemMeta.itemName(Component.text("§7Faire une annonce"));
                    itemMeta.lore(loreLawAnnounce);
                }).setOnClick(inventoryClickEvent -> {
                    if (DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-announce")) {

                        ChatInput.sendInput(
                                player,
                                "§eVous pouvez entrer votre message que vous voulez diffuser dans toute la ville ! Tapez cancel pour annuler l'action",
                                input -> {
                                    for (UUID uuidMember : city.getMembers()) {
                                        if (uuidMember == player.getUniqueId()) continue;

                                        Player playerMember = Bukkit.getPlayer(uuidMember);
                                        if (playerMember == null) continue;

                                        if (playerMember.isOnline()) {
                                            MessagesManager.sendMessage(playerMember, Component.text("§8-- §6Annonce du Maire §8--"), Prefix.MAYOR, MessageType.INFO, false);
                                            MessagesManager.sendMessage(playerMember, Component.text(input), Prefix.MAYOR, MessageType.INFO, false);
                                        }
                                    }

                                    MessagesManager.sendMessage(player, Component.text("Vous avez bien envoyé le message a tous les membres de la villes"), Prefix.MAYOR, MessageType.SUCCESS, false);

                                }
                        );
                        DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-announce", COOLDOWN_TIME_ANNOUNCE);
                    }

                });
            };

            MenuUtils.runDynamicItem(player, this, 14, announceItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

            Perks perkEvent = PerkManager.getPerkEvent(mayor);
            if (PerkManager.getPerkEvent(mayor) != null) {
                Supplier<ItemStack> perkEventItemSupplier = () -> {
                    ItemStack iaPerkEvent = perkEvent.getItemStack();
                    String namePerkEvent = perkEvent.getName();
                    List<Component> lorePerkEvent = new ArrayList<>(perkEvent.getLore());
                    if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-perk-event")) {
                        lorePerkEvent.addAll(
                                List.of(
                                        Component.text(""),
                                        Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mayor.getUUID().toString(), "mayor:law-perk-event")))
                                )
                        );
                    } else {
                        lorePerkEvent.addAll(
                                List.of(
                                        Component.text(""),
                                        Component.text("§e§lCLIQUEZ ICI POUR UTILISER LA REFORME")
                                )
                        );
                    }
                    return new ItemBuilder(this, iaPerkEvent, itemMeta -> {
                        itemMeta.itemName(Component.text(namePerkEvent));
                        itemMeta.lore(lorePerkEvent);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    }).setOnClick(inventoryClickEvent -> {
                        if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:law-perk-event")) {
                            MessagesManager.sendMessage(player, Component.text("Vous devez attendre avant de pouvoir utiliser cette §3Réforme"), Prefix.MAYOR, MessageType.ERROR, false);
                            return;
                        }

                        // Prélévement d'impot (id : 2) - Perk Event
                        if (PerkManager.hasPerk(city.getMayor(), Perks.IMPOT.getId())) {
                            for (UUID uuid : city.getMembers()) {
                                if (uuid == city.getMayor().getUUID()) continue;

                                Player member = Bukkit.getPlayer(uuid);

                                if (member == null || !member.isOnline()) continue;

                                ImpotCollection.spawnZombies(member, city);
                                MessagesManager.sendMessage(member, Component.text("Le §6Maire §fa déclenché le §ePrélévement d'Impot §f!"), Prefix.MAYOR, MessageType.INFO, false);

                            }
                            DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                            return;
                        } else if (PerkManager.hasPerk(city.getMayor(), Perks.AGRICULTURAL_ESSOR.getId())) {
                            // Essor agricole (id : 11) - Perk Event
                            for (UUID uuid : city.getMembers()) {
                                Player member = Bukkit.getPlayer(uuid);

                                if (member == null || !member.isOnline()) continue;

                                MessagesManager.sendMessage(member, Component.text("Le §6Maire §fa déclenché l'§eEssor Agricole §f!"), Prefix.MAYOR, MessageType.INFO, false);
                            }

                            DynamicCooldownManager.use(city.getUUID(), "city:agricultural_essor", 30 * 60 * 1000L); // 30 minutes
                            DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                        } else if (PerkManager.hasPerk(city.getMayor(), Perks.MINERAL_RUSH.getId())) {
                            // Ruée Miniere (id : 12) - Perk Event
                            for (UUID uuid : city.getMembers()) {
                                Player member = Bukkit.getPlayer(uuid);

                                if (member == null || !member.isOnline()) continue;

                                MessagesManager.sendMessage(member, Component.text("Le §6Maire §fa déclenché la §eRuée Minière §f!"), Prefix.MAYOR, MessageType.INFO, false);
                            }

                            DynamicCooldownManager.use(city.getUUID(), "city:mineral_rush", 5 * 60 * 1000L); // 5 minutes
                            DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                        } else if (PerkManager.hasPerk(city.getMayor(), Perks.MILITARY_DISSUASION.getId())) {
                            // Dissuasion Militaire (id : 13) - Perk Event
                            for (UUID uuid : city.getMembers()) {
                                Player member = Bukkit.getPlayer(uuid);

                                if (member == null || !member.isOnline()) continue;

                                MessagesManager.sendMessage(member, Component.text("Le §6Maire §fa déclenché la §eDissuasion Militaire §f!"), Prefix.MAYOR, MessageType.INFO, false);
                            }

                            MilitaryDissuasion.spawnIronMan(city, 10);
                            DynamicCooldownManager.use(city.getUUID(), "city:military_dissuasion", 10 * 60 * 1000L); // 10 minutes
                            DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (DynamicCooldownManager.isReady(city.getUUID(), "city:military_dissuasion")) {
                                        MilitaryDissuasion.clearCityGolems(city);
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(OMCPlugin.getInstance(), 20L, 100L);
                        } else if (PerkManager.hasPerk(city.getMayor(), Perks.IDYLLIC_RAIN.getId())) {
                            // Pluie idyllique (id : 14) - Perk Event
                            for (UUID uuid : city.getMembers()) {
                                Player member = Bukkit.getPlayer(uuid);

                                if (member == null || !member.isOnline()) continue;

                                MessagesManager.sendMessage(member, Component.text("Le §6Maire §fa déclenché la §ePluie idyllique §f!"), Prefix.MAYOR, MessageType.INFO, false);
                            }

                            // spawn d'un total de 100 aywenite progressivement sur une minute
                            IdyllicRain.spawnAywenite(city, 100);

                            DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                        }
                    });
                };

                MenuUtils.runDynamicItem(player, this, 16, perkEventItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            }

            inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.itemName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
                        Component.text("§7Vous allez retourner au Menu du Mandat du Maire"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                new MayorMandateMenu(player).open();
            }));

            List<Component> loreInfo = Arrays.asList(
                    Component.text("§7Apprenez en plus sur les Maires !"),
                    Component.text("§7Le déroulement..., Les éléctions, ..."),
                    Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
            );

            inventory.put(26, new ItemBuilder(this, Material.BOOK, itemMeta -> {
                itemMeta.displayName(Component.text("§r§aPlus d'info !"));
                itemMeta.lore(loreInfo);
            }).setNextMenu(new MoreInfoMenu(getOwner())));

            return inventory;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return null;
    }
}
