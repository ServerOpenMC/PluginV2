package fr.openmc.core.features.city.menu.mascots;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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
        return "§cMascotte";
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

        List<MascotOption> mascotsOptions = List.of(
                // price : 10 taille normale, 15 taille petite, 20 taille très petite
                new MascotOption(3, Material.PIG_SPAWN_EGG, EntityType.PIG, "Cochon", 15),
                new MascotOption(4, Material.PANDA_SPAWN_EGG, EntityType.PANDA, "Panda", 10),
                new MascotOption(5, Material.SHEEP_SPAWN_EGG, EntityType.SHEEP, "Mouton",10),
                new MascotOption(10, Material.AXOLOTL_SPAWN_EGG, EntityType.AXOLOTL, "Axolotl",20),
                new MascotOption(11, Material.CHICKEN_SPAWN_EGG, EntityType.CHICKEN, "Poulet",20),
                new MascotOption(12, Material.COW_SPAWN_EGG, EntityType.COW, "Vache",10),
                new MascotOption(13, Material.GOAT_SPAWN_EGG, EntityType.GOAT, "Chèvre",15),
                new MascotOption(14, Material.MOOSHROOM_SPAWN_EGG, EntityType.MOOSHROOM, "Vache champignon",10),
                new MascotOption(15, Material.WOLF_SPAWN_EGG, EntityType.WOLF, "Loup",15),
                new MascotOption(16, Material.VILLAGER_SPAWN_EGG, EntityType.VILLAGER, "Villageois",10),
                new MascotOption(21, Material.SKELETON_SPAWN_EGG, EntityType.SKELETON, "Squelette",10),
                new MascotOption(22, Material.SPIDER_SPAWN_EGG, EntityType.SPIDER, "Araignée",10),
                new MascotOption(23, Material.ZOMBIE_SPAWN_EGG, EntityType.ZOMBIE, "Zombie",10)
        );

        mascotsOptions.forEach(option -> map.put(option.slot(), createMascotButton(option)));

        map.put(18, new ItemBuilder(this, Material.ARROW, meta -> {
            meta.displayName(Component.text("§aRetour"));
            meta.lore(List.of(Component.text("§7Retourner au menu de votre mascotte")));
        }).setOnClick(event -> new CityMenu(getOwner()).open()));

        return map;
    }

    private ItemStack createMascotButton(MascotOption option) {
        return new ItemBuilder(this, option.material(), itemMeta -> {
            itemMeta.displayName(Component.text("§7" + option.displayName()));
            itemMeta.lore(List.of(Component.text("§7Nécessite §d" + option.price + " d'Aywenites")));
            if (egg.equals(option.material())) {
                itemMeta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }).setOnClick(event -> {

            if (!egg.equals(option.material())) {
                int aywenite = option.price;
                Material matAywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest().getType();
                if (ItemUtils.hasEnoughItems(getOwner(), matAywenite, aywenite)){
                    changeMascotsSkin(mascots, option.entityType(), getOwner(), matAywenite, aywenite);
                    getOwner().playSound(getOwner().getLocation(), selectSound, 1, 1);
                    getOwner().closeInventory();
                } else {
                    MessagesManager.sendMessage(getOwner(), Component.text("Vous n'avez pas assez d'§dAywenite"), Prefix.CITY, MessageType.ERROR, false);
                    getOwner().closeInventory();
                }
            } else {
                getOwner().playSound(getOwner().getLocation(), deniedSound, 1, 1);
            }
        });
    }

    private record MascotOption(int slot, Material material, EntityType entityType, String displayName, int price) {}
}