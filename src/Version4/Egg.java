package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Egg extends Projectile{
    // Damage constants
    public static final double[] DMG = {2 * Omegaman.PERC_MULT, 4 * Omegaman.PERC_MULT, 8 * Omegaman.PERC_MULT};
    public static final double DURA = 1;
    public static final double[] KB = {3, 6, 12};
    public static final double KB_SPREAD = Math.PI / 3;
    public static final int[] PROJS_PER_SPLIT = {2, 2};

    // Size constants
    public static final Coord[] SIZE = {new Coord(70, 60), new Coord(120, 95), new Coord(200, 160)};
    public static final double SIZE_TO_HITBOX = 0.7;
    public static final double SIZE_TO_SMOKE_SIZE = 0.5;

    // Movement constants
    public static final double[] VELOCITY = {16, 22, 40};
    public static final double ACCEL = 1;
    public static final double ROTATION_PER_SECOND = 2;
    public static final double ROTATION_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROTATION_PER_SECOND;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Sprite constants
    public static final int NO_OF_STATES = 3;
    public static final int[] NUM_TYPES = {3, 1, 1};
    public static final int[] NUM_SPRITES = {1, 1, 1};
    public static final String[] STATE_NAMES = {"bit", "shell", "egg"};
    public static final int SPRITE_CHANGE_HZ = -1;

    // Static images
    public static BufferedImage[][][] images = new BufferedImage[NO_OF_STATES][][];

    // Instance variables
    public Coord eggVelocity;
    public double rotation;
    public int state;
    public int type;

    // Constructor with custom stats
    public Egg(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int state, int type, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        eggVelocity = new Coord(velocity * Math.cos(dir), velocity * Math.sin(dir));
        this.state = state;
        this.type = type;
    }

    // Constructors with default stats
    public Egg(Boss boss, Coord coord, double dir, int state) {
        this(boss, coord, SIZE[state].copy(), SIZE[state].scaledBy(SIZE_TO_HITBOX), VELOCITY[state], dir, DMG[state], KB[state], KB_SPREAD, DURA, state, (int) (Math.random() * NUM_TYPES[state]), CAN_HIT_PROJ, IS_ON_TOP);
    }
    public Egg(Boss boss, Coord coord, double dir) {
        this(boss, coord, dir, NO_OF_STATES - 1);
    }

    // Description: Draws the egg object
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(images[state][type][frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }

    // Description: Processes the egg
    public void process() {
        // Sprite change
        frameCounter = (frameCounter + 1) % (NUM_SPRITES[type] * SPRITE_CHANGE_HZ);
        rotation = (rotation + ROTATION_SPEED) % (Math.PI * 2);

        // Movement
        coord.x += eggVelocity.x;
        coord.y += eggVelocity.y;
        eggVelocity.y += ACCEL;

        // Smokes (Does every state need smoke?)
        character.smokeQ.add(new Smoke(coord.copy(), new Coord(Math.max(size.x, size.y) * SIZE_TO_SMOKE_SIZE), Math.random() * Math.PI * 2));

        // Check if out of screen
        if (coord.x < -size.x / 2 || coord.x > OmegaFight3.SCREEN_SIZE.x + size.x / 2 || coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
            die();
        }
        else {
            for (Platform platform: OmegaFight3.stage[OmegaFight3.stageNo].platforms) {
                if (platform.landed(coord.x, size.y, coord.y, coord.y + eggVelocity.y)) {
                    coord.y = platform.y;
                    die();
                    break;
                }
            }
        }
    }

    public void die() { // Change this to be like spike
        if (!dead) {
            super.die();

            // Split if not at final state
            if (state != 0) {
                for (int i = 0; i != PROJS_PER_SPLIT[state - 1]; i++) {
                    OmegaFight3.babyProjectiles.add(new Egg((Boss) character, coord.copy(), -Math.PI / (PROJS_PER_SPLIT[state - 1] + 1) * (i + 1), state - 1));
                }
            }
        }
    }

    public void hitPlayer(Omegaman omega) {
        omega.hurt(damage, knockback, coord, Math.atan2(eggVelocity.y, eggVelocity.x), kbSpread);
        super.die();
    }
    public void hitBoss(Boss boss) {}
    public void hitBossProj(Projectile proj) {}
}

class Feather extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(155, 40);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 8;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = false;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Feather(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
    }

    // Constructor with default stats
    public Feather(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE.copy(), (new Coord(Math.min(SIZE.x, SIZE.y))).scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: THis method draws the pellet of energy on screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[-frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: This method processes the pellet
    public void process() {
        super.process();
        if (frameCounter == -SPRITE_CHANGE_HZ * NO_OF_SPRITES) frameCounter = 0;
    }

    public void hitBoss(Boss boss) {}
    public void hitBossProj(Projectile proj) {}
}

class Diver extends Projectile {
    // Size constants
    public static final Coord SIZE = new Coord(160, 110);
    public static final double SIZE_TO_SMOKE_SIZE = 0.35;
    public static final double SIZE_TO_HITBOX = 1.25;
    public static final Coord EXPLOSION_SIZE_MULT = new Coord(33.0/20, 48.0/20);

    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;
    public static final double DMG_KB_MULT = 0.35; // FIDDLE WITH THIS

    // Velocity constants
    public static final double VELOCITY = 6;
    public static final double DIVE_ANGLE = 7.0 / 9 * Math.PI / 2;
    public static final double DIVE_ANGLE_LEEWAY = Math.PI / 60;
    public static final double ACCEL = 1;

    // State constants
    public static final int TRAVELLING = 0;
    public static final int DIVING = 1;

    // Misc constants
    public static final int SCREENSHAKE = 2;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NO_OF_SPRITES = 4;
    public static final int SPRITE_CHANGE_HZ = 5;

    // Instance variables
    public int state = TRAVELLING;
    public int sign;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Diver(Boss boss, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int sign, boolean canHitProj, boolean isOnTop) {
        super(boss, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
        this.sign = sign;
    }

    // General constructor with default stats
    public Diver(Boss boss, Coord coord, double dir, int sign) {
        this(boss, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, sign, CAN_HIT_PROJ, IS_ON_TOP);
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
        g2.drawImage(images[-frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 + size.y * ((sign - 1) / -2)), (int) size.x, (int) size.y * sign, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: this method processes the bombot
    public void process() {
        // Movement
        super.process();
        if (frameCounter == -SPRITE_CHANGE_HZ * NO_OF_SPRITES) frameCounter = 0;

        if (state == TRAVELLING) {
            // Loop through all players and their projectiles
            for (Omegaman enemy: OmegaFight3.omegaman) {
                // Check if should dive (maybe check if state alr diving just in case?) I just check the angle and see if it's within a few degrees of diving angle
                if (Math.abs(Math.atan2(enemy.coord.y - coord.y, enemy.coord.x - coord.x) - (sign == OmegaFight3.RIGHT_SIGN? DIVE_ANGLE: Math.PI - DIVE_ANGLE)) <= DIVE_ANGLE_LEEWAY) {
                    dir = (sign == OmegaFight3.RIGHT_SIGN? DIVE_ANGLE: Math.PI - DIVE_ANGLE);
                    state = DIVING;
                }
            }
        }

        // Diving
        else {
            // Accelerate
            velocity += ACCEL;

            for (Platform platform: OmegaFight3.stage[OmegaFight3.stageNo].platforms) {
                if (platform.isMain && platform.landed(coord.x, size.y, coord.y, coord.y + velocity * Math.sin(dir))) {
                    coord.y = platform.y;
                    die();
                    break;
                }
            }

            // Smoke
            character.smokeQ.add(new Smoke(coord.copy(), new Coord(Math.max(size.x, size.y) * SIZE_TO_SMOKE_SIZE), Math.random() * Math.PI * 2));
        }
    }

    public boolean shouldDieTo(Projectile proj) {
        return true;
    }

    public void hitPlayer(Omegaman omega) {
        omega.hurt(damage * Math.sqrt(velocity) * DMG_KB_MULT, knockback * Math.sqrt(velocity) * DMG_KB_MULT, coord, dir, KB_SPREAD);
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * Math.sqrt(velocity));
        die();
    }
    public void hitBoss(Boss boss) {}
    public void hitBossProj(Projectile proj) {}
}

class Plush extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(134, 103);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = false;
    public static final int NO_OF_STATES = 3;
    public static final int NO_OF_SPRITES = 1;
    public static final int SPRITE_CHANGE_HZ = 1;
    public static final int DEAD = -1;

    // Static images
    public static BufferedImage[][] images = new BufferedImage[NO_OF_STATES][NO_OF_SPRITES];

    public int state = NO_OF_STATES - 1; // rotate on it's own?

    // Constructor with custom stats
    public Plush(Boss boss, Coord size, Coord hitBoxSize, double damage, double knockback, double kbSpread, double dura, boolean canHitProj, boolean isOnTop) {
        super(boss, new Coord(OmegaFight3.SCREEN_CENTER.x, OmegaFight3.stage[OmegaFight3.NORTH_CAVE_NO].platforms[0].y - SIZE.y / 2), size, hitBoxSize, 0, 0, damage, knockback, kbSpread, dura, INF_LIFE, canHitProj, isOnTop);
    }

    // Constructor with default stats
    public Plush(Boss boss) {
        this(boss, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), DMG, KB, KB_SPREAD, DURA, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: THis method draws the plush on screen
    public void draw(Graphics2D g2) {
        if (state != DEAD) {
            g2.rotate(dir, coord.x, coord.y);
            g2.drawImage(images[state][frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
            g2.rotate(-dir, coord.x, coord.y);
        }
    }

    // Description: This method processes the pellet
    public void process() {
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);
    }

    public void die() {
        if (state != DEAD) {
            state--;
            if (state == DEAD) {
                hitBoxActive = false;
                ((Punk) character).trueHurt(1);
            }
        }
    }

    public boolean shouldDieTo(double enemyDura) {
        return true;
    }

    public void hitBoss(Boss boss) {}
    public void hitBossProj(Projectile proj) {}
}