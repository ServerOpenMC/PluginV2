package fr.openmc.api.datapacks;

import fr.openmc.api.datapacks.injectors.PackMetadataInjector;
import fr.openmc.core.utils.FilesUtils;
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

    public void build(BootstrapContext context, boolean generateFiles) throws IOException {
        String idDatapacks = "datapacks-openmc";
        Path dir;

        if (generateFiles) {
            dir = context.getDataDirectory().resolve(idDatapacks);

            FilesUtils.deleteDirectory(dir.toFile());
            Files.createDirectories(dir);
        } else {
            dir = Files.createTempDirectory(idDatapacks);
        }

        runInjector(dir, new PackMetadataInjector());

        for (DatapackInjector injector : injectors) {
            System.out.println(injector.getClass().getSimpleName());
            runInjector(dir, injector);
        }

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {
                        URI uri = dir.toUri();

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
