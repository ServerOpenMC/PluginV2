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
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
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
    public @NotNull String getName() {
	    return "Menu de §cmascotte (niv. " + city.getMascot().getLevel() + ")";
    }

    @Override
    public String getTexture() {
        return null;
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
            MessagesManager.sendMessage(player, Component.text("§cUne erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            return map;
        }

        List<Component> loreSkinMascot = List.of(
		        Component.text("§7Vous pouvez changer l'apparence de votre §cmascotte"),
                Component.empty(),
                Component.text("§e§lCLIQUEZ ICI POUR CHANGER DE SKIN")
        );

        map.put(11, new ItemBuilder(this, this.mascot.getMascotEgg(), itemMeta -> {
	        itemMeta.displayName(Component.text("§7Le skin de la §cmascotte"));
            itemMeta.lore(loreSkinMascot);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
        })
                .hide(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS)
                .setOnClick(inventoryClickEvent -> {
                    if (!city.hasPermission(player.getUniqueId(), CityPermission.MASCOT_SKIN)) {
                        MessagesManager.sendMessage(player, MessagesManager.Message.NO_PERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                        player.closeInventory();
                        return;
                    }
                    new MascotsSkinMenu(player, this.mascot.getMascotEgg(), this.mascot).open();
                }));

        Supplier<ItemBuilder> moveMascotItemSupplier = () -> {
            List<Component> lorePosMascot;

            if (!DynamicCooldownManager.isReady(this.mascot.getMascotUUID(), "mascots:move")) {
                lorePosMascot = List.of(
		                Component.text("§7Vous ne pouvez pas changer la position de votre §cmascotte"),
                        Component.empty(),
                        Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(this.mascot.getMascotUUID(), "mascots:move")))
                );
            } else {
                lorePosMascot = List.of(
		                Component.text("§7Vous pouvez changer la position de votre §cmascotte"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR LA CHANGER DE POSITION")
                );
            }

            return new ItemBuilder(this, Material.CHEST, itemMeta -> {
	            itemMeta.displayName(Component.text("§7Déplacer votre §cmascotte"));
                itemMeta.lore(lorePosMascot);
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
            })
                    .hide(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS)
                    .setOnClick(inventoryClickEvent -> {
                        if (!DynamicCooldownManager.isReady(this.mascot.getMascotUUID(), "mascots:move")) {
                            return;
                        }
                        if (!city.hasPermission(getOwner().getUniqueId(), CityPermission.MASCOT_MOVE)) {
                            MessagesManager.sendMessage(getOwner(), MessagesManager.Message.NO_PERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                            return;
                        }

                        if (!ItemUtils.hasAvailableSlot(getOwner())) {
                            MessagesManager.sendMessage(getOwner(), Component.text("Libérez de la place dans votre inventaire"), Prefix.CITY, MessageType.ERROR, false);
                            return;
                        }

                        city = CityManager.getPlayerCity(getOwner().getUniqueId());
                        if (city == null) {
                            MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                            getOwner().closeInventory();
                            return;
                        }

                        UUID cityUUID = city.getUniqueId();
                        if (movingMascots.contains(cityUUID)) return;

                        movingMascots.add(cityUUID);

                        ItemStack mascotsMoveItem = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
                        ItemMeta meta = mascotsMoveItem.getItemMeta();

                        if (meta != null) {
                            List<Component> info = new ArrayList<>();
                            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre"));
                            info.add(Component.text("§cCe coffre n'est pas retirable"));
	                        meta.displayName(Component.text("§7Déplacer votre §lmascotte"));
                            meta.lore(info);
                        }
                        mascotsMoveItem.setItemMeta(meta);

                        ItemInteraction.runLocationInteraction(
                                player,
                                mascotsMoveItem,
                                "mascots:moveInteraction",
                                120,
		                        "Temps restant : %sec%s",
		                        "§cDéplacement de la mascotte annulée",
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
                                        MessagesManager.sendMessage(player, Component.text("§cImpossible de déplacer la mascotte ici car ce chunk ne vous appartient pas ou est adjacent à une autre ville"), Prefix.CITY, MessageType.INFO, false);
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
            requiredAmount.add(Component.text("§7Niveau max atteint"));
        } else if (currentMascotLevel >= maxMascotLevel) {
	        requiredAmount.add(Component.text("§cVous devez être niveau " + (maxMascotLevel + 1) + " pour améliorer la mascotte"));
        } else {
            requiredAmount.add(Component.text("§7Nécessite §d" + mascotsLevels.getUpgradeCost() + " d'Aywenites"));
        }

        ItemBuilder itemBuilder = new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.displayName(Component.text("§7Améliorer votre §cMascotte"));
            itemMeta.lore(requiredAmount);
            itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
        }).hide(DataComponentTypes.ENCHANTMENTS, DataComponentTypes.ATTRIBUTE_MODIFIERS);

        itemBuilder.setData(DataComponentTypes.ITEM_MODEL, Key.key("minecraft:netherite_upgrade_smithing_template"));
        map.put(15, itemBuilder
                .setOnClick(inventoryClickEvent -> {
                    if (mascotsLevels.equals(MascotsLevels.level10)) return;
                    if (currentMascotLevel >= maxMascotLevel) return;

                    if (city == null) {
                        MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                        player.closeInventory();
                        return;
                    }
                    if (city.hasPermission(player.getUniqueId(), CityPermission.MASCOT_UPGRADE)) {
                        UUID cityUUID = city.getUniqueId();
                        int aywenite = mascotsLevels.getUpgradeCost();
                        if (ItemUtils.takeAywenite(player, aywenite)) {
                            upgradeMascots(cityUUID);
	                        MessagesManager.sendMessage(player, Component.text("Vous avez amélioré votre mascotte au §cniveau " + mascot.getLevel()), Prefix.CITY, MessageType.ERROR, false);
                            player.closeInventory();
                            return;
                        }
                        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez d'§dAywenite"), Prefix.CITY, MessageType.ERROR, false);

                    } else {
                        MessagesManager.sendMessage(player, MessagesManager.Message.NO_PERMISSION.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    }
                    player.closeInventory();
                }));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
	        itemMeta.lore(List.of(Component.text("§7Retourner au menu précédent")));
        }, true));

        if (city.isImmune()) {
            Supplier<ItemBuilder> immunityItemSupplier = () -> {
                List<Component> lore = List.of(
		                Component.text("§7Vous avez une §bimmunité §7sur votre §cmascotte"),
                        Component.text("§cTemps restant §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:immunity"))),
                        Component.text("§7Pour réduire le temps de 1 heure, vous devez posséder de :"),
                        Component.text("§8- §d" + AYWENITE_REDUCE + " d'Aywenite"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR REDUIRE LE TEMPS D'IMMUNITÉ")
                );

                return new ItemBuilder(this, Material.DIAMOND, itemMeta -> {
	                itemMeta.displayName(Component.text("§7Votre §cmascotte §7est §bimmunisée§7!"));
                    itemMeta.lore(lore);
                }).setOnClick(inventoryClickEvent -> {
                    if (city == null) {
                        MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                        player.closeInventory();
                        return;
                    }

                    if (!ItemUtils.takeAywenite(player, AYWENITE_REDUCE)) return;
                    DynamicCooldownManager.reduceCooldown(player, city.getUniqueId(), "city:immunity", COOLDOWN_REDUCE);

                    MessagesManager.sendMessage(player, Component.text("Vous venez de dépenser §d" + AYWENITE_REDUCE + " d'Aywenite §fpour §bréduire §fle cooldown d'une heure"), Prefix.CITY, MessageType.SUCCESS, false);
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
