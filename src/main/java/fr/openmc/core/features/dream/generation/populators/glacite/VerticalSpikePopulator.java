package fr.openmc.core.features.dream.generation.populators.glacite;

import java.util.List;

public class VerticalSpikePopulator extends CavePopulator {
    public VerticalSpikePopulator() {
        super(0.4, 0.007, List.of(
                "glacite/spike_vertical_1",
                "glacite/spike_vertical_2",
                "glacite/spike_vertical_3",
                "glacite/spike_vertical_4",
                "glacite/spike_vertical_5"
        ));
    }
}

