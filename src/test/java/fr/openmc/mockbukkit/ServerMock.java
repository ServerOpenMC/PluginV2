package fr.openmc.mockbukkit;

import fr.openmc.mockbukkit.util.UnsafeValuesMock;
import org.jetbrains.annotations.NotNull;
import org.mockbukkit.mockbukkit.command.CommandMapMock;

public class ServerMock extends org.mockbukkit.mockbukkit.ServerMock {
    private UnsafeValuesMock unsafe = new UnsafeValuesMock();
    public CommandMapMock commandMap;

    public ServerMock() {
        super();

        this.commandMap = new CommandMapMock(this);
    }

    @Override
    public @NotNull CommandMapMock getCommandMap() {
        return commandMap;
    }

    @Override
    public @NotNull UnsafeValuesMock getUnsafe() {
        return unsafe;
    }
}
