package ca.innov8solutions.packetsender;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private PacketSender plugin;

    public CommandHandler(PacketSender plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("sendpacket")) {

            if (!(commandSender instanceof Player)) {
                return true;
            }

            Player player = (Player) commandSender;
            player.sendMessage(ChatColor.GREEN + "Here comes the totem!");
            plugin.sendPacket(player);
        }
        return false;
    }
}
