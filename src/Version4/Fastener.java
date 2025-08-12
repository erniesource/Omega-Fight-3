package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fastener extends Projectile {
    // Damage constants
    public static final double DMG = 7.5 * Omegaman.PERC_MULT;
    public static final double DURABILITY = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

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
    public static final int NO_OF_TYPES = 2;
    public static final int[] NUM_SPRITES = {3, 4};
    public static final int SPRITE_CHANGE_HZ = 5;

    // Static images
    public static BufferedImage[][] images = new BufferedImage[NO_OF_TYPES][];

    // Instance variables
    public Coord fastenerVelocity;
    public int type;

    // Constructor with custom stats
    public Fastener(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double durability, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, durability, INF_LIFE, canHitProj, isOnTop);
        fastenerVelocity = new Coord(velocity * Math.cos(dir), velocity * Math.sin(dir));
        type = (int) (Math.random() * NO_OF_TYPES);
    }

    // Constructor with default stats
    public Fastener(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, DURABILITY, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the fastener object
    public void draw(Graphics2D g2) {
        g2.drawImage(images[type][frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    // Description: Processes the fastener
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES[type] * SPRITE_CHANGE_HZ);

        // Movement
        coord.x += fastenerVelocity.x;
        coord.y += fastenerVelocity.y;
        if (fastenerVelocity.x < 0) {
            fastenerVelocity.x += DECCEL;
            if (fastenerVelocity.x > 0) fastenerVelocity.x = 0;
        } else if (fastenerVelocity.x > 0) {
            fastenerVelocity.x -= DECCEL;
            if (fastenerVelocity.x < 0) fastenerVelocity.x = 0;
        }
        fastenerVelocity.y += ACCEL;

        // Check if out of screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Loop through players and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, Math.atan2(fastenerVelocity.y, fastenerVelocity.x), KB_SPREAD);
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

class Energy extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURABILITY = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(75, 55);
    public static final double SIZE_TO_HITBOX = 0.7;

    // Movement constants
    public static final double VELOCITY = 4;
    public static final double ROTATION_PER_SECOND = 2;
    public static final double ROTATION_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROTATION_PER_SECOND;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = false;
    public static final int NO_OF_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    // Instance variables
    public double rotation;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Energy(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double durability, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, durability, INF_LIFE, canHitProj, isOnTop);
    }

    // Constructor with default stats
    public Energy(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, DURABILITY, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: THis method draws the pellet of energy on screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(images[-frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }

    // Description: This method processes the pellet
    public void process() {
        super.process();
        if (frameCounter == -SPRITE_CHANGE_HZ * NO_OF_SPRITES) frameCounter = 0;
        rotation = (rotation + ROTATION_SPEED) % (Math.PI * 2);

        // Check player hitboxes and projectiles
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

class Pincer extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURABILITY = 2;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(80, 90);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 8;
    public static final int X_MOVEMENT_LEN = 40;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Instance variables
    public double trueDir;
    public int xMovementCounter = 10;

    // static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Customized constructor
    public Pincer(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double durability, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, durability, INF_LIFE, canHitProj, isOnTop);
        this.trueDir = dir;
    }

    // General constructor
    public Pincer(Boss boss, Coord coord) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, coord.x < OmegaFight3.SCREEN_SIZE.x / 2? 0: Math.PI, DMG, KB, DURABILITY, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the pincer
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: THis method processes the pincer
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);

        // X-movement
        if (xMovementCounter != 0) {
            coord.x += velocity * Math.cos(dir);
            xMovementCounter--;
            if (xMovementCounter == 0) {
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
                xMovementCounter = X_MOVEMENT_LEN;
            }
            coord.y += velocity * Math.sin(dir);
        }

        // Check if out of left-right sides of the screen
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x) {
            die();
        }

        // Check player hitboxes and projectiles
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

class Bombot extends Projectile {
    // Size constants
    public static final Coord SIZE = new Coord(150, 75);
    public static final double SIZE_TO_SMOKE_SIZE = 0.35;
    public static final double SIZE_TO_HITBOX = 1.0;
    public static final Coord EXPLOSION_SIZE_MULT = new Coord(2, 4);

    // Damage constants
    public static final double DMG = 15 * Omegaman.PERC_MULT;
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 3;

    // Velocity constants
    public static final double VELOCITY = 6;
    public static final int LIFE = 200;
    public static final double TURN_SPEED = Math.PI * (180.0 / LIFE / 180.0);

    // Misc constants
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NO_OF_SPRITES = 2;
    public static final int SPRITE_CHANGE_HZ = 10;

    // Instance variables
    public int sign;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Bombot(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int sign, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, durability, frameCounter, canHitProj, isOnTop);
        this.sign = sign;
    }

    // General constructor with default stats
    public Bombot(Boss boss, Coord coord, double dir, int sign) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, DURABILITY, LIFE, sign, CAN_HIT_PROJ, IS_ON_TOP);
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
        g2.drawImage(images[frameCounter % (SPRITE_CHANGE_HZ * NO_OF_SPRITES) / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 + size.y * ((sign - 1) / -2)), (int) size.x, (int) size.y * sign, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: this method processes the bombot
    public void process() {
        // Movement
        super.process();

        // Homing
        // Loop thru all players and find closest target
        Char target = null;
        double closestDist = Double.MAX_VALUE;
        for (Omegaman enemy : OmegaFight3.omegaman) {
            if (enemy.state == Omegaman.ALIVE_STATE) {
                double dist = Math.hypot(enemy.coord.x - coord.x, enemy.coord.y - coord.y);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = enemy;
                }
            }
        }
        
        // Adjust direction and home
        if (target != null) {
            double targetDir = Math.atan2(target.coord.y - coord.y, target.coord.x - coord.x);
            double angleDif = targetDir - dir;
            angleDif = Math.atan2(Math.sin(angleDif), Math.cos(angleDif));
            if (Math.abs(angleDif) <= TURN_SPEED) dir = targetDir;
            else dir += Math.signum(angleDif) * TURN_SPEED;
        }

        // Smoke
        character.smokeQ.add(new Smoke(coord.copy(), new Coord(Math.max(size.x, size.y) * SIZE_TO_SMOKE_SIZE), Math.random() * Math.PI * 2));

        // Loop through all players and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, dir, KB_SPREAD);
                OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
                die();
            }

            // Enemy projectiles
            if (canHitProj) {
                for (Projectile proj: enemy.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                        die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    // Description: This method returns that the missile should die to anything
    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}