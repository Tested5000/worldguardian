package me.test5000.worldguardian.command;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3f;

public class TempPlayerSel {
    public Vector3f getPos1() {
        return pos1;
    }

    public void setPos1(Vector3f pos1) {
        this.pos1 = pos1;
    }

    public Vector3f getPos2() {
        return pos2;
    }

    public void setPos2(Vector3f pos2) {
        this.pos2 = pos2;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    private Vector3f pos1;
    private Vector3f pos2;
    private Level level;

}
