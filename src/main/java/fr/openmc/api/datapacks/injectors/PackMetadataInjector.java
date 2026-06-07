package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PackMetadataInjector implements DatapackInjector {
    private static final double[] PACK_FORMAT = new double[] {101.1, 1};

    @Override
    public void inject(File rootFile) {
        Path root = rootFile.toPath();
        try {
            Path metaDataFile = root.resolve("pack.mcmeta");
            Files.writeString(metaDataFile, packMcMeta());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write pack mcmeta file", e);
        }
    }

    private String packMcMeta() {
        return String.format("""
                {
                  "pack": {
                    "description": "OMC datapack injected from plugin",
                    "pack_format": %s,
                    "min_format": [%s, %s],
                    "max_format": [%s, %s]
                  }
                }
                """, PACK_FORMAT[0], PACK_FORMAT[0], PACK_FORMAT[1], PACK_FORMAT[0], PACK_FORMAT[1]);
    }
}
