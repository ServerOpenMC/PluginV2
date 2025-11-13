package fr.openmc.mock;

import org.mockbukkit.mockbukkit.MockBukkit;

public final class MockBukkitHelper {
    public static ServerMock safeMock() {
        ServerMock server;

        if (MockBukkit.isMocked()) {
            server = (ServerMock) MockBukkit.getMock();
        } else {
            server = MockBukkit.mock(new ServerMock());
        }

        server.addSimpleWorld("world");

        return server;
    }

    public static void safeUnmock() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }
}
