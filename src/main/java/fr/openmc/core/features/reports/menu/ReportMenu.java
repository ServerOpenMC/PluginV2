package fr.openmc.core.features.reports.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

//TODO : Menu qui affiche les report des joueurs (Les joueurs peuvent voir leur propre report et le status)
public class ReportMenu extends Menu {

    protected ReportMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return Component.text("Reports").color(NamedTextColor.RED);
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        return Map.of();
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
