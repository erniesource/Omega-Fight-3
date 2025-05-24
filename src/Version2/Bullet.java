package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends Projectile {
    public static BufferedImage image;
    
    // Damage constants
    public static final double DMG = 2 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
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
                for (Projectile proj: enemy.projectiles) {
                    if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
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
    public static final double HITBOX_TO_SIZE = 2.5;
    public static final double MINIMUM_SIZE_PERCENTAGE = 0.2;
    public static final Coord SIZE = new Coord(50, 50);

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
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    public Rocket(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size.scaledBy(HITBOX_TO_SIZE), velocity, dir, damage, knockback, durability, frameCounter);
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
                        enemy.hurt(damage, knockback, coord, (int) (SCREENSHAKE * (size.x / SIZE.x)));
                        die();
                    }
                    else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                        die();
                    }
                    for (Projectile proj: enemy.projectiles) {
                        if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                            die();
                            if (proj.shouldDieTo(durability)) proj.die();
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
    public static final double DMG = 2 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
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
                for (Projectile proj: enemy.projectiles) {
                    if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
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
    public static final Coord SIZE = new Coord(50, 50);

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
                for (Projectile proj: enemy.projectiles) {
                    if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                        if (proj.shouldDieTo(durability)) proj.die();
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
    public static final double DMG = 1.3 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 1;
    public static final double KB = 4;

    // Size constants
    public static final Coord SIZE = new Coord(22, 18);

    // Movement constants
    public static final double VELOCITY = 19; 
    public static final int LIFE = 20;

    // Shot orientation constants
    public static final double SPREAD = Math.PI / 9;

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
                for (Projectile proj: enemy.projectiles) {
                    if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
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
    public int sign;

    // Size constants
    public static final double HITBOX_TO_SIZE = 1.5;
    public static final double MINIMUM_SIZE_PERCENTAGE = 0.2;
    public static final Coord SIZE = new Coord(60, 60);

    // Damage constants
    public static final double DMG = 12 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 16;
    public static final double EXPLOSION_SIZE_MULTIPLIER = 3;

    // Velocity constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 70;
    public static final double TURN_SPEED = Math.PI * (90.0 / LIFE / 180.0);

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    public Missile(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int sign) {
        super(player, coord, size, size.scaledBy(HITBOX_TO_SIZE), velocity, dir, damage, knockback, durability, frameCounter);
        image = images[player.playerNo];
        this.sign = sign;
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
            g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 + size.y * ((sign - 1) / -2)), (int) size.x, (int) size.y * sign, null);
            g2.rotate(-dir, coord.x, coord.y);
        }
    }

    public void process() {
        if (state == 0) {
            super.process();

            // Homing
            // Loop thru all boss parts (replace Omegaman with Boss class and OmegaFight.omegaman with OmegaFight.boss)
            Omegaman target = null;
            double closestDist = Double.MAX_VALUE;
            for (Omegaman enemy : OmegaFight3.omegaman) {
                if (enemy != character) {
                    double dist = Math.hypot(enemy.coord.x - coord.x, enemy.coord.y - coord.y);
                    if (dist < closestDist) {
                        closestDist = dist;
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
                        enemy.hurt(damage, knockback, coord, (int) (SCREENSHAKE * (size.x / SIZE.x)));
                        die();
                    }
                    else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                        die();
                    }
                    for (Projectile proj: enemy.projectiles) {
                        if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                            die();
                            if (proj.shouldDieTo(durability)) proj.die();
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

class Sniper extends Projectile {
    public static BufferedImage image;

    // Damage constants
    public static final double DMG = 5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 3;
    public static final double KB = 5;

    // Size constants
    public static final Coord SIZE = new Coord(41, 14);

    // Movement constants
    public static final double VELOCITY = 15; 
    public static final int LIFE = 30;
    public static final double ACCELERATION = 1;

    // Misc constants
    public static final int SKILL_PT_GAIN = 10;
    public static final int SCREENSHAKE = 0;
    public static final double RECOIL = 4;

    public Sniper(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
    }

    public void process() {
        super.process();
        velocity += ACCELERATION;
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage * velocity / VELOCITY, knockback * velocity / VELOCITY, coord, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
                }
                else if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                    die();
                }
                for (Projectile proj: enemy.projectiles) {
                    if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
            // Also check boss hitbox
        }
    }
}

class Laser extends Projectile {
    public static BufferedImage ball;
    public static BufferedImage beam;

    // Size constants
    public static final double MINIMUM_SIZE_PERCENTAGE = 0.2;
    public static final double SIZE_Y = 80; // x-size not impacted by charge, must be calculated
    public static final Coord BEAM_SIZE_Y_TO_BALL_SIZE = new Coord(50 / SIZE_Y, 50 / SIZE_Y);

    // Damage constants
    public static final double DMG = 1 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 2;

    // Velocity constants
    public static final int LIFE = 20; // Not affected by charging

    // Animation constants
    public static final int SCREENSHAKE = 0;
    public static final double FADE_LEN = 0.2;
    public static final double SHRINK_AMT = 0.2;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 16;

    public Laser(Omegaman player, Coord coord, Coord size, double dir, double damage, double knockback, double durability, int frameCounter) {
        super(player, coord, size, size, 0, dir, damage, knockback, durability, frameCounter); // change hitbox?
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(beam, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null); // animate laser
        g2.drawImage(ball, (int) (coord.x - (size.x / 2 + size.y * BEAM_SIZE_Y_TO_BALL_SIZE.x / 2) * Math.cos(dir)), (int) (coord.y - size.y * BEAM_SIZE_Y_TO_BALL_SIZE.y / 2), (int) (size.y * BEAM_SIZE_Y_TO_BALL_SIZE.x * Math.cos(dir)), (int) (size.y * BEAM_SIZE_Y_TO_BALL_SIZE.y), null);
    }

    public void process() {
        super.process();
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (enemy.checkHitbox(coord, hitBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, SCREENSHAKE);
                }
                for (Projectile proj: enemy.projectiles) {
                    if (proj.checkHitbox(coord, hitBoxSize) && proj.hitBoxActive) {
                        if (proj.shouldDieTo(durability)) proj.die();
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