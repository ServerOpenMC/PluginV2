package fr.openmc.riftengine.api.registry.scanner;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.riftengine.api.scanner.blocks.BlockStateScanner;
import fr.openmc.riftengine.api.scanner.general.YamlNamespaceIAScanner;
import fr.openmc.riftengine.api.scanner.general.YamlScanner;
import fr.openmc.riftengine.api.scanner.icons.IconScanner;
import fr.openmc.riftengine.api.scanner.items.CustomModelDataScanner;
import fr.openmc.riftengine.api.scanner.items.ItemScanner;
import fr.openmc.riftengine.api.scanner.sounds.SoundScanner;

public class ScannerRegistry extends Registry<String, AbstractScanner<?, ?>>
    implements KeyedRegistry<String, AbstractScanner<?, ?>> {

    public final SoundScanner SOUNDS = register(new SoundScanner());

    public final IconScanner ICONS = register(new IconScanner());

    public final ItemScanner ITEMS = register(new ItemScanner());

    public final CustomModelDataScanner CUSTOM_MODEL_DATA_CACHE = register(new CustomModelDataScanner());
    public final BlockStateScanner BLOCKSTATES_CACHE = register(new BlockStateScanner());

    public final YamlNamespaceIAScanner YAML_ITEMSADDER_NAMESPACE = register(new YamlNamespaceIAScanner());
    public final YamlScanner YAML = register(new YamlScanner());

    @Override
    public String key(AbstractScanner<?, ?> abstractScanner) {
        return abstractScanner.getClass().getSimpleName();
    }
}