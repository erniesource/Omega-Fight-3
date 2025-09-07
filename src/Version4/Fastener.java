package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fastener extends Projectile {
    // Damage constants
    public static final double DMG = 7.5 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(80, 75);
    public static final double SIZE_TO_HITBOX = 0.75;

    // Movement constants
    public static final double VELOCITY = 25;
    public static final double ACCEL = 1;
    public static final double DECCEL = 0.5;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Sprite constants
    public static final int NUT = 0;
    public static final int BOLT = 1;
    public static final int NUM_TYPES = 2;
    public static final int[] NUM_SPRITES = {3, 4};
    public static final int SPRITE_CHANGE_HZ = 5;
    public static final String[] TYPE_NAME = {"nut", "bolt"};

    // Static images
    public static BufferedImage[][] images = new BufferedImage[NUM_TYPES][];

    // Instance variables
    public Coord fastenerVelocity;
    public int type;

    // Constructor with custom stats
    public Fastener(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        fastenerVelocity = new Coord(velocity * Math.cos(dir), velocity * Math.sin(dir));
        type = (int) (Math.random() * NUM_TYPES);
    }

    // Constructor with default stats
    public Fastener(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the fastener object
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(images[type][frameCounter / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        super.draw(g2);
    }

    // Description: Processes the fastener
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES[type] * SPRITE_CHANGE_HZ);

        // Movement
        coord = coord.add(fastenerVelocity);
        if (fastenerVelocity.x < 0) {
            fastenerVelocity.x += DECCEL;
            if (fastenerVelocity.x > 0) fastenerVelocity.x = 0;
        } else if (fastenerVelocity.x > 0) {
            fastenerVelocity.x -= DECCEL;
            if (fastenerVelocity.x < 0) fastenerVelocity.x = 0;
        }
        fastenerVelocity.y += ACCEL;

        // Check if out of screen
        checkLeave();
    }

    public void dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, Math.atan2(fastenerVelocity.y, fastenerVelocity.x), kbSpread);
            die();
        }
    }

    public void dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) super.dieTo(proj);
    }
}

class Energy extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(75, 55);
    public static final double SIZE_TO_HITBOX = 0.7;

    // Movement constants
    public static final double VELOCITY = 4;
    public static final double ROT_PER_SECOND = 2;
    public static final double ROT_SPD = Math.PI * 2 / OmegaFight3.FPS * ROT_PER_SECOND;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = false;
    public static final int NUM_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    // Instance variables
    public double rotation;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];

    // Constructor with custom stats
    public Energy(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
    }

    // Constructor with default stats
    public Energy(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: THis method draws the pellet of energy on screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(images[-frameCounter / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: This method processes the pellet
    public void process() {
        super.process();
        if (frameCounter == -SPRITE_CHANGE_HZ * NUM_SPRITES) frameCounter = 0;
        rotation = (rotation + ROT_SPD) % (Math.PI * 2);
    }

    public void dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            die();
        }
    }

    public void dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) super.dieTo(proj);
    }
}

class Pincer extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(80, 90);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 8;
    public static final int X_MOVEMENT_LEN = 40;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Instance variables
    public double trueDir;
    public int xMoveCounter = 10;

    // static images
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];

    // Customized constructor
    public Pincer(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        this.trueDir = dir;
    }

    // General constructor
    public Pincer(Boss boss, Coord coord) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, coord.x < OmegaFight3.SCREEN_SIZE.x / 2? 0: Math.PI,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the pincer
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: THis method processes the pincer
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES * SPRITE_CHANGE_HZ);

        // X-movement
        if (xMoveCounter != 0) {
            coord.x += velocity * Math.cos(dir);
            xMoveCounter--;
            if (xMoveCounter == 0) {
                if (coord.y < OmegaFight3.SCREEN_SIZE.y / 2) {
                    dir = Math.PI / 2; // down
                } else {
                    dir = -Math.PI / 2; // up
                }
            }
        }

        // Y-movement
        else {
            if (coord.y + velocity * Math.sin(dir) > OmegaFight3.SCREEN_SIZE.y || coord.y + velocity * Math.sin(dir) < 0) {
                dir = trueDir;
                xMoveCounter = X_MOVEMENT_LEN;
            }
            coord.y += velocity * Math.sin(dir);
        }

        // Check if out of left-right sides of the screen
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x) {
            die();
        }
    }

    public void dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            die();
        }
    }

    public void dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) super.dieTo(proj);
    }
}

class Bombot extends Projectile {
    // Size constants
    public static final Coord SIZE = new Coord(150, 75);
    public static final double SIZE_TO_SMOKE = 0.35;
    public static final double SIZE_TO_HITBOX = 1.0;
    public static final Coord EXPLOSION_SIZE_MULT = new Coord(2, 4);

    // Damage constants
    public static final double DMG = 15 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 4;

    // Velocity constants
    public static final double VELOCITY = 6;
    public static final int LIFE = 300;
    public static final double TURN_SPD = Math.PI * (180.0 / LIFE / 180.0);

    // Misc constants
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    // Instance variables
    public int sign;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NUM_SPRITES];

    // Constructor with custom stats
    public Bombot(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, int sign, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        this.sign = sign;
    }

    // General constructor with default stats
    public Bombot(Boss boss, Coord coord, double dir, int sign) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir,
        DMG * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB * OmegaFight3.DIFFICULTY_MULT[OmegaFight3.difficulty], KB_SPREAD, DURA, LIFE, sign, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method explodes the bombot
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
        }
        super.die();
    }

    // Description: This method draws the bombot
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter % (SPRITE_CHANGE_HZ * NUM_SPRITES) / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 * sign), (int) size.x, (int) size.y * sign, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: this method processes the bombot
    public void process() {
        // Movement
        super.process();

        // Homing
        Omegaman nearest = nearestOmegaman();
        if (nearest != null) {
            home(nearest, TURN_SPD);
        }

        // Smoke
        owner.smokeQ.add(new Smoke(coord.copy(), new Coord(Math.max(size.x, size.y) * SIZE_TO_SMOKE)));
    }

    public void dieTo(Char enemy) {
        if (enemy instanceof Omegaman) {
            ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            die();
            OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        }
    }

    public void dieTo(Projectile proj) {
        if (!(proj.owner instanceof Boss)) die();
    }
}