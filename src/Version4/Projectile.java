package Version4;

import java.awt.*;

abstract public class Projectile {
    // Misc Variables
    public static final double INF_DURA = 100;
    public static final int NUM_PLAYER_PROJS = 6;
    public static final int INF_LIFE = 0;
    
    // General variables
    public Char owner;
    public Coord coord;
    public Coord size;
    public Coord hitBoxSize;
    public boolean hitBoxActive = true;
    public boolean canHitProj;
    public boolean isOnTop;
    public boolean dead;

    // Movement variables
    public double velocity;
    public double dir;
    public int frameCounter;

    // Combat variables
    public double damage;
    public double knockback;
    public double kbSpread;
    public double dura;

    // Constructor
    public Projectile(Char owner, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        this.owner = owner;
        this.coord = coord;
        this.size = size;
        this.hitBoxSize = hitBoxSize;
        this.velocity = velocity;
        this.dir = dir;
        this.damage = damage;
        this.knockback = knockback;
        this.kbSpread = kbSpread;
        this.dura = dura;
        this.frameCounter = frameCounter;
        this.canHitProj = canHitProj;
        this.isOnTop = isOnTop;
    }

    // Description: THis method kills the projectile
    public void die() {
        if (!dead) {
            dead = true;
            OmegaFight3.deadProjectiles.add(this); // Make it so that all projs die in smoke or explosion?
        }
    }

    public void dieTo(Projectile proj) {
        if (dura < proj.dura) die();
    }

    public void dieTo(Char enemy) {
        enemy.hurt(damage);
        die();
    }

    // Description: THis method processes the movement and expiry of the projectile
    public void process() {
        move();
        expire();
        checkLeave();
    }

    public void checkLeave() {
        if (OmegaFight3.outOfScreen(coord, size)) die();
    }

    public void expire() {
        frameCounter--;
        if (frameCounter == 0) die();
    }

    public void move() {
        coord.x += velocity * Math.cos(dir);
        coord.y += velocity * Math.sin(dir);
    }

    // Abstract method(s)
    public void draw(Graphics2D g2) {
        if (OmegaFight3.hitBoxVis) {
            Coord hitBoxCoord = coord.add(hitBoxSize.scaledBy(-0.5));
            g2.setColor(OmegaFight3.CHEAT_COLOR);
            g2.drawRect((int) (hitBoxCoord.x), (int) (hitBoxCoord.y), (int) (hitBoxSize.x), (int) (hitBoxSize.y)); // FIGURE THIS OUT
        }
    }
}