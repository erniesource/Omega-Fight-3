package Version4;

import java.awt.*;

abstract public class Projectile {
    // Misc Variables
    public static final double INF_DURA = 100;
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
        dura -= proj.dura;
        if (dura <= 0) die();
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

    public Char nearestChar() {
        Char nearestOmega = nearestOmegaman();
        Char nearestBoss = nearestBoss();

        double minDist = Double.MAX_VALUE;
        Char nearest = null;

        if (nearestOmega != null) {
            double dist = coord.disto(nearestOmega.coord);
            if (dist < minDist) {
                minDist = dist;
                nearest = nearestOmega;
            }
        }
        if (nearestBoss != null) {
            double dist = coord.disto(nearestBoss.coord);
            if (dist < minDist) {
                minDist = dist;
                nearest = nearestBoss;
            }
        }
        return nearest;
    }

    public Omegaman nearestOmegaman() {
        Omegaman nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Omegaman omega : OmegaFight3.omegaman) {
            if (omega == owner || omega.state != Omegaman.ALIVE_STATE) continue;
            double dist = coord.disto(omega.coord);
            if (dist < minDist) {
                minDist = dist;
                nearest = omega;
            }
        }
        return nearest;
    }

    public Boss nearestBoss() {
        Boss nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Boss boss : OmegaFight3.bosses) {
            if (boss == owner || boss.state == Boss.DEAD) continue;
            double dist = coord.disto(boss.coord);
            if (dist < minDist) {
                minDist = dist;
                nearest = boss;
            }
        }
        return nearest;
    }

    public void home(Char target, double turnSpd) {
        double targetDir = coord.angleTo(target.coord);
        double angleDif = targetDir - dir;
        angleDif = Math.atan2(Math.sin(angleDif), Math.cos(angleDif));
        if (Math.abs(angleDif) <= turnSpd) dir = targetDir;
        else dir += Math.signum(angleDif) * turnSpd;
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