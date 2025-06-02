package fr.openmc.core.features.city.menu.mayor;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
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
        return "Maires - Plus d'info";
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
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();
        MayorManager mayorManager = MayorManager.getInstance();

        try {
            List<Component> lore0 = Arrays.asList(
                    Component.text("§7Tous les §6Mercredi§7, les §6Elections §7commencent"),
                    Component.text("§7Si vous avez plus de §6" + mayorManager.MEMBER_REQ_ELECTION + " §7membres,"),
                    Component.text("§7vous pouvez élire un §6Maire §7pour votre ville"),
                    Component.text("§7Sinon, le propriétaire choisira les §3Réformes qu'il veut!")
            );

            List<Component> lore1 = Arrays.asList(
                    Component.text("§7Tous les §3Jeudi§7, le Maire est élu!"),
                    Component.text("§7Les §3Réformes §7choisies par le Maire sont appliquées"),
                    Component.text("§7Vous pouvez y jettez un coup d'oeil sur §3/city mayor")
            );


            int phase = mayorManager.phaseMayor;

            boolean ench0;
            boolean ench1;

            switch (phase) {
                case 2: {
                    ench1 = true;
                    ench0 = false;
                    break;
                }
                default: {
                    ench1 = false;
                    ench0 = true;
                    break;
                }
            }

            inventory.put(11, new ItemBuilder(this, Material.ORANGE_STAINED_GLASS_PANE, itemMeta -> {
                itemMeta.displayName(Component.text("§r§6Les Elections - Mercredi"));
                itemMeta.lore(lore0);
                itemMeta.setEnchantmentGlintOverride(ench0);
            }));


            inventory.put(15, new ItemBuilder(this, Material.CYAN_STAINED_GLASS_PANE, itemMeta -> {
                itemMeta.displayName(Component.text("§r§3Les Réformes - Jeudi"));
                itemMeta.lore(lore1);
                itemMeta.setEnchantmentGlintOverride(ench1);
            }));

            inventory.put(26, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(Component.text("§r§aRetour"))).setBackButton());

            return inventory;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return inventory;
    }
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
