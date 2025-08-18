package fr.openmc.core.features.city.sub.war.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.sub.war.WarManager;
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
        return "Menu des Guerres - Plus d'info";
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
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        List<Component> lore0 = Arrays.asList(
                Component.text("§7Choisissez une §6ville §7a attaquer !"),
                Component.text("§7Le nombre de combattants sera le même partout !"),
                Component.text("§8§oex. Si vous êtes 5 joueurs de connectés et 2 en face,"),
                Component.text("§8§oalors vous aurez le choix de faire un 1vs1 ou un 2vs2"),
                Component.empty(),
                Component.text("§7Lorsque vous venez déclarer la §6guerre §7à une ville, les personnes concernées seront informées "),
                Component.text("§7Vous aurez §6" + WarManager.TIME_PREPARATION + " min §7de préparation"),
                Component.text("§7ce qui vous laisse le temps de s’équiper et d’aller au front !")
        );

        List<Component> lore1 = Arrays.asList(
                Component.text("§7Le §ccombat §7commence, la partie se finira §c30 min §7après le commencent."),
                Component.text("§7Elle peut être finie avant si la §cMascotte ennemie §7est tuée. "),
                Component.text("§7Et pendant ce moment, vous pouvez §ctout faire §7dans la ville ennemie.")
        );

        List<Component> lore2 = Arrays.asList(
                Component.text("§7La fin de la guerre, la §fpaix §7s’impose entre les deux camps."),
                Component.text("§7Le §fvainqueur §7est désigné, la §frécompense §7est donnée."),
                Component.text("§7Et les deux villes obtiennent une immunité de §f2 jours §7!")
        );

        inventory.put(11, new ItemBuilder(this, Material.ORANGE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§6La Préparation - " + WarManager.TIME_PREPARATION + " min"));
            itemMeta.lore(lore0);
        }));

        inventory.put(13, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§cLe Combat - " + WarManager.TIME_FIGHT + " min"));
            itemMeta.lore(lore1);
        }));

        inventory.put(15, new ItemBuilder(this, Material.WHITE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(Component.text("§r§fLes Résultats"));
            itemMeta.lore(lore2);
        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour")), true));

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
