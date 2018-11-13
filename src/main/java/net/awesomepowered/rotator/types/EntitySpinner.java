package net.awesomepowered.rotator.types;

import com.google.common.util.concurrent.AtomicDouble;
import net.awesomepowered.rotator.RotatoR;
import net.awesomepowered.rotator.Spinnable;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.logging.Level;

public class EntitySpinner implements Spinnable {

    private LivingEntity entity;
    private int mode;
    private int taskID;
    private int rpm;
    private String effect;
    private String sound;
    private String particle;
    private double yawChange = 15; //lower = slower turn
    private AtomicDouble trouble = new AtomicDouble();

    public EntitySpinner(LivingEntity ent, int mode, int rpm) {
        this.entity = ent;
        this.mode = mode;
        this.rpm = rpm;
        entity.setAI(false);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public double getYawChange() {
        return this.yawChange;
    }

    public void setYawChange(double yawChange) {
        this.yawChange = yawChange;
    }

    public Location getLocation() {
        return entity.getLocation();
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        setYawChange(getYawChange() * -1);
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int taskId) {
        this.taskID = taskId;
    }

    public int getRpm() {
        return this.rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public String getEffect() {
        return this.effect;
    }

    public String getSound() {
        return this.sound;
    }

    public String getParticle() {
        return this.particle;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }

    public void refresh() {
        Bukkit.getScheduler().cancelTask(taskID);
        spoolUp();
    }

    public void selfDestruct() {
        entity.setAI(true);
        Bukkit.getScheduler().cancelTask(taskID);
        RotatoR.getMain().blockSpinners.remove(entity.getLocation());
    }

    public void spoolUp() {
        Location constant = entity.getLocation();
        RotatoR.getMain().debug("eSpool","Using mode 0");
        setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(RotatoR.getMain(), () -> {
            if (entity.isDead()) {
                RotatoR.getMain().getLogger().log(Level.WARNING, "Oh noes! An entity is ded!");
                selfDestruct();
            }
            constant.setYaw((float) trouble.getAndAdd(yawChange) % 360); //shhh
            entity.teleport(constant);
            play();
        }, 0, rpm));
    }

    public void play() {
        if (sound != null) {
            entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.valueOf(sound), 1, 1);
        }
        if (effect != null) {
            entity.getLocation().getWorld().playEffect(entity.getLocation().add(0.5,0,0.5), Effect.valueOf(effect), 1);
        }
        if (particle != null) {
            entity.getLocation().getWorld().spawnParticle(Particle.valueOf(particle), entity.getLocation().add(0.5,0,0.5), 1);
        }
    }
}
