package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

abstract public class Projectile {
    // General variables
    public Char character;
    public Coord coord;
    public Coord size;
    public Coord hitBoxSize;
    public boolean hitBoxActive = true;

    // Movement variables
    public double velocity;
    public double dir;

    // Combat variables
    public double damage;
    public double knockback;
    public double durability;

    // Life variables
    public int frameCounter;

    // Explosion variables
    public static final int EXPLOSION_FRAME_HZ = 4;
    public static final int NUM_EXPLOSION_IMAGES = 8;
    public static final int EXPLOSION_TIME = EXPLOSION_FRAME_HZ * NUM_EXPLOSION_IMAGES;
    public static BufferedImage[] explosionImages = new BufferedImage[NUM_EXPLOSION_IMAGES];

    public static final double INFINITE_DURABILITY = 100;

    public Projectile(Char character, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        this.character = character;
        this.coord = coord;
        this.size = size;
        this.hitBoxSize = hitBoxSize;
        this.velocity = velocity;
        this.dir = dir;
        this.damage = damage;
        this.knockback = knockback;
        this.durability = durability;
        this.frameCounter = frameCounter;
    }

    public boolean checkHitbox(Coord enemyCoord, Coord enemyHitBoxSize) {
        return Math.abs(enemyCoord.x - coord.x) <= (enemyHitBoxSize.x + hitBoxSize.x) / 2 - OmegaFight3.HITBOX_LEEWAY && Math.abs(enemyCoord.y - coord.y) <= (enemyHitBoxSize.y + hitBoxSize.y) / 2 - OmegaFight3.HITBOX_LEEWAY;  
    }

    public void die() {
        ((Omegaman) character).deadProjectiles.add(this);
    }

    public void process() {
        coord.x += velocity * Math.cos(dir);
        coord.y += velocity * Math.sin(dir);
        frameCounter--;
        if (frameCounter == 0) die();
    }

    public boolean shouldDieTo(double enemyDurability) {
        return durability <= enemyDurability;
    }

    // Methods that exist purely for polymorphism to happen
    abstract public void draw(Graphics2D g2);
}

class Bullet extends Projectile {
    public static BufferedImage image;
    
    // Damage constants
    public static final double DMG = 2 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);;
    public static final double DURABILITY = 2;
    public static final double KB = 5;

    // Size constants
    public static final Coord SIZE = new Coord(25, 18);

    // Movement constants
    public static final double VELOCITY = 20;
    public static final int LIFE = 25;

    // Misc constants
    public static final int SKILL_PT_GAIN = 4;
    public static final int SCREENSHAKE = 0;

    public Bullet(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    public void process() {
        super.process();
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
                }
                else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                    die();
                }
                else {
                    for (Projectile proj: enemy.projectiles) {
                        if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                            if (shouldDieTo(proj.durability)) die();
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
            // Also check boss hitbox
        }
    }
}

class Rocket extends Projectile {
    public BufferedImage image;
    public int state; // 0: Travelling, 1: Exploding

    // Size constants
    public static final double HITBOX_TO_SIZE_RATIO = 2.5;
    public static final double MINIMUM_SIZE_PERCENTAGE = 0.2;
    public static final Coord MAX_SIZE = new Coord(50, 50);

    // Damage constants
    public static final double DMG = 15 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 20;
    public static final double EXPLOSION_SIZE_MULTIPLIER = 4;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 30;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double MAX_RECOIL = 8;
    public static final int MAX_SCREENSHAKE = 15;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    public Rocket(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, new Coord(size.x * HITBOX_TO_SIZE_RATIO, size.y * HITBOX_TO_SIZE_RATIO), velocity, dir, damage, knockback, durability, frameCounter);
        image = images[player.playerNo];
    }

    public void die() {
        if (state == 0) {
            state = 1;
            hitBoxActive = false;
            frameCounter = EXPLOSION_TIME;
            size.x *= EXPLOSION_SIZE_MULTIPLIER;
            size.y *= EXPLOSION_SIZE_MULTIPLIER;
        }
    }

    public void draw(Graphics2D g2) {
        if (state == 1) g2.drawImage(explosionImages[(EXPLOSION_TIME - frameCounter - 1) / EXPLOSION_FRAME_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        else g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    public void process() {
        if (state == 0) {
            super.process();
            for (Omegaman enemy: OmegaFight3.omegaman) {
                if (enemy != character) {
                    if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                        enemy.hurt(damage, knockback, coord, (int) (MAX_SCREENSHAKE * (size.x / MAX_SIZE.x)));
                        die();
                    }
                    else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                        die();
                    }
                    else {
                        for (Projectile proj: enemy.projectiles) {
                            if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                                die();
                                if (proj.shouldDieTo(durability)) proj.die();
                            }
                        }
                    }
                }
                // Also check boss hitbox
            }
        }
        else {
            frameCounter--;
            if (frameCounter == 0) super.die();
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}

class Shotgun extends Projectile {
    public static BufferedImage image;

    // Damage constants
    public static final double DMG = 2 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);;
    public static final double DURABILITY = 1;
    public static final double KB = 4;

    // Size constants
    public static final Coord SIZE = new Coord(24, 20);

    // Movement constants
    public static final double VELOCITY = 18; 
    public static final int LIFE = 16;

    // Shot orientation constants
    public static final int NUM_SHOTS = 4;
    public static final double SPREAD = Math.PI / 6;

    // Misc constants
    public static final int SKILL_PT_GAIN = 1;
    public static final int SCREENSHAKE = 0;

    public Shotgun(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        super.process();
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
                }
                else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                    die();
                }
                else {
                    for (Projectile proj: enemy.projectiles) {
                        if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                            if (shouldDieTo(proj.durability)) die();
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
            // Also check boss hitbox
        }
    }
}

class Firework extends Projectile {
    public BufferedImage image;
    public int state; // 0: Travelling, 1: Exploding

    // Size constants
    public static final double MINIMUM_SIZE_PERCENTAGE = 0.2;
    public static final Coord MAX_SIZE = new Coord(48, 40);
    public static final Coord MAX_CHARGE_SIZE = new Coord(60, 60);

    // Damage constants
    public static final double DMG = 3 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 3;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 15;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final int NUM_SHOTS = 8;
    public static final int SCREENSHAKE = 0;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static BufferedImage[] chargingImages = new BufferedImage[Omegaman.NUM_PLAYERS];

    public Firework(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter);
        image = images[player.playerNo];
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        super.process();
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + 1, Omegaman.MAX_SKILL_PTS);
                }
                else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                    die();
                }
                else {
                    for (Projectile proj: enemy.projectiles) {
                        if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                            if (shouldDieTo(proj.durability)) die();
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
            // Also check boss hitbox
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Spammer extends Projectile {
    public static BufferedImage image;

    // Damage constants
    public static final double DMG = 1.3 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);;
    public static final double DURABILITY = 1;
    public static final double KB = 4;

    // Size constants
    public static final Coord SIZE = new Coord(22, 18);

    // Movement constants
    public static final double VELOCITY = 19; 
    public static final int LIFE = 20;

    // Shot orientation constants
    public static final double SPREAD = Math.PI / 6;

    // Misc constants
    public static final int SKILL_PT_GAIN = 1;
    public static final int SCREENSHAKE = 0;

    public Spammer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        super.process();
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
                }
                else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                    die();
                }
                else {
                    for (Projectile proj: enemy.projectiles) {
                        if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                            if (shouldDieTo(proj.durability)) die();
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
            // Also check boss hitbox
        }
    }
}

class Missile extends Projectile {
    public BufferedImage image;
    public int state; // 0: Travelling, 1: Exploding

    // Size constants
    public static final double HITBOX_TO_SIZE_RATIO = 1.5;
    public static final double MINIMUM_SIZE_PERCENTAGE = 0.2;
    public static final Coord MAX_SIZE = new Coord(60, 60);

    // Damage constants
    public static final double DMG = 12 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 16;
    public static final double EXPLOSION_SIZE_MULTIPLIER = 3;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 45; // slower velocity, longer life
    public static final double TURN_SPEED = Math.PI / 90; // Let computer do math for at most 90 degrees

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double MAX_RECOIL = 8;
    public static final int MAX_SCREENSHAKE = 15;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    public Missile(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, new Coord(size.x * HITBOX_TO_SIZE_RATIO, size.y * HITBOX_TO_SIZE_RATIO), velocity, dir, damage, knockback, durability, frameCounter);
        image = images[player.playerNo];
    }

    public void die() {
        if (state == 0) {
            state = 1;
            hitBoxActive = false;
            frameCounter = EXPLOSION_TIME;
            size.x *= EXPLOSION_SIZE_MULTIPLIER;
            size.y *= EXPLOSION_SIZE_MULTIPLIER;
        }
    }

    public void draw(Graphics2D g2) {
        if (state == 1) g2.drawImage(explosionImages[(EXPLOSION_TIME - frameCounter - 1) / EXPLOSION_FRAME_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        else {
            g2.rotate(dir, coord.x, coord.y);
            g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
            g2.rotate(-dir, coord.x, coord.y);
        }
    }

    public void process() {
        if (state == 0) {
            super.process();

            // Homing
            // Loop thru all boss parts (replace Omegaman with Boss class and OmegaFight.omegaman with OmegaFight.boss)
            Omegaman target = null;
            double closestDistance = Double.MAX_VALUE;
            for (Omegaman enemy : OmegaFight3.omegaman) {
                if (enemy != character) {
                    double distance = Math.hypot(enemy.coord.x - coord.x, enemy.coord.y - coord.y);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        target = enemy;
                    }
                }
            }

            if (target != null) {
                double targetDir = Math.atan2(target.coord.y - coord.y, target.coord.x - coord.x);
                double angleDif = targetDir - dir;
                angleDif = Math.atan2(Math.sin(angleDif), Math.cos(angleDif));
                if (Math.abs(angleDif) <= TURN_SPEED) dir = targetDir;
                else dir += Math.signum(angleDif) * TURN_SPEED;
            }

            for (Omegaman enemy: OmegaFight3.omegaman) {
                if (enemy != character) {
                    if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                        enemy.hurt(damage, knockback, coord, (int) (MAX_SCREENSHAKE * (size.x / MAX_SIZE.x)));
                        die();
                    }
                    else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                        die();
                    }
                    else {
                        for (Projectile proj: enemy.projectiles) {
                            if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                                die();
                                if (proj.shouldDieTo(durability)) proj.die();
                            }
                        }
                    }
                }
                // Also check boss hitbox
            }
        }
        else {
            frameCounter--;
            if (frameCounter == 0) super.die();
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}