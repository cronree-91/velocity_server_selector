package jp.cron.selector.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "server-selector", name = "ServerSelector", version = "1.0.0", description = "for minamikana", authors = {"cronree-91"})
public class VelocityPlugin {

    private final ProxyServer proxy;
    private final Logger logger;

    private final ChannelIdentifier customChannel = MinecraftChannelIdentifier.create("serverselector", "main");

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger) {
        this.proxy = server;
        this.logger = logger;

        logger.info("========= ServerSelector by cronree-91 ==========");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getCommandManager().unregister("server");
        proxy.getChannelRegistrar().register(customChannel);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("========= ServerSelector by cronree-91 ==========");
    }


    // Messaging Channel
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(customChannel)) {
            return;
        }

        if (event.getData().length == 0) {
            return;
        }

        if (!(event.getTarget() instanceof Player))
            return;
        Player p = (Player) event.getTarget();

        ByteArrayDataInput in = event.dataAsDataStream();
        String subChannel = in.readUTF();
        if (subChannel.equals("Ping")) {
            String serverName = in.readUTF();
            RegisteredServer server = proxy.getServer(serverName).orElseThrow(() -> new IllegalArgumentException("Server not found"));
            server.ping().thenAcceptAsync(ping -> {
                        ServerPing.Players players = ping.getPlayers().orElse(null);
                        boolean isOnline = players != null;

                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF(serverName);
                        out.writeUTF(isOnline ? "Online" : "Offline");

                        p.getCurrentServer().get().sendPluginMessage(customChannel, out.toByteArray());
                    })
                    .exceptionally(throwable -> {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF(serverName);
                        out.writeUTF("Offline");

                        p.getCurrentServer().get().sendPluginMessage(customChannel, out.toByteArray());
                        return null;
                    });
        } else if (subChannel.equals("Send")) {
            String serverName = in.readUTF();
            RegisteredServer server = proxy.getServer(serverName).orElseThrow(() -> new IllegalArgumentException("Server not found"));
            p.createConnectionRequest(server).fireAndForget();
        }
    }

}
