package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Doctor extends Boss {
    // Images
    public static BufferedImage[] sprite = new BufferedImage[2];

    // Combat constants
    public static final double INITIAL_HEALTH = 1000;

    // Sprite constants
    public static final int IDLE_SPRITE_START = 0;
    public static final int NO_IDLE_SPRITES = 2;

    // State constants
    public static final int IDLE = 0;

    // IDLE constants
    public static final Coord IDLE_SIZE = new Coord(280, 400);
    public static final int IDLE_SPRITE_CHANGE_HZ = 10;
    public static final Coord IDLE_COORD = new Coord(1440, 480);

    public Doctor(double difficulty) {
        super(IDLE_COORD.copy(), IDLE_SPRITE_START, OmegaFight3.LEFT_SIGN, 0, IDLE_SIZE.copy(), IDLE, INITIAL_HEALTH * difficulty);
    }

    public void process() {
        super.process();
        if (state == IDLE) {
            frameCounter = (frameCounter + 1) % IDLE_SPRITE_CHANGE_HZ;
            if (frameCounter == 0) {
                spriteNo = IDLE_SPRITE_START + (spriteNo - IDLE_SPRITE_START + 1) % NO_IDLE_SPRITES;
            }
        }
    }

    public void draw(Graphics g) {
        if (spriteSign == 1) g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), null);
        else g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 + size.x), (int) (coord.y - size.y / 2), (int) -size.x, (int) size.y, null);
    }
}
