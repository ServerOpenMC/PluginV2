package fr.openmc.api.datapacks.builders;

import com.google.gson.JsonObject;

// * Interface qui permet au classes de reconnaitre que c'est un builder de contenu
public interface ContentBuilder {
    JsonObject toJson();
}
