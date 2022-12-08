package jp.cron.selector.bukkit;

import co.aikar.commands.PaperCommandManager;
import jp.cron.selector.bukkit.command.ServerCommand;
import jp.cron.selector.bukkit.config.Config;
import jp.cron.selector.bukkit.inv.ServerSelector;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public final class BukkitPlugin extends JavaPlugin implements PluginMessageListener {

    public Config config;

    public List<ServerSelector> selectors = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("========================= " + getName() + " v" + getDescription().getVersion() + " =========================");
        getLogger().info("  Developed by cronree-91");

        config = new Config(this);

        PaperCommandManager manager = new PaperCommandManager(this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "serverselector:main");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeecord:main");

        getServer().getMessenger().registerIncomingPluginChannel(this, "serverselector:main", this);

        manager.registerCommand(new ServerCommand());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        for (ServerSelector selector : selectors) {
            selector.onPluginMessageReceived(channel, player, message);
        }
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }
}
