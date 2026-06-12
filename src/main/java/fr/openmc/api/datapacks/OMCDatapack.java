package fr.openmc.api.datapacks;

import fr.openmc.api.datapacks.injectors.PackMetadataInjector;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.datapack.DatapackManager;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.Bukkit;

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

    private final String ID_DATAPACK = "datapacks-openmc";
    private final DatapackManager DATAPACK_MANAGER = OMCPlugin.getInstance().getServer().getDatapackManager();

    public OMCDatapack(String packName, String namespace) {
        this.packName = packName;
        this.namespace = namespace;
    }

    public void buildBootstrap(BootstrapContext context, boolean generateFiles) throws IOException {
        Path dir;

        if (generateFiles) {
            dir = context.getDataDirectory().resolve(ID_DATAPACK);

            FilesUtils.deleteDirectory(dir.toFile());
            Files.createDirectories(dir);
        } else {
            dir = Files.createTempDirectory(ID_DATAPACK);
        }

        runInjector(dir, new PackMetadataInjector());

        for (DatapackInjector injector : injectors) {
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

    /**
     * Dangeureux à utiliser, veillez bien que serveur restart apres, car les registres nms de minecraft
     * (pas reloadable, ex worldgen/biome et worldgen/..) ne peuvent pas etre reload. et donc vous pourrez pas acceder à vos valeures
     * @param onBuilded execute une action lorsque le datapack est build (par ex redemarrer le serveur si y'a des registres pas reloadable affecté)
     * @throws IOException
     */
    public void buildRuntime(Runnable onBuilded) throws IOException {
        Path dir = OMCPlugin.getInstance().getServer().getLevelDirectory().resolve("datapacks").resolve(ID_DATAPACK);

        cleanup();
        Files.createDirectories(dir);

        runInjector(dir, new PackMetadataInjector());

        for (DatapackInjector injector : injectors) {
            runInjector(dir, injector);
        }

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), onBuilded, 20L);
    }

    public void cleanup() throws IOException {
        Path dir = OMCPlugin.getInstance().getServer().getLevelDirectory().resolve("datapacks").resolve(ID_DATAPACK);
        FilesUtils.deleteDirectory(dir.toFile());
    }

    public void addInjector(DatapackInjector injector) {
        injectors.add(injector);
    }

    public void addInjector(Iterable<DatapackInjector> injectors) {
        for (DatapackInjector injector : injectors) {
            addInjector(injector);
        }
    }

    private static void runInjector(Path datapackRoot, DatapackInjector injector) {
        injector.inject(datapackRoot.toFile());
    }
}
