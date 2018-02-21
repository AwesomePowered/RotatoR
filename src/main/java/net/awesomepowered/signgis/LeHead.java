package net.awesomepowered.signgis;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import java.util.logging.Level;

public class LeHead implements Spinnable {

    private Skull head;
    private int mode;
    private int taskID;
    private int rpm;
    private String effect;
    private String sound;

    public LeHead(Skull head, int mode, int taskID, int rpm) {
        this.head = head;
        this.mode = mode;
        this.taskID = taskID;
        this.rpm = rpm;
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

    public Skull getHead() { //( ͡° ͜ʖ ͡°)
        return head;
    }

    public void setHead(Skull head) {
        this.head = head;
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
        SigngiS.getMain().leHead.remove(head.getLocation());
    }

    public void refresh() {
        Bukkit.getScheduler().cancelTask(taskID);
        spoolUp();
    }

    public void spoolUp() {
        SigngiS.getMain().debug("LeHead","spoolUp called");
        if (mode == 0) {
            SigngiS.getMain().debug("LeHead","Using mode 0");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(SigngiS.getMain(), () -> {
                if (getHead().getType() != Material.SKULL) {
                    SigngiS.getMain().getLogger().log(Level.WARNING, "Oh noes! A head disappeared.");
                    selfDestruct();
                }
                if (head.getRotation() == getBlockFace(15)) {
                    head.setRotation(getBlockFace(0));
                } else {
                    head.setRotation(getBlockFace(getBlockFace(head.getRotation()) + 1));
                }
                head.update();
                play();
            }, 0, rpm));
        }

        if (mode == 1) {
            SigngiS.getMain().debug("LeHead","Using mode 1");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(SigngiS.getMain(), () -> {
                if (getHead().getType() != Material.SKULL) {
                    SigngiS.getMain().getLogger().log(Level.WARNING, "Oh noes! A head disappeared.");
                    selfDestruct();
                }
                if (head.getRotation() == getBlockFace(0)) {
                    head.setRotation(getBlockFace(15));
                } else {
                    head.setRotation(getBlockFace(getBlockFace(head.getRotation()) - 1));
                }
                head.update();
                play();
            }, 0, rpm));
        }
    }

    public void play() {
        if (sound != null) {
            head.getLocation().getWorld().playSound(head.getLocation(), Sound.valueOf(sound), 1, 1);
        }
        if (effect != null) {
            head.getLocation().getWorld().playEffect(head.getLocation().add(0.5,0,0.5), Effect.valueOf(effect), 1);
        }
    }

    private BlockFace getBlockFace(int rotation) {
        switch (rotation) {
            case 0:
                return BlockFace.NORTH;
            case 1:
                return BlockFace.NORTH_NORTH_EAST;
            case 2:
                return BlockFace.NORTH_EAST;
            case 3:
                return BlockFace.EAST_NORTH_EAST;
            case 4:
                return BlockFace.EAST;
            case 5:
                return BlockFace.EAST_SOUTH_EAST;
            case 6:
                return BlockFace.SOUTH_EAST;
            case 7:
                return BlockFace.SOUTH_SOUTH_EAST;
            case 8:
                return BlockFace.SOUTH;
            case 9:
                return BlockFace.SOUTH_SOUTH_WEST;
            case 10:
                return BlockFace.SOUTH_WEST;
            case 11:
                return BlockFace.WEST_SOUTH_WEST;
            case 12:
                return BlockFace.WEST;
            case 13:
                return BlockFace.WEST_NORTH_WEST;
            case 14:
                return BlockFace.NORTH_WEST;
            case 15:
                return BlockFace.NORTH_NORTH_WEST;
            default:
                throw new AssertionError(rotation);
        }
    }

    private int getBlockFace(BlockFace rotation) {
        switch (rotation) {
            case NORTH:
                return 0;
            case NORTH_NORTH_EAST:
                return 1;
            case NORTH_EAST:
                return 2;
            case EAST_NORTH_EAST:
                return 3;
            case EAST:
                return 4;
            case EAST_SOUTH_EAST:
                return 5;
            case SOUTH_EAST:
                return 6;
            case SOUTH_SOUTH_EAST:
                return 7;
            case SOUTH:
                return 8;
            case SOUTH_SOUTH_WEST:
                return 9;
            case SOUTH_WEST:
                return 10;
            case WEST_SOUTH_WEST:
                return 11;
            case WEST:
                return 12;
            case WEST_NORTH_WEST:
                return 13;
            case NORTH_WEST:
                return 14;
            case NORTH_NORTH_WEST:
                return 15;
            default:
                throw new IllegalArgumentException("Invalid BlockFace rotation: " + rotation);
        }
    }
}
