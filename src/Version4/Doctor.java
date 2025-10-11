package Version4;

import java.awt.image.BufferedImage;
import javax.sound.sampled.*;

public class Doctor extends Boss {
    // Combat constants
    public static final double INIT_HEALTH = (OmegaFight3.DEV_MODE? 100: 600) * Omegaman.PERC_MULT;
    public static final double SIZE_TO_HITBOX = 0.4;
    public static final double SIZE_TO_HURTBOX = 0.8;

    // State constants
    public static final int SPIT = 2;
    public static final int LAUGH = 3;
    public static final int NUM_STATES = 4;
    public static final int TRANS_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(220, 420), new Coord(280, 400), new Coord(280, 440), new Coord(400, 525)};
    public static final int[] STATE_SPRITE_HZ = {10, 10, 7, 5};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.SCREEN_CENTER.y),
        new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.stage[OmegaFight3.BATTLEFIELD_NO].platforms[1].y - (OmegaFight3.stage[OmegaFight3.BATTLEFIELD_NO].platforms[0].y - OmegaFight3.stage[OmegaFight3.BATTLEFIELD_NO].platforms[1].y)),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_CENTER.y)};
    public static final int[] STATE_TIME = {0, 60, 30, 300};
    public static final String[] STATE_NAME = {"spit", "laugh"};

    // Sprite constants
    public static int[] STATE_SPRITE_START = new int[NUM_STATES];
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.RIT_SIGN, OmegaFight3.LFT_SIGN, OmegaFight3.LFT_SIGN, OmegaFight3.RIT_SIGN};
    public static final int[] STATE_NUM_SPRITES = {2, 2, 3, 4};
    public static int[] STATE_ANIM_LEN = new int[NUM_STATES];
    public static final int TOT_NUM_SPRITES = 11;
    public static final double SIZE_TO_FIRE = 0.8;

    // Spit Constants
    public static final double SPIT_SPD = 7;
    public static final double SPIT_START_ANGLE = Math.PI / 3;
    public static final double SPIT_SPREAD = Math.PI / 6;
    public static final int SPIT_NUM_PROJS = 4;
    public static final int SPIT_HZ = 48;
    public static final Coord COORD_TO_SPIT = new Coord(STATE_SIZE[SPIT].x / 4, STATE_SIZE[SPIT].y * 3 / 10);

    // Laugh constants
    public static final int LAUGH_NUM_PROJS = 5;
    public static final int LAUGH_HZ = 60;
    public static final double ANGLE_BETWEEN_ENERGY = Math.PI / (LAUGH_NUM_PROJS - 1) * STATE_SPRITE_SIGN[LAUGH];
    public static final Coord COORD_TO_GEM = new Coord(STATE_SIZE[LAUGH].x * 23 / 80, STATE_SIZE[LAUGH].y * 7 / 20);

    // Background attack constants
    public static final double PINCER_AMT_SCALING_TO_HEALTH = 0.3;
    public static final int PINCER_HZ = 1200;
    public static final int MIN_PINCER_HZ = 240;
    public static final double PINCER_THRESHOLD = 0.7;
    public static final int BOMBOT_HZ = 1800;
    public static final int MIN_BOMBOT_HZ = 300;
    public static final double BOMBOT_THRESHOLD = 0.4;

    // Background attack variables
    public int pincerCounter;
    public int bombotCounter;

    // Static variables
    public static BufferedImage[] docSprite = new BufferedImage[TOT_NUM_SPRITES];
    public static Clip hahaha;
    public static Clip blblbl;

    // Constructor
    public Doctor() {
        super(docSprite, STATE_COORD, STATE_SIZE, STATE_SPRITE_HZ, STATE_TIME, STATE_SPRITE_START, STATE_SPRITE_SIGN, STATE_NUM_SPRITES, INIT_HEALTH , SIZE_TO_HITBOX, SIZE_TO_HURTBOX, SIZE_TO_FIRE, IDLE, NUM_STATES, TRANS_TIME);
    }

    // Description: This method calculates the attacks of the Doctor
    public void attack() {
        calcSprites();

        // Spit nuts and bolts attack
        if (state == SPIT) {
            // Movement
            coord.x += (spriteSign * SPIT_SPD);
            if (coord.x < size.x / 2) {
                spriteSign = OmegaFight3.RIT_SIGN;
            }
            if (coord.x == STATE_COORD[SPIT].x) {
                spriteSign = OmegaFight3.LFT_SIGN;
                frameCounter = 0;
            }

            // Spits nuts and bolts periodically
            else if (frameCounter == 0) {
                frameCounter = SPIT_HZ;
                for (int i = 0; i != SPIT_NUM_PROJS; i++) {
                    OmegaFight3.projectiles.add(new Fastener(this, new Coord(coord.x + COORD_TO_SPIT.x * spriteSign, coord.y + COORD_TO_SPIT.y),
                    -Math.PI / 2 + (SPIT_START_ANGLE + Math.random() * SPIT_SPREAD) * spriteSign));
                }
            }
        }

        // Laughing gem attack
        else if (state == LAUGH) {
            // Alternate between LAUGH_NUM_PROJS and LAUGH_NUM_PROJS - 1 so that players can't just stay in one place to dodge
            if (frameCounter % (LAUGH_HZ * 2) == 0) {
                for (int i = 0; i != LAUGH_NUM_PROJS - 1; i++) { // Might add spritesign when adding COORD_TO_GEM if needed
                    OmegaFight3.projectiles.add(new Energy(this, coord.add(COORD_TO_GEM), -Math.PI / 2 + ANGLE_BETWEEN_ENERGY * (i + 0.5)));
                }
            }
            else if (frameCounter % LAUGH_HZ == 0) {
                for (int i = 0; i != LAUGH_NUM_PROJS; i++) {
                    OmegaFight3.projectiles.add(new Energy(this, coord.add(COORD_TO_GEM), -Math.PI / 2 + ANGLE_BETWEEN_ENERGY * i));
                }
            }
        }

        checkFinish();
    }

    // Description: This method calculates all of the background attacks of the doctor
    public void backgroundAttack() {
        double difficultyMult = OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty];
        // Pincer attack
        if (health <= INIT_HEALTH  * PINCER_THRESHOLD) {
            pincerCounter++;
            if (pincerCounter >= Math.max(MIN_PINCER_HZ, PINCER_HZ * health / (INIT_HEALTH  * PINCER_THRESHOLD)) / difficultyMult) {
                OmegaFight3.projectiles.add(new Pincer(this, new Coord((int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.x, (int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.y)));
                pincerCounter = 0;
                OmegaFight3.play(Pincer.zzzClick);
            }
        }

        // Homing missile attack
        if (health <= INIT_HEALTH  * BOMBOT_THRESHOLD) {
            bombotCounter++;
            if (bombotCounter >= Math.max(MIN_BOMBOT_HZ, BOMBOT_HZ * health / (INIT_HEALTH  * BOMBOT_THRESHOLD)) / difficultyMult) {
                int sign = OmegaFight3.randomSign();
                OmegaFight3.projectiles.add(new Bombot(this, new Coord(OmegaFight3.SCREEN_CENTER.x - (OmegaFight3.SCREEN_CENTER.x + Bombot.SIZE.x / 2) * sign, OmegaFight3.SCREEN_SIZE.y * Math.random()), OmegaFight3.signToRadians(sign), sign));
                bombotCounter = 0;
            }
        }
    }

    public void changeStateSfx() {
        if (state == LAUGH) {
            OmegaFight3.play(hahaha);
        }
        else if (state == SPIT) {
            OmegaFight3.play(blblbl);
        }
    }
}
