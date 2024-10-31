package fr.openmc.core.utils.serializer;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.*;

public class LocationsTests {
    ServerMock server;
    World world;
    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        world = server.getWorld("world"); //  Docs: "Every time MockBukkit is started a world called “world” is automatically created."
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Serialize XYZ")
    void serializeXYZ() {
        Assertions.assertEquals(
                "5894,69;946612,51;1651,52;0,00;0,00",
                LocationSerializer.serialize(5894.694, 946612.51, 1651.5166154651135)
        );
    }

    @Test
    @DisplayName("Serialize XYZ;YP")
    void serializeXYZwithYP() {
        Assertions.assertEquals(
                "5894,69;139810,10;11319,00;95,16;10,59",
                LocationSerializer.serialize(5894.694, 139810.1, 11319, 95.159, 10.59)
        );
    }

    @Test
    @DisplayName("Deserialize invalid data")
    void deserializeInvalidData() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            LocationSerializer.deserialize("11;;ç9U2;1S?KO/////", world);
        });
    }

    @Test
    @DisplayName("Deserialize data (XYZ)")
    void deserializeData() {
        Assertions.assertEquals(
                new Location(world, 130118,71521,2001, (float) 10.20,13),
                LocationSerializer.deserialize("130118;071521;2001;10.20;13", world)
        );
    }
}
