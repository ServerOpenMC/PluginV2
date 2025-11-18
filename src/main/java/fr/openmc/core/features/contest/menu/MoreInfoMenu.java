package fr.openmc.core.features.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
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
        return "Menu des Contests - Plus d'info";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::contest_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        List<Component> lore0 = Arrays.asList(
                Component.text("§7Tout les vendredi, le contest commence"),
                Component.text("§7Et les votes s'ouvrent, et il faut choisir"),
                Component.text("§7Entre 2 camps, une ambience se crée dans le spawn...")
        );

        List<Component> lore1 = Arrays.asList(
                Component.text("§7La nuit tombe sur le spawn pendant 2 jours"),
                Component.text("§7Que la fête commence !"),
                Component.text("§7Des trades sont disponible"),
                Component.text("§7Donnant des coquillages de contest !")
        );

        List<Component> lore2 = Arrays.asList(
                Component.text("§7Le levé de soleil sur le spawn !"),
                Component.text("§7Les résultats tombent, et un camp"),
                Component.text("§7sera gagnant. Et des récompenses seront attribuées"),
                Component.text(("§7à chacun."))
        );


            int phase = ContestManager.data.getPhase();

        boolean ench0 = phase == 2;
        boolean ench1 = phase == 3;

        inventory.put(11, new ItemBuilder(this, Material.BLUE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§1Les votes - Vendredi"));
            itemMeta.lore(lore0);
            itemMeta.setEnchantmentGlintOverride(ench0);
        }));

        inventory.put(13, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§cL'affrontement - Samedi-Dimanche"));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(ench1);
        }));

        inventory.put(15, new ItemBuilder(this, Material.YELLOW_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§eLes résultats - Lundi"));
            itemMeta.lore(lore2);
        }));

        inventory.put(35, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour")), true));

        return inventory;
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
