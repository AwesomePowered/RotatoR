package net.awesomepowered.signgis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import java.util.logging.Level;

public class LeSign { //implement Sign?

    private Sign sign;
    private int mode;
    private int taskID;
    private int rpm;

    public LeSign(Sign sign, int mode, int taskID, int rpm) {
        this.sign = sign;
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

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public void selfDestruct() {
        Bukkit.getScheduler().cancelTask(taskID);
        SigngiS.getMain().leSign.remove(sign.getLocation());
    }

    public void spoolUp() {
        SigngiS.getMain().debug("LeSign","spoolUp called");
        if (mode == 0) {
            SigngiS.getMain().debug("LeSign","Using mode 0");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(SigngiS.getMain(), () -> {
                if (getSign().getLocation().getBlock().getType() != Material.SIGN_POST) {
                    SigngiS.getMain().getLogger().log(Level.WARNING, "Oh noes! A sign disappeared.");
                    selfDestruct();
                }
                if (sign.getRawData() == 15) {
                    sign.setRawData((byte) 0);
                } else {
                    sign.setRawData((byte) (sign.getRawData()+1));
                }
                sign.update();
            }, 0, rpm));
        }

        if (mode == 1) {
            SigngiS.getMain().debug("LeSign","Using mode 1");
            setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(SigngiS.getMain(), () -> {
                if (getSign().getLocation().getBlock().getType() != Material.SIGN_POST) {
                    SigngiS.getMain().getLogger().log(Level.WARNING, "Oh noes! A sign disappeared.");
                    selfDestruct();
                }
                if (sign.getRawData() == 0) {
                    sign.setRawData((byte) 15);
                } else {
                    sign.setRawData((byte) (sign.getRawData()-1));
                }
                sign.update();
            }, 0, rpm));
        }
    }

}