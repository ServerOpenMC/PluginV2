package fr.openmc.api.datapacks.builders.dimensions;

import com.google.gson.JsonObject;

// * Interface qui permet au classes de reconnaitre que c'est un dimension builder
public interface DimensionBuilder {
    JsonObject toJson();
}
