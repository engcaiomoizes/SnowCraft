package br.com.caiomoizes.snowCraft.factions;

import org.bukkit.Location;
import org.bukkit.World;

public class Region {

    private Location center;

    private int x;
    private int y;
    private int z;
    private World world;
    private int range;

    public Region() {
    }

    public Location getCenter() {
        return center;
    }
    public void setCenter(Location center) {
        this.center = center;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public World getWorld() {
        return world;
    }
    public void setWorld(World world) {
        this.world = world;
    }

    public int getRange() {
        return range;
    }
    public void setRange(int range) {
        this.range = range;
    }

}