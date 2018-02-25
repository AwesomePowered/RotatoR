package net.awesomepowered.rotator;

import net.awesomepowered.rotator.commands.SignCommand;
import net.awesomepowered.rotator.listeners.BlockSignListener;
import net.awesomepowered.rotator.listeners.EntitySignListener;
import net.awesomepowered.rotator.listeners.SignerListener;
import net.awesomepowered.rotator.types.BlockSpinner;
import net.awesomepowered.rotator.utils.Spinner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class RotatoR extends JavaPlugin {

    static RotatoR main;
    public HashMap<Location, Spinnable> blockSpinners = new HashMap<>();
    public HashMap<UUID, Spinnable> entitySpinners = new HashMap<>();
    public Map<UUID, Spinnable> leSigners = new HashMap<>();
    public int rpm = 10;
    boolean debug = false;

    @Override
    public void onEnable() {
        main =  this;
        saveDefaultConfig();
        rpm = getConfig().getInt("rpm");
        debug = getConfig().getBoolean("debug");
        getCommand("lesign").setExecutor(new SignCommand(this));
        Bukkit.getPluginManager().registerEvents(new SignerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockSignListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntitySignListener(this), this);
        checkForSigns();
        spoolSpinners();
        signerTimer();
    }

    public void onDisable() {
        saveSpinners();
    }

    public void checkForSigns() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SigngiS");
        if (plugin != null) {
            getLogger().log(Level.INFO, "I asked life for some documentation and it gave me a bloody lemon.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public static RotatoR getMain() {
        return main;
    }


    public void spoolSpinners() {
        if (getConfig().getConfigurationSection("spinner") == null) {
            debug("Spinner section is null");
            return;
        }
        for (String s : getConfig().getConfigurationSection("spinner").getKeys(false)) {
            debug("Loading spinner", s);
            Location loc = stringToLoc(s);
            if (Spinner.isSpinnable(loc.getBlock())) {
                debug("It's spinnable");
                BlockState blockState = loc.getBlock().getState();
                int mode = getConfig().getInt("spinner."+s+".mode");
                String sound = getConfig().getString("spinner."+s+".sound");
                String effect = getConfig().getString("spinner."+s+".effect");
                int rpm = getConfig().getInt("spinner."+s+".rpm", this.rpm);
                BlockSpinner blockSpinner = new BlockSpinner(blockState, mode, rpm);
                blockSpinner.setEffect(effect);
                blockSpinner.setSound(sound);
                debug( "Main", "Spooling up spinner at " + s, "Mode: " + mode, "RPM: " + rpm, "Sound: " + sound, "Effect: " + effect);
                blockSpinner.spoolUp();
                blockSpinners.put(loc, blockSpinner);
            }
        }
    }


    public void saveSpinners() {
        getConfig().set("spinner", null);
        for (Spinnable spinner : blockSpinners.values()) {
            String loc = locToString(spinner.getLocation());
            getConfig().set("spinner."+loc+".mode", spinner.getMode());
            getConfig().set("spinner."+loc+".rpm", spinner.getRpm());
            getConfig().set("spinner."+loc+".sound", spinner.getSound());
            getConfig().set("spinner."+loc+".effect", spinner.getEffect());
            Bukkit.getScheduler().cancelTask(spinner.getTaskID());
        }
        blockSpinners.clear();
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
            for (UUID uuid : leSigners.keySet()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&6[&aSigner mode&6]")));
                } else {
                    leSigners.remove(uuid);
                }
            }
        }, 20, 20);
    }
}