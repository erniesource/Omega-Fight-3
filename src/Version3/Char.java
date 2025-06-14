package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

abstract class Char {
    // Instance variables
    public Coord coord;
    public int spriteNo;
    public int spriteSign; // 1: Positive, -1: Negative
    public int frameCounter; // Framecounter for running player
    public Coord size;
    public Coord velocity = new Coord();
    public int state;
    public HashSet<Projectile> babyProjectiles = new HashSet<>();
    public HashSet<Projectile> projectiles = new HashSet<>();
    public HashSet<Projectile> deadProjectiles = new HashSet<>();

    // Constructor
    public Char(Coord coord, int spriteNo, int spriteSign, int frameCounter, Coord size, int state) {
        this.coord = coord;
        this.spriteNo = spriteNo;
        this.spriteSign = spriteSign;
        this.frameCounter = frameCounter;
        this.size = size;
        this.state = state;
    }

    // Description: This method adds all new Projectiles into the main projectiles HashSet
    public void addbabyProjectiles() {
        for (Projectile proj: babyProjectiles) {
            projectiles.add(proj);
        }
        babyProjectiles.clear();
    }

    // Description: This method processes all projectiles in the main projectiles HashSet
    public void processProjectiles() {
        for (Projectile proj: projectiles) {
            proj.process();
        }
    }

    // Description: This method draws all projectiles in the main projectiles HashSet
    public void drawProjectiles(Graphics2D g2) {
        for (Projectile proj: projectiles) {
            proj.draw(g2);
        }
    }

    // Descrption: This method deletes all dead projectiles from the main projectiles HashSet
    public void deleteDeadProjectiles() {
        for (Projectile proj: deadProjectiles) {
            projectiles.remove(proj);
        }
        deadProjectiles.clear();
    }

    // Abstract methods for each char
    abstract public void hurt(double damage);
    abstract public void draw(Graphics g);
}

abstract class Boss extends Char {
    // Constants
    public static final double BOSS_HITBOX_LEEWAY = 0.2;
    public static final int NO_TRANSITION = -1;
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

    // Combat stats
    public int transitionTo = NO_TRANSITION;
    public double health;
    public double difficulty;
    public boolean hurt;
    public int hurtCounter = NOT_HURT;

    // Constructor
    public Boss(Coord coord, int spriteNo, int spriteSign, int frameCounter, Coord size, int state, double health, double difficulty) {
        super(coord, spriteNo, spriteSign, frameCounter, size, state);
        this.health = health * difficulty;
        this.difficulty = difficulty;
    }

    // Description: This method hurts and prepares the boss to die if they're gonna die
    public void hurt(double damage) {
        health -= damage;
        if (!hurt) hurt = true;
        if (health <= 0 && state != DEAD) prepareToDie();
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
    }

    // Description: This method calculates the boss falling to his death
    public void fall() {
        coord.y += velocity.y;
        velocity.y += DIE_ACCEL;
    }

    // Description: This method calculates the surge animation and sounds effects
    public void surge() {
        // Calculate surge animation
        frameCounter++;

        //Cheer sound effect
        if (frameCounter == OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            OmegaFight3.cheer.stop();
            OmegaFight3.cheer.setFramePosition(0);
            OmegaFight3.cheer.start();
        }
    }

    // Description: This method draws the surge sprites of the boss' death
    public void drawSurge(Graphics g) {
        if (frameCounter < OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            g.drawImage(surge[frameCounter / OmegaFight3.SURGE_FRAME_HZ], (int) (coord.x - OmegaFight3.SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y), null);
        }
    }

    // Abstract methods for each boss
    abstract public void transition();
    abstract public void attack();
    abstract public void backgroundAttack();
}