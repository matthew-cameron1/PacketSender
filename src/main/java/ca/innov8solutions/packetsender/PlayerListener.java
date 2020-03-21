package ca.innov8solutions.packetsender;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private PacketSender plugin;

    public PlayerListener(PacketSender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            plugin.addPacketListener(e.getPlayer());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        try {
            plugin.removePacketListener(e.getPlayer());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
