package fr.openmc.riftengine.api.scanner.general;

import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.util.Map;

public class YamlNamespaceIAScanner extends AbstractScanner<String, Map<String, Object>> {
    @Override
    public String scan(Map<String, Object> rootYaml) {
        if (rootYaml.get("info") instanceof Map<?, ?> infoMap && infoMap.get("namespace") != null) {
            return infoMap.get("namespace").toString();
        }

        return null;
    }
}
