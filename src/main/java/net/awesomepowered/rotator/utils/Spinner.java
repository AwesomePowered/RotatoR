package net.awesomepowered.rotator.utils;

import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;

public class Spinner {


    public static boolean isSpinnable(BlockState blockState) {
        //return (blockState.getBlockData() instanceof Rotatable);
        return ((blockState instanceof Sign && blockState.getBlockData() instanceof Rotatable) || blockState instanceof Skull || blockState instanceof Banner);
    }

    public static boolean isSpinnable(Block block) {
        return isSpinnable(block.getState());
    }

    public static boolean isSpinnable(Location loc) {
        return isSpinnable(loc.getBlock());
    }

    /*
    Make sure we only spin certain entities.
    Maybe Items someday but not today;
     */
    public static boolean isSpinnable(Entity entity) {
        return  (entity instanceof LivingEntity || entity instanceof ItemFrame || entity instanceof Vehicle);
    }

}
