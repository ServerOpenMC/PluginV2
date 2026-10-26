package fr.openmc.core.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtils {
    public static JsonObject loadJson(Path path) {
        JsonObject root;
        try {
            root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
        } catch (IOException e) {
            return null;
        }
        return root;
    }

    public static JsonArray loadJsonArray(Path path) {
        JsonArray root;
        try {
            root = JsonParser.parseString(Files.readString(path)).getAsJsonArray();
        } catch (IOException e) {
            return null;
        }
        return root;
    }

    public static JsonObject parseJson(String content) {
        return JsonParser.parseString(content).getAsJsonObject();
    }

    public static JsonArray parseJsonArray(String content) {
        return JsonParser.parseString(content).getAsJsonArray();
    }

    public static String getString(JsonObject object, String key, String def) {
        return object.has(key) ? object.get(key).getAsString() : def;
    }

    public static Integer getInt(JsonObject object, String key, Integer def) {
        return object.has(key) ? object.get(key).getAsInt() : def;
    }

    public static Boolean getBool(JsonObject object, String key, Boolean def) {
        return object.has(key) ? object.get(key).getAsBoolean() : def;
    }

    public static Float getFloat(JsonObject object, String key, Float def) {
        return object.has(key) ? object.get(key).getAsFloat() : def;
    }

    public static JsonObject getJsonObject(JsonObject object, String key, JsonObject def) {
        return object.has(key) && object.get(key).isJsonObject() ? object.get(key).getAsJsonObject() : def;
    }

    public static JsonArray getJsonArray(JsonObject object, String key, JsonArray def) {
        return object.has(key) && object.get(key).isJsonArray() ? object.get(key).getAsJsonArray() : def;
    }
}
