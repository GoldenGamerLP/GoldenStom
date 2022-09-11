package de.alex.goldenstom.game.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class ServerInfoCommand extends Command {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ServerInfoCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(this::getRamUsage);
        });

        MinecraftServer.getCommandManager().register(this);
    }


    private @NotNull Component getRamUsage() {
        Runtime runtime = Runtime.getRuntime();

        double used = (runtime.totalMemory() - runtime.freeMemory()) / 1048576.0;
        double maxRam = runtime.maxMemory() / 1048576.0;
        int percentage = (int) Math.round((used / maxRam) * 100);

        String numbers = " " + decimalFormat.format(used) + "(MB)/" + decimalFormat.format(maxRam) + "(MB) (" + percentage + "%) ";
        int number = numbers.length();
        int maxLines = 100 - number;

        numbers = "|".repeat(maxLines / 2) + numbers + "|".repeat(maxLines / 2);

        TextComponent.Builder builder = Component.text().append();
        TagResolver placeholders = TagResolver.resolver(Placeholder.unparsed("first", numbers.substring(0, percentage)), Placeholder.unparsed("second", numbers.substring(percentage, numbers.length() - 1)));
        builder.append(miniMessage.deserialize("<gradient:dark_green:green><first></gradient>", placeholders));
        builder.append(miniMessage.deserialize("<gradient:red:dark_red><second></gradient>", placeholders));

        return builder.build();
    }
}
