package fr.openmc.api.datapacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public interface DatapackInjector {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    void inject(File rootFile);
}
