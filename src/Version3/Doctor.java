package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Doctor extends Boss {
    // Combat constants
    public static final double INITIAL_HEALTH = 600 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);

    // Sprite constants
    public static final int[] STATE_SPRITE_START = {0, 2, 4, 7};
    public static final int[] STATE_SPRITE_SIGN = {OmegaFight3.LEFT_SIGN, OmegaFight3.LEFT_SIGN, OmegaFight3.LEFT_SIGN, OmegaFight3.RIGHT_SIGN};
    public static final int[] STATE_NO_SPRITES = {2, 2, 3, 4};
    public static final int NO_OF_SPRITES = 11;

    // State constants
    public static final int SPIT = 2;
    public static final int LAUGH = 3;
    public static final int NO_OF_STATES = 4;
    public static final int TRANSITION_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(220, 420), new Coord(280, 400), new Coord(280, 440), new Coord(400, 525)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {10, 10, 7, 5};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord(OmegaFight3.SCREEN_SIZE.x * 7 / 8, OmegaFight3.stage[OmegaFight3.BATTLEFIELD_NO].platforms[1].y - (OmegaFight3.stage[OmegaFight3.BATTLEFIELD_NO].platforms[0].y - OmegaFight3.stage[OmegaFight3.BATTLEFIELD_NO].platforms[1].y)),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 8, OmegaFight3.SCREEN_SIZE.y / 2)};
    public static final int[] STATE_TIME = {20, 60, 30, 300};

    // Spit Constants
    public static final double SPIT_SPEED = 7;
    public static final double SPIT_START_ANGLE = Math.PI / 3;
    public static final double SPIT_SPREAD = Math.PI / 6;
    public static final int SPIT_NUM_PROJS = 4;
    public static final int SPIT_HZ = 48;
    public static final Coord COORD_TO_SPIT_COORD = new Coord(STATE_SIZE[SPIT].x / 4, STATE_SIZE[SPIT].y * 3 / 10);

    // Laugh constants
    public static final int LAUGH_NO_PROJS = 5;
    public static final int LAUGH_HZ = 60;
    public static final Coord COORD_TO_GEM_COORD = new Coord(STATE_SIZE[LAUGH].x * 23 / 80, STATE_SIZE[LAUGH].y * 7 / 20);

    // Background attack constants
    public static final double PINCER_AMT_SCALING_TO_HEALTH = 0.3;
    public static final int PINCER_HZ = 480;
    public static final int MIN_PINCER_HZ = 240;
    public static final double PINCER_THRESHOLD = 0.7;
    public static final double BOMBOT_AMT_SCALING_TO_HEALTH = 0.25;
    public static final int BOMBOT_HZ = 600;
    public static final int MIN_BOMBOT_HZ = 300;
    public static final double BOMBOT_THRESHOLD = 0.4;
    public static final int BOMBOT_NUM_SPAWN_LOCS = 2;
    public static final int LEFT_SPAWN = 0;
    public static final int RIGHT_SPAWN = 1;

    // Background attack variables
    public int pincerCounter;
    public int bombotCounter;

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    // Constructor
    public Doctor(double difficulty) {
        super(STATE_COORD[IDLE].copy(), STATE_SPRITE_START[IDLE], STATE_SPRITE_SIGN[IDLE], STATE_TIME[IDLE], STATE_SIZE[IDLE].copy(), IDLE, INITIAL_HEALTH, difficulty);
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

    // Description: This method calculates the attacks of the Doctor
    public void attack() {
        // Calculate frames
        frameCounter--;

        // Idle
        if (state == IDLE) {
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[IDLE] == 0) {
                spriteNo = STATE_SPRITE_START[IDLE] + (spriteNo - STATE_SPRITE_START[IDLE] + 1) % STATE_NO_SPRITES[IDLE];
            }
        }

        // Spit nuts and bolts attack
        else if (state == SPIT) {
            // Sprite change
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[SPIT] == 0) {
                spriteNo = STATE_SPRITE_START[SPIT] + (spriteNo - STATE_SPRITE_START[SPIT] + 1) % STATE_NO_SPRITES[SPIT];
            }

            // Movement
            coord.x += (spriteSign * SPIT_SPEED);
            if (coord.x < size.x / 2) {
                spriteSign = OmegaFight3.RIGHT_SIGN;
            }
            if (coord.x == STATE_COORD[SPIT].x) {
                spriteSign = OmegaFight3.LEFT_SIGN;
                frameCounter = 0;
            }

            // Spits nuts and bolts periodically
            else if (frameCounter == 0) {
                frameCounter = SPIT_HZ;
                for (int i = 0; i != SPIT_NUM_PROJS; i++) {
                    projectiles.add(new Fastener(this, new Coord(coord.x + COORD_TO_SPIT_COORD.x * spriteSign, coord.y + COORD_TO_SPIT_COORD.y),
                    -Math.PI / 2 + (SPIT_START_ANGLE + Math.random() * SPIT_SPREAD) * spriteSign));
                }
            }
        }

        // Laughing gem attack
        else if (state == LAUGH) {
            // Sprite change
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[LAUGH] == 0) {
                spriteNo = STATE_SPRITE_START[LAUGH] + (spriteNo - STATE_SPRITE_START[LAUGH] + 1) % STATE_NO_SPRITES[LAUGH];
            }
            
            // Alternate between LAUGH_NO_PROJS and LAUGH_NO_PROJS - 1 so that players can't just stay in one place to dodge
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

    // Description: This method calculates all of the background attacks of the doctor
    public void backgroundAttack() {
        // Pincer attack
        if (health < INITIAL_HEALTH * difficulty * PINCER_THRESHOLD) {
            pincerCounter++;
            if (pincerCounter >= Math.max(MIN_PINCER_HZ, PINCER_HZ * health / (INITIAL_HEALTH * difficulty * PINCER_THRESHOLD) / PINCER_AMT_SCALING_TO_HEALTH)) {
                projectiles.add(new Pincer(this, new Coord((int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.x, (int) (Math.random() * 2) * OmegaFight3.SCREEN_SIZE.y)));
                pincerCounter = 0;
            }
        }

        // Homing missile attack
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

    // Description: This method draws the doctor using the graphics object provided
    public void draw(Graphics g) {
        if (!hurt || hurtCounter >= Boss.HURT_BLINK_HZ) {
            g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 * spriteSign), (int) (coord.y - size.y / 2), (int) size.x * spriteSign, (int) size.y, null);
        }
    }

    // Description: This method calculates the doctor falling to his death 
    public void fall() {
        // Move doctor
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

    // Description: This method changes the doctors stats to prepare him for death
    public void prepareToDie() {
        super.prepareToDie();
        spriteNo = STATE_SPRITE_START[DEAD];
        size = STATE_SIZE[DEAD];
    }
}
