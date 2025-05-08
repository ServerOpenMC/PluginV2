package fr.openmc.api.menulib.utils;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.menulib.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The {@code ItemBuilder} class is a utility for creating and customizing {@link ItemStack} objects
 * in a menu context. It provides methods to set item properties, handle click events, and manage
 * metadata, making it easier to create interactive items within a menu system.
 */
public class ItemBuilder extends ItemStack {
	private final Menu itemMenu;
	private ItemMeta meta;
	
	
	/**
	 * Constructs an {@code ItemBuilder} with the specified {@link Menu} and {@link Material}.
	 * This constructor initializes the {@code ItemBuilder} to create items using the given menu and material,
	 * with no additional customizations for the {@link ItemMeta}.
	 *
	 * @param itemMenu The {@link Menu} this item will be associated with. It represents the context in which
	 *                 the item exists, such as a specific inventory or menu framework.
	 * @param material The {@link Material} of the item. It determines the base appearance and behavior
	 *                 of the item being created.
	 */
	public ItemBuilder(Menu itemMenu, Material material) {
		this(itemMenu, material, null);
	}
	
	/**
	 * Constructs an {@code ItemBuilder} with the specified {@link Menu} and {@link ItemStack}.
	 * This constructor initializes the {@code ItemBuilder} to create items with the given menu and item,
	 * while not applying any additional customizations to the {@link ItemMeta}.
	 *
	 * @param itemMenu The {@link Menu} that this item will be associated with. This parameter represents
	 *                 the context in which the item will exist, such as an inventory or menu framework.
	 * @param item     The {@link ItemStack} defining the base item configuration. It includes the material,
	 *                 amount, and current metadata of the item.
	 */
	public ItemBuilder(Menu itemMenu, ItemStack item) {
		this(itemMenu, item, null);
	}
	
	/**
	 * Constructs an {@code ItemBuilder} with the specified {@link Menu}, {@link Material},
	 * and an {@link Consumer} for customizing the {@link ItemMeta}.
	 * This constructor initializes the {@code ItemBuilder} with a menu, a material to define the base item,
	 * and a consumer for applying additional metadata customization to the item.
	 *
	 * @param itemMenu The {@link Menu} this item will be associated with. It represents the context in which
	 *                 the item exists, such as a specific inventory or menu framework.
	 * @param material The {@link Material} of the item. It determines the base appearance and behavior
	 *                 of the item being created.
	 * @param itemMeta A {@link Consumer} that customizes the {@link ItemMeta} of the item. It allows further
	 *                 modification of properties such as the display name, lore, enchantments, and more.
	 */
	public ItemBuilder(Menu itemMenu, Material material, Consumer<ItemMeta> itemMeta) {
		this(itemMenu, new ItemStack(material), itemMeta);
	}
	
	/**
	 * Constructs an {@code ItemBuilder} with the specified {@link Menu}, {@link ItemStack},
	 * and an {@link Consumer} for customizing the {@link ItemMeta}.
	 * This constructor initializes the {@code ItemBuilder} with a menu, a specific item to define
	 * the base configuration, and a consumer for applying additional metadata customizations to the item.
	 *
	 * @param itemMenu The {@link Menu} this item will be associated with. It represents the context in which
	 *                 the item exists, such as a specific inventory or menu framework.
	 * @param item     The {@link ItemStack} defining the base item configuration. It includes the material,
	 *                 amount, and current metadata of the item.
	 * @param itemMeta A {@link Consumer} that customizes the {@link ItemMeta} of the item. It allows further
	 *                 modification of properties such as the display name, lore, enchantments, and more.
	 */
	public ItemBuilder(Menu itemMenu, ItemStack item, Consumer<ItemMeta> itemMeta) {
		super(item);
		this.itemMenu = itemMenu;
		meta = getItemMeta();
		if (itemMeta != null) {
			itemMeta.accept(meta);
		}
		setItemMeta(meta);
	}
	
	/**
	 * Sets the unique identifier for the item using the specified {@code itemId}.
	 * The identifier is stored in the item's {@link PersistentDataContainer} as a
	 * {@link String} in a lower-case format, allowing it to be associated with
	 * specific functionality in the menu system.
	 *
	 * @param itemId The unique identifier to associate with the item. This value is stored
	 *               in a lower-case format within the item's {@link PersistentDataContainer}.
	 * @return The current instance of {@link ItemBuilder}, allowing for method chaining
	 * when creating and customizing items.
	 */
	public ItemBuilder setItemId(String itemId) {
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		dataContainer.set(MenuLib.getItemIdKey(), PersistentDataType.STRING, itemId.toLowerCase());
		setItemMeta(meta);
		return this;
	}
	
	/**
	 * Sets the click event handler for the item. This method associates the specified
	 * {@link Consumer} with the item to define a custom behavior when the item is clicked
	 * in the menu.
	 *
	 * @param e A {@link Consumer} of {@link InventoryClickEvent} that specifies the action
	 *          to be performed when the item is clicked.
	 * @return The current instance of {@link ItemBuilder}, allowing method chaining
	 * for further customization of the item.
	 */
	public ItemBuilder setOnClick(Consumer<InventoryClickEvent> e) {
		try {
			MenuLib.setItemClickEvent(itemMenu, this, e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Sets the next menu to be displayed when the associated item is clicked.
	 * This method assigns a click event consumer that transitions the player
	 * to the specified menu and updates the last accessed menu.
	 *
	 * @param menu The {@link Menu} that will be opened upon clicking the item.
	 * @return The current instance of {@link ItemBuilder}, enabling method chaining
	 * for additional configurations.
	 */
	public ItemBuilder setNextMenu(Menu menu) {
		Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
			Player player = (Player) inventoryClickEvent.getWhoClicked();
			MenuLib.setLastMenu(player, itemMenu);
			menu.open();
		};
		setOnClick(clickEventConsumer);
		return this;
	}
	
	/**
	 * Sets the item to act as a close button. When the item is clicked, it closes
	 * the inventory of the menu owner.
	 *
	 * @return The current instance of {@link ItemBuilder}, allowing method chaining
	 * for further customization of the item.
	 */
	public ItemBuilder setCloseButton() {
		Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> itemMenu.getOwner().closeInventory();
		setOnClick(clickEventConsumer);
		return this;
	}
	
	/**
	 * Sets the item to act as a back button. When the item is clicked, it navigates the user
	 * to the previously viewed menu associated with the current menu owner.
	 *
	 * @return The current instance of {@link ItemBuilder}, enabling method chaining
	 * for additional configurations.
	 */
	public ItemBuilder setBackButton() {
		Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> itemMenu.back();
		setOnClick(clickEventConsumer);
		return this;
	}
	
	/**
	 * Sets the item to act as a button that navigates to the next page in a paginated menu.
	 * When the item is clicked, the page within the associated {@link PaginatedMenu} is incremented,
	 * provided the current page is not the last. The updated menu is then reopened for the user.
	 *
	 * @return The current instance of {@link ItemBuilder}, enabling method chaining
	 * for additional configurations of the item.
	 */
	public ItemBuilder setNextPageButton() {
		Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
			if (itemMenu instanceof PaginatedMenu menu) {
				menu.setPage(menu.isLastPage() ? menu.getPage() : menu.getPage() + 1);
				menu.open();
			}
		};
		setOnClick(clickEventConsumer);
		return this;
	}
	
	/**
	 * Sets the item to act as a button that navigates to the previous page in a paginated menu.
	 * When the item is clicked, the page within the associated {@link PaginatedMenu} is decremented,
	 * provided the current page is not the first. The updated menu is then reopened for the user.
	 *
	 * @return The current instance of {@link ItemBuilder}, enabling method chaining
	 * for additional configurations of the item.
	 */
	public ItemBuilder setPreviousPageButton() {
		Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
			if (itemMenu instanceof PaginatedMenu menu) {
				menu.setPage(menu.getPage() == 0 ? 0 : menu.getPage() - 1);
				menu.open();
			}
		};
		setOnClick(clickEventConsumer);
		return this;
	}
	
	/**
	 * Sets the {@link ItemFlag}s to be applied to the item. This method first clears all
	 * existing item flags from the item's metadata, then applies the specified flags.
	 * Item flags control specific visual and functional attributes of the item, such as
	 * hiding enchantments or attributes.
	 *
	 * @param itemFlags The {@link ItemFlag}s to be added to the item. Multiple flags
	 *                  can be specified, allowing for a combination of attributes to
	 *                  be hidden or modified.
	 * @return The current instance of {@link ItemBuilder}, enabling method chaining for
	 * further customization of the item.
	 */
	public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
		meta.getItemFlags().forEach(meta::removeItemFlags);
		meta.addItemFlags(itemFlags);
		setItemMeta(meta);
		return this;
	}
	
	/**
	 * Sets the metadata for the item. This method updates the current {@code ItemMeta} of the item and applies it.
	 *
	 * @param itemMeta The {@link ItemMeta} to be applied to the item.
	 *                 May be {@code null} to clear any existing metadata.
	 * @return {@code true} if the item meta was successfully applied, {@code false} otherwise.
	 */
	@Override
	public final boolean setItemMeta(@Nullable ItemMeta itemMeta) {
		meta = itemMeta;
		return super.setItemMeta(itemMeta);
	}
}