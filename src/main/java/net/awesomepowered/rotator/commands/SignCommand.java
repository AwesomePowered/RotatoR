package net.awesomepowered.rotator.commands;

import net.awesomepowered.rotator.RotatoR;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Lax on 12/15/2017.
 */
public class SignCommand implements CommandExecutor {

    private RotatoR plugin;

    public SignCommand(RotatoR rotatoR) {
        this.plugin = rotatoR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("rotator.signer") && sender instanceof Player) {
            Player p = (Player) sender;
            if (plugin.leSigners.containsKey(p.getUniqueId())) {
                plugin.leSigners.remove(p.getUniqueId());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7] &cYou are no longer an active signer"));
            } else {
                plugin.leSigners.put(p.getUniqueId(), null);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7] &aYou are now an active signer"));
            }
        }
        return false;
    }
}
