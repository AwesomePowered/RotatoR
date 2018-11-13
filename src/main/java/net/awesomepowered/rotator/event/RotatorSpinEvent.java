package net.awesomepowered.rotator.event;

import net.awesomepowered.rotator.Spinnable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RotatorSpinEvent extends Event implements Cancellable {

    private static final HandlerList panHandlers = new HandlerList();
    private boolean cancelled;
    private Spinnable rotator;

    public RotatorSpinEvent(Spinnable rotator) {
        this.rotator = rotator;
    }

    @Override
    public HandlerList getHandlers() {
        return panHandlers;
    }

    public Spinnable getRotator() {
        return rotator;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
