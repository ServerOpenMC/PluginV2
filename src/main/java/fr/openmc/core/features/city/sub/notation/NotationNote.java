package fr.openmc.core.features.city.sub.notation;

import lombok.Getter;

@Getter
public enum NotationNote {
    NOTE_ARCHITECTURAL(40),
    NOTE_COHERENCE(10),

    NOTE_ACTIVITY(5),
    NOTE_MILITARY(20),

    NOTE_PIB(15);

    private final int maxNote;

    NotationNote(int maxNote) {
        this.maxNote = maxNote;
    }

    /**
     * Calcule la somme de toutes les notes maximales.
     *
     * @return la somme des valeurs maximales de toutes les notes
     */
    public static double getMaxTotalNote() {
        double maxTotalNote = 0;
        for (NotationNote value : NotationNote.values()) {
            maxTotalNote += value.getMaxNote();
        }
        return maxTotalNote;
    }
}
