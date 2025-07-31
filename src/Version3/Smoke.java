package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Smoke {
    // Constants
    public static final int SMOKE_CHANGE_HZ = 8;
    public static final int NUM_SMOKES = 4;
    public static final int SMOKE_LEN = SMOKE_CHANGE_HZ * NUM_SMOKES;
    public static final Coord SMOKE_SIZE = new Coord(64);
    public static final double SMOKE_VEL_SCALE = 0.25;
    public static final double SMOKE_DESIGNED_SIZE = 100;

    // static images
    public static BufferedImage[] smokes = new BufferedImage[NUM_SMOKES];

    // Instance variables (Data encapsulation)
    private int frameCounter;
    private double rotation;
    private Coord coord;
    private Coord size;

    // Constructor
    public Smoke(Coord coord, Coord size, double rotation) {
        this.coord = coord;
        this.size = size;
        this.rotation = rotation;
        frameCounter = SMOKE_LEN;
    }

    // Description: This method draws the smoke
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(smokes[(SMOKE_LEN - frameCounter) / SMOKE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }

    // Getters
    public int getFrameCounter() {
        return frameCounter;
    }

    public double getRotation() {
        return rotation;
    }

    public Coord getCoord() {
        return coord;
    }

    public Coord getSize() {
        return size;
    }

    // Setters
    public void setFrameCounter(int frameCounter) {
        this.frameCounter = frameCounter;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public void setSize(Coord size) {
        this.size = size;
    }
}
