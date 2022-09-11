package de.alex.goldenstom.game.instances;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class DefaultInstance {

    public static final DimensionType DEFAULT_DIM;
    public static final Instance INSTANCE;

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
                .createInstanceContainer(
                        DEFAULT_DIM,
                        new AnvilLoader(System.getProperty("server.world", "world"))
                );
        INSTANCE.enableAutoChunkLoad(true);
    }

}
