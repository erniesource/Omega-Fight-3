package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Egg extends Projectile{
    // Damage constants
    public static final double[] DMG = {8 * Omegaman.percMult(), 4 * Omegaman.percMult(), 2 * Omegaman.percMult()};
    public static final double DURABILITY = 1;
    public static final double[] KB = {12, 6, 3};
    public static final double KB_SPREAD = Math.PI / 3;
    public static final int[] PROJS_PER_SPLIT = {2, 2};

    // Size constants
    public static final Coord[] SIZE = {new Coord(70, 60), new Coord(120, 95), new Coord(200, 160)};
    public static final double SIZE_TO_HITBOX = 0.7;
    public static final double SIZE_TO_SMOKE_SIZE = 0.5;

    // Movement constants
    public static final double[] VELOCITY = {16, 22, 40};
    public static final int LIFE = Projectile.INF_LIFE;
    public static final double ACCEL = 1;
    public static final double ROTATION_PER_SECOND = 2;
    public static final double ROTATION_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROTATION_PER_SECOND;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;

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
    public boolean dead;

    // Constructor with custom stats
    public Egg(Boss boss, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int state, boolean canHitProj) {
        super(boss, coord, size, size.scaledBy(SIZE_TO_HITBOX), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        eggVelocity = new Coord(velocity * Math.cos(dir), velocity * Math.sin(dir));
        type = (int) (Math.random() * NUM_TYPES[state]);
        this.state = state;
    }

    // Constructors with default stats
    public Egg(Boss boss, Coord coord, double dir, int state) {
        this(boss, coord, SIZE[state], VELOCITY[state], dir, DMG[state], KB[state], DURABILITY, LIFE, state, CAN_HIT_PROJ);
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
        if (coord.x < -size.x / 2 || coord.x > OmegaFight3.SCREEN_SIZE.x +size.x / 2 || coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
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

        // Loop through players and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            // Enemy hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                enemy.hurt(damage, knockback, coord, Math.atan2(eggVelocity.y, eggVelocity.x), KB_SPREAD);
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

    public void die() { // Change this to be like spike
        if (!dead) {
            dead = true;
            super.die();

            // Split if not at final state
            if (state != 0) {
                for (int i = 0; i != PROJS_PER_SPLIT[state - 1]; i++) {
                    character.babyProjectiles.add(new Egg((Boss) character, coord.copy(), -Math.PI / (PROJS_PER_SPLIT[state - 1] + 1) * (i + 1), state - 1));
                }
            }
        }
    }
}

class Feather extends Projectile {
    // Damage constants
    public static final double DMG = 10 * Omegaman.percMult();
    public static final double DURABILITY = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(155, 40);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 12;
    public static final int LIFE = INF_LIFE;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final int NO_OF_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static images
    public static BufferedImage[] images = new BufferedImage[NO_OF_SPRITES];

    // Constructor with custom stats
    public Feather(Boss boss, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(boss, coord, size, (new Coord(Math.min(size.x, size.y))).scaledBy(SIZE_TO_HITBOX), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Constructor with default stats
    public Feather(Boss boss, Coord coord, double dir) {
        this(boss, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Description: THis method draws the pellet of energy on screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(images[frameCounter / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: This method processes the pellet
    public void process() {
        // Movement
        coord.x += velocity * Math.cos(dir);
        coord.y += velocity * Math.sin(dir);

        // Sprite change
        frameCounter = (frameCounter + 1) % (NO_OF_SPRITES * SPRITE_CHANGE_HZ);

        // Check if out of screen
        if (OmegaFight3.outOfScreen(coord, size)) {
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