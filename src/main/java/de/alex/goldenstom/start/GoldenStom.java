package de.alex.goldenstom.start;

import de.alex.goldenstom.game.commands.AboutServerCommand;
import de.alex.goldenstom.game.commands.ServerInfoCommand;
import de.alex.goldenstom.game.commands.ExtensionManager;
import de.alex.goldenstom.game.commands.StopServerCommand;
import de.alex.goldenstom.game.events.PlayerEvents;
import eu.cloudnetservice.driver.CloudNetDriver;
import eu.cloudnetservice.driver.network.HostAndPort;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.extras.velocity.VelocityProxy;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

/*
server.port
server.ip
server.secret

-- Custom Lobby
server.lobby
server.reset.height
server.spawn.x
server.spawn.y
server.spawn.z

server.world
 */
public class GoldenStom {


    private static final Logger LOGGER = LoggerFactory.getLogger(GoldenStom.class);
    private static final String SERVER_ADDRESS = "server.ip";
    private static final String SERVER_PORT = "server.port";
    private static final String SERVER_SECRET = "server.secret";
    private static MinecraftServer minecraftServer;
    private static Properties currentConfig;

    public static void main(String[] args) {
        long ms = System.currentTimeMillis();
        //Config
        try {
            loadConfig();
        } catch (IOException e) {
            LOGGER.error("Error occoured while loading config", e);
            return;
        }


        //CloudNet
        if(enableCloudNet()) {
            LOGGER.info("Enabling Cloudnet Support. Overriding IP/Port of server.");
        }

        //Velocity
        String velocitySecret = System.getProperty(SERVER_SECRET);
        if (velocitySecret != null) {
            LOGGER.info("Enabling Velocity support.");
            VelocityProxy.enable(velocitySecret);
        }


        //Init Server and Register CMDs etc
        minecraftServer = MinecraftServer.init();

        //Lobby Innit
        enableGoldenStomExtensions();

        new ExtensionManager("extensionmanager", "exm", "ex");
        new ServerInfoCommand("serverinfo");
        new AboutServerCommand("about","version");
        new StopServerCommand("stop","end");

        LOGGER.info("Started Internals in {}ms", System.currentTimeMillis() - ms);

        minecraftServer.start(
                System.getProperty(SERVER_ADDRESS, "0.0.0.0"),
                Integer.getInteger(SERVER_PORT, 25565)
        );
    }

    private static void enableGoldenStomExtensions() {
        if (Boolean.getBoolean("server.lobby")) new PlayerEvents();
    }

    private static boolean enableCloudNet() {
        try {
            Class.forName(CloudNetDriver.class.getName());
        } catch (ClassNotFoundException e) {
            return false;
        }

        CloudNetDriver cloudNet = CloudNetDriver.instance();

        if (cloudNet == null) {
            return false;
        }

        String thisServiceName = cloudNet.componentName();
        @Nullable ServiceInfoSnapshot service = cloudNet.cloudServiceProvider().serviceByName(thisServiceName);

        if (service == null) {
            return false;
        }


        HostAndPort hostAndPort = service.address();
        System.setProperty(SERVER_PORT, String.valueOf(hostAndPort.port()));
        System.setProperty(SERVER_ADDRESS, hostAndPort.host());
        LOGGER.info("Opening via CloudNet on Address: {}:{}", hostAndPort.host(), hostAndPort.port());
        return true;
    }

    private static void loadConfig() throws IOException {
        final Properties properties = new Properties();
        final File configFile = Paths.get("server.properties").toAbsolutePath().toFile();

        if (!configFile.exists()) {
            LOGGER.info("Creating new Config file.");
            configFile.createNewFile();
            properties.store(new FileWriter(configFile), "Used to add Properties. Get Properties via System.getProperties(). \n Default Properties are: server.port, adress,secret");
        }


        properties.load(new FastBufferedInputStream(new FileInputStream(configFile)));

        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            String one = (String) objectObjectEntry.getKey();
            String two = (String) objectObjectEntry.getValue();

            if (System.getProperty(one) == null) System.setProperty(one, two);
            LOGGER.info("Proptery added: {}, {}", one, two);
        }
        currentConfig = properties;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public static Properties getCurrentConfig() {
        return currentConfig;
    }
}
