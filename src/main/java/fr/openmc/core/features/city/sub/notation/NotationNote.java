package fr.openmc.core.features.city.sub.notation;

import lombok.Getter;

@Getter
public enum NotationNote {
    NOTE_ARCHITECTURAL(30),
    NOTE_COHERENCE(5);
    private final int maxNote;


    NotationNote(int maxNote) {
        this.maxNote = maxNote;
    }
}
