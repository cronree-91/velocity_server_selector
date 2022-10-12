package jp.cron.sample.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("test")
public class TestCommand extends BaseCommand {
    @Default
    public void test(Player player, String string) {
        player.sendMessage("test " + string);
    }
}
