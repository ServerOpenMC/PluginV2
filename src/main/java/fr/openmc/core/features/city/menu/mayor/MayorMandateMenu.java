package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorMandateMenu extends Menu {

    public MayorMandateMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
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
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        MayorManager mayorManager = MayorManager.getInstance();

        inventory.put(11, new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
            itemMeta.itemName(Component.text("§7Créer §dvotre ville"));
        }).setOnClick(inventoryClickEvent -> {

        }));

        // on regarde si le joueur s'est déjà présenter
        boolean playerAleardyCandidate = mayorManager.hasCandidated(player);
        List<Component> loreCandidature;
        if (playerAleardyCandidate) {
            loreCandidature = List.of(
                    Component.text("§7Vous vous êtes déjà §5présenter §7!"),
                    Component.text("§7Modifier votre couleur et regardez les Réformes que vous avez choisis"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
            );
        } else {
            loreCandidature = List.of(
                    Component.text("§7Vous pouvez vous §5inscire §7afin d'être maire !"),
                    Component.text("§7Séléctionner vos Réformes et votre couleur !"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR VOUS INSCRIRE")
            );
        }



        inventory.put(15, new ItemBuilder(this, PlayerUtils.getPlayerSkull(player), itemMeta -> {
            itemMeta.itemName(Component.text("§7Votre §5Candidature"));
            itemMeta.lore(loreCandidature);
        }).setOnClick(inventoryClickEvent -> {

        }));


        return inventory;
    }
}
