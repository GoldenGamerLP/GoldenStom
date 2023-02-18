package de.alex.goldenstom.game.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.extensions.DiscoveredExtension;
import net.minestom.server.extensions.Extension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;


public class ExtensionManager extends Command {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final String extensionInfo = "<gray><hover:show_text:'<extensioninfo>'><status><extenionname><gray>";
    private final String extensionHoverInfo = """
            Name: <name>\s
            Author: <author>\s
            Entry-Point: <entrypoint>\s
            Depend: <depend>""";

    public ExtensionManager(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);

        //extensions list
        var list = ArgumentType.Literal("list");


        addSyntax((sender, context) -> {
            TextComponent.Builder text = Component.text();
            text.append(miniMessage.deserialize("<gray>There were <green><size><gray> extensions loaded: "));

            List<Extension> extensions = MinecraftServer.getExtensionManager().getExtensions().stream().toList();

            if (extensions.size() == 0) {
                text.append(miniMessage.deserialize("<gray>None"));
                return;
            }

            int max = extensions.size() - 1;
            for (int i = 0; i < extensions.size(); i++) {
                Extension extension = extensions.get(i);
                DiscoveredExtension origin = extension.getOrigin();

                TagResolver placeholders1 = TagResolver.resolver(
                        Placeholder.unparsed("name", origin.getName()),
                        Placeholder.unparsed("author", Arrays.toString(origin.getAuthors())),
                        Placeholder.unparsed("entrypoint", origin.getEntrypoint()),
                        Placeholder.unparsed("depend", Arrays.toString(extension.getDependents().toArray())));

                Component exHoverInfo = miniMessage.deserialize(extensionHoverInfo, placeholders1);


                TagResolver placeholders2 = TagResolver.resolver(
                        Placeholder.component("extensioninfo", exHoverInfo),
                        Placeholder.unparsed("extenionname", origin.getName()),
                        //Atm there are only enabled plugins in the list, maybe later disabled plugins too?
                        Placeholder.parsed("status", "<green>")
                );
                Component exInfo = miniMessage.deserialize(extensionInfo, placeholders2);

                text.append(exInfo);

                if (i == max) text.append(Component.text("."));
                else text.append(Component.text(", "));
            }

            sender.sendMessage(text.asComponent());
        }, list);


        MinecraftServer.getCommandManager().register(this);
    }
}
