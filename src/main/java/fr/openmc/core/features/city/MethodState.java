package fr.openmc.core.features.city;

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
