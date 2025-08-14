package Version4;

import java.awt.*;

abstract public class Projectile {
    // Misc Variables
    public static final double INF_DURA = 100;
    public static final int NO_OF_PLAYER_PROJECTILES = 6;
    public static final int INF_LIFE = 0;
    
    // General variables
    public Char character;
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
    public Projectile(Char character, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        this.character = character;
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
            OmegaFight3.deadProjectiles.add(this);
        }
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

    public void hitPlayerProj(Projectile proj) {
        if (shouldDieTo(proj.dura)) die();
        if (proj.shouldDieTo(dura)) proj.die();
    }

    public void hitBossProj(Projectile proj) {
        if (shouldDieTo(proj.dura)) die();
        if (proj.shouldDieTo(dura)) proj.die();
    }

    public void hitPlayer(Omegaman omega) {
        omega.hurt(damage, knockback, coord, dir, kbSpread);
        die();
    }

    public void hitBoss(Boss boss) {
        boss.hurt(damage);
        die();
    }

    // Description: This method determines whether or not this projectile should die to another projectile with the specified dura
    public boolean shouldDieTo(double enemyDura) {
        return dura <= enemyDura;
    }

    // Abstract method(s)
    abstract public void draw(Graphics2D g2);
}