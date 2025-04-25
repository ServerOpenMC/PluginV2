package fr.openmc.core.features.city.menu.mascots;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.mascots.Mascot;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mascots.MascotsLevels;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.interactions.ItemInteraction;
import fr.openmc.core.utils.menu.MenuUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.mascots.MascotsListener.*;
import static fr.openmc.core.features.city.mascots.MascotsManager.*;
import static fr.openmc.core.utils.chronometer.Chronometer.startChronometer;

public class MascotMenu extends Menu {

    private final Entity mascots;
    private City city;

    public MascotMenu(Player owner, Entity mascots) {
        super(owner);
        this.mascots = mascots;
        this.city = CityManager.getPlayerCity(owner.getUniqueId());
    }

    @Override
    public @NotNull String getName() {
        return "§cMascotte (niv. " + MascotUtils.getMascotOfCity(city.getUUID()).getLevel() + ")";
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

        try {
            List<Component> loreSkinMascot = List.of(
                    Component.text("§7Vous pouvez changer l'apparence de votre §cMascotte"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CHANGER DE SKIN")
            );

            map.put(11, new ItemBuilder(this, getSpawnEgg(mascots), itemMeta -> {
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
                new MascotsSkinMenu(player, getSpawnEgg(mascots), mascots).open();
            }));

            Supplier<ItemStack> moveMascotItemSupplier = () -> {
                List<Component> lorePosMascot;

                if (!DynamicCooldownManager.isReady(mascots.getUniqueId().toString(), "mascots:move")) {
                    lorePosMascot = List.of(
                            Component.text("§7Vous ne pouvez pas changer la position de votre §cMascotte"),
                            Component.text(""),
                            Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mascots.getUniqueId().toString(), "mascots:move")))
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
                    if (!DynamicCooldownManager.isReady(mascots.getUniqueId().toString(), "mascots:move")) {
                        MessagesManager.sendMessage(getOwner(), Component.text("eded"), Prefix.CITY, MessageType.ERROR, false);
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

                    ItemStack mascotsMoveItem = new ItemStack(Material.STICK);
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
                            mascotMove -> {;
                                if (!movingMascots.contains(city_uuid)) return;

                                Mascot mascot = MascotUtils.getMascotOfCity(city_uuid);
                                if (mascot==null) return;

                                Entity mob = MascotUtils.loadMascot(mascot);
                                if (mob==null) return;

                                Chunk chunk = mascotMove.getChunk();
                                int chunkX = chunk.getX();
                                int chunkZ = chunk.getZ();

                                if (!city.hasChunk(chunkX,chunkZ)){
                                    MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre car ce chunk ne vous appartient pas ou est adjacent à une autre ville"), Prefix.CITY, MessageType.INFO, false);
                                    return;
                                }

                                mob.teleport(mascotMove);
                                movingMascots.remove(city_uuid);
                                mascot.setChunk(mascotMove.getChunk());

                                //Cooldown de 5h pour déplacer la mascotte
                                DynamicCooldownManager.use(mascot.getMascotUuid().toString(), "mascots:move", 5*3600*1000L);
                            }
                            );
                    ;
                    player.closeInventory();
                });
            };
            if (!DynamicCooldownManager.isReady(mascots.getUniqueId().toString(), "mascots:move")) {
                MenuUtils.runDynamicItem(player, this, 13, moveMascotItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                map.put(13, new ItemBuilder(this, moveMascotItemSupplier.get()));
            }

            List<Component> requiredAmount = new ArrayList<>();
            MascotsLevels mascotsLevels = MascotsLevels.valueOf("level" + MascotUtils.getMascotLevel(city.getUUID()));

            if (mascotsLevels.equals(MascotsLevels.level10)){
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
                        MessagesManager.sendMessage(player, Component.text("Vous avez amélioré votre mascotte au §cNiveau " + MascotUtils.getMascotLevel(city_uuid)), Prefix.CITY, MessageType.ERROR, false);
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

            if (MascotUtils.getMascotImmunity(city.getUUID())) {
                Supplier<ItemStack> immunityItemSupplier = () -> {
                    List<Component> lore = List.of(
                            Component.text("§7Vous avez une §bimmunité §7sur votre §cMascotte"),
                            Component.text("§cTemps restant §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUUID(), "mascot:immunity")))
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
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return map;
    }

    public static Material getSpawnEgg(Entity entity) {
        String eggName = entity.getType().name() + "_SPAWN_EGG";
        if (Material.matchMaterial(eggName) == null){
            return Material.ZOMBIE_SPAWN_EGG;
        }
        return Material.matchMaterial(eggName);
    }
}
