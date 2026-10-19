package fr.openmc.riftengine.api.scanner.items.entry;

public record ItemInfo(String namespace,
                       String key) {
    public static ItemInfo from(String namespace,
                                String key) {
        return new ItemInfo(namespace, key);
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}
