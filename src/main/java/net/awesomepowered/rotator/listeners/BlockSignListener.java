package net.awesomepowered.rotator.listeners;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.types.BlockSpinner;
import net.awesomepowered.rotator.utils.Spinner;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockSignListener implements Listener {

    RotatoR plugin;

    public BlockSignListener(RotatoR rotatoR) {
        this.plugin = rotatoR;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent ev) {
        if (!plugin.leSigners.containsKey(ev.getPlayer().getUniqueId())) {
            plugin.debug("Interact","was called but player is not a signer");
            return;
        }

        Player p = ev.getPlayer();
        ev.setCancelled(true);

        if (ev.getClickedBlock() == null) {
            plugin.debug("Interact","was called but block is not a SIGN_POST/SKULL/BAN");
            return;
        }

        if (ev.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (plugin.blockSpinners.containsKey(ev.getClickedBlock().getLocation())) {
                plugin.debug("Interact L", "on a signed spinner, selecting.");
                plugin.leSigners.put(p.getUniqueId(), plugin.blockSpinners.get(ev.getClickedBlock().getLocation()));
                sendMessage(p, "&aYou have selected a signed spinner");
                return;
            }

            if (Spinner.isSpinnable(ev.getClickedBlock())) { //
                plugin.debug("Interact L","Making a BlockSpinner object");
                BlockSpinner spinner = new BlockSpinner(ev.getClickedBlock().getState(), 0, plugin.rpm);
                plugin.blockSpinners.put(ev.getClickedBlock().getLocation(), spinner);
                spinner.setMode((p.isSneaking()) ? 1 : 0);
                spinner.spoolUp();
                plugin.leSigners.put(p.getUniqueId(), spinner);
                sendMessage(p, "&aYou have signed a Block spinner");
                plugin.debug("Interact","tried to spool spinner");
                return;
            }

        }

        if (ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            plugin.debug("Interact", "was called but is not a right click");
            return;
        }

        if (plugin.blockSpinners.containsKey(ev.getClickedBlock().getLocation())) {
            plugin.debug("Interact", "was called on an signed spinner, unsigning..");
            plugin.blockSpinners.get(ev.getClickedBlock().getLocation()).selfDestruct();
            plugin.leSigners.put(p.getUniqueId(), null);
            sendMessage(ev.getPlayer(), "&cThe spinner is no longer signed");
        }
    }

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7]&r " + message));
    }
}
