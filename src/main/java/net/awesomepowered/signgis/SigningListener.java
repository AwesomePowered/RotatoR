package net.awesomepowered.signgis;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SigningListener implements Listener {

    private SigngiS plugin;

    public SigningListener(SigngiS signgiS) {
        this.plugin = signgiS;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent ev) {
        if (!plugin.leSigners.contains(ev.getPlayer().getUniqueId())) {
            plugin.debug("Interact","was called but player is not a signer");
            return;
        }
        ev.setCancelled(true);
        if (ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            plugin.debug("Interact", "was called but is not a right click");
            return;
        }
        if (plugin.leSign.containsKey(ev.getClickedBlock().getLocation())) {
            plugin.debug("Interact", "was called on an signed sign, unsigning..");
            plugin.leSign.get(ev.getClickedBlock().getLocation()).selfDestruct();
            ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &cThe sign is no longer signed"));
            return;
        }
        if (ev.getClickedBlock() == null || ev.getClickedBlock().getType() != Material.SIGN_POST) {
            plugin.debug("Interact","was called but block is not a SIGN_POST");
            return;
        }
        plugin.debug("Interact","Making a LeSign object");
        LeSign sign = new LeSign((Sign) ev.getClickedBlock().getState(), 0,0, plugin.rpm);
        plugin.leSign.put(sign.getSign().getLocation(), sign);
        sign.setMode((ev.getPlayer().isSneaking()) ? 1 : 0);
        sign.spoolUp();
        ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &aYou have signed a sign"));
        plugin.debug("Interact","tried to spool sign");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        if (!plugin.leSigners.contains(ev.getPlayer().getUniqueId())) {
            plugin.debug("Chat","was called but player is not a signer");
            return;
        }
        String message = ev.getMessage();
        if (message.equalsIgnoreCase("exit")) {
            plugin.debug("Chat","exit was called. Player no longer a signer");
            plugin.leSigners.remove(ev.getPlayer().getUniqueId());
            ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &cYou are no longer an active signer"));
            ev.setCancelled(true);
            return;
        }
        if (StringUtils.isNumeric(ev.getMessage())) {
            plugin.debug("Chat","was called and the message is numeric", message, "RPM set");
            plugin.rpm = Integer.valueOf(message);
            ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &aYou have set the RPM to &b" + ev.getMessage()));
            ev.setCancelled(true);
        } else {
            ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &aYou are currently a signer. Type: &cexit &ato exit."));
        }
    }

}
