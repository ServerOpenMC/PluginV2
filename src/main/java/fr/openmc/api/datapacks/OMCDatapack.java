package fr.openmc.api.datapacks;

import fr.openmc.api.datapacks.injectors.PackMetadataInjector;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class OMCDatapack {
    private final String packName;
    private final String namespace;
    private final Set<DatapackInjector> injectors = new HashSet<>();

    public OMCDatapack(String packName, String namespace) {
        this.packName = packName;
        this.namespace = namespace;
    }

    public void build(BootstrapContext context) throws IOException {
        Path tempDir = Files.createTempDirectory("datapacks-openmc");

        runInjector(tempDir, new PackMetadataInjector());

        for (DatapackInjector injector : injectors) {
            runInjector(tempDir, injector);
        }

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {
                        URI uri = tempDir.toUri();

                        event.registrar().discoverPack(uri, "openmc-injected");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ));
    }

    public void addInjector(DatapackInjector injector) {
        injectors.add(injector);
    }

    private static void runInjector(Path datapackRoot, DatapackInjector injector) {
        injector.inject(datapackRoot.toFile());
    }
}
