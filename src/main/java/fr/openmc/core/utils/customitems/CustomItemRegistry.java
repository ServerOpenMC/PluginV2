package fr.openmc.core.utils.customitems;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.utils.customitems.buttons.*;
import fr.openmc.core.utils.customitems.items.company.CompanyBox;
import fr.openmc.core.utils.customitems.items.homes.Bin;
import fr.openmc.core.utils.customitems.items.homes.BinRed;
import fr.openmc.core.utils.customitems.items.homes.Information;
import fr.openmc.core.utils.customitems.items.homes.Upgrade;
import fr.openmc.core.utils.customitems.items.homes.icons.*;
import fr.openmc.core.utils.customitems.armors.SuitBoots;
import fr.openmc.core.utils.customitems.armors.SuitChestplate;
import fr.openmc.core.utils.customitems.armors.SuitHelmet;
import fr.openmc.core.utils.customitems.armors.SuitLeggings;
import fr.openmc.core.utils.customitems.buttons.*;
import fr.openmc.core.utils.customitems.items.*;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class CustomItemRegistry {
    static HashMap<String, CustomItem> items = new HashMap<>();
    static NamespacedKey customNameKey = new NamespacedKey("aywen", "custom_item");

    static public void init() {
        CommandsManager.getHandler().register(new CustomItemsDebugCommand());

        // Ici, enregistrer tous les items custom

        /* Buttons */
        new CloseButton();
        new PreviousPage();
        new NextPage();
        new AcceptButton();
        new RefuseButton();
        new SearchButton();
        new OneButton();
        new TenButton();
        new StackButton();
        new MinusButton();
        new PlusButton();

        /* Items */
        new ContestShell();
        new Aywenite();
        new KebabItem();
        new MascotWand();
        new WarpWand();

        new SuitHelmet();
        new SuitChestplate();
        new SuitLeggings();
        new SuitBoots();

        new CompanyBox();

        new BinRed();
        new Bin();
        new Information();
        new Upgrade();
        new Axenq();
        new Bank();
        new Chateau();
        new Chest();
        new Default();
        new Farm();
        new Home();
        new Sandblock();
        new Shop();
        new Xernas();
    }

    public static void register(String name, CustomItem item) {
        if (items.containsKey(name)) {
            throw new IllegalArgumentException("Custom item with name " + name + " already exists");
        }

        if (!name.matches("[a-zA-Z0-9_:]+")) {
            throw new IllegalArgumentException("Custom item name dont match regex \"[a-zA-Z0-9_:]+\"");
        }

        items.put(name, item);
    }

    @Nullable
    public static CustomItem getByName(String name) {
        return items.get(name);
    }

    @Nullable
    public static CustomItem getByItemStack(ItemStack stack) {
        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String name = view.get(customNameKey, PersistentDataType.STRING);

        if (name == null) return null;
        return getByName(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(items.keySet());
    }
}
