package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.ChatInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.actions.MayorSetWarpAction;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.mayor.perks.event.IdyllicRain;
import fr.openmc.core.features.city.sub.mayor.perks.event.ImpotCollection;
import fr.openmc.core.features.city.sub.mayor.perks.event.MilitaryDissuasion;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.mayor.menu.law.name");
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
        Mayor mayor = city.getMayor();

        CityLaw law = city.getLaw();

        Supplier<ItemBuilder> pvpItemSupplier = () -> {
            Component nameLawPVP = law.isPvp()
                    ? TranslationManager.translation("feature.city.mayor.menu.law.pvp.name.disable")
                    : TranslationManager.translation("feature.city.mayor.menu.law.pvp.name.enable");
            List<Component> loreLawPVP = new ArrayList<>(List.of(
                    law.isPvp()
                            ? TranslationManager.translation("feature.city.mayor.menu.law.pvp.lore.active")
                            : TranslationManager.translation("feature.city.mayor.menu.law.pvp.lore.inactive"),
                    TranslationManager.translation("feature.city.mayor.menu.law.pvp.lore.members")
            ));

            if (!DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-pvp")) {
                loreLawPVP.addAll(
                        List.of(
                                Component.empty(),
                                TranslationManager.translation(
                                        "feature.city.mayor.menu.law.cooldown",
                                        Component.text(DateUtils.convertMillisToTime(
                                                DynamicCooldownManager.getRemaining(mayor.getMayorUUID(), "mayor:law-pvp")))
                                                .color(NamedTextColor.RED)
                                )
                        )
                );
            } else {
                loreLawPVP.addAll(List.of(
                        Component.empty(),
                        TranslationManager.translation(law.isPvp()
                                ? "feature.city.mayor.menu.law.pvp.click.enable"
                                : "feature.city.mayor.menu.law.pvp.click.disable")
                        )
                );
            }

            return new ItemBuilder(this, Material.IRON_SWORD, itemMeta -> {
                itemMeta.itemName(nameLawPVP);
                itemMeta.lore(loreLawPVP);
            }).setOnClick(inventoryClickEvent -> {
                if (DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-pvp")) {
                    DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-pvp", COOLDOWN_TIME_PVP);

                    law.setPvp(!law.isPvp());
                    boolean pvpEnabled = law.isPvp();
                    Component messageLawPVP = pvpEnabled
                            ? TranslationManager.translation("feature.city.mayor.menu.law.pvp.message.enable")
                            : TranslationManager.translation("feature.city.mayor.menu.law.pvp.message.disable");
                    MessagesManager.sendMessage(player, messageLawPVP, Prefix.MAYOR, MessageType.SUCCESS, false);

                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                        new MayorLawMenu(player).open();
                    }, 2);
                }
            });
        };

        if (!DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-pvp")) {
            MenuUtils.runDynamicItem(player, this, 19, pvpItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            inventory.put(19, pvpItemSupplier.get());
        }

        Supplier<ItemBuilder> warpItemSupplier = () -> {
            Location warpLoc = law.getWarp();

            List<Component> loreLawWarp;

            if (warpLoc == null) {
                loreLawWarp = new ArrayList<>(TranslationManager.translationLore("feature.city.mayor.menu.law.warp.lore.unset"));
            } else {
                loreLawWarp = new ArrayList<>(TranslationManager.translationLore("feature.city.mayor.menu.law.warp.lore.set"));
                loreLawWarp.add(TranslationManager.translation(
                        "feature.city.mayor.menu.law.warp.lore.x",
                        Component.text(warpLoc.getX()).color(NamedTextColor.GOLD)
                ));
                loreLawWarp.add(TranslationManager.translation(
                        "feature.city.mayor.menu.law.warp.lore.y",
                        Component.text(warpLoc.getY()).color(NamedTextColor.GOLD)
                ));
                loreLawWarp.add(TranslationManager.translation(
                        "feature.city.mayor.menu.law.warp.lore.z",
                        Component.text(warpLoc.getZ()).color(NamedTextColor.GOLD)
                ));
            }

            if (!DynamicCooldownManager.isReady(city.getUniqueId(), "mayor:law-move-warp")) {
                loreLawWarp.addAll(
                        List.of(
                                Component.empty(),
                                TranslationManager.translation(
                                        "feature.city.mayor.menu.law.cooldown",
                                        Component.text(DateUtils.convertMillisToTime(
                                                DynamicCooldownManager.getRemaining(city.getUniqueId(), "mayor:law-move-warp")))
                                                .color(NamedTextColor.RED)
                                )
                        )
                );
            } else {
                loreLawWarp.addAll(
                        List.of(
                                Component.empty(),
                                TranslationManager.translation("feature.city.mayor.menu.law.warp.click")
                        )
                );
            }


            return new ItemBuilder(this, Material.ENDER_PEARL, itemMeta -> {
                itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.law.warp.name"));
                itemMeta.lore(loreLawWarp);
            }).setOnClick(inventoryClickEvent -> {
                MayorSetWarpAction.setWarp(player);
            });
        };

        if (law.getWarp() == null) {
            inventory.put(21, warpItemSupplier.get());
        } else {
            MenuUtils.runDynamicItem(player, this, 21, warpItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        }

        Supplier<ItemBuilder> announceItemSupplier = () -> {
            List<Component> loreLawAnnounce = new ArrayList<>(List.of(
                    TranslationManager.translation("feature.city.mayor.menu.law.announce.lore")
            ));

            if (!DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-announce")) {
                loreLawAnnounce.addAll(
                        List.of(
                                Component.empty(),
                                TranslationManager.translation(
                                        "feature.city.mayor.menu.law.cooldown",
                                        Component.text(DateUtils.convertMillisToTime(
                                                DynamicCooldownManager.getRemaining(mayor.getMayorUUID(), "mayor:law-announce")))
                                                .color(NamedTextColor.RED)
                                )
                        )
                );
            } else {
                loreLawAnnounce.addAll(
                        List.of(
                                Component.empty(),
                                TranslationManager.translation("feature.city.mayor.menu.law.announce.click")
                        )
                );
            }

            return new ItemBuilder(this, Material.BELL, itemMeta -> {
                itemMeta.itemName(TranslationManager.translation("feature.city.mayor.menu.law.announce.name"));
                itemMeta.lore(loreLawAnnounce);
            }).setOnClick(inventoryClickEvent -> {
                if (DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-announce")) {

                    ChatInput.sendInput(
                            player,
                            TranslationManager.translation("feature.city.mayor.menu.law.announce.prompt"),
                            input -> {
                                if (input == null)
                                    return;

                                for (UUID uuidMember : city.getMembers()) {
                                    if (uuidMember == player.getUniqueId()) continue;

                                    Player playerMember = Bukkit.getPlayer(uuidMember);
                                    if (playerMember == null) continue;

                                    if (playerMember.isOnline()) {
                                        MessagesManager.sendMessage(playerMember, TranslationManager.translation("feature.city.mayor.menu.law.announce.header"), Prefix.MAYOR, MessageType.INFO, false);
                                        MessagesManager.sendMessage(playerMember, Component.text(input), Prefix.MAYOR, MessageType.INFO, false);
                                    }
                                }

                                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.menu.law.announce.success"), Prefix.MAYOR, MessageType.SUCCESS, false);

                            }
                    );
                    DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-announce", COOLDOWN_TIME_ANNOUNCE);
                }

            });
        };

        MenuUtils.runDynamicItem(player, this, 23, announceItemSupplier)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

        Perks perkEvent = PerkManager.getPerkEvent(mayor);
        if (PerkManager.getPerkEvent(mayor) != null) {
            Supplier<ItemBuilder> perkEventItemSupplier = () -> {
                ItemStack iaPerkEvent = perkEvent.getItemStack();
                Component namePerkEvent = TranslationManager.translation(perkEvent.getNameKey());
                List<Component> lorePerkEvent = new ArrayList<>(TranslationManager.translationLore(perkEvent.getLoreKey()));
                if (!DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-perk-event")) {
                    lorePerkEvent.addAll(
                            List.of(
                                    Component.empty(),
                                    TranslationManager.translation(
                                            "feature.city.mayor.menu.law.cooldown",
                                            Component.text(DateUtils.convertMillisToTime(
                                                    DynamicCooldownManager.getRemaining(mayor.getMayorUUID(), "mayor:law-perk-event")))
                                                    .color(NamedTextColor.RED)
                                    )
                            )
                    );
                } else {
                    lorePerkEvent.addAll(
                            List.of(
                                    Component.empty(),
                                    TranslationManager.translation("feature.city.mayor.menu.law.perk_event.click")
                            )
                    );
                }
                return new ItemBuilder(this, iaPerkEvent, itemMeta -> {
                    itemMeta.itemName(namePerkEvent);
                    itemMeta.lore(lorePerkEvent);
                })
                        .hide(perkEvent.getToHide())
                        .setOnClick(inventoryClickEvent -> {
                    if (!DynamicCooldownManager.isReady(mayor.getMayorUUID(), "mayor:law-perk-event")) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.wait"), Prefix.MAYOR, MessageType.ERROR, false);
                        return;
                    }

                    // Prélévement d'impot (id : 2) - Perk Event
                    if (PerkManager.hasPerk(city.getMayor(), Perks.IMPOT.getId())) {
                        for (UUID uuid : city.getMembers()) {
                            if (uuid == city.getMayor().getMayorUUID()) continue;

                            Player member = Bukkit.getPlayer(uuid);

                            if (member == null || !member.isOnline()) continue;

                            if (DreamUtils.isDreamWorld(member.getWorld())) continue;

                            ImpotCollection.spawnZombies(member, city);
                            MessagesManager.sendMessage(member, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.impot.trigger"), Prefix.MAYOR, MessageType.INFO, false);

                        }
                        DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                    } else if (PerkManager.hasPerk(city.getMayor(), Perks.AGRICULTURAL_ESSOR.getId())) {
                        // Essor agricole (id : 11) - Perk Event
                        for (UUID uuid : city.getMembers()) {
                            Player member = Bukkit.getPlayer(uuid);

                            if (member == null || !member.isOnline()) continue;
	                        
	                        MessagesManager.sendMessage(member, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.agricultural.trigger"), Prefix.MAYOR, MessageType.INFO, false);
                        }

                        DynamicCooldownManager.use(city.getUniqueId(), "city:agricultural_essor", 30 * 60 * 1000L); // 30 minutes
                        DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                    } else if (PerkManager.hasPerk(city.getMayor(), Perks.MINERAL_RUSH.getId())) {
                        // Ruée Miniere (id : 12) - Perk Event
                        for (UUID uuid : city.getMembers()) {
                            Player member = Bukkit.getPlayer(uuid);

                            if (member == null || !member.isOnline()) continue;
	                        
	                        MessagesManager.sendMessage(member, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.mineral.trigger"), Prefix.MAYOR, MessageType.INFO, false);
                        }

                        DynamicCooldownManager.use(city.getUniqueId(), "city:mineral_rush", 5 * 60 * 1000L); // 5 minutes
                        DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                    } else if (PerkManager.hasPerk(city.getMayor(), Perks.MILITARY_DISSUASION.getId())) {
                        // Dissuasion Militaire (id: 13) - Perk Event
                        for (UUID uuid : city.getMembers()) {
                            Player member = Bukkit.getPlayer(uuid);

                            if (member == null || !member.isOnline()) continue;
	                        
	                        MessagesManager.sendMessage(member, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.military.trigger"), Prefix.MAYOR, MessageType.INFO, false);
                        }

                        MilitaryDissuasion.startEvent(city, 10);
                        DynamicCooldownManager.use(city.getUniqueId(), "city:military_dissuasion", 10 * 60 * 1000L); // 10 minutes
                        DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (DynamicCooldownManager.isReady(city.getUniqueId(), "city:military_dissuasion")) {
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
	                        
	                        MessagesManager.sendMessage(member, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.idyllic.trigger"), Prefix.MAYOR, MessageType.INFO, false);
                        }

                        // spawn d'un total de 100 aywenite progressivement sur une minute
                        IdyllicRain.spawnAywenite(city, 100);

                        DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                    } else if (PerkManager.hasPerk(city.getMayor(), Perks.CHAOS_DREAM.getId())) {
                        // Reve chaotique (id: 18) - Perk Event
                        for (UUID uuid : city.getMembers()) {
                            Player member = Bukkit.getPlayer(uuid);

                            if (member == null || !member.isOnline()) continue;

                            MessagesManager.sendMessage(member, TranslationManager.translation("feature.city.mayor.menu.law.perk_event.dream.trigger"), Prefix.MAYOR, MessageType.INFO, false);

                            DBDreamPlayer dbDreamPlayer = DreamManager.getCacheDreamPlayer(player);

                            if (dbDreamPlayer == null || (dbDreamPlayer.getDreamX() == null || dbDreamPlayer.getDreamY() == null || dbDreamPlayer.getDreamZ() == null)) {
                                DreamManager.tpPlayerDream(player);
                            } else {
                                DreamManager.tpPlayerToLastDreamLocation(player);
                            }
                        }
                        DynamicCooldownManager.use(mayor.getMayorUUID(), "mayor:law-perk-event", PerkManager.getPerkEvent(mayor).getCooldown());
                    }
                });
            };

            MenuUtils.runDynamicItem(player, this, 25, perkEventItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        }

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
