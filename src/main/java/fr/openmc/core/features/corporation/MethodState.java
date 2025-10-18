package fr.openmc.core.features.corporation;

import lombok.Getter;

@Getter
public enum MethodState {
    SUCCESS,
    WARNING,
    ERROR,
    FAILURE,
    ESCAPE,
    SPECIAL
}
