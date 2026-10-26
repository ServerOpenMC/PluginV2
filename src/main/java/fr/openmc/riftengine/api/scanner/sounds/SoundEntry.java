package fr.openmc.riftengine.api.scanner.sounds;

import java.util.List;

public record SoundEntry(String namespace, String path, List<String> soundResourcesId) {
    public String toBedrockEventId() {
        return namespace + "." + path;
    }

    public String toJavaPath(String defaultNamespace, String resourceId) {
        String namespace = defaultNamespace;
        String path = resourceId;
        String[] split = resourceId.split(":");
        if (split.length == 2) {
            namespace = split[0];
            path = split[1];
        }

        return "assets/" + namespace + "/sounds/" + path;
    }

    /**
     * omc_sounds:ambient/join_rift -> sounds/omc_sounds/ambient/join_rift
     */
    public String toBedrockPath(String defaultNamespace, String resourceId) {
        String namespace = defaultNamespace;
        String path = resourceId;

        String[] split = resourceId.split(":");
        if (split.length == 2) {
            namespace = split[0];
            path = split[1];
        }
        return "sounds/" + namespace + "/" + path;
    }
}