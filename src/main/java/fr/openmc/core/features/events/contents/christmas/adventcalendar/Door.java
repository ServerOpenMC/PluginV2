package fr.openmc.core.features.events.contents.christmas.adventcalendar;

import java.util.HashSet;
import java.util.Set;

public record Door(int day, int column, int row, int width, int height) {
    public Set<Integer> getSlots() {
        Set<Integer> slots = new HashSet<>();

        for (int y = row; y < row + height; y++) {
            for (int x = column; x < column + width; x++) {
                slots.add(y * 9 + x);
            }
        }

        return slots;
    }
}
