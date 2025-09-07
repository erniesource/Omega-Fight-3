package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

abstract class Char {
    public static final int FIRE_HURT_HZ = 20;
    public static final int FIRE_SPRITE_CHANGE_HZ = 5;
    public static double FIRE_DMG = 2;

    public static BufferedImage fireImg;

    // Instance variables
    public Coord coord;
    public int spriteNo;
    public int spriteSign; // 1: Positive, -1: Negative
    public int frameCounter; // Framecounter for running player
    public Coord size;
    public Coord velocity = new Coord();
    public int state;
    public Deque<Smoke> smokeQ = new LinkedList<>();
    public double sizeToFire;
    public int fireCounter;

    // Constructor
    public Char(Coord coord, Coord size, double sizeToFire, int spriteNo, int spriteSign, int frameCounter, int state) {
        this.coord = coord;
        this.spriteNo = spriteNo;
        this.spriteSign = spriteSign;
        this.frameCounter = frameCounter;
        this.size = size;
        this.state = state;
        this.sizeToFire = sizeToFire;
    }

    public void processSmokes() { // make an explosion class
        // Process smoke trails
        for (Smoke smoke: smokeQ) {
            smoke.setFrameCounter(smoke.getFrameCounter() - 1);
        }

        // Delete dead smoke
        while (!smokeQ.isEmpty() && smokeQ.getFirst().getFrameCounter() == 0) {
            smokeQ.removeFirst();
        }
    }

    public void processFire() {
        if (fireCounter != 0) {
            fireCounter--;
            if (fireCounter % FIRE_HURT_HZ == 0) hurt(FIRE_DMG);
        }
    }

    // Description: This method draws the smoke trails of the player
    public void drawSmokes(Graphics2D g2) {
        for (Smoke smoke: smokeQ) {
            smoke.draw(g2);
        }
    }

    public void draw(Graphics g) {
        drawFire(g);

        if (OmegaFight3.hitBoxVis) {
            Coord hitBoxCoord = coord.add(size.scaledBy(-0.5));
            g.setColor(OmegaFight3.CHEAT_COLOR);
            g.drawRect((int) (hitBoxCoord.x), (int) (hitBoxCoord.y), (int) (size.x), (int) (size.y));
        }
    }

    public void drawFire(Graphics g) {
        if (fireCounter != 0) {
            Coord fireSize = size.scaledBy(sizeToFire);
            int sign = ((fireCounter % (2 * FIRE_SPRITE_CHANGE_HZ) < FIRE_SPRITE_CHANGE_HZ)? -1: 1);
            g.drawImage(fireImg, (int) (coord.x - fireSize.x / 2 * sign), (int) (coord.y - fireSize.y / 2), (int) (fireSize.x * sign), (int) fireSize.y, null);
        }
    }

    // Abstract methods for each char
    abstract public double hurt(double damage);
}

abstract class Boss extends Char {
    // Constants
    public static final double BOSS_HITBOX_LEEWAY = 0.2;
    public static final int NO_TRANS = -1;
    public static final int NOT_HURT = -1;
    public static final int HURT_BLINK_HZ = 1;

    // Die constants
    public static final double DIE_ACCEL = 1;
    public static final int DIE_SCREENSHAKE = 60;

    // State constants
    public static final int DEAD = 0;
    public static final int IDLE = 1;

    // Static surge images for bosses
    public static BufferedImage[] surge = new BufferedImage[OmegaFight3.NUM_SURGE_IMAGES];

    // State stats
    public int numStates;
    public int transTime;
    public Coord[] stateSize;
    public int[] stateSpriteHz;
    public Coord[] stateCoord;
    public int[] stateTime;
    public int[] stateSpriteStart;
    public int[] stateSpriteSign;
    public int[] stateNumSprites;

    public Boss(BufferedImage[] sprite, Coord[] stateCoord, Coord[] stateSize, int[] stateSpriteHz, int[] stateTime,
    int[] stateSpriteStart, int[] stateSpriteSign, int[] stateNumSprites, double health, double sizeToHitbox, double sizeToFire, int state, int numStates, int transTime) {
        super(stateCoord[state].copy(), stateSize[state].copy(), sizeToFire, stateSpriteStart[state], stateSpriteSign[state], stateTime[state], state);
        this.sprite = sprite;
        this.stateCoord = stateCoord;
        this.stateSize = stateSize;
        this.stateSpriteHz = stateSpriteHz;
        this.stateTime = stateTime;
        this.stateSpriteStart = stateSpriteStart;
        this.stateSpriteSign = stateSpriteSign;
        this.stateNumSprites = stateNumSprites;
        this.health = health;
        this.sizeToHitbox = sizeToHitbox;
        this.state = state;
        this.numStates = numStates;
        this.transTime = transTime;
    }

    // Combat stats
    public double health;
    public double sizeToHitbox;
    public int transTo = NO_TRANS;
    public int hurtCounter = NOT_HURT;
    public boolean hurt;

    // Images
    public BufferedImage[] sprite;

    // Description: This method hurts and prepares the boss to die if they're gonna die
    public double hurt(double damage) {
        if (!hurt) hurt = true;
        if (state != DEAD && OmegaFight3.transitiono != OmegaFight3.GAME_SET && OmegaFight3.transitiono != OmegaFight3.GAME_OVER) {
            health -= damage;
            if (health <= 0 && state != DEAD) prepareToDie();
            return damage;
        }
        else {
            return 0;
        }
    }

    // Description: This method calculates the hurt blinking of the boss
    public void processHurt() {
        // Calculate hurt boss
        if (hurt) {
            hurtCounter = (hurtCounter + 1) % (HURT_BLINK_HZ * 2);
            hurt = false;
        }

        // Calculate not hurt boss
        else {
            if (hurtCounter != NOT_HURT) hurtCounter = NOT_HURT;
        }
    }

    // Description: This method changes the variables required to prepare the boss to die
    public void prepareToDie() {
        state = DEAD;
        frameCounter = 0;
        velocity.y = 0;
        spriteNo = stateSpriteStart[DEAD];
        size = stateSize[DEAD];
    }

    // Description: This method calculates the boss falling to his death
    public void fall() {
        coord.y += velocity.y;
        velocity.y += DIE_ACCEL;

        // Sprite changes
        frameCounter = (frameCounter + 1) % stateSpriteHz[DEAD];
        if (frameCounter == 0) {
            spriteNo = (spriteNo + 1) % stateNumSprites[DEAD];
        }

        // Start surge animation
        if (coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
            frameCounter = 0;
            spriteNo = 0;
            OmegaFight3.screenShakeCounter += DIE_SCREENSHAKE;
            OmegaFight3.play(OmegaFight3.boom);
        }
    }

    // Description: This method calculates the surge animation and sounds effects
    public void surge() {
        // Calculate surge animation
        if (frameCounter <= OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            frameCounter++;
        }

        //Cheer sound effect
        if (frameCounter == OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            OmegaFight3.play(OmegaFight3.cheer);
        }
    }

    // Description: This method draws the surge sprites of the boss' death
    public void drawSurge(Graphics g) {
        if (frameCounter < OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            g.drawImage(surge[frameCounter / OmegaFight3.SURGE_FRAME_HZ], (int) (coord.x - OmegaFight3.SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y), null);
        }
    }

    public void draw(Graphics g) {
        if (!hurt || hurtCounter >= Boss.HURT_BLINK_HZ) {
            g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 * spriteSign), (int) (coord.y - size.y / 2), (int) size.x * spriteSign, (int) size.y, null);
        }

        drawFire(g);

        if (OmegaFight3.hitBoxVis) {
            Coord hitBoxSize = size.scaledBy(sizeToHitbox);
            Coord hitBoxCoord = coord.add(hitBoxSize.scaledBy(-0.5));
            g.setColor(OmegaFight3.CHEAT_COLOR);
            g.drawRect((int) (hitBoxCoord.x), (int) (hitBoxCoord.y), (int) (hitBoxSize.x), (int) (hitBoxSize.y));
        }
    }

    public void transition() {
        // Sprite change
        frameCounter--;
        if (frameCounter % stateSpriteHz[IDLE] == 0) {
            spriteNo = stateSpriteStart[IDLE] + (spriteNo - stateSpriteStart[IDLE] + 1) % stateNumSprites[IDLE];
        }

        // Change position
        coord.x = OmegaFight3.lerp(stateCoord[transTo].x, stateCoord[state].x, (double) frameCounter / transTime);
        coord.y = OmegaFight3.lerp(stateCoord[transTo].y, stateCoord[state].y, (double) frameCounter / transTime);

        // Change state if finished trans
        if (frameCounter == 0) {
            state = transTo;
            transTo = NO_TRANS;
            frameCounter = stateTime[state];
            spriteNo = stateSpriteStart[state];
            spriteSign = stateSpriteSign[state];
            size = stateSize[state];
        }
    }

    public void calcSprites() {
        frameCounter--;
        if (frameCounter % stateSpriteHz[state] == 0) {
            spriteNo = stateSpriteStart[state] + (spriteNo - stateSpriteStart[state] + 1) % stateNumSprites[state];
        }
    }

    public void checkFinish() {
        if (frameCounter == 0) {
            transTo = (int) (Math.random() * (numStates - 1)) + 1;

            // Transition to same attack
            if (transTo == state) {
                transTo = NO_TRANS;
                frameCounter = stateTime[state];
                spriteNo = stateSpriteStart[state];
            }

            // Transition to different attack
            else {
                frameCounter = transTime;
                spriteNo = stateSpriteStart[IDLE];
                spriteSign = (int) Math.signum(stateCoord[transTo].x - stateCoord[state].x);
                if (spriteSign == 0) spriteSign = stateSpriteSign[transTo];
                size = stateSize[IDLE];
            }
        }
    }

    // Abstract methods for each boss
    abstract public void attack();
    abstract public void backgroundAttack();
}