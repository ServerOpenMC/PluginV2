package fr.openmc.core.features.city.menu.mascots;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.menu.CityMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotsManager.changeMascotsSkin;


public class MascotsSkinMenu extends Menu {

    private final Material egg;
    private final Entity mascots;
    Sound selectSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    Sound deniedSound = Sound.BLOCK_NOTE_BLOCK_BASS;

    public MascotsSkinMenu(Player owner, Material egg, Entity mascots) {
        super(owner);
        this.egg = egg;
        this.mascots = mascots;
    }

    @Override
    public @NotNull String getName() {
        return "";
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

        map.put(3, new ItemBuilder(this, Material.PIG_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Cochon"));
            if (egg.equals(Material.PIG_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.PIG_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.PIG);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(4, new ItemBuilder(this, Material.PANDA_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Panda"));
            if (egg.equals(Material.PANDA_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.PANDA_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.PANDA);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(5, new ItemBuilder(this, Material.SHEEP_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Mouton"));
            if (egg.equals(Material.SHEEP_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.SHEEP_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.SHEEP);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(10, new ItemBuilder(this, Material.AXOLOTL_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Axolotl"));
            if (egg.equals(Material.AXOLOTL_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.AXOLOTL_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.AXOLOTL);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(11, new ItemBuilder(this, Material.CHICKEN_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Poulet"));
            if (egg.equals(Material.CHICKEN_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.CHICKEN_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.CHICKEN);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(12, new ItemBuilder(this, Material.COW_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Vache"));
            if (egg.equals(Material.COW_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.COW_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.COW);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(13, new ItemBuilder(this, Material.GOAT_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Chèvre"));
            if (egg.equals(Material.GOAT_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.GOAT_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.GOAT);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(14, new ItemBuilder(this, Material.MOOSHROOM_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Vache champignon"));
            if (egg.equals(Material.MOOSHROOM_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.MOOSHROOM_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.MOOSHROOM);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(15, new ItemBuilder(this, Material.WOLF_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Loup"));
            if (egg.equals(Material.WOLF_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.WOLF_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.WOLF);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(16, new ItemBuilder(this, Material.VILLAGER_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Villageois"));
            if (egg.equals(Material.VILLAGER_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.VILLAGER_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.VILLAGER);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(21, new ItemBuilder(this, Material.SKELETON_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Squelette"));
            if (egg.equals(Material.SKELETON_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.SKELETON_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.SKELETON);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(22, new ItemBuilder(this, Material.SPIDER_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Araignée"));
            if (egg.equals(Material.SPIDER_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.SPIDER_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.SPIDER);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(23, new ItemBuilder(this, Material.ZOMBIE_SPAWN_EGG, itemMeta -> {
            itemMeta.displayName(Component.text("§7Zombie"));
            if (egg.equals(Material.ZOMBIE_SPAWN_EGG)){
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(inventoryClickEvent -> {
            if (!egg.equals(Material.ZOMBIE_SPAWN_EGG)){
                changeMascotsSkin(mascots, EntityType.ZOMBIE);
                getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                getOwner().closeInventory();
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        }));

        map.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(Component.text("§aRetour"));
            itemMeta.lore(List.of(Component.text("§7Retourner au menu de votre mascotte")));
        }).setOnClick(event -> {
            CityMenu menu = new CityMenu(getOwner());
            menu.open();
        }));

        return map;
    }
}
