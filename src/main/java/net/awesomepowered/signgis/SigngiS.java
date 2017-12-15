package net.awesomepowered.signgis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class SigngiS extends JavaPlugin implements Listener {

    public HashMap<Location, Integer> leSign = new HashMap<>();
    public List<UUID> leSigners = new ArrayList<>();
    int rpm = 10;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        rpm = getConfig().getInt("rpm");
        Bukkit.getPluginManager().registerEvents(this, this);
        spoolSigns();
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelAllTasks();
        saveSigns();
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent ev) {
        if (ev.getAction() == Action.LEFT_CLICK_BLOCK && ev.getClickedBlock().getState() instanceof Sign && leSigners.contains(ev.getPlayer().getUniqueId())) {
            Sign s = (Sign) ev.getClickedBlock().getState();
            if (s.getType() == Material.SIGN_POST) {
                spoolSign(s);
            }
        }
    }

    public void spoolSign(Sign s) {
        s.setRawData((byte) 0); //should sync all on start
        s.update();
        if (leSign.keySet().contains(s.getLocation())) { //should probably move this up there
            Bukkit.getScheduler().cancelTask(leSign.get(s.getLocation()));
            leSign.remove(s.getLocation());
            return;
        }
        leSign.put(s.getLocation(), Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (s.getLocation().getBlock().getType() != Material.SIGN_POST) {
                getLogger().log(Level.WARNING, "Oh noes! A sign disappeared.");
                Bukkit.getScheduler().cancelTask(leSign.get(s.getLocation()));
            }
            if (s.getRawData() == 15) {
                s.setRawData((byte) 0);
            } else {
                s.setRawData((byte) (s.getRawData()+1));
            }
            s.update();
        }, 0,rpm));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent ev) { //Permanent solution to a temporary problem... or is it?
        String cmd = ev.getMessage().replace("/","");
        if (cmd.equalsIgnoreCase("lesign")) {
            if (leSigners.contains(ev.getPlayer().getUniqueId())) {
                leSigners.remove(ev.getPlayer().getUniqueId());
                ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &cYou are no longer an active signer"));
            } else {
                leSigners.add(ev.getPlayer().getUniqueId());
                ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[SigngiS] &aYou are now an active signer"));
            }
            ev.setCancelled(true);
        }
    }

    public void spoolSigns() {
        for (String s : getConfig().getStringList("signs")) {
            Location loc = stringToLoc(s);
            getLogger().log(Level.INFO, "Spooling up sign at " + s);
            if (loc.getBlock().getType() == Material.SIGN_POST) {
                spoolSign((Sign) loc.getBlock().getState());
            }
        }
    }

    public void saveSigns() {
        ArrayList<String> locs = new ArrayList<>();
        for (Location loc : leSign.keySet()) {
            locs.add(locToString(loc));
        }
        getConfig().set("signs", locs);
        saveConfig();
    }

    public String locToString(Location loc) {
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return world+"."+x+"."+y+"."+z;
    }

    public Location stringToLoc(String s) {
        String[] loc = s.split("\\.");
        return new Location(Bukkit.getWorld(loc[0]),Double.valueOf(loc[1]),Double.valueOf(loc[2]),Double.valueOf(loc[3]));
    }
}
