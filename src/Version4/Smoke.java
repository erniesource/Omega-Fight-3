package Version4;

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
    public Smoke(Coord coord, Coord size) {
        this(coord, size, OmegaFight3.randomDir());
    }

    public Smoke(Coord coord, Coord size, double rotation) {
        this.coord = coord;
        this.size = size;
        this.rotation = rotation;
        frameCounter = SMOKE_LEN;
    }

    // Description: This method draws the smoke
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(smokes[(SMOKE_LEN - frameCounter) / SMOKE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
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

class Explosion {
    // Constants
    public static final int EXPLOSION_CHANGE_HZ = 4;
    public static final int NUM_EXPLOSIONS = 8;
    public static final int EXPLOSION_LEN = EXPLOSION_CHANGE_HZ * NUM_EXPLOSIONS;

    // static images
    public static BufferedImage[] explosions = new BufferedImage[NUM_EXPLOSIONS];

    // Instance variables (Data encapsulation)
    public int frameCounter;
    public Coord coord;
    public Coord size;

    // Constructor
    public Explosion(Coord coord, Coord size) {
        this.coord = coord;
        this.size = size;
        frameCounter = EXPLOSION_LEN;
        OmegaFight3.play(OmegaFight3.boom);
    }

    // Description: This method draws the explosion
    public void draw(Graphics g) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g.drawImage(explosions[(EXPLOSION_LEN - frameCounter) / EXPLOSION_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
    }
}

class Wake{
    // Constants
    public static final int WAKE_LEN = 10;
    public static final int DESIGNED_SIZE_X = 120;
    public static final double SIZE_X_TO_Y = 43.0 / DESIGNED_SIZE_X;
    public static final double CHAR_SIZE_X_TO_MAX = DESIGNED_SIZE_X / Omegaman.SIZE.x;
    public static final double MIN_SIZE = 0.2;
    public static final double MIN_OPACITY = 0;

    public static BufferedImage wakeImg;

    public int frameCounter;
    public Coord coord;
    public double maxSizeX;

    public Wake(Coord coord, double charSizeX) {
        this.coord = coord;
        maxSizeX = charSizeX * CHAR_SIZE_X_TO_MAX;
    }

    public void draw(Graphics2D g2) {
        Coord drawSize = (new Coord(maxSizeX, maxSizeX * SIZE_X_TO_Y)).scaledBy(OmegaFight3.lerp(MIN_SIZE, 1, (double) frameCounter / WAKE_LEN));
        Coord drawCoord = coord.add(drawSize.scaledBy(-0.5));
        OmegaFight3.setOpacity(OmegaFight3.lerp(1, MIN_OPACITY, (double) frameCounter / WAKE_LEN), g2);
        g2.drawImage(wakeImg, (int) (drawCoord.x), (int) (drawCoord.y), (int) drawSize.x, (int) drawSize.y, null);
        OmegaFight3.resetOpacity(g2);
    }
}