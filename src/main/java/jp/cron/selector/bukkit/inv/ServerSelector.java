package jp.cron.selector.bukkit.inv;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import jp.cron.selector.bukkit.BukkitPlugin;
import jp.cron.selector.bukkit.config.component.Server;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ServerSelector implements Listener {

    private final Inventory inv;
    private final BukkitPlugin pl;
    private final Player p;
    private final List<Integer> SLOTS = Arrays.asList(19, 21, 23, 25, 37, 38);

    public ServerSelector(BukkitPlugin pl, Player p) {
        this.pl = pl;
        this.p = p;
        inv = Bukkit.createInventory(null, 54, "移動するサーバを選択してください");

        int i = 0;

        for (Server server : pl.config.servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Ping");
            out.writeUTF(server.name);
            p.sendPluginMessage(pl, "serverselector:main", out.toByteArray());

            ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a§l" + server.title);
            meta.setLore(Arrays.asList(server.name, "Loading..."));
            item.setItemMeta(meta);

            inv.setItem(SLOTS.get(i), item);
            i++;
        }

    }

    public void open() {
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getView().getTopInventory().equals(inv)) return;
        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();

        if (item==null)
            return;

        if (item.getItemMeta()==null || !item.getItemMeta().hasLore())
            return;

        if (item.getType() == Material.GRASS_BLOCK) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Send");
            out.writeUTF(item.getItemMeta().getLore().get(0));

            p.sendPluginMessage(pl, "serverselector:main", out.toByteArray());

            p.sendMessage("§a§lサーバに移動します...");
        } else if (item.getType() == Material.BARRIER) {

            p.sendMessage("§c§l現在移動できません");

        }

    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (!e.getView().getTopInventory().equals(inv)) return;
        e.setCancelled(true);
    }

    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("serverselector:main")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String serverName = in.readUTF();
        String status = in.readUTF();

        for (Integer slot : SLOTS) {
            ItemStack item = inv.getItem(slot);
            if (item==null)
                continue;
            ItemMeta meta = item.getItemMeta();
            String name = meta.getLore().get(0);
            if (name.equals(serverName)) {
                if (status.equalsIgnoreCase("Online")) {
                    item.setType(Material.GRASS_BLOCK);
                    meta.setLore(Arrays.asList(name, "§a§lオンライン"));
                } else {
                    item.setType(Material.BARRIER);
                    meta.setLore(Arrays.asList(name, "§a§lオフライン"));
                }
                item.setItemMeta(meta);
            }
        }

    }
}
