package de.alex.goldenstom.game.events;

import de.alex.goldenstom.game.instances.DefaultInstance;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.PlayerEvent;

public class PlayerEvents {

    private static final Pos position = new Pos(
            Integer.getInteger("server.spawn.x", 0),
            Integer.getInteger("server.spawn.y", 0),
            Integer.getInteger("server.spawn.z", 0)
    );

    private static final Integer resetHeight = Integer.getInteger("server.reset.height", 0);

    public PlayerEvents() {
        EventNode<PlayerEvent> eventNode = EventNode.type("goldenstom-player-events", EventFilter.PLAYER);
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        eventNode.addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(DefaultInstance.INSTANCE);
            event.getPlayer().setRespawnPoint(position);
        });

        eventNode.addListener(PlayerSpawnEvent.class, playerSpawnEvent -> {
            playerSpawnEvent.getPlayer().teleport(position).thenRun(() -> {
                playerSpawnEvent.getPlayer().sendMessage("Willkommen auf GoldenMelon!");
            });
        });

        eventNode.addListener(PlayerBlockBreakEvent.class, playerBlockBreakEvent -> {
            if (playerBlockBreakEvent.getPlayer().getInstance() == DefaultInstance.INSTANCE)
                playerBlockBreakEvent.setCancelled(true);
        });

        eventNode.addListener(PlayerBlockPlaceEvent.class, playerBlockPlaceEvent -> {
            if (playerBlockPlaceEvent.getPlayer().getInstance() == DefaultInstance.INSTANCE)
                playerBlockPlaceEvent.setCancelled(true);
        });

        eventNode.addListener(PlayerMoveEvent.class, playerMoveEvent -> {
            if (playerMoveEvent.getNewPosition().y() < resetHeight)
                playerMoveEvent.setNewPosition(position);
        });

        eventNode.addListener(PlayerDeathEvent.class, playerDeathEvent -> {
            playerDeathEvent.setChatMessage(Component.empty());
        });
    }
}
