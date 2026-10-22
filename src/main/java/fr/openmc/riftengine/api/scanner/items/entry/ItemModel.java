package fr.openmc.riftengine.api.scanner.items.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.openmc.core.utils.JsonUtils;
import fr.openmc.riftengine.api.utils.IdentifierUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record ItemModel(ItemInfo info, ItemResource resource, ItemGraphics graphics) {
    public List<String> texturesPath(Path javaRootPath) {
        List<String> texturesPath = new ArrayList<>();

        String model = getModel();

        if (model == null) return texturesPath;

        Path targetJson = IdentifierUtils.resolveModelId(javaRootPath, model);
        JsonObject rootModelJson = JsonUtils.loadJson(targetJson);

        if (rootModelJson == null) return texturesPath;

        JsonObject textures = rootModelJson.getAsJsonObject("textures");

        for (JsonElement textureName : textures.asMap().values()) {
            if (!(textureName.isJsonPrimitive())) continue;

            String textureNameStr = textureName.getAsString();
            if (textureNameStr.startsWith("_"))
                textureNameStr = textureNameStr.substring(1);

            texturesPath.add(textureNameStr);
        }
        return texturesPath;
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
        return IdentifierUtils.normalizeId(this.getModel(), info.namespace()); // ⚠ adapte
    }

}
