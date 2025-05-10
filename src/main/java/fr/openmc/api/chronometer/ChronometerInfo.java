package fr.openmc.api.chronometer;

import lombok.Getter;


@Getter
public class ChronometerInfo {
    private final String chronometerGroup;
    private final int chronometerTime;

    public ChronometerInfo(String chronometerGroup, int chronometerTime) {
        this.chronometerTime = chronometerTime;
        this.chronometerGroup = chronometerGroup;
    }
}