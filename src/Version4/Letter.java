package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Letter {
    // Constants
    public static final int LETTER_ANIM_LEN = 12;
    public static final int NUM_LETTERS = 10;
    public static final double LETTERS_LFT_X = OmegaFight3.SCREEN_CENTER.x - (OmegaFight3.TITLE_SIZE.x) / 2;
    public static final double LETTER_MIN_SIZE = 0;
    public static final double LETTER_POW = 6;
    public static final Coord[] LETTER_COORDS = {new Coord(194 + LETTERS_LFT_X, 58 + OmegaFight3.TITLE_Y),
    new Coord(292 + LETTERS_LFT_X, 59 + OmegaFight3.TITLE_Y),
    new Coord(397.5 + LETTERS_LFT_X, 59 + OmegaFight3.TITLE_Y),
    new Coord(501.5 + LETTERS_LFT_X, 59 + OmegaFight3.TITLE_Y),
    new Coord(604.5 + LETTERS_LFT_X, 60 + OmegaFight3.TITLE_Y),
    new Coord(110 + LETTERS_LFT_X, 249 + OmegaFight3.TITLE_Y),
    new Coord(259.5 + LETTERS_LFT_X, 249 + OmegaFight3.TITLE_Y),
    new Coord(402 + LETTERS_LFT_X, 249.5 + OmegaFight3.TITLE_Y),
    new Coord(546.5 + LETTERS_LFT_X, 249.5 + OmegaFight3.TITLE_Y),
    new Coord(672 + LETTERS_LFT_X, 249.5 + OmegaFight3.TITLE_Y)};
    public static final Coord[] LETTER_SIZE = {new Coord(128, 116),
    new Coord(120, 116),
    new Coord(111, 116),
    new Coord(119, 116),
    new Coord(135, 116),
    new Coord(220, 214),
    new Coord(157, 214),
    new Coord(150, 213),
    new Coord(179, 213),
    new Coord(162, 213)};

    // Instance variables
    public Coord coord;
    public Coord size;
    public int frameCounter;
    public boolean isVertical;
    public BufferedImage image;

    // Static images
    public static BufferedImage[] letters = new BufferedImage[NUM_LETTERS];

    // Constructor
    public Letter(Coord coord, Coord size, BufferedImage image, boolean isVertical) {
        this.coord = coord;
        this.size = size;
        this.image = image;
        this.isVertical = isVertical;
    }

    // Description: This method processes the letter
    public void process() {
        if (frameCounter != LETTER_ANIM_LEN) {
            frameCounter++;
        }
    }

    // Description: This method draws the letter
    public void draw(Graphics g) {
        // Variables
        Coord curSize;
        double progress = Math.pow((double) frameCounter / LETTER_ANIM_LEN, LETTER_POW);

        // Vertical blip calculation
        if (isVertical) {
            curSize = new Coord(size.x * OmegaFight3.lerp(LETTER_MIN_SIZE, 1.0, progress), OmegaFight3.lerp(OmegaFight3.SCREEN_SIZE.y, size.y, progress));
        }

        // Horizontal blip calculation
        else {
            curSize = new Coord(OmegaFight3.lerp(OmegaFight3.SCREEN_SIZE.x, size.x, progress), size.y * OmegaFight3.lerp(LETTER_MIN_SIZE, 1.0, progress));
        }

        // Drawing
        Coord drawCoord = coord.add(curSize.scaledBy(-0.5));
        g.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) (curSize.x), (int) (curSize.y), null);
    }
}
