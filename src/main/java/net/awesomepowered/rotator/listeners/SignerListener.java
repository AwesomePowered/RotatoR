package net.awesomepowered.rotator.listeners;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.types.EntitySpinner;
import net.awesomepowered.rotator.utils.Rotation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SignerListener implements Listener {

    private RotatoR plugin;

    public SignerListener(RotatoR rotatoR) {
        this.plugin = rotatoR;
    }



    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        if (!plugin.leSigners.contains(ev.getPlayer().getUniqueId())) {
            plugin.debug("Chat","was called but player is not a signer");
            return;
        }
        String message = ev.getMessage();
        Player p = ev.getPlayer();
        ev.setCancelled(true);
        if (message.equalsIgnoreCase("@test")) {
            plugin.debug("Damage", "Making an EntitySpinner object");
            EntitySpinner spinner = new EntitySpinner(p, 0, plugin.rpm);
            spinner.spoolUp();
            plugin.selected = spinner;
            sendMessage(p, "&aYou spun urself");
            return;
        }

        if (message.equalsIgnoreCase("exit")) {
            plugin.debug("Chat","exit was called. Player no longer a signer");
            plugin.leSigners.remove(p.getUniqueId());
            plugin.selected = null;
            sendMessage(p, "&cYou are no longer an active signer");
            return;
        }
        if (message.toLowerCase().startsWith("stop") && plugin.selected != null && message.split(" ").length == 2) {
            plugin.debug("Chat", "Stop is called with: " + message);
            parseClears(message.split(" ")[1]);
            return;
        }
        if (message.equalsIgnoreCase("mode") && plugin.selected != null) {
            plugin.debug("Chat", "mode is called, changing..");
            plugin.selected.setMode(plugin.selected.getMode() == 1 ? 0 : 1);
            plugin.selected.refresh();
            sendMessage(p, "&aYou have changed the spinner mode");
            return;
        }
        if (trySound(message) && plugin.selected != null) {
            plugin.selected.setSound(message.toUpperCase());
            plugin.debug("Chat","was called and the message is SOUND", message, "sound set");
            sendMessage(p, "&aYou have set the sound to &b" + message.toUpperCase());
            return;
        }
        if (tryEffect(message) && plugin.selected != null) {
            plugin.selected.setEffect(message.toUpperCase());
            plugin.debug("Chat","was called and the message is EFFECT", message, "effect set");
            sendMessage(p, "&aYou have set the effect to &b" + message.toUpperCase());
            return;
        }
        if (message.startsWith("y") || message.startsWith("yaw")) {
            String yaw = message.replace("yaw","").replace("y", "");
            plugin.debug("Chat","was called and the message yaw", yaw);
            if (Rotation.isDouble(yaw) && plugin.selected instanceof EntitySpinner) {
                if (Double.valueOf(yaw) > 360) {
                    sendMessage(p, "&aInput must be between 1 - 360 not&b" + yaw);
                }
                EntitySpinner spinner = (EntitySpinner) plugin.selected;
                spinner.setYawChange(Double.valueOf(yaw));
                spinner.refresh();
                sendMessage(p, "&aYou have set the YawChange to &b" + yaw);
            }
            return;
        }
        if (StringUtils.isNumeric(ev.getMessage())) {
            plugin.debug("Chat","was called and the message is numeric", message, "RPM set");
            plugin.rpm = Integer.valueOf(message);
            sendMessage(p, "&aYou have set the RPM to &b" + message);
            if (plugin.selected != null) {
                plugin.selected.setRpm(Integer.valueOf(message));
                plugin.selected.refresh();
            }
            ev.setCancelled(true);
        } else {
            sendMessage(p, "&aYou are currently a signer. Type: &cexit &ato exit.");
            ev.setCancelled(true);
        }
    }

    public void parseClears(String message) {
        if (message.equalsIgnoreCase("sound")) {
            plugin.selected.setSound(null);
        }
        if (message.equalsIgnoreCase("effect")) {
            plugin.selected.setEffect(null);
        }
        if (message.equalsIgnoreCase("spin") || message.equalsIgnoreCase("sign") || message.equalsIgnoreCase("head") || message.equalsIgnoreCase("banner")) {
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

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7]&r " + message));
    }

}
