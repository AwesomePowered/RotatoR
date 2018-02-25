package net.awesomepowered.rotator.listeners;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.types.EntitySpinner;
import net.awesomepowered.rotator.utils.Spinner;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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
            sendMessage(p, "&aYou have selected a signed spinner");
            return;
        }

        if (Spinner.isSpinnable(ev.getEntity())) {
            plugin.debug("Damage", "Making an EntitySpinner object");
            EntitySpinner spinner = new EntitySpinner((LivingEntity) ev.getEntity(), 0, plugin.rpm);
            plugin.entitySpinners.put(ev.getEntity().getUniqueId(), spinner);
            spinner.spoolUp();
            plugin.leSigners.put(p.getUniqueId(), spinner);
            sendMessage(p, "&aYou have signed an Entity spinner");
            plugin.debug("Damage","tried to spool spinner");
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent ev) {
        if (plugin.leSigners.containsKey(ev.getPlayer().getUniqueId()) && plugin.entitySpinners.containsKey(ev.getRightClicked().getUniqueId())) {
            ev.setCancelled(true);

            plugin.debug("eInteract", "was called on an signed entity spinner, unsigning..");
            plugin.entitySpinners.get(ev.getRightClicked().getUniqueId()).selfDestruct();
            plugin.leSigners.put(ev.getPlayer().getUniqueId(), null);
            sendMessage(ev.getPlayer(), "&cThe spinner is no longer signed");
        }
    }

    public void sendMessage(Player p, String message) { //todo proper lang support.
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bR&fotato&bR&7]&r " + message));
    }
}
