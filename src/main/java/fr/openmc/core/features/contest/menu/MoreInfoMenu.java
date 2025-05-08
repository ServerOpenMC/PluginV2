package fr.openmc.core.features.contest.menu;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import fr.openmc.api.menulib.Menu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MoreInfoMenu extends Menu {
    private final ContestManager contestManager;

    public MoreInfoMenu(Player owner) {
        super(owner);
        this.contestManager = ContestManager.getInstance();
    }

    @Override
    public @NotNull String getName() {
        if (PapiAPI.hasPAPI() && CustomItemRegistry.hasItemsAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-48%%img_contest_menu%");
        } else {
            return "Menu des Contests";
        }
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
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();

        List<Component> lore0 = Arrays.asList(
                Component.text("§7Tout les vendredi, le Contest commence"),
                Component.text("§7Et les votes s'ouvrent, et il faut choisir"),
                Component.text("§7Entre 2 camps, une ambience se crée dans le spawn...")
        );

        List<Component> lore1 = Arrays.asList(
                Component.text("§7La nuit tombe sur le spawn pendant 2 jours"),
                Component.text("§7Que la Fête commence!"),
                Component.text("§7Des trades sont disponible"),
                Component.text("§7Donnant des Coquillages de Contest!")
        );

        List<Component> lore2 = Arrays.asList(
                Component.text("§7Le levé de Soleil sur le Spawn!"),
                Component.text("§7Les résultats tombent, et un camp"),
                Component.text("§7sera gagnant. Et des récompenses seront attribué"),
                Component.text(("§7A chacun."))
        );


        int phase = contestManager.data.getPhase();

        boolean ench0;
        boolean ench1;

        switch (phase) {
            case 2 : {
                ench1 = false;
                ench0 = true;
                break;
            }
            case 3 : {
                ench0 = false;
                ench1 = true;
                break;
            }
            default : {
                ench1 = false;
                ench0 = false;
                break;
            }
        }

        inventory.put(11, new ItemBuilder(this, Material.BLUE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§1Les Votes - Vendredi"));
            itemMeta.lore(lore0);
            itemMeta.setEnchantmentGlintOverride(ench0);
        }));

        inventory.put(13, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§cL'Affrontement - Samedi-Dimanche"));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(ench1);
        }));

        inventory.put(15, new ItemBuilder(this, Material.YELLOW_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§eLes Résultats - Lundi"));
            itemMeta.lore(lore2);
        }));

        inventory.put(35, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour"))).setBackButton());

        return inventory;
    }
}
