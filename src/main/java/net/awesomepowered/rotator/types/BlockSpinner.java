package net.awesomepowered.rotator.types;

import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.Spinnable;
import net.awesomepowered.rotator.utils.Rotation;
import net.awesomepowered.rotator.utils.Spinner;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;

import java.util.logging.Level;

public class BlockSpinner implements Spinnable {

    private BlockData data;
    private BlockState state;
    private int mode;
    private int taskID;
    private int rpm;
    private String effect;
    private String sound;
    private Rotatable rotor;

    public BlockSpinner(BlockState state, int mode, int rpm) {
        this.state = state;
        this.data = state.getBlockData();
        this.rotor = (Rotatable) data;
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
        if (mode == 0) {
            RotatoR.getMain().debug("Primary","Using mode 0");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
                if (!Spinner.isSpinnable(getState())) {
                    RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! A spinner disappeared.");
                    selfDestruct();
                }
                rotor.setRotation(Rotation.getBlockFace(Rotation.getBlockFace(rotor.getRotation()) + 1));
                state.setBlockData(rotor);
                state.update();
                play();
            }, 0, rpm));
        }

        if (mode == 1) {
            RotatoR.getMain().debug("Primary","Using mode 1");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
                if (!Spinner.isSpinnable(getState())) {
                    RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! A spinner disappeared.");
                    selfDestruct();
                }
                rotor.setRotation(Rotation.getBlockFace(Rotation.getBlockFace(rotor.getRotation()) + -1));
                state.setBlockData(rotor);
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

}