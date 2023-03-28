package me.test5000.worldguardian.utils;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;

public class Utils {

    public static String chunkToRaw(FullChunk chunk) {
        return "" + chunk.getX() + ":" + chunk.getZ();
    }
    public static String chunkToRaw(Vector2f chunk) {
        return "" + chunk.getX() + ":" + chunk.getY();
    }


    public static boolean collision(Vector2f rect1, Vector2f rect2) {
        // Check if the rectangles intersect along the x-axis
        if (
                rect1.x <= rect2.x && rect2.x <= rect1.x  &&
                rect1.y <= rect2.y && rect2.x <= rect1.y
        ) {
            return true;
        }
        return false; // No collision detected
    }


}
