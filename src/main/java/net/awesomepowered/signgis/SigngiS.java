package net.awesomepowered.signgis;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class SigngiS extends JavaPlugin implements Listener {

    public HashMap<Location, Integer> leSign = new HashMap<>();
    public static List<UUID> leSigners = new ArrayList<>();
    int rpm = 10;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        rpm = getConfig().getInt("rpm");
        getCommand("lesign").setExecutor(new SignCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        spoolSigns();
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelAllTasks();
        saveSigns();
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent ev) {
        if (ev.getAction() == Action.LEFT_CLICK_BLOCK || (ev.getClickedBlock().getType() != Material.SIGN_POST)) {
            return;
        }
        Sign s = (Sign) ev.getClickedBlock().getState();
        if (leSigners.contains(ev.getPlayer().getUniqueId())) {
                spoolSign(s);
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
                leSign.remove(s.getLocation());
            }
            if (s.getRawData() == 15) {
                s.setRawData((byte) 0);
            } else {
                s.setRawData((byte) (s.getRawData()+1));
            }
            s.update();
        }, 0,rpm));
    }

    public void spoolSigns() {
        if (getConfig().getConfigurationSection("signs") == null) {
            return;
        }
        for (String s : getConfig().getConfigurationSection("signs").getKeys(false)) {
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
        for (String s : locs) {
            //getConfig().getConfigurationSection("signs").set(s+".mode", 0);
            getConfig().set("signs."+s+".mode",0);
        }
        saveConfig();
    }

    public String locToString(Location loc) {
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return world+"~"+x+"~"+y+"~"+z;
    }

    public Location stringToLoc(String s) {
        String[] loc = s.split("~");
        return new Location(Bukkit.getWorld(loc[0]),Double.valueOf(loc[1]),Double.valueOf(loc[2]),Double.valueOf(loc[3]));
    }
}
