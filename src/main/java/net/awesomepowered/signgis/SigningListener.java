package net.awesomepowered.signgis;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
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
        if (ev.getAction() == Action.LEFT_CLICK_BLOCK && plugin.leSign.containsKey(ev.getClickedBlock().getLocation())) {
            plugin.debug("Interact L", "on a signed sign, selecting.");
            plugin.selected = plugin.leSign.get(ev.getClickedBlock().getLocation());
            sendMessage(ev.getPlayer(), "&7[SigngiS] &aYou have selected a signed sign");
            return;
        }
        if (ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            plugin.debug("Interact", "was called but is not a right click");
            return;
        }
        if (plugin.leSign.containsKey(ev.getClickedBlock().getLocation())) {
            plugin.debug("Interact", "was called on an signed sign, unsigning..");
            plugin.leSign.get(ev.getClickedBlock().getLocation()).selfDestruct();
            plugin.selected = null;
            sendMessage(ev.getPlayer(), "&7[SigngiS] &cThe sign is no longer signed");
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
        plugin.selected = sign;
        sendMessage(ev.getPlayer(), "&7[SigngiS] &aYou have signed a sign");
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
            plugin.selected = null;
            sendMessage(ev.getPlayer(), "&7[SigngiS] &cYou are no longer an active signer");
            ev.setCancelled(true);
            return;
        }
        if (message.toLowerCase().startsWith("stop") && plugin.selected != null && message.split(" ").length == 2) {
            plugin.debug("Chat", "Stop is called with: " + message);
            parseClears(message.split(" ")[1]);
            ev.setCancelled(true);
            return;
        }
        if (trySound(message) && plugin.selected != null) {
            plugin.selected.setSound(message.toUpperCase());
            plugin.debug("Chat","was called and the message is SOUND", message, "sound set");
            sendMessage(ev.getPlayer(), "&7[SigngiS] &aYou have set the sound to &b" + message.toUpperCase());
            ev.setCancelled(true);
            return;
        }
        if (tryEffect(message) && plugin.selected != null) {
            plugin.selected.setEffect(message.toUpperCase());
            plugin.debug("Chat","was called and the message is EFFECT", message, "effect set");
            sendMessage(ev.getPlayer(), "&7[SigngiS] &aYou have set the effect to &b" + message.toUpperCase());
            ev.setCancelled(true);
            return;
        }
        if (StringUtils.isNumeric(ev.getMessage())) {
            plugin.debug("Chat","was called and the message is numeric", message, "RPM set");
            plugin.rpm = Integer.valueOf(message);
            sendMessage(ev.getPlayer(), "&7[SigngiS] &aYou have set the RPM to &b" + message);
            if (plugin.selected != null) {
                plugin.selected.setRpm(Integer.valueOf(message));
                plugin.selected.refresh();
            }
            ev.setCancelled(true);
        } else {
            sendMessage(ev.getPlayer(), "&7[SigngiS] &aYou are currently a signer. Type: &cexit &ato exit.");
        }
    }

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void parseClears(String message) {
        if (message.equalsIgnoreCase("sound")) {
            plugin.selected.setSound(null);
        }
        if (message.equalsIgnoreCase("effect")) {
            plugin.selected.setEffect(null);
        }
        if (message.equalsIgnoreCase("sign")) {
            plugin.selected.selfDestruct();
            plugin.selected = null;
        }
    }

    public boolean trySound(String sound) {
        try {
            Sound.valueOf(sound.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean tryEffect(String effect) {
        try {
            Effect.valueOf(effect.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
