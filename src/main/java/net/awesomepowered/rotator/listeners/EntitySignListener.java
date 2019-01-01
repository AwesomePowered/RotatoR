package net.awesomepowered.rotator.listeners;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.Spinnable;
import net.awesomepowered.rotator.types.EntitySpinner;
import net.awesomepowered.rotator.utils.Spinner;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

public class EntitySignListener implements Listener {

    RotatoR plugin;

    public EntitySignListener(RotatoR rotatoR) {
        this.plugin = rotatoR;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent ev) {
        if (!plugin.leSigners.containsKey(ev.getDamager().getUniqueId())) {
            plugin.debug("Damage", "was called but player is not a signer");
            return;
        }

        ev.setCancelled(true);

        Player p = (Player) ev.getDamager();
        if (plugin.entitySpinners.containsKey(ev.getEntity().getUniqueId())) {
            plugin.debug("Damage", "on a signed spinner, selecting.");
            plugin.leSigners.put(p.getUniqueId(), plugin.entitySpinners.get(ev.getEntity().getUniqueId()));
            sendMessage(p, "&aYou have selected a signed espinner");
            return;
        }

        if (Spinner.isSpinnable(ev.getEntity())) {
            plugin.debug("Damage", "Making an EntitySpinner object");
            EntitySpinner spinner = new EntitySpinner(ev.getEntity(), 0, plugin.rpm);
            spinner.setMode((p.isSneaking()) ? 1 : 0);
            plugin.entitySpinners.put(ev.getEntity().getUniqueId(), spinner);
            spinner.spoolUp();
            plugin.leSigners.put(p.getUniqueId(), spinner);
            sendMessage(p, "&aYou have signed an Entity spinner");
            plugin.debug("Damage","tried to spool espinner");
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent ev) {
        if (!plugin.leSigners.containsKey(ev.getPlayer().getUniqueId())) {
            return;
        }
        ev.setCancelled(true);
        if (plugin.entitySpinners.containsKey(ev.getRightClicked().getUniqueId())) {
            ev.setCancelled(true);
            plugin.debug("eInteract", "was called on an signed entity spinner, unsigning..");
            plugin.entitySpinners.get(ev.getRightClicked().getUniqueId()).selfDestruct();
            sendMessage(ev.getPlayer(), "&cThe spinner is no longer signed");
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent ev) {
        if (!plugin.leSigners.containsKey(ev.getAttacker().getUniqueId())) {
            plugin.debug("VDamage", "was called but player is not a signer");
            return;
        }

        if (Spinner.isSpinnable(ev.getVehicle())) {
            Player p = (Player) ev.getAttacker();
            plugin.debug("VDamage", "Making an EntitySpinner object");
            EntitySpinner spinner = new EntitySpinner(ev.getVehicle(), 0, plugin.rpm);
            spinner.setMode((p.isSneaking()) ? 1 : 0);
            plugin.entitySpinners.put(ev.getVehicle().getUniqueId(), spinner);
            spinner.spoolUp();
            plugin.leSigners.put(p.getUniqueId(), spinner);
            sendMessage(p, "&aYou have signed an Vehicle spinner");
            plugin.debug("VDamage","tried to spool espinner");
        }
    }

    public void sendMessage(Player p, String message) { //todo proper lang support.
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7]&r " + message));
    }
}
