package fr.openmc.core.features.dream.generation.populators.glacite;

import java.util.List;

public class GroundSpikePopulator extends CavePopulator {
    public GroundSpikePopulator() {
        super(0.8, 0.03, List.of(
                "glacite/spike_normal_1",
                "glacite/spike_normal_2",
                "glacite/spike_normal_3",
                "glacite/spike_normal_4"
        ));
    }
}

