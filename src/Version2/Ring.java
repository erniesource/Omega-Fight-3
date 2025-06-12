package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Ring extends Projectile{
    public static BufferedImage[] images = new BufferedImage[3];
    
    // Damage constants
    public static final double DMG = 10 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(90, 135);
    public static final double SIZE_TO_HITBOX = 0.85;

    // Movement constants
    public static final double VELOCITY = 4;
    public static final int LIFE = INF_LIFE;

    // Misc constants
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    public Ring(Boss boss, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    public Ring(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        coord.x += velocity * Math.cos(dir);
        coord.y += velocity * Math.sin(dir);
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                die();
            }
            if (canHitProj) {
                for (Projectile proj: enemy.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Meteor extends Projectile {
    public int sign;

    public static BufferedImage[] images = new BufferedImage[3];

    // Damage constants
    public static final double DMG = 5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = (new Coord(220, 170)).scaledBy(0.75);
    public static final double SIZE_TO_HITBOX = 0.7;

    // Movement constants
    public static final double VELOCITY = 8;
    public static final int LIFE = INF_LIFE;
    public static final double PERIODS = 1.75;

    // Misc constants
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    public Meteor(Boss boss, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int sign, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        this.sign = sign;
        func();
    }

    public Meteor(Boss boss, double xCoord, int sign) {
        this(boss, new Coord(xCoord, 0), SIZE, VELOCITY, 0, DMG, KB, DURABILITY, LIFE, sign, CAN_HIT_PROJ);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        coord.x += velocity * sign;
        func();
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
            }
            if (canHitProj) {
                for (Projectile proj: enemy.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    public void func() {
        coord.y = Math.abs(OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 - (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF_COORD.y)) / 2 * -Math.cos(Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS) * (coord.x - (Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * sign))) + (OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 + (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF_COORD.y)) / 2;
        dir = Math.atan(Math.abs(OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 - (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF_COORD.y)) / 2 * (Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS)) * Math.sin(Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS) * (coord.x - (Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * sign)))) + (sign == OmegaFight3.LEFT_SIGN? 0: Math.PI);
    }
}

class Bubble extends Projectile {
    public Coord bubbleVelocity;

    public static BufferedImage[] images = new BufferedImage[3];
    
    // Damage constants
    public static final double DMG = 10 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 2;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(110, 120);
    public static final double SIZE_TO_HITBOX = 0.75;

    // Movement constants
    public static final int LIFE = Projectile.INF_LIFE;
    public static final double VELOCITY = 10;
    public static final double ACCEL = 2;
    public static final double ACCEL_TO_BIG_JUMP_VEL = -22;
    public static final double ACCEL_TO_SML_JUMP_VEL = -8;
    public static final double CHANGE_OF_BIG_JUMP = 0.33;

    // Misc constants
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    // Sprite constants
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    public Bubble(Boss boss, Coord coord, Coord size, double velocity, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), 0, (coord.x < OmegaFight3.SCREEN_SIZE.x / 2? 0: Math.PI), damage, knockback, durability, frameCounter, canHitProj);
        bubbleVelocity = new Coord(velocity * Math.cos(dir), 0);
    }

    public Bubble(Boss boss, Coord coord) {
        this(boss, coord, SIZE, VELOCITY, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    public void process() {
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);
        coord.x += bubbleVelocity.x;
        coord.y += bubbleVelocity.y;
        if (coord.y > OmegaFight3.SCREEN_SIZE.y - size.y / 2) {
            bubbleVelocity.y = ACCEL * (Math.random() < CHANGE_OF_BIG_JUMP? ACCEL_TO_BIG_JUMP_VEL: ACCEL_TO_SML_JUMP_VEL);
            coord.y = OmegaFight3.SCREEN_SIZE.y - size.y / 2;
        }
        bubbleVelocity.y += ACCEL;
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                die();
            }
            if (canHitProj) {
                for (Projectile proj: enemy.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Fire extends Projectile {
    public double trueDir;

    public static BufferedImage[] images = new BufferedImage[2];
    
    // Damage constants
    public static final double DMG = 10 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 2;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(150, 645);
    public static final double SIZE_TO_HITBOX = 0.75;

    // Movement constants
    public static final int LIFE = Projectile.INF_LIFE;
    public static final double VELOCITY = 5;

    // Misc constants
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;

    // Sprite constants
    public static final int NO_OF_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    public Fire(Boss boss, Coord coord, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, new Coord(SIZE.x, 0), (new Coord(SIZE.x, 0)).scaledBy(SIZE_TO_HITBOX), velocity, OmegaFight3.normalizeAngle(dir) < 0? -Math.PI / 2: Math.PI / 2, damage, knockback, durability, frameCounter, canHitProj);
        trueDir = dir;
    }

    public Fire(Boss boss, Coord coord, double dir) {
        this(boss, coord, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 * Math.signum(trueDir)), (int) size.x, (int) (size.y * Math.signum(trueDir)), null);
    }

    public void process() {
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);
        coord.y += velocity * Math.sin(dir);
        size.y += velocity * Math.sin(dir) * 2;
        hitBoxSize.y += velocity * Math.sin(dir) * 2 * SIZE_TO_HITBOX;
        if (Math.abs(size.y) > Math.abs(SIZE.y)) {
            size.y = SIZE.y;
            dir *= -1;
        }
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
            }
            if (canHitProj) {
                for (Projectile proj: enemy.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}