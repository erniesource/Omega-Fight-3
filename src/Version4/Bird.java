package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;
import javafx.util.Pair;

public class Bird extends Boss {
    // Combat constants
    public static final double INITIAL_HEALTH = 50 * Omegaman.PERC_MULT;//600 * Omegaman.PERC_MULT;
    public static final double SIZE_TO_HITBOX = 0.5;

    // State constants
    public static final int EAGLE_ARTILLERY = 2;
    public static final int TWEAK = 3;
    public static final int NO_OF_STATES = 4;
    public static final int TRANSITION_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(520, 530), new Coord(645, 425), new Coord(650, 685), new Coord(710, 670)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {10, 5, 5, 7};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x - STATE_SIZE[IDLE].x / 2, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord((OmegaFight3.stage[OmegaFight3.NORTH_CAVE_NO].platforms[1].leftX + OmegaFight3.stage[OmegaFight3.NORTH_CAVE_NO].platforms[1].rightX) / 2, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 2, STATE_SIZE[TWEAK].y / 2)};
    public static final int[] STATE_TIME = {0, 60, 320, 480};
    public static final String[] STATE_NAME = {"eagleArtillery", "tweak"};

    // Sprite constants
    public static int[] STATE_SPRITE_START = new int[NO_OF_STATES];
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.RIGHT_SIGN, OmegaFight3.LEFT_SIGN, OmegaFight3.RIGHT_SIGN, OmegaFight3.RIGHT_SIGN};
    public static final int[] STATE_NO_SPRITES = {2, 4, 4, 3};
    public static final int NO_OF_SPRITES = 13;

    // Eagle Artillery Constants
    public static final double EAGLE_ARTILLERY_SPREAD = -Math.PI / 7;
    public static final double EAGLE_ARTILLERY_START_ANGLE = -Math.PI * 5 / 14;
    public static final double EAGLE_ARTILLERY_SPAWN_SPRITE_NO = 2.5;
    public static final int GAGS_TO_EAGLE_ARTILLERY = 4;
    public static final Coord COORD_TO_EAGLE_ARTILLERY_COORD = new Coord(STATE_SIZE[EAGLE_ARTILLERY].x / 3, -STATE_SIZE[EAGLE_ARTILLERY].y / 4);

    // Tweak constants
    public static final int TWEAK_HZ = 10;
    public static final Coord COORD_TO_TWEAK_COORD = new Coord();
    public static final double TWEAK_AMP = Math.PI * 5 / 8;
    public static final double TWEAKS_PER_CYCLE = 2;

    // Background attack constants
    public static final double WAVE_AMT_SCALING_TO_HEALTH = 0.3;
    public static final int WAVE_HZ = 360;
    public static final int MIN_WAVE_HZ = 180;
    public static final double WAVE_THRESHOLD = 0.7;
    public static final int DIVER_PER_WAVE = 3;
    public static final int WAVE_TIME = 90;
    public static final int NOT_WAVING = 0;
    public static final int WAVING_LEFT = -1;
    public static final int WAVING_RIGHT = 1;
    public static final double DIVER_ALTITUDE = 25 + Diver.SIZE.y / 2;
    public static final double PUNK_AMT_SCALING_TO_HEALTH = 0.25;
    public static final int PUNK_HZ = 120;//600;
    public static final int MIN_PUNK_HZ = 120; //300;
    public static final double PUNK_THRESHOLD = 1.0;//0.4;
    public static final int MAX_PUNK = 2;

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    // Background attack variables
    public int waveCounter;
    public int waving;
    public int punkCounter;
    public int numPunks;

    // Constructor
    public Bird() {
        super(STATE_COORD[IDLE].copy(), STATE_SPRITE_START[IDLE], STATE_SPRITE_SIGN[IDLE], STATE_TIME[IDLE], STATE_SIZE[IDLE].copy(), IDLE, INITIAL_HEALTH, SIZE_TO_HITBOX);
    }

    // Description: This method transitions the boss from state to state (from each attack's location to another attack's location)
    public void transition() {
        // Sprite change
        frameCounter--;
        if (frameCounter % STATE_SPRITE_CHANGE_HZ[IDLE] == 0) {
            spriteNo = STATE_SPRITE_START[IDLE] + (spriteNo - STATE_SPRITE_START[IDLE] + 1) % STATE_NO_SPRITES[IDLE];
        }

        // Change position
        coord.x = OmegaFight3.lerp(STATE_COORD[transitionTo].x, STATE_COORD[state].x, (double) frameCounter / TRANSITION_TIME);
        coord.y = OmegaFight3.lerp(STATE_COORD[transitionTo].y, STATE_COORD[state].y, (double) frameCounter / TRANSITION_TIME);

        // Change state if finished transition
        if (frameCounter == 0) {
            state = transitionTo;
            transitionTo = NO_TRANSITION;
            frameCounter = STATE_TIME[state];
            spriteNo = STATE_SPRITE_START[state];
            spriteSign = STATE_SPRITE_SIGN[state];
            size = STATE_SIZE[state].copy();
        }
    }

    // Description: This method calculates the attacks of the bird
    public void attack() {
        // Calculate frames
        frameCounter--;

        // Idle
        if (state == IDLE) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[IDLE] == 0) {
                spriteNo = STATE_SPRITE_START[IDLE] + (spriteNo - STATE_SPRITE_START[IDLE] + 1) % STATE_NO_SPRITES[IDLE];
            }
        }

        // Eagle Artillery splitting egg attack
        else if (state == EAGLE_ARTILLERY) {
            // Sprite change
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[EAGLE_ARTILLERY] == 0) {
                spriteNo = STATE_SPRITE_START[EAGLE_ARTILLERY] + (spriteNo - STATE_SPRITE_START[EAGLE_ARTILLERY] + 1) % STATE_NO_SPRITES[EAGLE_ARTILLERY];
            }

            // Eagle Artillery splitting egg periodically
            if (frameCounter % (STATE_SPRITE_CHANGE_HZ[EAGLE_ARTILLERY] * STATE_NO_SPRITES[EAGLE_ARTILLERY] * GAGS_TO_EAGLE_ARTILLERY) == (int) (STATE_SPRITE_CHANGE_HZ[EAGLE_ARTILLERY] * (STATE_NO_SPRITES[EAGLE_ARTILLERY] - EAGLE_ARTILLERY_SPAWN_SPRITE_NO))) {
                projectiles.add(new Egg(this, new Coord(coord.x + COORD_TO_EAGLE_ARTILLERY_COORD.x, coord.y + COORD_TO_EAGLE_ARTILLERY_COORD.y), EAGLE_ARTILLERY_START_ANGLE + EAGLE_ARTILLERY_SPREAD * Math.random()));
            }
        }

        // Tweaking feather barrage attack
        else if (state == TWEAK) {
            // Sprite change
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[TWEAK] == 0) {
                spriteNo = STATE_SPRITE_START[TWEAK] + (spriteNo - STATE_SPRITE_START[TWEAK] + 1) % STATE_NO_SPRITES[TWEAK];
            }
            
            // Rapid fire feathers
            if (frameCounter % TWEAK_HZ == 0) {
                projectiles.add(new Feather(this, new Coord(coord.x + COORD_TO_TWEAK_COORD.x, coord.y + COORD_TO_TWEAK_COORD.y), TWEAK_AMP * Math.cos((Math.PI * 2) / (STATE_TIME[TWEAK] / TWEAKS_PER_CYCLE) * frameCounter) + Math.PI / 2));
            }
            else if (frameCounter % TWEAK_HZ == TWEAK_HZ / 2) {
                projectiles.add(new Feather(this, new Coord(coord.x + COORD_TO_TWEAK_COORD.x, coord.y + COORD_TO_TWEAK_COORD.y), -TWEAK_AMP * Math.cos((Math.PI * 2) / (STATE_TIME[TWEAK] / TWEAKS_PER_CYCLE) * frameCounter) + Math.PI / 2));
            }
        }

        // Attack has finished, transition to same or new attack
        if (frameCounter == 0) {
            transitionTo = (int) (Math.random() * (NO_OF_STATES - 1)) + 1;

            // Transition to same attack
            if (transitionTo == state) {
                transitionTo = NO_TRANSITION;
                frameCounter = STATE_TIME[state];
                spriteNo = STATE_SPRITE_START[state];
            }

            // Transition to different attack
            else {
                frameCounter = TRANSITION_TIME;
                spriteNo = STATE_SPRITE_START[IDLE];
                spriteSign = (int) Math.signum(STATE_COORD[transitionTo].x - STATE_COORD[state].x);
                if (spriteSign == 0) spriteSign = STATE_SPRITE_SIGN[transitionTo];
                size = STATE_SIZE[IDLE];
            }
        }
    }

    // Description: This method calculates all of the background attacks of the bird
    public void backgroundAttack() {
        // Diver attack
        if (health <= INITIAL_HEALTH * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty] * WAVE_THRESHOLD) {
            waveCounter++;
            if (waving != NOT_WAVING) {
                if (waveCounter % (WAVE_TIME / DIVER_PER_WAVE) == 0) {
                    projectiles.add(new Diver(this, new Coord(OmegaFight3.SCREEN_CENTER.x - (OmegaFight3.SCREEN_SIZE.x / 2 + Diver.SIZE.x / 2) * waving, DIVER_ALTITUDE), (waving == WAVING_RIGHT? 0: Math.PI), waving));
                }
                if (waveCounter == WAVE_TIME) {
                    waving = NOT_WAVING;
                    waveCounter = 0;
                }
            }
            else if (waveCounter >= Math.max(MIN_WAVE_HZ, WAVE_HZ * health / (INITIAL_HEALTH * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty] * WAVE_THRESHOLD) / WAVE_AMT_SCALING_TO_HEALTH)) {
                waving = OmegaFight3.randomSign();
                waveCounter = 0;
            }
        }

        // Punk attack
        if (health <= INITIAL_HEALTH * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty] * PUNK_THRESHOLD) {
            punkCounter++;
            if (punkCounter >= Math.max(MIN_PUNK_HZ, PUNK_HZ * health / (INITIAL_HEALTH * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty] * PUNK_THRESHOLD) / PUNK_AMT_SCALING_TO_HEALTH) && numPunks != MAX_PUNK) {
                OmegaFight3.babyBosses.addLast(new Pair<>(0, new Punk(this, coord.copy(), -spriteSign)));
                punkCounter = 0;
                numPunks++;
            }
        }
    }

    // Description: This method draws the bird using the graphics object provided
    public void draw(Graphics g) {
        if (!hurt || hurtCounter >= Boss.HURT_BLINK_HZ) {
            g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 * spriteSign), (int) (coord.y - size.y / 2), (int) size.x * spriteSign, (int) size.y, null);
        }
    }

    // Description: This method calculates the bird falling to his death 
    public void fall() {
        // Move bird
        super.fall();
        
        // Sprite changes
        frameCounter = (frameCounter + 1) % STATE_SPRITE_CHANGE_HZ[DEAD];
        if (frameCounter == 0) {
            spriteNo = (spriteNo + 1) % STATE_NO_SPRITES[DEAD];
        }

        // Start surge animation
        if (coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
            frameCounter = 0;
            spriteNo = 0;
            OmegaFight3.screenShakeCounter += DIE_SCREENSHAKE;
            OmegaFight3.play(OmegaFight3.boom);
        }
    }

    // Description: This method changes the birds stats to prepare him for death
    public void prepareToDie() {
        super.prepareToDie();
        spriteNo = STATE_SPRITE_START[DEAD];
        size = STATE_SIZE[DEAD];
        for (Boss boss : OmegaFight3.bosses) {
            if (boss instanceof Punk && ((Punk) boss).mommy == this) {
                boss.prepareToDie();
            }
        }
    }
}

class Punk extends Boss {
    public static final double INITIAL_HEALTH = 4;
    public static final double SIZE_TO_HITBOX = 0.5;

    // State constants
    public static final int NO_OF_STATES = 2;
    public static final Coord[] STATE_SIZE = {new Coord(175, 285), new Coord(185, 250)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {7, 5};

    // Sprite constants
    public static int[] STATE_SPRITE_START = new int[NO_OF_STATES];
    public static final int[] STATE_NO_SPRITES = {3, 4};
    public static final int NO_OF_SPRITES = 7;

    // Idle constants
    public static final double ROTATION_SPD = Math.PI / 4 / OmegaFight3.FPS;
    public static final double CIRCLE_RADIUS_SPD = 1;
    public static final double CIRCLE_RADIUS_MIN = 250;
    public static final double CIRCLE_RADIUS_MAX = 290;
    public static final double VEL_MIN = 4;
    public static final double VEL_MAX = 8;

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    // Background attack variables
    public int circleRadiusDir = 1;
    public double circleRadius = CIRCLE_RADIUS_MIN;
    public double rotation;
    public Coord velocity;
    public Bird mommy;

    // Constructor
    public Punk(Bird mommy, Coord coord, int sign) {
        super(coord, STATE_SPRITE_START[IDLE], sign, 0, STATE_SIZE[IDLE].copy(), IDLE, INITIAL_HEALTH, SIZE_TO_HITBOX);
        this.mommy = mommy;
        for (int i = 0; i != INITIAL_HEALTH; i++) {
            projectiles.add(new Plush(this));
        }
        // Create plushes here and set coords use func for that
    }

    // Description: This method transitions the boss from state to state (from each attack's location to another attack's location)
    public void transition() {}

    // Description: This method calculates the attacks of the punk
    public void attack() {
        // Calculate frames
        frameCounter++;
        if (frameCounter % STATE_SPRITE_CHANGE_HZ[IDLE] == 0) {
            spriteNo = STATE_SPRITE_START[IDLE] + (spriteNo - STATE_SPRITE_START[IDLE] + 1) % STATE_NO_SPRITES[IDLE];
            frameCounter = 0;
        }
        // Move and bounce punk
        // Calculate plush coord and dirs here make func
    }

    // Description: This method calculates all of the background attacks of the punk
    public void backgroundAttack() {}

    // Description: This method draws the punk using the graphics object provided
    public void draw(Graphics g) {
        if (!hurt || hurtCounter >= Boss.HURT_BLINK_HZ) {
            g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 * spriteSign), (int) (coord.y - size.y / 2), (int) size.x * spriteSign, (int) size.y, null);
        }
    }

    // Description: This method calculates the punk falling to his death 
    public void fall() {
        // Move bird
        super.fall();
        
        // Sprite changes
        frameCounter = (frameCounter + 1) % STATE_SPRITE_CHANGE_HZ[DEAD];
        if (frameCounter == 0) {
            spriteNo = (spriteNo + 1) % STATE_NO_SPRITES[DEAD];
        }

        // Start surge animation
        if (coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
            frameCounter = 0;
            spriteNo = 0;
            OmegaFight3.screenShakeCounter += DIE_SCREENSHAKE;
            OmegaFight3.play(OmegaFight3.boom);
        }
    }

    // Description: This method changes the punk stats to prepare him for death
    public void prepareToDie() {
        super.prepareToDie();
        spriteNo = STATE_SPRITE_START[DEAD];
        size = STATE_SIZE[DEAD];
        projectiles.clear();
    }

    public void hurt(double damage) {}
    
    public void trueHurt(double damage) {
        super.hurt(damage);
    }

    public void surge() {
        super.surge();
        if (frameCounter == OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            OmegaFight3.deadBosses.addLast(this);
            mommy.numPunks--;
        }
    }
}
