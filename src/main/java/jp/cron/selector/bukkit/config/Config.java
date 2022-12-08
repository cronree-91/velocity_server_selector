package jp.cron.selector.bukkit.config;

import jp.cron.selector.bukkit.BukkitPlugin;
import jp.cron.selector.bukkit.config.component.Server;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {
    BukkitPlugin pl;

    public List<Server> servers;

    public Config(BukkitPlugin pl) {
        this.pl = pl;
        this.servers = new ArrayList<>();
        reloadConfig();
    }

    public void reloadConfig() {
        pl.saveDefaultConfig();
        pl.reloadConfig();

        FileConfiguration config = pl.getConfig();

        config.getMapList("servers").forEach(map -> {
            try {
                String title = (String) map.get("title");
                String name = (String) map.get("name");
                servers.add(new Server(title, name));
            } catch (Exception e) {
                pl.getLogger().warning("Failed to load config: " + e.getMessage());
                e.printStackTrace();
            }
        });

        pl.getLogger().info("Config loaded.");

    }
}
