package fr.openmc.core.features.city.menu.main;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.main.buttons.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityMenu extends Menu {

    private static final int[] RANKS_SLOTS = {0, 1, 9, 10};
    private static final int[] MILESTONE_SLOTS = {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
    private static final int MANAGE_CITY_SLOT = 4;
    private static final int[] NOTATIONS_SLOTS = {40, 41, 49, 50};
    private static final int[] MAP_SLOTS = {18, 19, 27, 28};
    private static final int[] MASCOTS_SLOTS = {5, 6, 7, 8, 14, 15, 16, 17};
    private static final int MEMBERS_SLOT = 13;
    private static final int[] MAYOR_SLOTS = {2, 3, 11, 12};
    private static final int[] TYPE_SLOTS = {25, 26, 34, 35};
    private static final int[] CHEST_SLOTS = {36, 37, 45, 46};
    private static final int[] BANK_SLOTS = {38, 39, 47, 48};
    private static final int[] LEAVE_SLOTS = {42, 43, 44, 51, 52, 53};

    public CityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes";
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_mainmenu:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

		City city = CityManager.getPlayerCity(player.getUniqueId());
		assert city != null;

        // ** Rank Button
        RankButton.init(this, inventory, city, RANKS_SLOTS);

        // ** Milestone Button
        MilestoneButton.init(this, inventory, city, MILESTONE_SLOTS);

        // ** Manage City Button
        ManageButton.init(this, inventory, city, MANAGE_CITY_SLOT);

        // ** Notation Button
        NotationsButton.init(this, inventory, city, NOTATIONS_SLOTS);

        // ** Mascots Button
        MascotsButton.init(this, inventory, city, MASCOTS_SLOTS);

        // ** Map Button
        MapButton.init(this, inventory, city, MAP_SLOTS);

        // ** Members Button
        MembersButton.init(this, inventory, city, MEMBERS_SLOT);

        // ** Mayor Button
        MayorButton.init(this, city, MAYOR_SLOTS);

        // ** Type Button
        TypeButton.init(this, inventory, city, TYPE_SLOTS);

        // ** Chest Button
        ChestButton.init(this, inventory, city, CHEST_SLOTS);

        // ** Bank Button
        BankButton.init(this, inventory, city, BANK_SLOTS);

        // ** Leave Button
        LeaveButton.init(this, inventory, city, LEAVE_SLOTS);

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }
}
