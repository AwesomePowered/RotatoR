package net.awesomepowered.rotator;

import net.awesomepowered.rotator.types.BlockSpinner;
import net.awesomepowered.rotator.utils.Spinner;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SigningListener implements Listener {

    private RotatoR plugin;

    public SigningListener(RotatoR rotatoR) {
        this.plugin = rotatoR;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent ev) {
        if (!plugin.leSigners.contains(ev.getPlayer().getUniqueId())) {
            plugin.debug("Interact","was called but player is not a signer");
            return;
        }

        ev.setCancelled(true);

        if (ev.getClickedBlock() == null) {
            plugin.debug("Interact","was called but block is not a SIGN_POST/SKULL/BAN");
            return;
        }

        if (ev.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (plugin.spinners.containsKey(ev.getClickedBlock().getLocation())) {
                plugin.debug("Interact L", "on a signed spinner, selecting.");
                plugin.selected = plugin.spinners.get(ev.getClickedBlock().getLocation());
                sendMessage(ev.getPlayer(), "&aYou have selected a signed spinner");
                return;
            }

            if (Spinner.isSpinnable(ev.getClickedBlock())) { //
                plugin.debug("Interact L","Making a BlockSpinner object");
                BlockSpinner spinner = new BlockSpinner(ev.getClickedBlock().getState(), 0, plugin.rpm);
                plugin.spinners.put(ev.getClickedBlock().getLocation(), spinner);
                spinner.setMode((ev.getPlayer().isSneaking()) ? 1 : 0);
                spinner.spoolUp();
                plugin.selected = spinner;
                sendMessage(ev.getPlayer(), "&aYou have signed a spinner");
                plugin.debug("Interact","tried to spool spinner");
                return;
            }

        }

        if (ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            plugin.debug("Interact", "was called but is not a right click");
            return;
        }

        if (plugin.spinners.containsKey(ev.getClickedBlock().getLocation())) {
            plugin.debug("Interact", "was called on an signed spinner, unsigning..");
            plugin.spinners.get(ev.getClickedBlock().getLocation()).selfDestruct();
            plugin.selected = null;
            sendMessage(ev.getPlayer(), "&cThe sign is no longer signed");
        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        if (!plugin.leSigners.contains(ev.getPlayer().getUniqueId())) {
            plugin.debug("Chat","was called but player is not a signer");
            return;
        }
        String message = ev.getMessage();
        Player p = ev.getPlayer();
        if (message.equalsIgnoreCase("exit")) {
            plugin.debug("Chat","exit was called. Player no longer a signer");
            plugin.leSigners.remove(p.getUniqueId());
            plugin.selected = null;
            sendMessage(p, "&cYou are no longer an active signer");
            ev.setCancelled(true);
            return;
        }
        if (message.toLowerCase().startsWith("stop") && plugin.selected != null && message.split(" ").length == 2) {
            plugin.debug("Chat", "Stop is called with: " + message);
            parseClears(message.split(" ")[1]);
            ev.setCancelled(true);
            return;
        }
        if (message.equalsIgnoreCase("mode") && plugin.selected != null) {
            plugin.debug("Chat", "mode is called, changing..");
            plugin.selected.setMode(plugin.selected.getMode() == 1 ? 0 : 1);
            plugin.selected.refresh();
            sendMessage(p, "&aYou have changed the spinner mode");
            ev.setCancelled(true);
            return;
        }
        if (trySound(message) && plugin.selected != null) {
            plugin.selected.setSound(message.toUpperCase());
            plugin.debug("Chat","was called and the message is SOUND", message, "sound set");
            sendMessage(p, "&aYou have set the sound to &b" + message.toUpperCase());
            ev.setCancelled(true);
            return;
        }
        if (tryEffect(message) && plugin.selected != null) {
            plugin.selected.setEffect(message.toUpperCase());
            plugin.debug("Chat","was called and the message is EFFECT", message, "effect set");
            sendMessage(p, "&aYou have set the effect to &b" + message.toUpperCase());
            ev.setCancelled(true);
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

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7]&r " + message));
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

}
