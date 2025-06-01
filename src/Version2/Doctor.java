package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Doctor extends Boss {
    // Combat constants
    public static final double INITIAL_HEALTH = 1000;

    // Sprite constants
    public static final int[] STATE_SPRITE_START = {0, 2, 5};
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.LEFT_SIGN, OmegaFight3.LEFT_SIGN, OmegaFight3.RIGHT_SIGN};
    public static final int[] STATE_NO_SPRITES = {2, 3, 4};
    public static final int NO_OF_SPRITES = 9;

    // Spit Constants
    public static final double SPIT_SPEED = 10;

    // State constants
    public static final int SPIT = 1;
    public static final int LAUGH = 2;
    public static final int NO_OF_STATES = 3;
    public static final int TRANSITION_TIME = OmegaFight3.FPS * 2;
    public static final Coord[] STATE_SIZE = {new Coord(280, 400), new Coord(280, 440), new Coord(400, 525)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {10, 7, 5};
    public static final Coord[] STATE_COORD = {new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[1].y - (OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[0].y - OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[1].y)),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y / 2)};
    public static final int[] STATE_TIME = {60, STATE_SPRITE_CHANGE_HZ[SPIT], 300};

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    public Doctor(double difficulty) {
        super(STATE_COORD[IDLE].copy(), STATE_SPRITE_START[IDLE], STATE_SPRITE_SIGN[IDLE], STATE_TIME[IDLE], STATE_SIZE[IDLE].copy(), IDLE, INITIAL_HEALTH * difficulty);
    }

    public void transition() {
        frameCounter--;
        if (frameCounter % STATE_SPRITE_CHANGE_HZ[IDLE] == 0) {
            spriteNo = STATE_SPRITE_START[IDLE] + (spriteNo - STATE_SPRITE_START[IDLE] + 1) % STATE_NO_SPRITES[IDLE];
        }
        coord.x = OmegaFight3.lerp(STATE_COORD[transitionTo].x, STATE_COORD[state].x, (double) frameCounter / TRANSITION_TIME);
        coord.y = OmegaFight3.lerp(STATE_COORD[transitionTo].y, STATE_COORD[state].y, (double) frameCounter / TRANSITION_TIME);
        if (frameCounter == 0) {
            state = transitionTo;
            transitionTo = NO_TRANSITION;
            frameCounter = STATE_TIME[state];
            spriteNo = STATE_SPRITE_START[state];
            spriteSign = STATE_SPRITE_SIGN[state];
            size = STATE_SIZE[state].copy();
        }
    }

    public void attack() {
        frameCounter--;
        if (state == IDLE) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[IDLE] == 0) {
                spriteNo = STATE_SPRITE_START[IDLE] + (spriteNo - STATE_SPRITE_START[IDLE] + 1) % STATE_NO_SPRITES[IDLE];
            }
        }
        else if (state == SPIT) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[SPIT] == 0) {
                spriteNo = STATE_SPRITE_START[SPIT] + (spriteNo - STATE_SPRITE_START[SPIT] + 1) % STATE_NO_SPRITES[SPIT];
            }
            coord.x += (spriteSign * SPIT_SPEED);
            if (coord.x < OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[0].leftX) {
                spriteSign = OmegaFight3.RIGHT_SIGN;
            }
            if (coord.x == STATE_COORD[SPIT].x) {
                spriteSign = OmegaFight3.LEFT_SIGN;
                frameCounter = 0;
            }
            else if (frameCounter == 0) {
                frameCounter = STATE_SPRITE_CHANGE_HZ[SPIT];
            }
        }
        else if (state == LAUGH) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[LAUGH] == 0) {
                spriteNo = STATE_SPRITE_START[LAUGH] + (spriteNo - STATE_SPRITE_START[LAUGH] + 1) % STATE_NO_SPRITES[LAUGH];
            }
        }
        if (frameCounter == 0) {
            transitionTo = (int) (Math.random() * NO_OF_STATES);
            if (transitionTo == state) {
                transitionTo = NO_TRANSITION;
                frameCounter = STATE_TIME[state];
            }
            else {
                frameCounter = TRANSITION_TIME;
                spriteNo = STATE_SPRITE_START[IDLE];
            }
        }
    }

    public void draw(Graphics g) {
        if (spriteSign == 1) g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), null);
        else g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 + size.x), (int) (coord.y - size.y / 2), (int) -size.x, (int) size.y, null);
    }
}
