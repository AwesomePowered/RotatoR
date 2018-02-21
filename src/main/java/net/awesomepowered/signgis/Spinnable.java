package net.awesomepowered.signgis;

public interface Spinnable {

    public int getMode();

    public void setMode(int mode);

    public int getTaskID();

    public void setTaskID(int taskId);

    public int getRpm();

    public void setRpm(int rpm);

    public String getEffect();

    public String getSound();

    public void setEffect(String effect);

    public void setSound(String sound);

    public void refresh();

    public void selfDestruct();

    public void spoolUp();

    public void play();

}