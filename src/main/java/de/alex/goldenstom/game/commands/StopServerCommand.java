package de.alex.goldenstom.game.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StopServerCommand extends Command {

    private final String kickMessage = "<red>Shutting down.";

    public StopServerCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);

        setCondition((sender, commandString) ->
                sender instanceof ConsoleSender ||
                        sender.hasPermission("server.stop")
        );

        setDefaultExecutor((sender, context) -> {
            Component kickmsg = MiniMessage.miniMessage().deserialize(kickMessage);
            MinecraftServer
                    .getConnectionManager()
                    .getOnlinePlayers()
                    .forEach(player -> player.kick(kickmsg));
            MinecraftServer.stopCleanly();
        });
        MinecraftServer.getCommandManager().register(this);
    }
}
