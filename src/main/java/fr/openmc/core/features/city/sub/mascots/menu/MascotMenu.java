package fr.openmc.core.features.city.sub.mascots.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.location.ItemInteraction;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotsLevels;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.movingMascots;
import static fr.openmc.core.features.city.sub.mascots.MascotsManager.upgradeMascots;

public class MascotMenu extends Menu {

    private final Mascot mascots;
    private City city;

    public MascotMenu(Player owner, Mascot mascots) {
        super(owner);
        this.mascots = mascots;
        this.city = CityManager.getPlayerCity(owner.getUniqueId());
    }

    @Override
    public @NotNull String getName() {
        return "§cMascotte (niv. " + city.getMascot().getLevel() + ")";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();
        Player player = getOwner();

        Mascot mascot = city.getMascot();
        if (mascot == null) {
            MessagesManager.sendMessage(player, Component.text("§cUne erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            return map;
        }

        List<Component> loreSkinMascot = List.of(
                Component.text("§7Vous pouvez changer l'apparence de votre §cMascotte"),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR CHANGER DE SKIN")
        );

        map.put(11, new ItemBuilder(this, mascots.getMascotEgg(), itemMeta -> {
            itemMeta.displayName(Component.text("§7Le Skin de la §cMascotte"));
            itemMeta.lore(loreSkinMascot);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }).setOnClick(inventoryClickEvent -> {
            if (!city.hasPermission(player.getUniqueId(), CPermission.MASCOT_SKIN)) {
                MessagesManager.sendMessage(player, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                player.closeInventory();
                return;
            }
            new MascotsSkinMenu(player, mascots.getMascotEgg(), mascots).open();
        }));

        Supplier<ItemStack> moveMascotItemSupplier = () -> {
            List<Component> lorePosMascot;

            if (!DynamicCooldownManager.isReady(mascots.getMascotUUID().toString(), "mascots:move")) {
                lorePosMascot = List.of(
                        Component.text("§7Vous ne pouvez pas changer la position de votre §cMascotte"),
                        Component.text(""),
                        Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mascots.getMascotUUID().toString(), "mascots:move")))
                );
            } else {
                lorePosMascot = List.of(
                        Component.text("§7Vous pouvez changer la position de votre §cMascotte"),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR LA CHANGER DE POSITION")
                );
            }

            return new ItemBuilder(this, Material.CHEST, itemMeta -> {
                itemMeta.displayName(Component.text("§7Déplacer votre §cMascotte"));
                itemMeta.lore(lorePosMascot);
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }).setOnClick(inventoryClickEvent -> {
                if (!DynamicCooldownManager.isReady(mascots.getMascotUUID().toString(), "mascots:move")) {
                    return;
                }
                if (!city.hasPermission(getOwner().getUniqueId(), CPermission.MASCOT_MOVE)) {
                    MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    return;
                }

                if (!ItemUtils.hasAvailableSlot(getOwner())) {
                    MessagesManager.sendMessage(getOwner(), Component.text("Libérez de la place dans votre inventaire"), Prefix.CITY, MessageType.ERROR, false);
                    return;
                }

                city = CityManager.getPlayerCity(getOwner().getUniqueId());
                if (city == null) {
                    MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    getOwner().closeInventory();
                    return;
                }

                String city_uuid = city.getUUID();
                if (movingMascots.contains(city_uuid)) return;

                movingMascots.add(city_uuid);

                ItemStack mascotsMoveItem = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
                ItemMeta meta = mascotsMoveItem.getItemMeta();

                if (meta != null) {
                    List<Component> info = new ArrayList<>();
                    info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
                    info.add(Component.text("§cCe coffre n'est pas retirable"));
                    meta.displayName(Component.text("§7Déplacer votre §lMascotte"));
                    meta.lore(info);
                }
                mascotsMoveItem.setItemMeta(meta);

                ItemInteraction.runLocationInteraction(
                        player,
                        mascotsMoveItem,
                        "mascots:moveInteraction",
                        120,
                        "Temps Restant : %sec%s",
                        "§cDéplacement de la Mascotte annulée",
                        mascotMove -> {
                            if (mascotMove == null) return true;
                            if (!movingMascots.contains(city_uuid)) return false;

                            if (mascot == null) return false;

                            Entity mob = mascot.getEntity();
                            if (mob == null) return false;

                            Chunk chunk = mascotMove.getChunk();
                            int chunkX = chunk.getX();
                            int chunkZ = chunk.getZ();

                            if (!city.hasChunk(chunkX, chunkZ)) {
                                MessagesManager.sendMessage(player, Component.text("§cImpossible de déplacer la mascotte ici car ce chunk ne vous appartient pas ou est adjacent à une autre ville"), Prefix.CITY, MessageType.INFO, false);
                                return false;
                            }

                            mob.teleport(mascotMove);
                            movingMascots.remove(city_uuid);
                            mascot.setChunk(mascotMove.getChunk());

                            DynamicCooldownManager.use(mascot.getMascotUUID().toString(), "mascots:move", 5 * 3600 * 1000L);
                            return true;
                        },
                        null
                );
                player.closeInventory();
            });
        };
        if (!DynamicCooldownManager.isReady(mascots.getMascotUUID().toString(), "mascots:move")) {
            MenuUtils.runDynamicItem(player, this, 13, moveMascotItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
        } else {
            map.put(13, new ItemBuilder(this, moveMascotItemSupplier.get()));
        }

        List<Component> requiredAmount = new ArrayList<>();
        MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + mascot.getLevel());

        if (mascotsLevels.equals(MascotsLevels.level10)) {
            requiredAmount.add(Component.text("§7Niveau max atteint"));
        } else {
            requiredAmount.add(Component.text("§7Nécessite §d" + mascotsLevels.getUpgradeCost() + " d'Aywenites"));
        }

        map.put(15, new ItemBuilder(this, Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, itemMeta -> {
            itemMeta.displayName(Component.text("§7Améliorer votre §cMascotte"));
            itemMeta.lore(requiredAmount);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        }).setOnClick(inventoryClickEvent -> {

            if (mascotsLevels.equals(MascotsLevels.level10)) return;

            if (city == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                player.closeInventory();
                return;
            }
            if (city.hasPermission(player.getUniqueId(), CPermission.MASCOT_UPGRADE)) {
                String city_uuid = city.getUUID();
                int aywenite = mascotsLevels.getUpgradeCost();
                Material matAywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest().getType();
                if (ItemUtils.hasEnoughItems(player, matAywenite, aywenite)) {
                    ItemUtils.removeItemsFromInventory(player, matAywenite, aywenite);
                    upgradeMascots(city_uuid);
                    MessagesManager.sendMessage(player, Component.text("Vous avez amélioré votre mascotte au §cNiveau " + mascot.getLevel()), Prefix.CITY, MessageType.ERROR, false);
                    player.closeInventory();
                    return;
                }
                MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez d'§dAywenite"), Prefix.CITY, MessageType.ERROR, false);

            } else {
                MessagesManager.sendMessage(player, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            }
            player.closeInventory();
        }));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
            itemMeta.lore(List.of(Component.text("§7Retourner au menu des villes")));
        }).setOnClick(event -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
        }));

        if (mascot.isImmunity()) {
            Supplier<ItemStack> immunityItemSupplier = () -> {
                List<Component> lore = List.of(
                        Component.text("§7Vous avez une §bimmunité §7sur votre §cMascotte"),
                        Component.text("§cTemps restant §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUUID(), "city:immunity")))
                );

                return new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
                    itemMeta.displayName(Component.text("§7Votre §cMascotte §7est §bimmunisée§7!"));
                    itemMeta.lore(lore);
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
