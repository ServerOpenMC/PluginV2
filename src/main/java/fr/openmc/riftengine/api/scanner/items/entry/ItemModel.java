package fr.openmc.riftengine.api.scanner.items.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.openmc.core.utils.JsonUtils;
import fr.openmc.riftengine.api.utils.IdentifierUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record ItemModel(ItemInfo info, ItemResource resource, ItemGraphics graphics) {
    public List<String> texturesPath(Path javaRootPath) {
        List<String> texturesPath = new ArrayList<>();

        String model = getModel();

        if (model == null) return texturesPath;

        Path targetJson = IdentifierUtils.resolveModelId(javaRootPath, info.namespace(), model);
        JsonObject rootModelJson = JsonUtils.loadJson(targetJson);

        if (rootModelJson == null) return texturesPath;

        JsonObject textures = rootModelJson.getAsJsonObject("textures");

        if (textures == null) return texturesPath;
        Map<String, JsonElement> texturesMap = textures.asMap();

        if (texturesMap == null) return texturesPath;

        for (JsonElement textureName : texturesMap.values()) {
            String textureNameStr = textureName.getAsString();
            if (textureNameStr.startsWith("#")) continue;
            if (textureNameStr.startsWith("_"))
                textureNameStr = textureNameStr.substring(1);

            String[] textureSplit = textureNameStr.split(":");

            texturesPath.add(textureSplit[1]);
        }
        return texturesPath;
    }

    public String namespace() {
        return this.info.namespace();
    }

    /**
     * ex omc_dream:orb/orb_cloud en models/entity/attachable/omc_dream/orb/orb_cloud.geo.json
     */
    public String toBedrockModelPath() {
        String[] id = IdentifierUtils.normalizeId(modelId()).split(":");
        return "models/entity/attachable/" + id[0] + "/" + id[1] + ".geo.json";
    }

    /**
     * ex omc_dream:orb/orb_cloud en geometry.omc_dream.orb.orb_cloud
     */
    public String toBedrockGeometryId() {
        String[] id = IdentifierUtils.normalizeId(modelId()).split(":");
        return "geometry." + id[0] + "." + id[1].replace('/', '.');
    }

    /**
     * ex omc_dream:orb/orb_cloud en textures/omc_dream/atlas/orb_cloud
     */
    public String toBedrockAtlasPath() {
        String[] id = IdentifierUtils.normalizeId(modelId()).split(":");
        return "textures/" + id[0] + "/atlas/" + id[1];
    }

    public String toBedrockAnimationId() {
        String[] id = IdentifierUtils.normalizeId(modelId()).split(":");
        return "animation." + id[0] + "." + id[1].replace('/', '.');
    }

    public String toBedrockAnimationPath() {
        String[] id = IdentifierUtils.normalizeId(modelId()).split(":");
        return "animations/" + id[0] + "/" + id[1] + ".animation.json";
    }

    public boolean hasModel() {
        return getModel() != null;
    }

    public String getModel() {
        if (graphics != null && graphics.hasModel()) {
            return graphics.model();
        } else if (resource != null && resource.hasModel()) {
            return resource.model();
        }
        return null;
    }

    /**
     * @return l'id du model avec namespace, ex omc_dream:orb/orb_cloud
     */
    public String modelId() {
        if (getModel() == null) return null;
        return IdentifierUtils.normalizeId(this.getModel(), info.namespace());
    }
}
