package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends Projectile {
    public static BufferedImage image;
    
    // Damage constants
    public static final double DMG = 2 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 2;
    public static final double KB = 5;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(25, 18);

    // Movement constants
    public static final double VELOCITY = 20;
    public static final int LIFE = 25;

    // Misc constants
    public static final int BUTTONO = 4;
    public static final int SKILL_PT_GAIN = 4;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    public Bullet(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    public Bullet(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    public void process() {
        super.process();
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
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

        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Rocket extends Projectile {
    public BufferedImage image;
    public int state; // 0: Travelling, 1: Exploding

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Size constants
    public static final double HITBOX_TO_SIZE = 2.5;
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final Coord SIZE = new Coord(50, 50);

    // Damage constants
    public static final double DMG = 15 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 3;
    public static final double EXPLOSION_SIZE_MULTIPLIER = 4;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 40;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;

    public Rocket(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size.scaledBy(HITBOX_TO_SIZE), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
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
            if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                die();
            }
            for (Omegaman enemy: OmegaFight3.omegaman) {
                if (enemy != character) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                        enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, (int) (SCREENSHAKE * (size.x / SIZE.x)));
                        die();
                    }
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
            
            for (Boss boss: OmegaFight3.bosses) {
                if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                    boss.hurt(damage);
                    die();
                }
                if (canHitProj) {
                    for (Projectile proj: boss.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                            die();
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
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
    public static final double KB = 3;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(24, 20);

    // Movement constants
    public static final double VELOCITY = 18; 
    public static final int LIFE = 16;

    // Shot orientation constants
    public static final int NUM_SHOTS = 4;
    public static final double SPREAD = Math.PI / 6;

    // Misc constants
    public static final int BUTTONO = 5;
    public static final int SKILL_PT_GAIN = 1;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;

    public Shotgun(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public Shotgun(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        super.process();
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
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
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Firework extends Projectile {
    public BufferedImage image;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static BufferedImage[] chargingImages = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Size constants
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final Coord SIZE = new Coord(50, 50);

    // Damage constants
    public static final double DMG = 3.5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 3;
    public static final double KB_SPREAD = Math.PI / 3;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 15;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final int NUM_SHOTS = 8;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    public Firework(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        super.process();
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + 1, Omegaman.MAX_SKILL_PTS);
                }
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }

        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Spammer extends Projectile {
    public static BufferedImage image;

    // Damage constants
    public static final double DMG = 1.5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 1;
    public static final double KB = 4;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(22, 18);

    // Movement constants
    public static final double VELOCITY = 19; 
    public static final int LIFE = 20;

    // Shot orientation constants
    public static final double SPREAD = Math.PI / 9;

    // Misc constants
    public static final int BUTTONO = 6;
    public static final int SKILL_PT_GAIN = 1;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;

    public Spammer(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public Spammer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void process() {
        super.process();
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
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
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Missile extends Projectile {
    public BufferedImage image;
    public int state; // 0: Travelling, 1: Exploding
    public int sign;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Size constants
    public static final double HITBOX_TO_SIZE = 1.5;
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final Coord SIZE = new Coord(60, 60);

    // Damage constants
    public static final double DMG = 12 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 16;
    public static final double KB_SPREAD = Math.PI / 3;
    public static final double EXPLOSION_SIZE_MULTIPLIER = 3;

    // Velocity constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 70;
    public static final double TURN_SPEED = Math.PI * (90.0 / LIFE / 180.0);

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;

    public Missile(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int sign, boolean canHitProj) {
        super(player, coord, size, size.scaledBy(HITBOX_TO_SIZE), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
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
            Char target = null;
            double closestDist = Double.MAX_VALUE;
            for (Omegaman enemy : OmegaFight3.omegaman) {
                if (enemy != character && enemy.state == Omegaman.ALIVE_STATE) {
                    double dist = Math.hypot(enemy.coord.x - coord.x, enemy.coord.y - coord.y);
                    if (dist < closestDist) {
                        closestDist = dist;
                        target = enemy;
                    }
                }
            }
            for (Boss boss : OmegaFight3.bosses) {
                double dist = Math.hypot(boss.coord.x - coord.x, boss.coord.y - coord.y);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = boss;
                }
            }

            if (target != null) {
                double targetDir = Math.atan2(target.coord.y - coord.y, target.coord.x - coord.x);
                double angleDif = targetDir - dir;
                angleDif = Math.atan2(Math.sin(angleDif), Math.cos(angleDif));
                if (Math.abs(angleDif) <= TURN_SPEED) dir = targetDir;
                else dir += Math.signum(angleDif) * TURN_SPEED;
            }

            if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
                die();
            }
            for (Omegaman enemy: OmegaFight3.omegaman) {
                if (enemy != character) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                        enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, (int) (SCREENSHAKE * (size.x / SIZE.x)));
                        die();
                    }
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
            for (Boss boss: OmegaFight3.bosses) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                        boss.hurt(damage);
                        die();
                    }
                    if (canHitProj) {
                        for (Projectile proj: boss.projectiles) {
                            if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                                die();
                                if (proj.shouldDieTo(durability)) proj.die();
                            }
                        }
                    }
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
    public static final double DMG = 2.5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 3;
    public static final double KB = 1.5;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(41, 14);

    // Movement constants
    public static final double VELOCITY = 15; 
    public static final int LIFE = 30;
    public static final double ACCEL = 1;

    // Misc constants
    public static final int BUTTONO = 7;
    public static final int SKILL_PT_GAIN = 10;
    public static final int SCREENSHAKE = 0;
    public static final double RECOIL = 4;
    public static final boolean CAN_HIT_PROJ = true;

    public Sniper(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public Sniper(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
    }

    public void process() {
        super.process();
        velocity += ACCEL;
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage * velocity / VELOCITY, knockback * velocity / VELOCITY, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
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
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage * velocity / VELOCITY);
                die();
                ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Laser extends Projectile {
    public static BufferedImage ball;
    public static BufferedImage beam;

    // Size constants
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final double SIZE_Y = 80; // x-size not impacted by charge, must be calculated
    public static final Coord BEAM_SIZE_Y_TO_BALL_SIZE = new Coord(50 / SIZE_Y, 50 / SIZE_Y);

    // Damage constants
    public static final double DMG = 1 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 5;
    public static final double BASE_KB_DIR = Math.PI * 1.5;
    public static final double KB_DIR_TILT = Math.PI / 4;
    public static final double KB_SPREAD = Math.PI / 6;

    // Velocity constants
    public static final int LIFE = 20; // Not affected by charging

    // Animation constants
    public static final int SCREENSHAKE = 0;
    public static final int RESIZE_LEN = 4;
    public static final double SIZE_Y_TO_PULSE_SIZE_Y = 1.1;
    public static final int PULSE_HZ = 2;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 16;
    public static final boolean CAN_HIT_PROJ = false;

    public Laser(Omegaman player, Coord coord, Coord size, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, 0, dir, damage, knockback, durability, frameCounter, canHitProj);
        hitBoxActive = false;
    }

    public void draw(Graphics2D g2) {
        double sizeY = size.y * Math.min(Math.min(frameCounter, LIFE - frameCounter), RESIZE_LEN) / RESIZE_LEN;
        if (frameCounter % (PULSE_HZ * 2) < PULSE_HZ) sizeY *= SIZE_Y_TO_PULSE_SIZE_Y;
        g2.drawImage(beam, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - sizeY / 2), (int) (size.x * Math.cos(dir)), (int) sizeY, null);
        g2.drawImage(ball, (int) (coord.x - (size.x / 2 + size.y * BEAM_SIZE_Y_TO_BALL_SIZE.x / 2) * Math.cos(dir)), (int) (coord.y - size.y * BEAM_SIZE_Y_TO_BALL_SIZE.y / 2), (int) (size.y * BEAM_SIZE_Y_TO_BALL_SIZE.x * Math.cos(dir)), (int) (size.y * BEAM_SIZE_Y_TO_BALL_SIZE.y), null);
    }

    public void process() {
        super.process();
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, BASE_KB_DIR + KB_DIR_TILT * Math.cos(dir), KB_SPREAD, SCREENSHAKE);
                }
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Boomer extends Projectile {
    public BufferedImage image;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    
    // Damage constants
    public static final double DMG = 3.5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 2;
    public static final double KB = 5;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(30, 15);

    // Movement constants
    public static final double VELOCITY = 20;
    public static final double ACCEL = -2;
    public static final int LIFE = 36;

    // Misc constants
    public static final int BUTTONO = 8;
    public static final int SKILL_PT_GAIN = 6;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;

    public Boomer(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public Boomer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    public void process() {
        super.process();
        velocity += ACCEL;
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    int multiplier = velocity < 0? 2: 1;
                    enemy.hurt(damage * multiplier, knockback * multiplier, coord, dir + multiplier / 2 * Math.PI, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN * multiplier, Omegaman.MAX_SKILL_PTS);
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
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                int multiplier = velocity < 0? 2: 1;
                boss.hurt(damage * multiplier);
                die();
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Bouncer extends Projectile {
    public BufferedImage image;
    public double rotation = 0;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Size constants
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final Coord SIZE = new Coord(80, 80);

    // Damage constants
    public static final double DMG = 0.75 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Velocity constants
    public static final double VELOCITY = 25;
    public static final int LIFE = 160;
    public static final double ROTATION_HZ = 1;
    public static final double ROTATION_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROTATION_HZ;
    public static final double ROTATION_MAX = Math.PI / 2;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    public Bouncer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }

    public void process() {
        super.process();
        rotation = (rotation + ROTATION_SPEED * Math.cos(dir)) % ROTATION_MAX;
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            dir += Math.PI;
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + 1, Omegaman.MAX_SKILL_PTS);
                }
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Spike extends Projectile {
    public double rotation;
    public boolean dead;

    public static BufferedImage image;
    
    // Damage constants
    public static final double DMG = 0;
    public static final double DURABILITY = 1;
    public static final double KB = 0;
    public static final double KB_SPREAD = 0;
    public static final int NUM_THORNS = 6;
    public static final boolean CURVED_BABY_PROJS = true;

    // Size constants
    public static final Coord SIZE = new Coord(50, 50);

    // Movement constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 35;
    public static final double ROTATION_HZ = 1;
    public static final double ROTATION_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROTATION_HZ;
    public static final double ROTATION_MAX = Math.PI * 2 / 3;

    // Misc constants
    public static final int BUTTONO = 9;
    public static final int SKILL_PT_GAIN = 0;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    public Spike(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    public Spike(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);    
    }

    public void process() {
        super.process();
        rotation = (rotation + ROTATION_SPEED * Math.cos(dir)) % ROTATION_MAX;
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    die();
                }
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
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                die();
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    public void die() {
        if (!dead) {
            dead = true;
            super.die();
            for (int i = 0; i != NUM_THORNS; i++) {
                ((Omegaman) character).babyProjectiles.add(new Thorn((Omegaman) character, coord.copy(), Thorn.SIZE, Thorn.VELOCITY, i * Math.PI * 2 / NUM_THORNS, Thorn.DMG, Thorn.KB, Thorn.DURABILITY, Thorn.LIFE, CURVED_BABY_PROJS, Thorn.CAN_HIT_PROJ));
            }
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}

class Thorn extends Projectile {
    public boolean curved;

    public static BufferedImage image;
    
    // Damage constants
    public static final double DMG = 1 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
    public static final double DURABILITY = 1;
    public static final double KB = 3;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(25, 20);

    // Movement constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 20;
    public static final double TURN_AMT = Math.PI / 3;
    public static final double TURN_SPEED = TURN_AMT / LIFE;

    // Misc constants
    public static final int SKILL_PT_GAIN = 1;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;

    public Thorn(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean curved, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        this.curved = curved;
    }

    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);    
    }

    public void process() {
        super.process();
        if (curved) dir += TURN_SPEED;
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
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
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).skillPts = Math.min(((Omegaman) character).skillPts + SKILL_PT_GAIN, Omegaman.MAX_SKILL_PTS);
            }
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (shouldDieTo(proj.durability)) die();
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }
}

class Splitter extends Projectile {
    public BufferedImage image;

    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Size constants
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final Coord SIZE = new Coord(60, 50);

    // Damage constants
    public static final double DMG = 0;
    public static final double DURABILITY = INFINITE_DURABILITY;
    public static final double KB = 0;
    public static final double KB_SPREAD = Math.PI / 3;
    public static final int NUM_SPLITS = 3;
    public static final int PROJS_PER_SPLIT = 4;
    public static final double SPLIT_PROJS_START_ANGLE = Math.PI / 4;
    public static final boolean CURVED_BABY_PROJS = false;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 30;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    public Splitter(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
    }

    public void process() {
        super.process();
        if (frameCounter % (LIFE / NUM_SPLITS) == 0) {
            for (int i = 0; i != PROJS_PER_SPLIT; i++) {
                ((Omegaman) character).babyProjectiles.add(new Thorn((Omegaman) character, coord.copy(), Thorn.SIZE, Thorn.VELOCITY, SPLIT_PROJS_START_ANGLE + i * Math.PI * 2 / PROJS_PER_SPLIT, Thorn.DMG, Thorn.KB, Thorn.DURABILITY, (int) (Thorn.LIFE * ((double) frameCounter / LIFE + 1.0 / NUM_SPLITS)), CURVED_BABY_PROJS, Thorn.CAN_HIT_PROJ));
            }
        }
        if (coord.x < 0 || coord.x > OmegaFight3.SCREEN_SIZE.x || coord.y < 0 || coord.y > OmegaFight3.SCREEN_SIZE.y) {
            die();
        }
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                }
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }
        for (Boss boss: OmegaFight3.bosses) {
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}