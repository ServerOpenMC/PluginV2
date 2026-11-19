package fr.openmc.core.features.events.contents.christmas.adventcalendar;

import java.util.Set;

public class AdventLayouts {
    public static final Set<Door> DEFAULT = Set.of(
            door(1, 7, 2, 1, 1),
            door(2, 2, 4, 3, 1),
            door(3, 0, 1, 1, 2),
            door(4, 4, 0, 2, 1),
            door(5, 7, 3, 2, 3),
            door(6, 4, 5, 3, 1),
            door(7, 3, 3, 1, 1),
            door(8, 3, 2, 1, 1),
            door(9, 0, 5, 1, 1),
            door(10, 1, 2, 2, 2),
            door(11, 5, 4, 2, 1),
            door(12, 3, 1, 2, 1),
            door(13, 0, 0, 1, 1),
            door(14, 1, 4, 1, 2),
            door(15, 1, 0, 2, 2),
            door(16, 0, 3, 1, 2),
            door(17, 8, 2, 1, 1),
            door(18, 5, 1, 1, 1),
            door(19, 2, 5, 1, 1),
            door(20, 6, 0, 3, 2),
            door(21, 6, 2, 1, 2),
            door(22, 3, 0, 1, 1),
            door(23, 3, 5, 1, 1),
            door(24, 4, 2, 2, 2)
    );

    private static Door door(int day, int column, int row, int width, int height) {
        return new Door(day, column, row, width, height);
    }
}
