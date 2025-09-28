package Version4;

import java.awt.image.BufferedImage;
import javax.sound.sampled.*;

public class Dragon extends Boss{
    // Combat constants
    public static final double INIT_HEALTH = (OmegaFight3.DEV_MODE? 100: 600) * Omegaman.PERC_MULT;
    public static final double SIZE_TO_HITBOX = 0.2;
    public static final double SIZE_TO_HURTBOX = 0.7;

    // State constants
    public static final int DIZZY = 2;
    public static final int BARF = 3;
    public static final int NUM_STATES = 4;
    public static final int TRANS_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(880, 700), new Coord(660, 835), new Coord(530, 900), new Coord(690, 850)};
    public static final int[] STATE_SPRITE_HZ = {5, 5, 5, 7};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y * 5 / 8),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y * 5 / 8),
        new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, STATE_SIZE[BARF].y / 2)};
    public static final int[] STATE_TIME = {0, 60, 375, 336};
    public static final String[] STATE_NAME = {"dizzy", "barf"};

    // Sprite constants
    public static int[] STATE_SPRITE_START = new int[NUM_STATES];
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.RIT_SIGN, OmegaFight3.RIT_SIGN, OmegaFight3.RIT_SIGN, OmegaFight3.LFT_SIGN};
    public static final int[] STATE_NUM_SPRITES = {4, 4, 4, 3};
    public static final int TOT_NUM_SPRITES = 15;
    public static final double SIZE_TO_FIRE = 0.5;

    // Dizzy Constants
    public static final int DIZZY_NUM_PROJS = 3;
    public static final int DIZZY_HZ = 15;
    public static final int DIZZY_PAUSE = 80;
    public static final Coord COORD_TO_DIZZY = new Coord(STATE_SIZE[DIZZY].x / 8, -STATE_SIZE[DIZZY].y / 3);

    // Barf constants
    public static final int BARF_TO_GAG_HZ = 4;
    public static final int BARF_SPAWN_SPRITE_NO = 2;
    public static final Coord COORD_TO_BARF = new Coord(STATE_SIZE[BARF].x * 7 / 25, -STATE_SIZE[BARF].y / 5);

    // Background attack constants
    public static final int BUBBLE_HZ = 720;
    public static final int MIN_BUBBLE_HZ = 120;
    public static final double BUBBLE_THRESHOLD = 0.7;
    public static final int FIRE_HZ = 960;
    public static final int MIN_FIRE_HZ = 120;
    public static final double FIRE_THRESHOLD = 0.4;

    // Static variables
    public static BufferedImage[] dragonSprite = new BufferedImage[TOT_NUM_SPRITES];
    public static Clip donk;
    public static Clip roar;

    // Background attack variables
    public int bubbleCounter;
    public int fireCounter;
    public double dizzyAngle;

    // Constructor
    public Dragon() {
        super(dragonSprite, STATE_COORD, STATE_SIZE, STATE_SPRITE_HZ, STATE_TIME, STATE_SPRITE_START, STATE_SPRITE_SIGN, STATE_NUM_SPRITES, INIT_HEALTH , SIZE_TO_HITBOX, SIZE_TO_HURTBOX, SIZE_TO_FIRE, IDLE, NUM_STATES, TRANS_TIME);
    }

    // Description: This method calculates the dragon attacking
    public void attack() {
        calcSprites();

        // Laser eyes attack
        if (state == DIZZY) {
            // Fire laser ring at closest alive player
            if (frameCounter % (DIZZY_HZ * DIZZY_NUM_PROJS + DIZZY_PAUSE) < DIZZY_HZ * DIZZY_NUM_PROJS) {
                if (frameCounter % (DIZZY_HZ * DIZZY_NUM_PROJS + DIZZY_PAUSE) % DIZZY_HZ == 0) {
                    if (frameCounter % (DIZZY_HZ * DIZZY_NUM_PROJS + DIZZY_PAUSE) == DIZZY_HZ * (DIZZY_NUM_PROJS - 1)) {
                        Omegaman target = null;
                        double minDist = Double.MAX_VALUE;
                        for (Omegaman omega : OmegaFight3.omegaman) {
                            if (omega.state == Omegaman.ALIVE_STATE) {
                                double dist = Math.hypot(omega.coord.x - (coord.x + COORD_TO_DIZZY.x * spriteSign), omega.coord.y - (coord.y + COORD_TO_DIZZY.y));
                                if (dist < minDist) {
                                    minDist = dist;
                                    target = omega;
                                }
                            }
                        }
                        if (target != null) dizzyAngle = Math.atan2(target.coord.y - (coord.y + COORD_TO_DIZZY.y), target.coord.x - (coord.x + COORD_TO_DIZZY.x * spriteSign));
                    }

                    OmegaFight3.projectiles.add(new Ring(this, coord.add(COORD_TO_DIZZY), dizzyAngle));
                }
            }
        }

        // Meteors/Meatball cosine wave attack
        else if (state == BARF) {
            // Barf meteor
            if (frameCounter % (STATE_NUM_SPRITES[BARF] * STATE_SPRITE_HZ[BARF] * BARF_TO_GAG_HZ) == STATE_SPRITE_HZ[BARF] * (STATE_NUM_SPRITES[BARF] - BARF_SPAWN_SPRITE_NO)) {
                OmegaFight3.projectiles.add(new Meteor(this, coord.x + COORD_TO_BARF.x * spriteSign, spriteSign));
            }
        }

        checkFinish();
    }

    // Description: This method calculates the background attacks of the dragon
    public void backgroundAttack() {
        double difficultyMult = OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty];
        // Bubble attack
        if (health <= INIT_HEALTH  * BUBBLE_THRESHOLD) {
            bubbleCounter++;
            if (bubbleCounter >= Math.max(MIN_BUBBLE_HZ, BUBBLE_HZ * health / (INIT_HEALTH  * BUBBLE_THRESHOLD) / difficultyMult)) {
                OmegaFight3.projectiles.add(new Bubble(this, new Coord(OmegaFight3.randomSign() * (OmegaFight3.SCREEN_CENTER.x + Bubble.SIZE.x / 2) + OmegaFight3.SCREEN_CENTER.x, OmegaFight3.SCREEN_SIZE.y - Bubble.SIZE.y / 2)));
                bubbleCounter = 0;
                OmegaFight3.play(Bubble.bububup);
            }
        }

        // Flames from the ceiling attack
        if (health <= INIT_HEALTH  * FIRE_THRESHOLD) {
            fireCounter++;
            if (fireCounter >= Math.max(MIN_FIRE_HZ, FIRE_HZ * health / (INIT_HEALTH  * FIRE_THRESHOLD) / difficultyMult)) {
                OmegaFight3.projectiles.add(new Fire(this, new Coord(Math.random() * (OmegaFight3.SCREEN_SIZE.x - Fire.SIZE.x) + Fire.SIZE.x / 2, 0), Math.PI / 2));
                fireCounter = 0;
                OmegaFight3.play(Fire.foosh);
            }
        }
    }

    public void changeStateSfx() {
        if (state == DIZZY) {
            OmegaFight3.play(donk);
        }
        else if (state == BARF) {
            OmegaFight3.play(roar);
        }
    }
}
