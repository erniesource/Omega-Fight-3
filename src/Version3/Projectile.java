package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

abstract public class Projectile {
    // Explosion variables
    public static final int EXPLOSION_FRAME_HZ = 4;
    public static final int NUM_EXPLOSION_IMAGES = 8;
    public static final int EXPLOSION_TIME = EXPLOSION_FRAME_HZ * NUM_EXPLOSION_IMAGES;
    public static BufferedImage[] explosionImages = new BufferedImage[NUM_EXPLOSION_IMAGES];
    public static final int NUM_OF_WEAPONS = 6;
    public static final double INFINITE_DURABILITY = 100;

    // Misc Variables
    public static final int NO_OF_PLAYER_PROJECTILES = 6;
    public static final int INF_LIFE = 0;
    
    // General variables
    public Char character;
    public Coord coord;
    public Coord size;
    public Coord hitBoxSize;
    public boolean hitBoxActive = true;
    public boolean canHitProj;

    // Movement variables
    public double velocity;
    public double dir;
    public int frameCounter;

    // Combat variables
    public double damage;
    public double knockback;
    public double durability;

    public Projectile(Char character, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        this.character = character;
        this.coord = coord;
        this.size = size;
        this.hitBoxSize = hitBoxSize;
        this.velocity = velocity;
        this.dir = dir;
        this.damage = damage;
        this.knockback = knockback;
        this.durability = durability;
        this.frameCounter = frameCounter;
        this.canHitProj = canHitProj;
    }

    public void die() {
        character.deadProjectiles.add(this);
    }

    public void process() {
        coord.x += velocity * Math.cos(dir);
        coord.y += velocity * Math.sin(dir);
        frameCounter--;
        if (frameCounter == 0) die();
    }

    public boolean shouldDieTo(double enemyDurability) {
        return durability <= enemyDurability;
    }

    // Methods that exist purely for polymorphism to happen
    abstract public void draw(Graphics2D g2);
}