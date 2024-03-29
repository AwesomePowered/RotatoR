package net.awesomepowered.rotator.listeners;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.types.EntitySpinner;
import net.awesomepowered.rotator.utils.Rotation;
import net.awesomepowered.rotator.utils.Windows;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class SignerListener implements Listener {

    private RotatoR plugin;

    public SignerListener(RotatoR rotatoR) {
        this.plugin = rotatoR;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        if (!plugin.leSigners.containsKey(ev.getPlayer().getUniqueId())) {
            plugin.debug("Chat","was called but player is not a signer");
            return;
        }
        String message = ev.getMessage();
        Player p = ev.getPlayer();
        ev.setCancelled(true);
        if (message.equalsIgnoreCase("exit")) {
            plugin.debug("Chat","exit was called. Player no longer a signer");
            plugin.leSigners.remove(p.getUniqueId());
            sendMessage(p, "&cYou are no longer an active signer");
            return;
        }
        if (message.equalsIgnoreCase("debug")) {
            RotatoR.debug = !RotatoR.debug;
            sendMessage(p, "&aDebug mode: " + RotatoR.debug);
            return;
        }
        if (message.equalsIgnoreCase("gui") && RotatoR.isPremium) {
            plugin.debug("Chat","GUI was called. Opening main menu");
            sendMessage(p, "&aOpening RotatoR menu");
            new Windows(plugin, p).openRotatorsMainMenu();
            return;
        }
        if (message.toLowerCase().startsWith("stop") && plugin.leSigners.get(p.getUniqueId()) != null && message.split(" ").length == 2) {
            plugin.debug("Chat", "Stop is called with: " + message);
            parseClears(message.split(" ")[1], p.getUniqueId());
            return;
        }
        if (message.equalsIgnoreCase("mode") && plugin.leSigners.get(p.getUniqueId()) != null) {
            plugin.debug("Chat", "mode is called, changing..");
            plugin.leSigners.get(p.getUniqueId()).setMode(plugin.leSigners.get(p.getUniqueId()).getMode() == 1 ? 0 : 1);
            plugin.leSigners.get(p.getUniqueId()).refresh();
            sendMessage(p, "&aYou have changed the spinner mode");
            return;
        }
        if (trySound(message) && plugin.leSigners.get(p.getUniqueId()) != null) {
            plugin.leSigners.get(p.getUniqueId()).setSound(message.toUpperCase());
            plugin.debug("Chat","was called and the message is SOUND", message, "sound set");
            sendMessage(p, "&aYou have set the sound to &b" + message.toUpperCase());
            return;
        }
        if (tryEffect(message) && plugin.leSigners.get(p.getUniqueId()) != null) {
            plugin.leSigners.get(p.getUniqueId()).setEffect(message.toUpperCase());
            plugin.debug("Chat","was called and the message is EFFECT", message, "effect set");
            sendMessage(p, "&aYou have set the effect to &b" + message.toUpperCase());
            return;
        }
        if (tryParticle(message) && plugin.leSigners.get(p.getUniqueId()) != null) {
            plugin.leSigners.get(p.getUniqueId()).setEffect(message.toUpperCase());
            plugin.debug("Chat","was called and the message is PARTICLE", message, "particle set");
            sendMessage(p, "&aYou have set the particle to &b" + message.toUpperCase());
            return;
        }
        if (message.startsWith("y") || message.startsWith("yaw")) {
            String yaw = message.replace("yaw","").replace("y", "");
            plugin.debug("Chat","was called and the message yaw", yaw);
            if (Rotation.isDouble(yaw) && plugin.leSigners.get(p.getUniqueId()) instanceof EntitySpinner) {
                if (Double.valueOf(yaw) > 360) {
                    sendMessage(p, "&aInput must be between 1 - 360 not&b" + yaw);
                }
                EntitySpinner spinner = (EntitySpinner) plugin.leSigners.get(p.getUniqueId());
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
            if (plugin.leSigners.get(p.getUniqueId()) != null) {
                plugin.leSigners.get(p.getUniqueId()).setRpm(Integer.valueOf(message));
                plugin.leSigners.get(p.getUniqueId()).refresh();
            }
            ev.setCancelled(true);
        } else {
            sendMessage(p, "&aYou are currently a signer. Type: &cexit &ato exit.");
            ev.setCancelled(true);
        }
    }

    public void parseClears(String message, UUID uuid) {
        if (message.equalsIgnoreCase("sound")) {
            plugin.leSigners.get(uuid).setSound(null);
        }
        if (message.equalsIgnoreCase("effect")) {
            plugin.leSigners.get(uuid).setEffect(null);
        }
        if (message.equalsIgnoreCase("particle")) {
            plugin.leSigners.get(uuid).setParticle(null);
        }
        if (message.equalsIgnoreCase("spin") || message.equalsIgnoreCase("sign") || message.equalsIgnoreCase("head") || message.equalsIgnoreCase("banner")) {
            plugin.leSigners.get(uuid).selfDestruct();
            plugin.leSigners.put(uuid, null);
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

    public boolean tryParticle(String particle) {
        try {
            Particle.valueOf(particle.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7]&r " + message));
    }

}
