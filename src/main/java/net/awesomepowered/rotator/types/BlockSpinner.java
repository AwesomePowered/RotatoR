package net.awesomepowered.rotator.types;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.Spinnable;
import net.awesomepowered.rotator.utils.Rotation;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class BlockSpinner implements Spinnable {

    private BlockState state;
    private int mode;
    private int taskID;
    private int rpm;
    private String effect;
    private String sound;
    private Skull skull;

    public BlockSpinner(BlockState state, int mode, int rpm) {
        this.state = state;
        this.mode = mode;
        this.rpm = rpm;
    }

    public Location getLocation() {
        return state.getLocation();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getEffect() {
        return this.effect;
    }

    public String getSound() {
        return this.sound;
    }

    public void selfDestruct() {
        Bukkit.getScheduler().cancelTask(taskID);
        RotatoR.getMain().blockSpinners.remove(state.getLocation());
    }

    public void refresh() {
        Bukkit.getScheduler().cancelTask(taskID);
        spoolUp();
    }

    public void spoolUp() {
        RotatoR.getMain().debug("BlockSpinner","spoolUp called");
        if (state instanceof Sign || state instanceof Banner) {
            runLegacy();
        } else if (state instanceof Skull) {
            skull = (Skull) state;
            runPrimary();
        }
    }

    private void runPrimary() {
        if (mode == 0) {
            RotatoR.getMain().debug("Primary","Using mode 0");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
                if (state.getType() != Material.SKULL) {
                    RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! A head disappeared.");
                    selfDestruct();
                    return;
                }
                if (!nearbyAudience(skull.getLocation()))
                    return;
                if (skull.getRotation() == Rotation.getBlockFace(15)) {
                    skull.setRotation(Rotation.getBlockFace(0));
                } else {
                    skull.setRotation(Rotation.getBlockFace(Rotation.getBlockFace(skull.getRotation()) + 1));
                }
                state.update();
                play();
            }, 0, rpm));
        }

        if (mode == 1) {
            RotatoR.getMain().debug("Primary","Using mode 1");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
                if (state.getType() != Material.SKULL) {
                    RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! A head disappeared.");
                    selfDestruct();
                    return;
                }
                if (!nearbyAudience(skull.getLocation()))
                    return;
                if (skull.getRotation() == Rotation.getBlockFace(0)) {
                    skull.setRotation(Rotation.getBlockFace(15));
                } else {
                    skull.setRotation(Rotation.getBlockFace(Rotation.getBlockFace(skull.getRotation()) - 1));
                }
                state.update();
                play();
            }, 0, rpm));
        }
    }

    private void runLegacy() {
        if (mode == 0) {
            RotatoR.getMain().debug("Legacy","Using mode 0");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
                if (getState().getLocation().getBlock().getType() == Material.AIR) {
                    RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! A spinner disappeared.");
                    selfDestruct();
                }
                if (state.getRawData() == 15) {
                    state.setRawData((byte) 0);
                } else {
                    state.setRawData((byte) (state.getRawData()+1));
                }
                state.update();
                play();
            }, 0, rpm));
        }

        if (mode == 1) {
            RotatoR.getMain().debug("Legacy","Using mode 1");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
                if (getState().getLocation().getBlock().getType() == Material.AIR) {
                    RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! A spinner disappeared.");
                    selfDestruct();
                }
                if (state.getRawData() == 0) {
                    state.setRawData((byte) 15);
                } else {
                    state.setRawData((byte) (state.getRawData()-1));
                }
                state.update();
                play();
            }, 0, rpm));
        }
    }

    public void play() {
        if (sound != null) {
            state.getLocation().getWorld().playSound(state.getLocation(), Sound.valueOf(sound), 1, 1);
        }
        if (effect != null) {
            state.getLocation().getWorld().playEffect(state.getLocation().add(0.5,0,0.5), Effect.valueOf(effect), 1);
        }
    }

    public boolean nearbyAudience(Location location)
    {
        return !location.getWorld().getPlayers().isEmpty();
//        for (Player player : location.getWorld().getPlayers())
//        {
//            //TODO: get spigot entity-tracking-range value
//            if (player.getLocation().distanceSquared(location) < 64 * 64)
//                return true;
//        }
    }

}