package net.awesomepowered.rotator;

import org.bukkit.Location;

public interface Spinnable {

     Location getLocation();

     int getMode();

     void setMode(int mode);

     int getTaskID();

     void setTaskID(int taskId);

     int getRpm();

     void setRpm(int rpm);

     String getEffect();

     String getSound();

     String getParticle();

     void setEffect(String effect);

     void setSound(String sound);

     void setParticle(String particle);

     void refresh();

     void selfDestruct();

     void spoolUp();

     void play();

}