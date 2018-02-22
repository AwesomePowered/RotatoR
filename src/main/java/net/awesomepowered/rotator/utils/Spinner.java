package net.awesomepowered.rotator.utils;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

public class Spinner {

    public static boolean isSpinnable(Material material) {
         return (material == Material.SKULL || material == Material.SIGN_POST || material == Material.STANDING_BANNER);
    }

    public static boolean isSpinnable(BlockState blockState) {
        return (blockState instanceof Sign || blockState instanceof Skull || blockState instanceof Banner);
    }

    public static boolean isSpinnable(Block block) {
        return isSpinnable(block.getType());
    }

}
