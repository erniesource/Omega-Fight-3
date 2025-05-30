package Version2;

import java.awt.*;
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

    public void processProjectiles(Graphics2D g2) {
        for (Projectile proj: projectiles) {
            proj.process();
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
    // Combat stats
    public int transitionTo;
    public double health;

    public static final double BOSS_HITBOX_LEEWAY = 20;

    public Boss(Coord coord, int spriteNo, int spriteSign, int frameCounter, Coord size, int state, double health) {
        super(coord, spriteNo, spriteSign, frameCounter, size, state);
        this.health = health;
    }

    public void process() {
        coord.x += velocity.x;
        coord.y += velocity.y;
    }

    public void hurt(double damage) {
        health -= damage;
    }
}