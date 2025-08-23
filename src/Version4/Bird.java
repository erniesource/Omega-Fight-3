package Version4;

import java.awt.image.BufferedImage;
import javafx.util.Pair;

public class Bird extends Boss {
    // Combat constants
    public static final double INIT_HEALTH = (OmegaFight3.DEV_MODE? 100: 600) * Omegaman.PERC_MULT;
    public static final double SIZE_TO_HITBOX = 0.5;

    // State constants
    public static final int EAGLE_ART = 2;
    public static final int TWEAK = 3;
    public static final int NUM_STATES = 4;
    public static final int TRANS_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(520, 530), new Coord(645, 425), new Coord(650, 685), new Coord(710, 670)};
    public static final int[] STATE_SPRITE_HZ = {10, 5, 5, 7};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x - STATE_SIZE[IDLE].x / 2, OmegaFight3.SCREEN_CENTER.y),
        new Coord((OmegaFight3.stage[OmegaFight3.NORTH_CAVE_NO].platforms[1].leftX + OmegaFight3.stage[OmegaFight3.NORTH_CAVE_NO].platforms[1].rightX) / 2, OmegaFight3.SCREEN_CENTER.y),
        new Coord(OmegaFight3.SCREEN_CENTER.x, STATE_SIZE[TWEAK].y / 2)};
    public static final int[] STATE_TIME = {0, 60, 320, 480};
    public static final String[] STATE_NAME = {"eagleArtillery", "tweak"};

    // Sprite constants
    public static int[] STATE_SPRITE_START = new int[NUM_STATES];
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.RIT_SIGN, OmegaFight3.LFT_SIGN, OmegaFight3.RIT_SIGN, OmegaFight3.RIT_SIGN};
    public static final int[] STATE_NUM_SPRITES = {2, 4, 4, 3};
    public static final int TOT_NUM_SPRITES = 13;

    // Eagle Artillery Constants
    public static final double EAGLE_ART_SPREAD = -Math.PI / 7;
    public static final double EAGLE_ART_START_ANGLE = -Math.PI * 5 / 14;
    public static final double EAGLE_ART_SPAWN_SPRITE_NO = 2.5;
    public static final int GAGS_TO_EAGLE_ART = 4;
    public static final Coord COORD_TO_EAGLE_ART = new Coord(STATE_SIZE[EAGLE_ART].x / 3, -STATE_SIZE[EAGLE_ART].y / 4);

    // Tweak constants
    public static final int TWEAK_HZ = 15;
    public static final Coord COORD_TO_TWEAK = new Coord();
    public static final double TWEAK_AMP = Math.PI * 5 / 8;
    public static final double TWEAKS_PER_CYCLE = 2;

    // Background attack constants
    public static final int WAVE_HZ = 1080;
    public static final int MIN_WAVE_HZ = 180;
    public static final double WAVE_THRESHOLD = 0.7;
    public static final int DIVER_PER_WAVE = 3;
    public static final int WAVE_TIME = 90;
    public static final int NOT_WAVING = 0;
    public static final int WAVING_LFT = -1;
    public static final int WAVING_RIT = 1;
    public static final double DIVER_ALTITUDE = 25 + Diver.SIZE.y / 2;
    public static final int PUNK_HZ = 2400;
    public static final int MIN_PUNK_HZ = 300;
    public static final double PUNK_THRESHOLD = 0.5;
    public static final int MAX_PUNK = 2;

    // Images
    public static BufferedImage[] birdSprite = new BufferedImage[TOT_NUM_SPRITES];

    // Background attack variables
    public int waveCounter;
    public int waving;
    public int punkCounter;
    public int numPunks;

    // Constructor
    public Bird() {
        super(birdSprite, STATE_COORD, STATE_SIZE, STATE_SPRITE_HZ, STATE_TIME, STATE_SPRITE_START, STATE_SPRITE_SIGN, STATE_NUM_SPRITES, INIT_HEALTH * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], SIZE_TO_HITBOX, IDLE, NUM_STATES, TRANS_TIME);
    }

    // Description: This method calculates the attacks of the bird
    public void attack() {
        calcSprites();

        // Eagle Artillery splitting egg attack
        if (state == EAGLE_ART) {
            // Eagle Artillery splitting egg periodically Might add spriteSign to adding COORD_TO_EAGLE_ART if needed
            if (frameCounter % (STATE_SPRITE_HZ[EAGLE_ART] * STATE_NUM_SPRITES[EAGLE_ART] * GAGS_TO_EAGLE_ART) == (int) (STATE_SPRITE_HZ[EAGLE_ART] * (STATE_NUM_SPRITES[EAGLE_ART] - EAGLE_ART_SPAWN_SPRITE_NO))) {
                OmegaFight3.projectiles.add(new Egg(this, coord.add(COORD_TO_EAGLE_ART), EAGLE_ART_START_ANGLE + EAGLE_ART_SPREAD * Math.random()));
            }
        }

        // Tweaking feather barrage attack
        else if (state == TWEAK) {
            // Rapid fire feathers
            double dir = TWEAK_AMP * Math.cos((Math.PI * 2) / (STATE_TIME[TWEAK] / TWEAKS_PER_CYCLE) * frameCounter);
            if (frameCounter % TWEAK_HZ == 0) {
                OmegaFight3.projectiles.add(new Feather(this, coord.add(COORD_TO_TWEAK), dir + Math.PI / 2));
            }
            else if (frameCounter % TWEAK_HZ == TWEAK_HZ / 2) {
                OmegaFight3.projectiles.add(new Feather(this, coord.add(COORD_TO_TWEAK), -dir + Math.PI / 2));
            }
        }

        checkFinish();
    }

    // Description: This method calculates all of the background attacks of the bird
    public void backgroundAttack() {
        double difficultyMult = OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty];
        double maxHealth = INIT_HEALTH * difficultyMult;
        // Diver attack
        if (health <= maxHealth * WAVE_THRESHOLD) {
            waveCounter++;
            if (waving != NOT_WAVING) {
                if (waveCounter % (WAVE_TIME / DIVER_PER_WAVE) == 0) {
                    OmegaFight3.projectiles.add(new Diver(this, new Coord(OmegaFight3.SCREEN_CENTER.x - (OmegaFight3.SCREEN_SIZE.x / 2 + Diver.SIZE.x / 2) * waving, DIVER_ALTITUDE), (waving == WAVING_RIT? 0: Math.PI), waving));
                }
                if (waveCounter == WAVE_TIME) {
                    waving = NOT_WAVING;
                    waveCounter = 0;
                }
            }
            else if (waveCounter >= Math.max(MIN_WAVE_HZ, WAVE_HZ * health / (maxHealth * WAVE_THRESHOLD) / difficultyMult)) {
                waving = OmegaFight3.randomSign();
                waveCounter = 0;
            }
        }

        // Punk attack
        if (health <= maxHealth * PUNK_THRESHOLD && numPunks != MAX_PUNK) {
            punkCounter++;
            if (punkCounter >= Math.max(MIN_PUNK_HZ, PUNK_HZ * health / (maxHealth * PUNK_THRESHOLD) / difficultyMult)) {
                OmegaFight3.babyBosses.addLast(new Pair<>(0, new Punk(this, coord.copy(), -spriteSign)));
                punkCounter = 0;
                numPunks++;
            }
        }
    }

    // Description: This method changes the birds stats to prepare him for death
    public void prepareToDie() {
        super.prepareToDie();
        for (Boss boss : OmegaFight3.bosses) {
            if (boss instanceof Punk && ((Punk) boss).mommy == this) {
                ((Punk) boss).trueHurt(Punk.INIT_HEALTH);
            }
        }
    }
}

class Punk extends Boss {
    public static final double INIT_HEALTH = 3;
    public static final double SIZE_TO_HITBOX = 0.5;
    public static final double DEATH_DMG = 20 * Omegaman.PERC_MULT;

    // State constants
    public static final int NUM_STATES = 2;
    public static final Coord[] STATE_SIZE = {new Coord(175, 285), new Coord(185, 250)};
    public static final int[] STATE_SPRITE_HZ = {7, 5};

    // Sprite constants
    public static int[] STATE_SPRITE_START = new int[NUM_STATES];
    public static final int[] STATE_NUM_SPRITES = {3, 4};
    public static final int TOT_NUM_SPRITES = 7;

    // Idle constants
    public static final double ROTATION_SPD = Math.PI / 4 / OmegaFight3.FPS;
    public static final double CIRCLE_RAD_SPD = 1;
    public static final double CIRCLE_RAD_MIN = 90;
    public static final double CIRCLE_RAD_MAX = 240;
    public static final double VEL_MIN = 4;
    public static final double VEL_MAX = 6;

    // Images
    public static BufferedImage[] punkSprite = new BufferedImage[TOT_NUM_SPRITES];

    // Background attack variables
    public int circleRadDir = 1;
    public double circleRad = CIRCLE_RAD_MIN;
    public double rotation;
    public Bird mommy;
    public Plush[] plushes = new Plush[(int) INIT_HEALTH];

    // Constructor
    public Punk(Bird mommy, Coord coord, int sign) {
        super(punkSprite, new Coord[] {null, coord}, STATE_SIZE, STATE_SPRITE_HZ, new int[] {0, 0}, STATE_SPRITE_START, new int[] {OmegaFight3.RIT_SIGN, sign}, STATE_NUM_SPRITES, INIT_HEALTH, SIZE_TO_HITBOX, IDLE, NUM_STATES, 0);
        this.mommy = mommy;
        for (int i = 0; i < INIT_HEALTH; i++) {
            plushes[i] = new Plush(this);
            OmegaFight3.projectiles.add(plushes[i]);
        }
        calculatePlushes();
        velocity.x = getNewPunkVel() * sign;
        velocity.y = getNewPunkVel() * OmegaFight3.randomSign();
        sprite = punkSprite;
    }

    // Description: This method calculates the attacks of the punk
    public void attack() {
        // Calculate frames
        calcSprites();

        // Move and bounce punk
        coord.x += velocity.x;
        coord.y += velocity.y;
        if (coord.x + circleRad >= OmegaFight3.SCREEN_SIZE.x) {
            setVelX(-getNewPunkVel());
        }
        else if (coord.x - circleRad <= 0) {
            setVelX(getNewPunkVel());
        }
        if (coord.y + circleRad >= OmegaFight3.SCREEN_SIZE.y) {
            velocity.y = -getNewPunkVel();
        }
        else if (coord.y - circleRad <= 0) {
            velocity.y = getNewPunkVel();
        }

        for (Boss boss: OmegaFight3.bosses) {
            if (boss != this && boss instanceof Punk && Math.hypot(boss.coord.x - coord.x, boss.coord.y - coord.y) <= ((Punk) boss).circleRad + circleRad) {
                Punk punk = (Punk) boss;
                if (boss.coord.x > coord.x) {
                    punk.setVelX(getNewPunkVel());
                    setVelX(-getNewPunkVel());
                }
                else {
                    punk.setVelX(-getNewPunkVel());
                    setVelX(getNewPunkVel());
                }
                if (boss.coord.y > coord.y) {
                    boss.velocity.y = getNewPunkVel();
                    velocity.y = -getNewPunkVel();
                }
                else {
                    boss.velocity.y = -getNewPunkVel();
                    velocity.y = getNewPunkVel();
                }
            }
        }

        // Calculate plushes
        circleRad += circleRadDir * CIRCLE_RAD_SPD;
        if (circleRad >= CIRCLE_RAD_MAX) circleRadDir = -1;
        else if (circleRad <= CIRCLE_RAD_MIN) circleRadDir = 1;
        rotation = (rotation + ROTATION_SPD) % (Math.PI * 2);
        calculatePlushes();
    }

    public void setVelX(double velX) {
        velocity.x = velX;
        spriteSign = (int) Math.signum(velX);
        if (spriteSign == 0) spriteSign = OmegaFight3.RIT_SIGN;
    }

    private static double getNewPunkVel() {
        return VEL_MIN + (VEL_MAX - VEL_MIN) * Math.random();
    }

    // Description: This method calculates all of the background attacks of the punk
    public void backgroundAttack() {}

    private void calculatePlushes() {
        for (int i = 0; i < INIT_HEALTH; i++) {
            double angle = rotation + Math.PI * 2 / INIT_HEALTH * i;
            Coord temp = (new Coord(Math.cos(angle), Math.sin(angle))).scaledBy(circleRad);
            plushes[i].coord = coord.add(temp);
            plushes[i].dir = Math.atan2(temp.y, temp.x) + Math.PI / 2;
        }
    }

    // Description: This method changes the punk stats to prepare him for death
    public void prepareToDie() {
        super.prepareToDie();
        for (Plush plush: plushes) {
            OmegaFight3.deadProjectiles.add(plush);
        }
    }

    public double hurt(double damage) {
        return 0;
    }
    
    public void trueHurt(double damage) {
        super.hurt(damage);
    }

    public void surge() {
        super.surge();
        if (frameCounter == OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.SURGE_SPRITE_WIN_CHECK) {
            mommy.hurt(DEATH_DMG);
        }
        else if (frameCounter == OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            OmegaFight3.deadBosses.addLast(this);
            mommy.numPunks--;
        }
    }
}
