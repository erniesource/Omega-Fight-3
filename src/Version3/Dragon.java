package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Dragon extends Boss{
    // Combat constants
    public static final double INITIAL_HEALTH = 500 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final int HURT_BLINK_HZ = 1;

    // Sprite constants
    public static final int[] STATE_SPRITE_START = {0, 4, 8, 12};
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.RIGHT_SIGN, OmegaFight3.RIGHT_SIGN, OmegaFight3.RIGHT_SIGN, OmegaFight3.LEFT_SIGN};
    public static final int[] STATE_NO_SPRITES = {4, 4, 4, 3};
    public static final int NO_OF_SPRITES = 15;

    // State constants
    public static final int DIZZY = 2;
    public static final int BARF = 3;
    public static final int NO_OF_STATES = 4;
    public static final int TRANSITION_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(880, 700), new Coord(660, 835), new Coord(530, 900), new Coord(690, 850)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {5, 5, 5, 7};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y * 5 / 8),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y * 5 / 8),
        new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, STATE_SIZE[BARF].y / 2)};
    public static final int[] STATE_TIME = {20, 60, 320, 300};

    // Dizzy Constants
    public static final int DIZZY_NUM_PROJS = 3;
    public static final int DIZZY_HZ = 15;
    public static final int DIZZY_PAUSE = 80;
    public static final Coord COORD_TO_DIZZY_COORD = new Coord(STATE_SIZE[DIZZY].x / 8, -STATE_SIZE[DIZZY].y / 3);

    // Barf constants
    public static final int BARF_TO_GAG_HZ = 4;
    public static final Coord COORD_TO_BARF_COORD = new Coord(STATE_SIZE[BARF].x * 7 / 25, -STATE_SIZE[BARF].y / 5);

    // Background attack constants
    public static final double BUBBLE_AMT_SCALING_TO_HEALTH = 0.3;
    public static final int BUBBLE_HZ = 240;
    public static final int MIN_BUBBLE_HZ = 120;
    public static final double BUBBLE_THRESHOLD = 0.7;
    public static final double FIRE_AMT_SCALING_TO_HEALTH = 0.25;
    public static final int FIRE_HZ = 240;
    public static final int MIN_FIRE_HZ = 120;
    public static final double FIRE_THRESHOLD = 0.4;

    // Background attack variables
    public int bubbleCounter;
    public int fireCounter;

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    public Dragon(double difficulty) {
        super(STATE_COORD[IDLE].copy(), STATE_SPRITE_START[IDLE], STATE_SPRITE_SIGN[IDLE], STATE_TIME[IDLE], STATE_SIZE[IDLE].copy(), IDLE, INITIAL_HEALTH, difficulty);
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
        else if (state == DIZZY) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[DIZZY] == 0) {
                spriteNo = STATE_SPRITE_START[DIZZY] + (spriteNo - STATE_SPRITE_START[DIZZY] + 1) % STATE_NO_SPRITES[DIZZY];
            }
            if (frameCounter % (DIZZY_HZ * DIZZY_NUM_PROJS + DIZZY_PAUSE) < DIZZY_HZ * DIZZY_NUM_PROJS) {
                if (frameCounter % DIZZY_HZ == 0) {
                    Omegaman target = null;
                    double minDist = Double.MAX_VALUE;
                    for (Omegaman omega : OmegaFight3.omegaman) {
                        if (omega.state == Omegaman.ALIVE_STATE) {
                            double dist = Math.hypot(omega.coord.x - (coord.x + COORD_TO_DIZZY_COORD.x * spriteSign), omega.coord.y - (coord.y + COORD_TO_DIZZY_COORD.y));
                            if (dist < minDist) {
                                minDist = dist;
                                target = omega;
                            }
                        }
                    }

                    if (target != null) {
                        double angle = Math.atan2(target.coord.y - (coord.y + COORD_TO_DIZZY_COORD.y), target.coord.x - (coord.x + COORD_TO_DIZZY_COORD.x * spriteSign));
                        projectiles.add(new Ring(this, new Coord (coord.x + COORD_TO_DIZZY_COORD.x * spriteSign, coord.y + COORD_TO_DIZZY_COORD.y), angle));
                    }
                }
            }
        }
        else if (state == BARF) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[BARF] == 0) {
                spriteNo = STATE_SPRITE_START[BARF] + (spriteNo - STATE_SPRITE_START[BARF] + 1) % STATE_NO_SPRITES[BARF];
            }
            if (frameCounter % (STATE_NO_SPRITES[BARF] * STATE_SPRITE_CHANGE_HZ[BARF] * BARF_TO_GAG_HZ) == STATE_SPRITE_CHANGE_HZ[BARF]) {
                projectiles.add(new Meteor(this, coord.x + COORD_TO_BARF_COORD.x * spriteSign, spriteSign));
            }
        }
        if (frameCounter == 0) {
            transitionTo = (int) (Math.random() * (NO_OF_STATES - 1)) + 1;
            if (transitionTo == state) {
                transitionTo = NO_TRANSITION;
                frameCounter = STATE_TIME[state];
                spriteNo = STATE_SPRITE_START[state];
            }
            else {
                frameCounter = TRANSITION_TIME;
                spriteNo = STATE_SPRITE_START[IDLE];
            }
        }
    }

    public void backgroundAttack() {
        if (health < INITIAL_HEALTH * difficulty * BUBBLE_THRESHOLD) {
            bubbleCounter++;
            if (bubbleCounter >= Math.max(MIN_BUBBLE_HZ, BUBBLE_HZ * health / (INITIAL_HEALTH * difficulty * BUBBLE_THRESHOLD) / BUBBLE_AMT_SCALING_TO_HEALTH)) {
                projectiles.add(new Bubble(this, new Coord((int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.x, OmegaFight3.SCREEN_SIZE.y - Bubble.SIZE.y / 2)));
                bubbleCounter = 0;
            }
        }
        if (health < INITIAL_HEALTH * difficulty * FIRE_THRESHOLD) {
            fireCounter++;
            if (fireCounter >= Math.max(MIN_FIRE_HZ, FIRE_HZ * health / (INITIAL_HEALTH * difficulty * FIRE_THRESHOLD) / FIRE_AMT_SCALING_TO_HEALTH)) {
                projectiles.add(new Fire(this, new Coord(Math.random() * (OmegaFight3.SCREEN_SIZE.x - Fire.SIZE.x) + Fire.SIZE.x / 2, 0), Math.PI / 2));
                fireCounter = 0;
            }
        }
    }

    public void draw(Graphics g) {
        if (!hurt || hurtCounter >= HURT_BLINK_HZ) {
            if (spriteSign == 1) g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), null);
            else g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 + size.x), (int) (coord.y - size.y / 2), (int) -size.x, (int) size.y, null);
        }
        if (hurt) {
            hurtCounter = (hurtCounter + 1) % (HURT_BLINK_HZ * 2);
            hurt = false;
        }
        else {
            hurtCounter = Boss.NOT_HURT;
        }
    }

    public void fall() {
        super.fall();
        frameCounter = (frameCounter + 1) % STATE_SPRITE_CHANGE_HZ[DEAD];
        if (frameCounter == 0) {
            spriteNo = (spriteNo + 1) % STATE_NO_SPRITES[DEAD];
        }
        if (coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
            frameCounter = 0;
            spriteNo = 0;
            OmegaFight3.screenShakeCounter += DIE_SCREENSHAKE;
        }
    }

    public void prepareToDie() {
        super.prepareToDie();
        spriteNo = STATE_SPRITE_START[DEAD];
        size = STATE_SIZE[DEAD];
    }
}
