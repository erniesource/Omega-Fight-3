package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Doctor extends Boss {
    // Background attack variables
    int pincerCounter;
    int bombotCounter;

    // Combat constants
    public static final double INITIAL_HEALTH = 750 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final int HURT_BLINK_HZ = 1;

    // Sprite constants
    public static final int[] STATE_SPRITE_START = {0, 2, 4, 7};
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.LEFT_SIGN, OmegaFight3.LEFT_SIGN, OmegaFight3.LEFT_SIGN, OmegaFight3.RIGHT_SIGN};
    public static final int[] STATE_NO_SPRITES = {2, 2, 3, 4};
    public static final int NO_OF_SPRITES = 11;

    // State constants
    public static final int SPIT = 2;
    public static final int LAUGH = 3;
    public static final int NO_OF_STATES = 4;
    public static final int TRANSITION_TIME = OmegaFight3.FPS * 2;
    public static final Coord[] STATE_SIZE = {new Coord(390, 430), new Coord(280, 400), new Coord(280, 440), new Coord(400, 525)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {10, 10, 7, 5};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[1].y - (OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[0].y - OmegaFight3.stage[Stage.BATTLEFIELD_NO].platforms[1].y)),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y / 2)};
    public static final int[] STATE_TIME = {20, 60, 30, 300};

    // Spit Constants
    public static final double SPIT_SPEED = 7;
    public static final double SPIT_START_ANGLE = Math.PI / 3;
    public static final double SPIT_SPREAD = Math.PI / 6;
    public static final int SPIT_NUM_PROJS = 4;
    public static final int SPIT_HZ = 60;
    public static final Coord COORD_TO_SPIT_COORD = new Coord(STATE_SIZE[SPIT].x / 4, STATE_SIZE[SPIT].y * 3 / 10);

    // Laugh constants
    public static final int LAUGH_NO_PROJS = 5;
    public static final int LAUGH_HZ = 60;
    public static final Coord COORD_TO_GEM_COORD = new Coord(STATE_SIZE[LAUGH].x * 23 / 80, STATE_SIZE[LAUGH].y * 7 / 20);

    // Background attack constants
    public static final double PINCER_AMT_SCALING_TO_HEALTH = 0.3;
    public static final int PINCER_HZ = 600;
    public static final int MIN_PINCER_HZ = 300;
    public static final double PINCER_THRESHOLD = 0.7;
    public static final double BOMBOT_AMT_SCALING_TO_HEALTH = 0.25;
    public static final int BOMBOT_HZ = 720;
    public static final int MIN_BOMBOT_HZ = 360;
    public static final double BOMBOT_THRESHOLD = 0.4;
    public static final int BOMBOT_NUM_SPAWN_LOCS = 2;
    public static final int LEFT_SPAWN = 0;
    public static final int RIGHT_SPAWN = 1;

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    public Doctor(double difficulty) {
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
        else if (state == SPIT) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[SPIT] == 0) {
                spriteNo = STATE_SPRITE_START[SPIT] + (spriteNo - STATE_SPRITE_START[SPIT] + 1) % STATE_NO_SPRITES[SPIT];
            }
            coord.x += (spriteSign * SPIT_SPEED);
            if (coord.x < size.x / 2) {
                spriteSign = OmegaFight3.RIGHT_SIGN;
            }
            if (coord.x == STATE_COORD[SPIT].x) {
                spriteSign = OmegaFight3.LEFT_SIGN;
                frameCounter = 0;
            }
            else if (frameCounter == 0) {
                frameCounter = SPIT_HZ;
                for (int i = 0; i != SPIT_NUM_PROJS; i++) {
                    projectiles.add(new Fastener(this, new Coord(coord.x + COORD_TO_SPIT_COORD.x * spriteSign, coord.y + COORD_TO_SPIT_COORD.y),
                    -Math.PI / 2 + (SPIT_START_ANGLE + Math.random() * SPIT_SPREAD) * spriteSign));
                }
            }
        }
        else if (state == LAUGH) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[LAUGH] == 0) {
                spriteNo = STATE_SPRITE_START[LAUGH] + (spriteNo - STATE_SPRITE_START[LAUGH] + 1) % STATE_NO_SPRITES[LAUGH];
            }
            if (frameCounter % (LAUGH_HZ * 2) == 0) {
                for (int i = 0; i != LAUGH_NO_PROJS - 1; i++) {
                    projectiles.add(new Energy(this, new Coord(coord.x + COORD_TO_GEM_COORD.x * spriteSign, coord.y + COORD_TO_GEM_COORD.y),
                    -Math.PI / 2 + Math.PI / (LAUGH_NO_PROJS - 1) / 2 + Math.PI / (LAUGH_NO_PROJS - 1) * i * spriteSign));
                }
            }
            else if (frameCounter % LAUGH_HZ == 0) {
                for (int i = 0; i != LAUGH_NO_PROJS; i++) {
                    projectiles.add(new Energy(this, new Coord(coord.x + COORD_TO_GEM_COORD.x * spriteSign, coord.y + COORD_TO_GEM_COORD.y),
                    -Math.PI / 2 + Math.PI / (LAUGH_NO_PROJS - 1) * i * spriteSign));
                }
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
        if (health < INITIAL_HEALTH * difficulty * PINCER_THRESHOLD) {
            pincerCounter++;
            if (pincerCounter >= Math.max(MIN_PINCER_HZ, PINCER_HZ * health / (INITIAL_HEALTH * difficulty * PINCER_THRESHOLD) / PINCER_AMT_SCALING_TO_HEALTH)) {
                projectiles.add(new Pincer(this, new Coord((int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.x, (int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.y)));
                pincerCounter = 0;
            }
        }
        if (health < INITIAL_HEALTH * difficulty * BOMBOT_THRESHOLD) {
            bombotCounter++;
            if (bombotCounter >= Math.max(MIN_BOMBOT_HZ, BOMBOT_HZ * health / (INITIAL_HEALTH * difficulty * BOMBOT_THRESHOLD) / BOMBOT_AMT_SCALING_TO_HEALTH)) {
                int spawn = (int) (Math.random() * BOMBOT_NUM_SPAWN_LOCS);
                if (spawn == LEFT_SPAWN) {
                    projectiles.add(new Bombot(this, new Coord(0, OmegaFight3.SCREEN_SIZE.y * Math.random()), 0, OmegaFight3.RIGHT_SIGN));
                }
                else if (spawn == RIGHT_SPAWN) {
                    projectiles.add(new Bombot(this, new Coord(OmegaFight3.SCREEN_SIZE.x, OmegaFight3.SCREEN_SIZE.y * Math.random()), Math.PI, OmegaFight3.LEFT_SIGN));
                }
                bombotCounter = 0;
            }
        }
    }

    public void draw(Graphics g) {
        if (!hurt || hurtCounter >= HURT_BLINK_HZ) {
            if (health > 0) {
                if (spriteSign == 1) g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), null);
                else g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 + size.x), (int) (coord.y - size.y / 2), (int) -size.x, (int) size.y, null);
            }
        }
        if (hurt) {
            hurtCounter = (hurtCounter + 1) % (HURT_BLINK_HZ * 2);
            hurt = false;
        }
        else {
            hurtCounter = Boss.NOT_HURT;
        }
    }
}
