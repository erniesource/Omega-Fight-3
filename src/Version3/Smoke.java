package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Smoke {
    public static final int SMOKE_CHANGE_HZ = 8;
    public static final int NUM_SMOKES = 4;
    public static final int SMOKE_LEN = SMOKE_CHANGE_HZ * NUM_SMOKES;
    public static final Coord SMOKE_SIZE = new Coord(64, 64);
    public static final double SMOKE_VEL_SCALE = 0.25;
    public static final double SMOKE_DESIGNED_SIZE = 100;

    public static BufferedImage[] smokes = new BufferedImage[NUM_SMOKES];

    public int frameCounter;
    public double rotation;
    public Coord coord;
    public Coord size;

    public Smoke(Coord coord, Coord size, double rotation) {
        this.coord = coord;
        this.size = size;
        this.rotation = rotation;
        frameCounter = SMOKE_LEN;
    }

    public void process() {
        frameCounter--;
    }

    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(smokes[(SMOKE_LEN - frameCounter) / SMOKE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }
}
