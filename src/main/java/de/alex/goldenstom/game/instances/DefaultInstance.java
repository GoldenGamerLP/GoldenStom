package de.alex.goldenstom.game.instances;

import dev.hypera.scaffolding.Scaffolding;
import dev.hypera.scaffolding.instance.SchematicChunkLoader;
import dev.hypera.scaffolding.schematic.Schematic;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jglrxavpok.hephaistos.nbt.NBTException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class DefaultInstance {

    public static final DimensionType DEFAULT_DIM;
    public static final InstanceContainer INSTANCE;

    private static volatile boolean wasBuild = false;

    static {
        NamespaceID namespaceID = NamespaceID.from("goldenstom:dimension");
        DEFAULT_DIM = DimensionType.builder(namespaceID)
                .ambientLight(2F)
                .fixedTime(6000L)
                .minY(-64)
                .height(384)
                .logicalHeight(384)
                .natural(false)
                .skylightEnabled(false)
                .build();

        MinecraftServer.getDimensionTypeManager().addDimension(DEFAULT_DIM);

        INSTANCE = MinecraftServer
                .getInstanceManager()
                .createInstanceContainer(DEFAULT_DIM);


        File schem = Paths.get(System.getProperty("server.schematic", "spawn.schematic")).toAbsolutePath().toFile();

        try {
            Scaffolding.fromFile(schem).thenAcceptAsync(schematic -> {
                INSTANCE.setChunkLoader(
                        SchematicChunkLoader
                                .builder()
                                .addSchematic(schematic)
                                .offset(0, 0, 0)
                                .build()
                );
            });
        } catch (IOException | NBTException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Void> innitialize() {
        if (wasBuild) return CompletableFuture.completedFuture(null);
        else wasBuild = true;

        return CompletableFuture.runAsync(() -> {
            File schem = Paths.get(System.getProperty("server.schematic", "spawn.schematic")).toAbsolutePath().toFile();

            Schematic schematic;
            try {
                schematic = Scaffolding.fromFile(schem).join();
            } catch (IOException | NBTException e) {
                throw new RuntimeException(e);
            }

            schematic.build(INSTANCE, Pos.ZERO).join();
        });
    }
}
