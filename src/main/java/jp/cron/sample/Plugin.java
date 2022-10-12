package jp.cron.sample;

import jp.cron.sample.command.TestCommand;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class Plugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("========================= " + getName() + " v" + getDescription().getVersion() + " =========================");
        getLogger().info("  Developed by cronree-91");
        this.saveDefaultConfig();
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new TestCommand());
    }
}
