package fr.openmc.api.menulib.utils;

import lombok.Getter;

/**
 * Represents different inventory sizes that can be used for organizing game inventories.
 * Each inventory size is associated with a specific numerical value representing the
 * number of slots available in the inventory.
 * <p>
 * This enum provides a convenient way to define and manage various inventory sizes:<br>
 * - {@code SMALLEST}: 9 slots<br>
 * - {@code SMALL}: 18 slots<br>
 * - {@code NORMAL}: 27 slots<br>
 * - {@code LARGE}: 36 slots<br>
 * - {@code LARGER}: 45 slots<br>
 * - {@code LARGEST}: 54 slots<br>
 */
public enum InventorySize {
	
	SMALLEST(9),
	SMALL(18),
	NORMAL(27),
	LARGE(36),
	LARGER(45),
	LARGEST(54);
	
	@Getter
	private final int size;
	
	/**
	 * Constructs an InventorySize enum instance with the specified size.
	 *
	 * @param size the size of the inventory associated with this enum instance
	 */
	InventorySize(int size) {
		this.size = size;
	}
}