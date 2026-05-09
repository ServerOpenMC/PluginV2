package fr.openmc.core.utils.types;

import fr.openmc.core.utils.FilesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultiResourceBundle {

    private final String folderName;
    private final Locale locale;
    private final ClassLoader classLoader;

    private final Map<String, Map<String, String>> cache = new HashMap<>();

    public MultiResourceBundle(String folderName, Locale locale) {
        this(folderName, locale, MultiResourceBundle.class.getClassLoader());
    }

    public MultiResourceBundle(String folderName, Locale locale, ClassLoader classLoader) {
        this.folderName = folderName;
        this.locale = locale;
        this.classLoader = classLoader;
    }

    public Set<String> getKeys(String bundleName) {
        return getBundle(bundleName).keySet();
    }

    public String getString(String bundleName, String key) {
        return getBundle(bundleName).get(key);
    }

    public Map<String, String> getAllTranslations() {
        Map<String, String> result = new HashMap<>();

        for (String bundleName : findBundleNames()) {
            result.putAll(getBundle(bundleName));
        }

        return result;
    }

    private Map<String, String> getBundle(String name) {
        return cache.computeIfAbsent(name, this::loadBundle);
    }

    private Map<String, String> loadBundle(String name) {
        Map<String, String> result = new HashMap<>();

        for (String localePath : getLocaleHierarchy()) {
            String path = folderName + "/" + localePath + "/" + name + ".properties";
            Properties properties = loadProperties(path);
            if (properties == null) {
                continue;
            }

            for (String key : properties.stringPropertyNames()) {
                result.put(key, properties.getProperty(key));
            }
        }

        return result;
    }

    private Properties loadProperties(String resourcePath) {
        try (InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return null;
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getLocaleHierarchy() {
        List<String> locales = new ArrayList<>();

        locales.add("default");
        if (!locale.getCountry().isEmpty()) {
            locales.add(locale.toString());
        }

        return locales;
    }

    private List<String> findBundleNames() {
        Set<String> result = new HashSet<>();

        for (String localePath : getLocaleHierarchy()) {
            for (String fileName : FilesUtils.listFileNamesInResource(folderName + "/" + localePath)) {
                result.add(toBundleName(fileName));
            }
        }

        return result.stream().toList();
    }

    private String toBundleName(String entryName) {
        return entryName.substring(0, entryName.length() - ".properties".length());
    }
}