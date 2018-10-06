package net.awesomepowered.rotator.utils;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class Spinner {

    public static boolean isSpinnable(Material material) {
         return (material == Material.LEGACY_SKULL || material == Material.LEGACY_SIGN_POST || material == Material.LEGACY_STANDING_BANNER);
    }

    public static boolean isSpinnable(BlockState blockState) {
        return (blockState instanceof Sign || blockState instanceof Skull || blockState instanceof Banner);
    }

    public static boolean isSpinnable(Block block) {
        return isSpinnable(block.getType());
    }

    /*
    Make sure we only spin mobs and players.
    Maybe Items someday but not today;
     */
    public static boolean isSpinnable(Entity entity) {
        return  (entity instanceof LivingEntity);
    }

}
