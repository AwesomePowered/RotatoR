package net.awesomepowered.rotator.utils;

import org.bukkit.Location;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;

public class Spinner {


    public static boolean isSpinnable(BlockState blockState) {
        //return (blockState.getBlockData() instanceof Rotatable);
        return (blockState instanceof Sign || blockState instanceof Skull || blockState instanceof Banner);
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
