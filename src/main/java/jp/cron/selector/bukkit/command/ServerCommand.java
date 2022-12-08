package jp.cron.selector.bukkit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import jp.cron.selector.bukkit.BukkitPlugin;
import jp.cron.selector.bukkit.inv.ServerSelector;
import org.bukkit.entity.Player;

@CommandAlias("server")
@CommandPermission("selector.command")
public class ServerCommand extends BaseCommand {

    @Dependency
    private BukkitPlugin pl;

    @Default
    public void server(Player p) {
        ServerSelector serverSelector = new ServerSelector(pl, p);
        this.pl.getServer().getPluginManager().registerEvents(serverSelector, this.pl);
        this.pl.selectors.add(serverSelector);
        serverSelector.open();
    }
}
