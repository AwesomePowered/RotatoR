package net.awesomepowered.signgis;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
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
    public HashMap<Location, LeHead> leHead = new HashMap<>();
    public List<UUID> leSigners = new ArrayList<>();
    int rpm = 10;
    Spinnable selected = null;
    boolean debug = false;

    @Override
    public void onEnable() {
        main =  this;
        saveDefaultConfig();
        rpm = getConfig().getInt("rpm");
        debug = getConfig().getBoolean("debug");
        getCommand("lesign").setExecutor(new SignCommand(this));
        Bukkit.getPluginManager().registerEvents(new SigningListener(this), this);
        spoolSigns();
        spoolHeads();
        signerTimer();
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelAllTasks();
        saveSigns();
        saveHeads();
    }

    public static SigngiS getMain() {
        return main;
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
                String sound = getConfig().getString("signs."+s+".sound");
                String effect = getConfig().getString("signs."+s+".effect");
                int rpm = getConfig().getInt("signs."+s+".rpm", 0);
                LeSign elSign = new LeSign(sign, mode, 0, rpm);
                elSign.setEffect(effect);
                elSign.setSound(sound);
                debug( "Main", "Spooling up sign at " + s, "Mode: " + mode, "RPM: " + rpm, "Sound: " + sound, "Effect: " + effect);
                elSign.spoolUp();
                leSign.put(loc, elSign);
            }
        }
    }

    public void spoolHeads() {
        if (getConfig().getConfigurationSection("heads") == null) {
            return;
        }
        for (String s : getConfig().getConfigurationSection("heads").getKeys(false)) {
            Location loc = stringToLoc(s);
            if (loc.getBlock().getType() == Material.SKULL) {
                Skull skull = (Skull) loc.getBlock().getState();
                int mode = getConfig().getInt("heads."+s+".mode");
                String sound = getConfig().getString("heads."+s+".sound");
                String effect = getConfig().getString("heads."+s+".effect");
                int rpm = getConfig().getInt("heads."+s+".rpm", 0);
                LeHead head = new LeHead(skull, mode, 0, rpm);
                head.setEffect(effect);
                head.setSound(sound);
                debug( "Main", "Spooling up head at " + s, "Mode: " + mode, "RPM: " + rpm, "Sound: " + sound, "Effect: " + effect);
                head.spoolUp();
                leHead.put(loc, head);
            }
        }
    }

    public void saveSigns() {
        getConfig().set("signs", null);
        for (LeSign leSign : leSign.values()) {
            getConfig().set("signs."+locToString(leSign.getSign().getLocation())+".mode", leSign.getMode());
            getConfig().set("signs."+locToString(leSign.getSign().getLocation())+".rpm", leSign.getRpm());
            getConfig().set("signs."+locToString(leSign.getSign().getLocation())+".sound", leSign.getSound());
            getConfig().set("signs."+locToString(leSign.getSign().getLocation())+".effect", leSign.getEffect());
        }
        saveConfig();
    }

    public void saveHeads() {
        getConfig().set("heads", null);
        for (LeHead leHead : leHead.values()) {
            getConfig().set("heads."+locToString(leHead.getHead().getLocation())+".mode", leHead.getMode());
            getConfig().set("heads."+locToString(leHead.getHead().getLocation())+".rpm", leHead.getRpm());
            getConfig().set("heads."+locToString(leHead.getHead().getLocation())+".sound", leHead.getSound());
            getConfig().set("heads."+locToString(leHead.getHead().getLocation())+".effect", leHead.getEffect());
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

    public void signerTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (UUID uuid : leSigners) {
                if (Bukkit.getPlayer(uuid) != null) {
                    Player p = Bukkit.getPlayer(uuid);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&6[&aSigner mode&6]")));
                }
            }
        }, 20, 20);
    }
}