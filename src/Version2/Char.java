package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

abstract class Char {
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

    public Char(Coord coord, int spriteNo, int spriteSign, int frameCounter, Coord size, int state) {
        this.coord = coord;
        this.spriteNo = spriteNo;
        this.spriteSign = spriteSign;
        this.frameCounter = frameCounter;
        this.size = size;
        this.state = state;
    }

    public void addbabyProjectiles() {
        for (Projectile proj: babyProjectiles) {
            projectiles.add(proj);
        }
        babyProjectiles.clear();
    }

    public void processProjectiles() {
        for (Projectile proj: projectiles) {
            proj.process();
        }
    }

    public void drawProjectiles(Graphics2D g2) {
        for (Projectile proj: projectiles) {
            proj.draw(g2);
        }
    }

    public void deleteDeadProjectiles() {
        for (Projectile proj: deadProjectiles) {
            projectiles.remove(proj);
        }
        deadProjectiles.clear();
    }

    abstract public void hurt(double damage);
    abstract public void draw(Graphics g);
}

abstract class Boss extends Char {
    public static BufferedImage[] surge = new BufferedImage[5];

    // Combat stats
    public int transitionTo = -1;
    public double health;
    public double difficulty;
    public boolean hurt;
    public int hurtCounter = -1;

    public static final double BOSS_HITBOX_LEEWAY = 0.2;
    public static final int NO_TRANSITION = -1;
    public static final int NOT_HURT = -1;

    // Die constants
    public static final double DIE_ACCEL = 1;
    public static final int DIE_SCREENSHAKE = 60;

    public static final int DEAD = 0;
    public static final int IDLE = 1;

    public Boss(Coord coord, int spriteNo, int spriteSign, int frameCounter, Coord size, int state, double health, double difficulty) {
        super(coord, spriteNo, spriteSign, frameCounter, size, state);
        this.health = health * difficulty;
        this.difficulty = difficulty;
    }

    public void hurt(double damage) {
        health -= damage;
        if (!hurt) hurt = true;
        if (health <= 0) prepareToDie();
    }

    public void prepareToDie() {
        state = DEAD;
        frameCounter = 0;
        velocity.y = 0;
    }

    public void fall() {
        coord.y += velocity.y;
        velocity.y += DIE_ACCEL;
    }

    public void surge() {
        frameCounter++;
    }

    public void drawSurge(Graphics g) {
        if (frameCounter < OmegaFight3.SURGE_FRAME_HZ * OmegaFight3.NUM_SURGE_IMAGES) {
            g.drawImage(surge[frameCounter / OmegaFight3.SURGE_FRAME_HZ], (int) (coord.x - OmegaFight3.SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y), null);
        }
    }

    abstract public void transition();
    abstract public void attack();
    abstract public void backgroundAttack();
}