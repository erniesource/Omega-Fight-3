package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bird extends Boss {
    // Combat constants
    public static final double INITIAL_HEALTH = 600 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);

    // Sprite constants
    public static final int[] STATE_SPRITE_START = {0, 2, 6, 10};
    public static final int[] STATE_SPRITE_SIGN = {0, OmegaFight3.LEFT_SIGN, OmegaFight3.RIGHT_SIGN, OmegaFight3.RIGHT_SIGN};
    public static final int[] STATE_NO_SPRITES = {2, 4, 4, 3};
    public static final int NO_OF_SPRITES = 13;

    // State constants
    public static final int VOMIT = 2;
    public static final int TWEAK = 3;
    public static final int NO_OF_STATES = 4;
    public static final int TRANSITION_TIME = 120;
    public static final Coord[] STATE_SIZE = {new Coord(520, 530), new Coord(645, 425), new Coord(720, 485), new Coord(710, 670)};
    public static final int[] STATE_SPRITE_CHANGE_HZ = {10, 5, 5, 7};
    public static final Coord[] STATE_COORD = {null, new Coord(OmegaFight3.SCREEN_SIZE.x - STATE_SIZE[IDLE].x / 2, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord(STATE_SIZE[VOMIT].x / 2, OmegaFight3.SCREEN_SIZE.y / 2),
        new Coord(OmegaFight3.SCREEN_SIZE.x / 2, STATE_SIZE[TWEAK].y / 2)};
    public static final int[] STATE_TIME = {0, 60, 320, 480};

    // Vomit Constants
    public static final double VOMIT_START_ANGLE = -Math.PI / 6;
    public static final double VOMIT_SPREAD = Math.PI / 3;
    public static final int VOMIT_HZ = 40;
    public static final Coord COORD_TO_VOMIT_COORD = new Coord(STATE_SIZE[VOMIT].x * 7 / 9, 0);

    // Tweak constants
    public static final int TWEAK_HZ = 40;
    public static final Coord COORD_TO_TWEAK_COORD = new Coord();

    // Background attack constants
    public static final double DIVER_AMT_SCALING_TO_HEALTH = 0.3;
    public static final int DIVER_HZ = 480;
    public static final int MIN_DIVER_HZ = 240;
    public static final double DIVER_THRESHOLD = 0.7;
    public static final double BABY_AMT_SCALING_TO_HEALTH = 0.25;
    public static final int BABY_HZ = 600;
    public static final int MIN_BABY_HZ = 300;
    public static final double BABY_THRESHOLD = 0.4;

    // Background attack variables
    public int diverCounter;
    public int babyCounter;

    // Images
    public static BufferedImage[] sprite = new BufferedImage[NO_OF_SPRITES];

    // Constructor
    public Bird(double difficulty) {
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

        // Vomit splitting egg attack
        else if (state == VOMIT) {
            // Sprite change
            if (frameCounter % STATE_SPRITE_CHANGE_HZ[VOMIT] == 0) {
                spriteNo = STATE_SPRITE_START[VOMIT] + (spriteNo - STATE_SPRITE_START[VOMIT] + 1) % STATE_NO_SPRITES[VOMIT];
            }

            // Vomit splitting egg periodically
            if (frameCounter % VOMIT_HZ == 0) {
                
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
        if (health < INITIAL_HEALTH * difficulty * DIVER_THRESHOLD) {
            // This one needs some thinking
        }

        // Baby attack
        if (health < INITIAL_HEALTH * difficulty * BABY_THRESHOLD) {
            // This one also needs some thinking
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
    }
}
