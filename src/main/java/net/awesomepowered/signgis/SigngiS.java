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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class SigngiS extends JavaPlugin {

    static SigngiS main;
    public HashMap<Location, LeSign> leSign = new HashMap<>();
    public List<UUID> leSigners = new ArrayList<>();
    int rpm = 10;
    boolean debug = true;

    @Override
    public void onEnable() {
        main =  this;
        saveDefaultConfig();
        rpm = getConfig().getInt("rpm");
        getCommand("lesign").setExecutor(new SignCommand(this));
        Bukkit.getPluginManager().registerEvents(new SigningListener(this), this);
        spoolSigns();
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelAllTasks();
        saveSigns();
    }

    public static SigngiS getMain() {
        return main;
    }

    public void spoolSign(LeSign s) {
        s.getSign().setRawData((byte) 0);
        s.getSign().update();
        if (leSign.keySet().contains(s.getSign().getLocation())) {
            Bukkit.getScheduler().cancelTask(s.getTaskID());
            leSign.remove(s.getSign().getLocation());
            return;
        }
        s.spoolUp();
        leSign.put(s.getSign().getLocation(), s);
    }


    public void spoolSigns() {
        if (getConfig().getConfigurationSection("signs") == null) {
            return;
        }
        for (String s : getConfig().getConfigurationSection("signs").getKeys(false)) {
            Location loc = stringToLoc(s);
            if (loc.getBlock().getType() == Material.SIGN_POST) {
                Sign sign = (Sign) loc.getBlock().getState();
                int mode = getConfig().getInt("signs."+s+".mode");
                int rpm = (getConfig().getInt("signs."+s+".rpm") == 0) ? this.rpm : getConfig().getInt("signs."+s+".rpm");
                debug( "Main", "Spooling up sign at " + s, "Mode: " + mode, "RPM: " + rpm);
                spoolSign(new LeSign(sign, mode, 0, rpm));
            }
        }
    }

    public void saveSigns() {
        for (LeSign leSign : leSign.values()) {
            getConfig().set("signs."+locToString(leSign.getSign().getLocation())+".mode", leSign.getMode());
            getConfig().set("signs."+locToString(leSign.getSign().getLocation())+".rpm", leSign.getRpm());
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

    public void debug(Object... o) {
        if (debug) getLogger().log(Level.INFO, Arrays.toString(o));
    }
}