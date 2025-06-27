package fr.openmc.mockbukkit;

import fr.openmc.mockbukkit.util.UnsafeValuesMock;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.mockbukkit.mockbukkit.command.CommandMapMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

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

    public PlayerMock addPlayerWithPermissions(Plugin plugin, String ...perms) {
        PlayerMock player = this.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);

        for (String perm : perms) {
            attachment.setPermission(perm, true);
        }

        return player;
    }
}
