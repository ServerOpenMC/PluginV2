package fr.openmc.core.features.city.sub.mascots.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotsLevels;
import fr.openmc.core.features.city.sub.milestone.rewards.MascotsLevelsRewards;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.movingMascots;
import static fr.openmc.core.features.city.sub.mascots.MascotsManager.upgradeMascots;

@SuppressWarnings("UnstableApiUsage")
public class MascotMenu extends Menu {

    private static final int AYWENITE_REDUCE = 15;
    private static final long COOLDOWN_REDUCE = 3600000L;

    private final Mascot mascot;
    private City city;

    public MascotMenu(Player owner, Mascot mascot) {
        super(owner);
        this.mascot = mascot;
        this.city = CityManager.getPlayerCity(owner.getUniqueId());
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.city.mascots.menu.main.name",
                Component.text(city.getMascot().getLevel())
        ).color(NamedTextColor.GRAY);
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        Player player = getOwner();

        Mascot mascot = city.getMascot();
        if (mascot == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("api.menulib.an_error_occurred"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            return map;
        }

        List<Component> loreSkinMascot = TranslationManager.translationLore("feature.city.mascots.menu.main.skin.lore");

        map.put(11, new ItemBuilder(this, this.mascot.getMascotEgg(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mascots.menu.main.skin.title"));
            itemMeta.lore(loreSkinMascot);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
        })
                .hide(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS)
                .setOnClick(inventoryClickEvent -> {
                    if (!city.hasPermission(player.getUniqueId(), CityPermission.MASCOT_CHANGE_SKIN)) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.CITY, MessageType.ERROR, false);
                        player.closeInventory();
                        return;
                    }
                    new MascotsSkinMenu(player, this.mascot.getMascotEgg(), this.mascot).open();
                }));

        Supplier<ItemBuilder> moveMascotItemSupplier = () -> {
            List<Component> lorePosMascot;

            if (!DynamicCooldownManager.isReady(this.mascot.getMascotUUID(), "mascots:move")) {
                lorePosMascot = TranslationManager.translationLore(
                        "feature.city.mascots.menu.main.move.lore.cooldown",
                        Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(this.mascot.getMascotUUID(), "mascots:move"))).color(NamedTextColor.GRAY)
                );
            } else {
                lorePosMascot = TranslationManager.translationLore("feature.city.mascots.menu.main.move.lore.ready");
            }

            return new ItemBuilder(this, Material.CHEST, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.mascots.menu.main.move.title"));
                itemMeta.lore(lorePosMascot);
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            })
                    .hide(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS)
                    .setOnClick(inventoryClickEvent -> {
                        if (!DynamicCooldownManager.isReady(this.mascot.getMascotUUID(), "mascots:move")) {
                            return;
                        }
                        if (!city.hasPermission(getOwner().getUniqueId(), CityPermission.MASCOT_MOVE)) {
                            MessagesManager.sendMessage(getOwner(), TranslationManager.translation("messages.global.cannot_do_this"), Prefix.CITY, MessageType.ERROR, false);
                            return;
                        }

                        if (!ItemUtils.hasAvailableSlot(getOwner())) {
                            MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.city.mascots.menu.main.move.error.inventory_space"), Prefix.CITY, MessageType.ERROR, false);
                            return;
                        }

                        city = CityManager.getPlayerCity(getOwner().getUniqueId());
                        if (city == null) {
                            MessagesManager.sendMessage(getOwner(), TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
                            getOwner().closeInventory();
                            return;
                        }

                        UUID cityUUID = city.getUniqueId();
                        if (movingMascots.contains(cityUUID)) return;

                        movingMascots.add(cityUUID);

                        ItemStack mascotsMoveItem = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
                        ItemMeta meta = mascotsMoveItem.getItemMeta();

                        if (meta != null) {
                            List<Component> info = TranslationManager.translationLore("feature.city.mascots.menu.main.move.item.lore");
                            meta.displayName(TranslationManager.translation("feature.city.mascots.menu.main.move.item.title"));
                            meta.lore(info);
                        }
                        mascotsMoveItem.setItemMeta(meta);

                        ItemInteraction.runLocationInteraction(
                                player,
                                mascotsMoveItem,
                                "mascots:moveInteraction",
                                120,
                                TranslationManager.translation("feature.city.mascots.menu.main.move.interaction.remaining"),
                                TranslationManager.translation("feature.city.mascots.menu.main.move.interaction.cancelled"),
                                mascotMove -> {
                                    if (mascotMove == null) return true;
                                    if (!movingMascots.contains(cityUUID)) return false;

                                    if (mascot == null) return false;

                                    Entity mob = mascot.getEntity();
                                    if (mob == null) return false;

                                    Chunk chunk = mascotMove.getChunk();
                                    int chunkX = chunk.getX();
                                    int chunkZ = chunk.getZ();

                                    if (!city.hasChunk(chunkX, chunkZ)) {
                                        MessagesManager.sendMessage(player,
                                                TranslationManager.translation("feature.city.mascots.menu.main.move.error.invalid_chunk"),
                                                Prefix.CITY, MessageType.INFO, false);
                                        return false;
                                    }

                                    mob.teleport(mascotMove);
                                    movingMascots.remove(cityUUID);
                                    mascot.setChunk(mascotMove.getChunk());

                                    DynamicCooldownManager.use(mascot.getMascotUUID(), "mascots:move", 5 * 3600 * 1000L);
                                    return true;
                                },
                                null
                        );
                        player.closeInventory();
                    });
        };
        if (!DynamicCooldownManager.isReady(this.mascot.getMascotUUID(), "mascots:move")) {
            MenuUtils.runDynamicItem(player, this, 13, moveMascotItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            map.put(13, new ItemBuilder(this, moveMascotItemSupplier.get()));
        }

        List<Component> requiredAmount = new ArrayList<>();
        MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + mascot.getLevel());

        int maxMascotLevel = MascotsLevelsRewards.getMascotsLevelLimit(city.getLevel());

        int currentMascotLevel = mascot.getLevel();

        if (mascotsLevels.equals(MascotsLevels.level10)) {
            requiredAmount.add(TranslationManager.translation("feature.city.mascots.menu.main.upgrade.max_level"));
        } else if (currentMascotLevel >= maxMascotLevel) {
            requiredAmount.add(TranslationManager.translation(
                    "feature.city.mascots.menu.main.upgrade.level_required",
                    Component.text(maxMascotLevel + 1).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED)
            ));
        } else {
            requiredAmount.add(TranslationManager.translation(
                    "feature.city.mascots.menu.main.upgrade.cost",
                    Component.text(mascotsLevels.getUpgradeCost()).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
            ));
        }

        ItemBuilder itemBuilder = new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.mascots.menu.main.upgrade.title"));
            itemMeta.lore(requiredAmount);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
        }).hide(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS);

        itemBuilder.setData(DataComponentTypes.ITEM_MODEL, Key.key("minecraft:netherite_upgrade_smithing_template"));
        map.put(15, itemBuilder
                .setOnClick(inventoryClickEvent -> {
                    if (mascotsLevels.equals(MascotsLevels.level10)) return;
                    if (currentMascotLevel >= maxMascotLevel) return;

                    if (city == null) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
                        player.closeInventory();
                        return;
                    }
                    if (city.hasPermission(player.getUniqueId(), CityPermission.MASCOT_UPGRADE)) {
                        UUID cityUUID = city.getUniqueId();
                        int aywenite = mascotsLevels.getUpgradeCost();
                        if (ItemUtils.takeAywenite(player, aywenite)) {
                            upgradeMascots(cityUUID);
                            MessagesManager.sendMessage(player,
                                    TranslationManager.translation(
                                            "feature.city.mascots.menu.main.upgrade.success",
                                            Component.text(mascot.getLevel()).color(NamedTextColor.RED)
                                    ),
                                    Prefix.CITY, MessageType.ERROR, false);
                            player.closeInventory();
                            return;
                        }
                        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mascots.menu.main.upgrade.not_enough_aywenite"), Prefix.CITY, MessageType.ERROR, false);

                    } else {
                        MessagesManager.sendMessage(player, TranslationManager.translation("messages.global.cannot_do_this"), Prefix.CITY, MessageType.ERROR, false);
                    }
                    player.closeInventory();
                }));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(TranslationManager.translationLore("messages.menus.back_lore"));
        }, true));

        if (city.isImmune()) {
            Supplier<ItemBuilder> immunityItemSupplier = () -> {
                List<Component> lore = TranslationManager.translationLore(
                        "feature.city.mascots.menu.main.immunity.lore",
                        Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:immunity"))).color(NamedTextColor.GRAY),
                        Component.text(AYWENITE_REDUCE).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
                );

                return new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
                    itemMeta.displayName(TranslationManager.translation("feature.city.mascots.menu.main.immunity.title"));
                    itemMeta.lore(lore);
                }).setOnClick(inventoryClickEvent -> {
                    if (city == null) {
                        MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
                        player.closeInventory();
                        return;
                    }

                    if (!ItemUtils.takeAywenite(player, AYWENITE_REDUCE)) return;
                    DynamicCooldownManager.reduceCooldown(player, city.getUniqueId(), "city:immunity", COOLDOWN_REDUCE);

                    MessagesManager.sendMessage(player,
                            TranslationManager.translation(
                                    "feature.city.mascots.menu.main.immunity.reduce.success",
                                    Component.text(AYWENITE_REDUCE).color(NamedTextColor.LIGHT_PURPLE)
                            ),
                            Prefix.CITY, MessageType.SUCCESS, false);
                });
            };

            MenuUtils.runDynamicItem(player, this, 26, immunityItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        }

        return map;
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
