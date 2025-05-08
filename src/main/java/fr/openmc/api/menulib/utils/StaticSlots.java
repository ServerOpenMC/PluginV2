package fr.openmc.api.menulib.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The StaticSlots class provides predefined static lists of integers representing
 * various slot configurations and utility methods for managing and manipulating slot lists.
 * <p>
 * The predefined slot lists include:<br>
 * - {@code NONE}: An empty list of slots.<br>
 * - {@code MIDDLE_BUTTONS}: A predefined list of slots designated for middle buttons.<br>
 * - {@code SPREAD_BUTTONS}: A predefined list of slots designated for spread buttons.<br>
 * - {@code BOTTOM}: A predefined list of slots located at the bottom.<br>
 * - {@code TOP}: A predefined list of slots located at the top.<br>
 * - {@code RIGHT}: A predefined list of slots located on the right side.<br>
 * - {@code LEFT}: A predefined list of slots located on the left side.<br>
 * - {@code STANDARD}: A combined list of slots from RIGHT, LEFT, TOP, and BOTTOM with duplicate removal.
 * <p>
 * Utility methods provided include:<br>
 * - combine: Combines two slot lists into one, ensuring no duplicates and filtering slots to a valid range.<br>
 * - getStaticSlots: Creates a new list from specified slot integers.<br>
 * - removeRecurringIntegers: Removes duplicate integers from a list and ensures numbers fall within a valid range.
 */
public class StaticSlots {
	
	public final static List<Integer> NONE = new ArrayList<>();
	public final static List<Integer> MIDDLE_BUTTONS = new ArrayList<>();
	public final static List<Integer> SPREAD_BUTTONS = new ArrayList<>();
	public final static List<Integer> BOTTOM = new ArrayList<>();
	public final static List<Integer> TOP = new ArrayList<>();
	public final static List<Integer> RIGHT = new ArrayList<>();
	public final static List<Integer> LEFT = new ArrayList<>();
	public final static List<Integer> STANDARD = new ArrayList<>();
	
	static {
		// MIDDLE BUTTONS
		MIDDLE_BUTTONS.add(48);
		MIDDLE_BUTTONS.add(49);
		MIDDLE_BUTTONS.add(50);
		
		// SPREAD BUTTONS
		SPREAD_BUTTONS.add(45);
		SPREAD_BUTTONS.add(49);
		SPREAD_BUTTONS.add(53);
		
		// BOTTOM
		for (int i = 45; i < 54; i++) {
			BOTTOM.add(i);
		}
		
		// TOP
		for (int i = 0; i < 9; i++) {
			TOP.add(i);
		}
		
		// RIGHT
		RIGHT.add(0);
		RIGHT.add(9);
		RIGHT.add(18);
		RIGHT.add(27);
		RIGHT.add(36);
		RIGHT.add(45);
		
		// LEFT
		LEFT.add(8);
		LEFT.add(17);
		LEFT.add(26);
		LEFT.add(35);
		LEFT.add(44);
		LEFT.add(53);
		
		// STANDARD
		STANDARD.addAll(combine(combine(RIGHT, LEFT), combine(TOP, BOTTOM)));
	}
	
	/**
	 * Combines two lists of integers into a single list, removing any duplicate integers and ensuring
	 * all integers are within the range [0, 54).
	 *
	 * @param list1 the first list of integers to combine
	 * @param list2 the second list of integers to combine
	 * @return a new list of integers containing all unique values from both input lists
	 * within the range [0, 54)
	 */
	public static List<Integer> combine(List<Integer> list1, List<Integer> list2) {
		List<Integer> finalList = new ArrayList<>();
		finalList.addAll(list1);
		finalList.addAll(list2);
		return removeRecurringIntegers(finalList);
	}
	
	/**
	 * Creates a new list from the provided integer array of slots.
	 *
	 * @param slots one or more integers representing slot positions
	 * @return a list containing the specified integers
	 */
	public static List<Integer> getStaticSlots(Integer... slots) {
		return new ArrayList<>(Arrays.asList(slots));
	}
	
	/**
	 * Removes duplicate integers from the provided list, ensuring all integers are unique,
	 * and only includes integers within the range [0, 54).
	 *
	 * @param list the input list containing integers, which may include duplicates
	 * @return a new list of integers that contains only unique values within the range [0, 54)
	 */
	public static List<Integer> removeRecurringIntegers(List<Integer> list) {
		List<Integer> finalList = new ArrayList<>();
		for (Integer integer : list) {
			if (! finalList.contains(integer)) {
				if (integer >= 0 && integer < 54) {
					finalList.add(integer);
				}
			}
		}
		return finalList;
	}
	
}