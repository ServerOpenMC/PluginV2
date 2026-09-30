package fr.openmc.riftengine.api.scanner.icons;

import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.utils.IdentifierUtils;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

public record IconEntry(
        String namespace,
        String key,
        Function<Path, Path> path,
        String permission,
        boolean showInGui,
        Double scaleRatio,
        Integer yPosition,
        Path sourceYml
) {
    public static IconEntry from(Path sourceYml, String namespace, String key, Map<?, ?> data) {
        String permission;

        if (data.get("permission") instanceof String perm) {
            permission = perm;
        } else if (data.get("permission") instanceof Number num) {
            permission = "ia.user.image.use." + num.intValue();
        } else {
            permission = null;
        }

        return new IconEntry(
                namespace,
                key,
                javaRoot -> IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(String.valueOf(data.get("path")), namespace)),
                permission,
                YmlUtils.getBool(data.get("show_in_gui"), true),
                YmlUtils.getDouble(data.get("scale_ratio"), null),
                YmlUtils.getInt(data.get("y_position"), null),
                sourceYml
        );
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}