package net.awesomepowered.signgis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Lax on 12/15/2017.
 */
public class SignCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("lesign.signer") && sender instanceof Player) {
            Player p = (Player) sender;
            if (SigngiS.leSigners.contains(p.getUniqueId())) {
                SigngiS.leSigners.remove(p.getUniqueId());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &cYou are no longer an active signer"));
            } else {
                SigngiS.leSigners.add(p.getUniqueId());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &aYou are now an active signer"));
            }

        }
        return false;
    }
}
