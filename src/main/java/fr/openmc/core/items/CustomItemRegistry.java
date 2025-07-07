package fr.openmc.core.items;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.items.buttons.*;
import fr.openmc.core.items.items.company.CompanyBox;
import fr.openmc.core.items.items.homes.*;
import fr.openmc.core.items.items.homes.icons.*;
import fr.openmc.core.items.armors.SuitBoots;
import fr.openmc.core.items.armors.SuitChestplate;
import fr.openmc.core.items.armors.SuitHelmet;
import fr.openmc.core.items.armors.SuitLeggings;
import fr.openmc.core.items.items.*;
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

    public CustomItemRegistry() {
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
        new QuestsRightArrow();
        new QuestsLeftArrow();

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

        /* Homes icons */
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
        new Invisible();
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

        return name == null ? null : getByName(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(items.keySet());
    }
}
