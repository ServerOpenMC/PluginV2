package fr.openmc.core.features.city.models;

import fr.openmc.core.features.city.models.city.City;

import java.util.UUID;

public record CityInvite(UUID inviterUUID, City city) { }
