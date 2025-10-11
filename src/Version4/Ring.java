package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.sound.sampled.*;

public class Ring extends Projectile{
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(90, 135);
    public static final double SIZE_TO_HITBOX = 0.85;

    // Movement constants
    public static final double VELOCITY = 4;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];

    // Constructor with custom stats
    public Ring(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
    }

    // Constructor with default stats
    public Ring(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the laser ring
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(images[-frameCounter / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: this method processes the laser ring
    public void process() {
        super.process();
        if (frameCounter == -SPRITE_CHANGE_HZ * NUM_SPRITES) frameCounter = 0;
    }

    public boolean dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            die();
            return true;
        }
        return false;
    }

    public boolean dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) {
            return super.dieTo(proj);
        }
        return false;
    }
}

class Meteor extends Projectile {
    // Damage constants
    public static final double DMG = 5 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final int FIRE_TIME = 120;

    // Size constants
    public static final Coord SIZE = (new Coord(220, 170)).scaledBy(0.75);
    public static final double SIZE_TO_HITBOX = 0.7;
    public static final double SIZE_TO_SMOKE = 0.5;

    // Movement constants
    public static final double VELOCITY = 8;
    public static final double PERIODS = 1.75;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static variables
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];

    // Instance variables
    public int sign;

    // Constructor with custom stats
    public Meteor(Boss boss, Coord size, Coord hitBoxSize, double xCoord, double velocity, double dir, double damage, double knockback, double dura, double kbSpread, int sign, boolean canHitProj, boolean isOnTop) {
        super(boss, new Coord(xCoord), size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        this.sign = sign;
        func();
    }

    // Constructor with default stats
    public Meteor(Boss boss, double xCoord, int sign) {
        this(boss, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), xCoord, VELOCITY, 0,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, sign, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the meteor
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: This method processes the meteor
    public void process() {
        // Movement
        coord.x += velocity * sign;
        func();

        // Smoke
        owner.smokeQ.add(new Smoke(coord.copy(), new Coord(Math.max(size.x, size.y) * SIZE_TO_SMOKE)));

        // Sprite change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES * SPRITE_CHANGE_HZ);

        // Check if meteor is out of screen
        checkLeave();
    }

    // Description: This method uses a function and it's derivative to calculate the meteor's direction and y coordinate (First time I've ever actually applied differential calculus).
    // It's called func because it uses a function
    private void func() {
        coord.y = Math.abs(OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 - (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF.y)) / 2 * -Math.cos(Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS) * (coord.x - (Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF.x * sign))) + (OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 + (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF.y)) / 2;
        dir = Math.atan(Math.abs(OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 - (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF.y)) / 2 * (Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS)) * Math.sin(Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS) * (coord.x - (Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF.x * sign)))) + (sign == OmegaFight3.LFT_SIGN? Math.PI: 0);
    }

    public boolean dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            enemy.setFire(FIRE_TIME);
        }
        return false;
    }

    public boolean dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) {
            return super.dieTo(proj);
        }
        return false;
    }
}

class Bubble extends Projectile {
    // Damage constants
    public static final double DMG = 3 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final int FIRE_TIME = 120;

    // Size constants
    public static final Coord SIZE = new Coord(120, 120);
    public static final double SIZE_TO_HITBOX = 1.0;
    public static final double EXPLOSION_SIZE_MULT = 2;

    // Movement constants
    public static final double VELOCITY = 10;
    public static final double ACCEL = 2;
    public static final double ACCEL_TO_BIG_JUMP_VEL = -22;
    public static final double ACCEL_TO_SML_JUMP_VEL = -8;
    public static final double CHANCE_OF_BIG_JUMP = 0.33;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int SCREENSHAKE = 15;

    // Sprite constants
    public static final int NUM_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static variables
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];
    public static Clip bububup;

    // Instance variables
    public Coord bubbleVelocity;

    // Constructor with custom stats
    public Bubble(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, 0, (coord.x < OmegaFight3.SCREEN_SIZE.x / 2? 0: Math.PI), damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        bubbleVelocity = new Coord(velocity * Math.cos(dir), 0);
    }

    // Constructor with default stats
    public Bubble(Boss boss, Coord coord) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method explodes the bombot
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
        }
        super.die();
    }

    // Description: This method draws the Fire bubble
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        super.draw(g2);
    }

    // Description: This method processes the fire bubble
    public void process() {
        // Sprit change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES * SPRITE_CHANGE_HZ);

        // Movement
        coord.x += bubbleVelocity.x;
        coord.y += bubbleVelocity.y;
        if (coord.y > OmegaFight3.SCREEN_SIZE.y - size.y / 2) {
            bubbleVelocity.y = ACCEL * (Math.random() < CHANCE_OF_BIG_JUMP? ACCEL_TO_BIG_JUMP_VEL: ACCEL_TO_SML_JUMP_VEL);
            coord.y = OmegaFight3.SCREEN_SIZE.y - size.y / 2;
        }
        bubbleVelocity.y += ACCEL;
        dir = Math.atan2(bubbleVelocity.y, bubbleVelocity.x);

        // Check if fire bubble is outside of screen
        checkLeave();
    }

    // Description: This method returns that the missile should die to anything
    public boolean shouldDieTo(double enemyDura) {
        return true;
    }

    public boolean dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            die();
            OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
            enemy.setFire(FIRE_TIME);
            return true;
        }
        return false;
    }

    public boolean dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) return super.dieTo(proj);
        return false;
    }
}

class Fire extends Projectile {
    // Damage constants
    public static final double DMG = 6 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final int FIRE_TIME = 40;

    // Size constants
    public static final Coord SIZE = new Coord(150, 645);
    public static final double SIZE_TO_HITBOX = 0.75;

    // Movement constants
    public static final double VELOCITY = 5;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Sprite constants
    public static final int NUM_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    // Instance variables
    public double trueDir;

    // Static variables
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];
    public static Clip foosh;

    // Constructor with custom stats
    public Fire(Boss boss, Coord coord, double sizeX, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, new Coord(sizeX, 0), (new Coord(sizeX, 0)).scaledBy(SIZE_TO_HITBOX), velocity, OmegaFight3.normalizeAngle(dir) < 0? -Math.PI / 2: Math.PI / 2, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        trueDir = dir;
    }

    // Constructor with default stats
    public Fire(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.x, VELOCITY, dir,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the Fire from the ceiling
    public void draw(Graphics2D g2) {
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 * Math.sin(trueDir)), (int) size.x, (int) (size.y * Math.sin(trueDir)), null);
        super.draw(g2);
    }

    // Description: This method processes the fire from the ceiling
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES * SPRITE_CHANGE_HZ);

        // Locational changes
        coord.y += velocity * Math.sin(dir);
        size.y += velocity * Math.sin(dir) * 2;
        hitBoxSize.y += velocity * Math.sin(dir) * 2 * SIZE_TO_HITBOX;
        if (Math.abs(size.y) > Math.abs(SIZE.y)) {
            size.y = SIZE.y;
            dir *= -1;
        }

        // Check if fire from the ceiling is out of the screen
        if (coord.y <= 0 || coord.y >= OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
    }

    public boolean dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, trueDir, kbSpread);
            enemy.setFire(FIRE_TIME);
        }
        return false;
    }

    public boolean dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) return super.dieTo(proj);
        return false;
    }
}