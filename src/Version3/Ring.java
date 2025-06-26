package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Ring extends Projectile{
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
    public static final boolean CAN_HIT_PROJ = true;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Ring(Boss boss, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Constructor with default stats
    public Ring(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Description: This method draws the laser ring
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: this method processes the laser ring
    public void process() {
        // Movement
        coord.x += velocity * Math.cos(dir);
        coord.y += velocity * Math.sin(dir);
        
        // Sprite change
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);

        // Check if ring is out of screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check each player's hitbox and projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD);
                die();
            }

            // Enemy projectiles
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
    public static final boolean CAN_HIT_PROJ = false;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Instance variables
    public int sign;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Meteor(Boss boss, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int sign, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        this.sign = sign;
        func();
    }

    // Constructor with default stats
    public Meteor(Boss boss, double xCoord, int sign) {
        this(boss, new Coord(xCoord, 0), SIZE, VELOCITY, 0, DMG, KB, DURABILITY, LIFE, sign, CAN_HIT_PROJ);
    }

    // Description: This method draws the meteor
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: This method processes the meteor
    public void process() {
        // Movement
        coord.x += velocity * sign;
        func();

        // Sprite change
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);

        // Check if meteor is out of screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Loop through every player's hitbox and projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD);
            }

            // Enemy projectiles
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

    // Description: This method uses a function and it's derivative to calculate the meteor's direction and y coordinate (First time I've ever actually applied differential calculus).
    // It's called func because it uses a function
    public void func() {
        coord.y = Math.abs(OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 - (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF_COORD.y)) / 2 * -Math.cos(Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS) * (coord.x - (Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * sign))) + (OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 + (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF_COORD.y)) / 2;
        dir = Math.atan(Math.abs(OmegaFight3.SCREEN_SIZE.y - SIZE.y / 2 - (Dragon.STATE_COORD[Dragon.BARF].y + Dragon.COORD_TO_BARF_COORD.y)) / 2 * (Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS)) * Math.sin(Math.PI * 2 / ((Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * Dragon.STATE_SPRITE_SIGN[Dragon.BARF]) / PERIODS) * (coord.x - (Dragon.STATE_COORD[Dragon.BARF].x + Dragon.COORD_TO_BARF_COORD.x * sign)))) + (sign == OmegaFight3.LEFT_SIGN? 0: Math.PI);
    }
}

class Bubble extends Projectile {
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
    public static final boolean CAN_HIT_PROJ = true;

    // Sprite constants
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Instance variables
    public Coord bubbleVelocity;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Bubble(Boss boss, Coord coord, Coord size, double velocity, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), 0, (coord.x < OmegaFight3.SCREEN_SIZE.x / 2? 0: Math.PI), damage, knockback, durability, frameCounter, canHitProj);
        bubbleVelocity = new Coord(velocity * Math.cos(dir), 0);
    }

    // Constructor with default stats
    public Bubble(Boss boss, Coord coord) {
        this(boss, coord, SIZE, VELOCITY, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Description: This method draws the Fire bubble
    public void draw(Graphics2D g2) {
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    // Description: This method processes the fire bubble
    public void process() {
        // Sprit change
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);

        // Movement
        coord.x += bubbleVelocity.x;
        coord.y += bubbleVelocity.y;
        if (coord.y > OmegaFight3.SCREEN_SIZE.y - size.y / 2) {
            bubbleVelocity.y = ACCEL * (Math.random() < CHANGE_OF_BIG_JUMP? ACCEL_TO_BIG_JUMP_VEL: ACCEL_TO_SML_JUMP_VEL);
            coord.y = OmegaFight3.SCREEN_SIZE.y - size.y / 2;
        }
        bubbleVelocity.y += ACCEL;

        // Check if fire bubble is outside of screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Loop through every player's hitbox and projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD);
                die();
            }

            // Enemy projectiles
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
    public static final boolean CAN_HIT_PROJ = false;

    // Sprite constants
    public static final int NO_OF_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    // Instance variables
    public double trueDir;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Fire(Boss boss, Coord coord, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, new Coord(SIZE.x, 0), (new Coord(SIZE.x, 0)).scaledBy(SIZE_TO_HITBOX), velocity, OmegaFight3.normalizeAngle(dir) < 0? -Math.PI / 2: Math.PI / 2, damage, knockback, durability, frameCounter, canHitProj);
        trueDir = dir;
    }

    // Constructor with default stats
    public Fire(Boss boss, Coord coord, double dir) {
        this(boss, coord, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Description: This method draws the Fire from the ceiling
    public void draw(Graphics2D g2) {
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 * Math.signum(trueDir)), (int) size.x, (int) (size.y * Math.signum(trueDir)), null);
    }

    // Description: This method processes the fire from the ceiling
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);

        // Locational changes
        coord.y += velocity * Math.sin(dir);
        size.y += velocity * Math.sin(dir) * 2;
        hitBoxSize.y += velocity * Math.sin(dir) * 2 * SIZE_TO_HITBOX;
        if (Math.abs(size.y) > Math.abs(SIZE.y)) {
            size.y = SIZE.y;
            dir *= -1;
        }

        // Check if fire from the ceiling is out of the screen
        if (coord.y <= 0) {
            die();
        }

        // Loop through every player's hitbox and projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD);
            }

            // Enemy projectiles
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