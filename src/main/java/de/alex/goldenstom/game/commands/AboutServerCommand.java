package de.alex.goldenstom.game.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AboutServerCommand extends Command {

    private final MiniMessage miniMes = MiniMessage.miniMessage();

    private final String aboutMessage = "<gray>Made with <gold>Minestom <gray>and love from <gold>Alex/GG<gray>. \n <gray>Version: <gold><version>";

    public AboutServerCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(this::sendAbout);
        });

        MinecraftServer.getCommandManager().register(this);
    }

    private Component sendAbout() {
        TagResolver tagResolver = TagResolver.resolver(
                Placeholder.parsed("version", MinecraftServer.PROTOCOL_VERSION + "")
        );
        return miniMes.deserialize(aboutMessage, tagResolver);
    }


}
