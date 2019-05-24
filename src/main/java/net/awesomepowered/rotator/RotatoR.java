package net.awesomepowered.rotator;

import net.awesomepowered.rotator.commands.SignCommand;
import net.awesomepowered.rotator.listeners.BlockSignListener;
import net.awesomepowered.rotator.listeners.EntitySignListener;
import net.awesomepowered.rotator.listeners.SignerListener;
import net.awesomepowered.rotator.types.BlockSpinner;
import net.awesomepowered.rotator.types.EntitySpinner;
import net.awesomepowered.rotator.utils.Spinner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spigot.SpigotUpdater;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public final class RotatoR extends JavaPlugin {

    static RotatoR main;
    public Map<Location, Spinnable> blockSpinners = new HashMap<>();
    public Map<UUID, Spinnable> entitySpinners = new HashMap<>();
    public Map<UUID, Spinnable> leSigners = new HashMap<>(); //todo make lesigner object instead
    public int rpm = 10;
    public static boolean debug = false;
    public static boolean isPremium = false;
    private Metrics metrics;

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
        checkPremium();
        checkForSigns();
        spoolSpinners();
        metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("premium", () -> String.valueOf(isPremium)));
        updateCheck();
        if (!getServer().getVersion().contains("git-Bukkit")) {
            signerTimer();
        }
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
        } else {
            for (String s : getConfig().getConfigurationSection("spinner").getKeys(false)) {
                debug("Loading spinner", s);
                Location loc = stringToLoc(s);
                if (loc != null && Spinner.isSpinnable(loc.getBlock())) {
                    debug("It's spinnable");
                    BlockState blockState = loc.getBlock().getState();
                    int mode = getConfig().getInt("spinner."+s+".mode");
                    String sound = getConfig().getString("spinner."+s+".sound");
                    String effect = getConfig().getString("spinner."+s+".effect");
                    String particle = getConfig().getString("spinner."+s+".particle");
                    int rpm = getConfig().getInt("spinner."+s+".rpm", this.rpm);
                    BlockSpinner blockSpinner = new BlockSpinner(blockState, mode, rpm);
                    blockSpinner.setEffect(effect);
                    blockSpinner.setSound(sound);
                    blockSpinner.setParticle(particle);
                    debug( "Main", "Spooling up spinner at " + s, "Mode: " + mode, "RPM: " + rpm, "Sound: " + sound, "Effect: " + effect, "Particle: " + particle);
                    blockSpinner.spoolUp();
                    blockSpinners.put(loc, blockSpinner);
                }
            }
        }

        if (getConfig().getConfigurationSection("espinner") == null) {
            debug("eSpinner section is null");
        } else {
            for (String s : getConfig().getConfigurationSection("espinner").getKeys(false)) {
                debug("Loading espinner", s);
                String location = getConfig().getString("espinner."+s+".loc");
                if (location != null)
                    stringToLoc(location).getChunk().load(false);
                Entity entity = Bukkit.getEntity(UUID.fromString(s));
                if (entity != null && Spinner.isSpinnable(entity)) {
                    debug("It's espinnable");
                    String sound = getConfig().getString("espinner."+s+".sound");
                    String effect = getConfig().getString("espinner."+s+".effect");
                    String particle = getConfig().getString("espinner."+s+".particle");
                    int rpm = getConfig().getInt("espinner."+s+".rpm", this.rpm);
                    double yaw = getConfig().getDouble("espinner."+s+".yaw", 12.5);
                    EntitySpinner entitySpinner = new EntitySpinner(entity, 0, rpm);
                    entitySpinner.setEffect(effect);
                    entitySpinner.setSound(sound);
                    entitySpinner.setParticle(particle);
                    entitySpinner.setYawChange(yaw);
                    debug( "Main", "Spooling up espinner id " + s, "RPM: " + rpm, "Sound: " + sound, "Effect: " + effect, "Particle: " + particle, "Yaw: " + yaw);
                    entitySpinner.spoolUp();
                    entitySpinners.put(UUID.fromString(s), entitySpinner);
                }
            }
        }

    }

    public void saveSpinners() {
        /*
        Sets the entries to null to remove dead/stopped spinners.
        Then write the active ones over.
         */
        if (!blockSpinners.isEmpty()) {
            getConfig().set("spinner", null);
        }
        if (!entitySpinners.isEmpty()) {
            getConfig().set("espinner", null);
        }

        for (Spinnable spinner : blockSpinners.values()) {
            String loc = locToString(spinner.getLocation());
            getConfig().set("spinner."+loc+".mode", spinner.getMode());
            getConfig().set("spinner."+loc+".rpm", spinner.getRpm());
            getConfig().set("spinner."+loc+".sound", spinner.getSound());
            getConfig().set("spinner."+loc+".effect", spinner.getEffect());
            getConfig().set("spinner."+loc+".particle", spinner.getParticle());
            Bukkit.getScheduler().cancelTask(spinner.getTaskID());
        }

        for (UUID uuid : entitySpinners.keySet()) {
            EntitySpinner spinner = (EntitySpinner) entitySpinners.get(uuid);
            getConfig().set("espinner."+uuid+".yaw", spinner.getYawChange());
            getConfig().set("espinner."+uuid+".rpm", spinner.getRpm());
            getConfig().set("espinner."+uuid+".sound", spinner.getSound());
            getConfig().set("espinner."+uuid+".effect", spinner.getEffect());
            getConfig().set("espinner."+uuid+".particle", spinner.getParticle());
            getConfig().set("espinner."+uuid+".loc", locToString(spinner.getLocation()));
            Bukkit.getScheduler().cancelTask(spinner.getTaskID());
        }

        blockSpinners.clear();
        entitySpinners.clear();
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

    public void checkPremium() {
        isPremium = (getDescription().getVersion().contains("P") || getConfig().getBoolean("premium", false));
        debug("Premium: ", isPremium);
    }

    public void updateCheck() {
        try {
            new SpigotUpdater(this, 61960);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void debug(Object... o) {
        if (debug) getLogger().log(Level.INFO, Arrays.toString(o));
    }
}