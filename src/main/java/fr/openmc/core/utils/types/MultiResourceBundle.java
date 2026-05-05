package fr.openmc.core.utils.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
            String basePath = folderName + "/" + localePath;
            Enumeration<URL> resources;
            try {
                resources = classLoader.getResources(basePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if (protocol.equals("file")) {
                                try {
                                    Path rootPath = Paths.get(url.toURI());
                                    if (!Files.exists(rootPath)) {
                                        continue;
                        }
                        try (var stream = Files.walk(rootPath)) {
                            stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".properties"))
                                    .forEach(path -> result.add(toBundleName(rootPath, path)));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (protocol.equals("jar")) {
                    try {
                        JarURLConnection connection = (JarURLConnection) url.openConnection();
                        try (JarFile jar = connection.getJarFile()) {
                            String prefix = connection.getEntryName();
                            if (prefix == null) {
                                prefix = basePath;
                            }
                            if (!prefix.endsWith("/")) {
                                prefix += "/";
                            }

                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (!entry.isDirectory() && name.startsWith(prefix) && name.endsWith(".properties")) {
                                    result.add(toBundleName(prefix, name));
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return result.stream().toList();
    }

    private String toBundleName(Path rootPath, Path filePath) {
        String relative = rootPath.relativize(filePath).toString().replace("\\", "/");
        return relative.substring(0, relative.length() - ".properties".length());
    }

    private String toBundleName(String prefix, String entryName) {
        String relative = entryName.substring(prefix.length());
        return relative.substring(0, relative.length() - ".properties".length());
    }
}