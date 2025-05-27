package Version2;

import java.awt.*;
import java.util.*;

abstract class Char {
    public Coord coord;
    public int spriteNo;
    public int spriteSign; // 1: Positive, -1: Negative
    public int frameCounter; // Framecounter for running player
    public Coord size;
    public int state;
    public HashSet<Projectile> newProjectiles = new HashSet<>();
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

    public void addNewProjectiles() {
        for (Projectile proj: newProjectiles) {
            projectiles.add(proj);
        }
        newProjectiles.clear();
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

    public boolean checkHitbox(Coord enemyCoord, Coord enemySize) {
        return Math.abs(enemyCoord.x - coord.x) <= (enemySize.x + size.x) / 2 - OmegaFight3.HITBOX_LEEWAY && Math.abs(enemyCoord.y - coord.y) <= (enemySize.y + size.y) / 2 - OmegaFight3.HITBOX_LEEWAY;  
    }

    abstract public void hurt(double damage);
    abstract public void draw(Graphics g);
}
