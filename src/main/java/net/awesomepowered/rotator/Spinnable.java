package net.awesomepowered.rotator;

import org.bukkit.block.BlockState;

public interface Spinnable {

     BlockState getState();

     void setState(BlockState state);

     int getMode();

     void setMode(int mode);

     int getTaskID();

     void setTaskID(int taskId);

     int getRpm();

     void setRpm(int rpm);

     String getEffect();

     String getSound();

     void setEffect(String effect);

     void setSound(String sound);

     void refresh();

     void selfDestruct();

     void spoolUp();

     void play();

}