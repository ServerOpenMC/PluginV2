package fr.openmc.core.features.city.sub.mayor.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreInfoMenu extends Menu {

    public MoreInfoMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
	    return "Menu des maires - Plus d'info";
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

        List<Component> lore0 = Arrays.asList(
		        Component.text("§7Tous les §6mercredis§7, les §6élections §7commencent"),
                Component.text("§7Si vous avez plus de §6" + MayorManager.MEMBER_REQUEST_ELECTION + " §7membres,"),
		        Component.text("§7vous pouvez élire un §6maire §7pour votre ville"),
		        Component.text("§7Sinon, le propriétaire choisira les §3réformes qu'il veut!")
        );

        List<Component> lore1 = Arrays.asList(
		        Component.text("§7Tous les §3jeudis§7, le maire est élu!"),
		        Component.text("§7Les §3réformes §7choisies par le maire sont appliquées"),
		        Component.text("§7Vous pouvez y jetez un coup d'oeil sur §3/city mayor")
        );


        int phase = MayorManager.phaseMayor;

        inventory.put(11, new ItemBuilder(this, Material.ORANGE_STAINED_GLASS_PANE, itemMeta -> {
	        itemMeta.displayName(Component.text("§r§6Les élections - Mercredi"));
            itemMeta.lore(lore0);
            itemMeta.setEnchantmentGlintOverride(phase != 2);
        }));

        inventory.put(15, new ItemBuilder(this, Material.CYAN_STAINED_GLASS_PANE, itemMeta -> {
	        itemMeta.displayName(Component.text("§r§3Les réformes - Jeudi"));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(phase == 2);
        }));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour")), true));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
