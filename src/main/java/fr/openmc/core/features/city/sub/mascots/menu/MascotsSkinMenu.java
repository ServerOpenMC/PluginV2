package fr.openmc.core.features.city.sub.mascots.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotType;
import fr.openmc.core.features.city.sub.milestone.rewards.MascotsSkinUnlockRewards;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.sub.mascots.MascotsManager.changeMascotsSkin;

public class MascotsSkinMenu extends Menu {

    private final Sound selectSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    private final Sound deniedSound = Sound.BLOCK_NOTE_BLOCK_BASS;
    private final Material egg;
    private final Mascot mascots;

    public MascotsSkinMenu(Player owner, Material egg, Mascot mascots) {
        super(owner);
        this.egg = egg;
        this.mascots = mascots;
    }

    @Override
    public @NotNull String getName() {
	    return "Menu des skins des mascottes";
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

        City playerCity = CityManager.getPlayerCity(getOwner().getUniqueId());

        if (playerCity == null) return map;

        for (MascotType mascotType : MascotType.values()) {
            map.put(mascotType.getSlot(), createMascotButton(playerCity, mascotType));
        }

        map.put(18, new ItemBuilder(this, Material.ARROW, meta -> {
            meta.displayName(Component.text("§aRetour"));
	        meta.lore(List.of(Component.text("§7Retourner au menu précédent")));
        }, true));

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

    private ItemBuilder createMascotButton(City city, MascotType type) {
        List<Component> loreMascots = new ArrayList<>();

        if (city.getLevel() < MascotsSkinUnlockRewards.getLevelRequiredSkin(type)) {
	        loreMascots.add(Component.text("§cVous devez être niveau " + MascotsSkinUnlockRewards.getLevelRequiredSkin(type) + " pour débloquer ce skin"));
        }

        return new ItemBuilder(this, type.getMascotItem(egg.equals(type.getSpawnEgg())), meta -> meta.lore(loreMascots))
                .setOnClick(event -> {
                    if (city.getLevel() < MascotsSkinUnlockRewards.getLevelRequiredSkin(type)) {
                        MessagesManager.sendMessage(getOwner(), Component.text("Vous n'avez pas le niveau de ville requis pour mettre ce skin"), Prefix.CITY, MessageType.ERROR, false);
                        return;
                    }
                    if (!egg.equals(type.getSpawnEgg())) {
                        int aywenite = type.getPrice();
                        ItemStack ISAywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
                        if (ItemUtils.hasEnoughItems(getOwner(), ISAywenite, aywenite)) {
                            changeMascotsSkin(mascots, type.getEntityType(), getOwner(), aywenite);
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
}