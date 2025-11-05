package fr.openmc.mock;

import org.jetbrains.annotations.NotNull;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.command.CommandMapMock;

public class ServerMock extends org.mockbukkit.mockbukkit.ServerMock {
    public CommandMapMock commandMap;

    public ServerMock() {
        super();

        this.commandMap = new CommandMapMock(this);
    }

    @Override
    public @NotNull CommandMapMock getCommandMap() {
        return this.commandMap;
    }

    @Override
    public boolean isStopping() {
        return MockBukkit.isMocked();
    }
}
