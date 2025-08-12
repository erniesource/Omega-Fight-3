package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends Projectile {
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

    // Static image
    public static BufferedImage image;

    // Constructor
    public Bullet(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Overloaded constructor with default stats
    public Bullet(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Description: Draws the bullet on the screen
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    // Description: Processes the bullet's movement and interactions
    public void process() {
        // Move bullet and expire bullet
        super.process();

        // Check if bullet is out of screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }

            // Boss projectiles
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
    
    // State constants
    public static final int TRAVELLING = 0;
    public static final int EXPLODING = 1;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;
    public int state = TRAVELLING; // 0: Travelling, 1: Exploding

    // Constructor
    public Rocket(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size.scaledBy(HITBOX_TO_SIZE), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    // Description:
    // This overridden method makes the rocket explode
    public void die() {
        if (state == TRAVELLING) {
            state = EXPLODING;
            hitBoxActive = false;
            frameCounter = EXPLOSION_TIME;
            size.x *= EXPLOSION_SIZE_MULTIPLIER;
            size.y *= EXPLOSION_SIZE_MULTIPLIER;
            OmegaFight3.boom.stop();
            OmegaFight3.boom.setFramePosition(0);
            OmegaFight3.boom.start();
        }
    }

    // Description:
    // This overridden method draws the rocket or explosion image based on its state
    public void draw(Graphics2D g2) {
        if (state == EXPLODING) g2.drawImage(explosionImages[(EXPLOSION_TIME - frameCounter - 1) / EXPLOSION_FRAME_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        else g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    // Description:
    // This overridden method processes the rocket's movement and interactions
    public void process() {
        // Not exploding
        if (state == TRAVELLING) {
            // Move the rocket
            super.process();

            // Check if the rocket is out of the screen
            if (OmegaFight3.outOfScreen(coord, size)) {
                die();
            }

            // Check for collisions with other characters and their projectiles
            for (Omegaman enemy: OmegaFight3.omegaman) {
                if (enemy != character) {
                    // Enemy hitbox
                    if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                        enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, (int) (SCREENSHAKE * (size.x / SIZE.x)));
                        die();
                        ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
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
            
            // Check for collisions with bosses and their projectiles
            for (Boss boss: OmegaFight3.bosses) {
                // Boss hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                    boss.hurt(damage);
                    die();
                    ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
                }

                // Boss projectiles
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

        // Exploding
        else {
            frameCounter--;
            // Check if the explosion is done and die if so
            if (frameCounter == 0) super.die();
        }
    }

    // Description: This returns the fact that rocket dies to any projectile
    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}

class Shotgun extends Projectile {
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
    public static final boolean CAN_HIT_PROJ = true;

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Shotgun(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Constructor with custom stats
    public Shotgun(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Description: Draws the shotgun bullet on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: Processes the shotgun bullet's movement and interactions
    public void process() {
        // Move the bullet and expire it
        super.process();

        // Check if the bullet is out of the screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }

            // Boss projectiles
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

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static BufferedImage[] chargingImages = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;

    // Constructor
    public Firework(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    // Description: Draws the firework projectile
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: Processes the firework projectile's movement and interactions
    public void process() {
        // Move the firework and expire it
        super.process();

        // Check if the firework is out of the screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
                }
                // Enemy projectiles
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }
            // Boss projectiles
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    // This returns the fact that Firework dies to any projectile
    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Spammer extends Projectile {
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

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Spammer(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Constructor with custom stats
    public Spammer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Description: Draws the spammer bullet on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: Processes the spammer's movement and interactions
    public void process() {
        // Move the spammer and expire it
        super.process();

        // Check if the spammer is out of the screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
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

    // State constants
    public static final int TRAVELLING = 0;
    public static final int EXPLODING = 1;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;
    public int state = TRAVELLING; // 0: Travelling, 1: Exploding
    public int sign;

    // Constructor
    public Missile(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, int sign, boolean canHitProj) {
        super(player, coord, size, size.scaledBy(HITBOX_TO_SIZE), velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
        this.sign = sign;
    }

    // Description:
    // This overridden method makes the missile explode
    public void die() {
        if (state == TRAVELLING) {
            state = EXPLODING;
            hitBoxActive = false;
            frameCounter = EXPLOSION_TIME;
            size.x *= EXPLOSION_SIZE_MULTIPLIER;
            size.y *= EXPLOSION_SIZE_MULTIPLIER;
            OmegaFight3.boom.stop();
            OmegaFight3.boom.setFramePosition(0);
            OmegaFight3.boom.start();
        }
    }

    // Description: This method draws the missile or explosion image based on its state
    public void draw(Graphics2D g2) {
        if (state == EXPLODING) g2.drawImage(explosionImages[(EXPLOSION_TIME - frameCounter - 1) / EXPLOSION_FRAME_HZ], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        else {
            g2.rotate(dir, coord.x, coord.y);
            g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 + size.y * ((sign - 1) / -2)), (int) size.x, (int) size.y * sign, null);
            g2.rotate(-dir, coord.x, coord.y);
        }
    }

    // Description: This method processes the missile's movement and interactions
    public void process() {
        // Not exploding
        if (state == TRAVELLING) {
            // Move the missile and expire it
            super.process();

            // Homing
            // Loop thru all bosses and omegamen and find closest target
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

            // Home towards target
            if (target != null) {
                double targetDir = Math.atan2(target.coord.y - coord.y, target.coord.x - coord.x);
                double angleDif = targetDir - dir;
                angleDif = Math.atan2(Math.sin(angleDif), Math.cos(angleDif));
                if (Math.abs(angleDif) <= TURN_SPEED) dir = targetDir;
                else dir += Math.signum(angleDif) * TURN_SPEED;
            }

            // Check if the missile is out of the screen
            if (OmegaFight3.outOfScreen(coord, size)) {
                die();
            }

            // Check for collisions with other characters and their projectiles
            for (Omegaman enemy: OmegaFight3.omegaman) {
                if (enemy != character) {
                    // Enemy hitbox
                    if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                        enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, (int) (SCREENSHAKE * (size.x / SIZE.x)));
                        die();
                        ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
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

            // Check for collisions with bosses and their projectiles
            for (Boss boss: OmegaFight3.bosses) {
                // Boss hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                    boss.hurt(damage);
                    die();
                    ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
                }

                // Boss projectiles
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

        // Exploding
        else {
            frameCounter--;
            // Check if the explosion is done and die if so
            if (frameCounter == 0) super.die();
        }
    }

    // Description: This returns the fact that missile dies to any projectile
    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}

class Sniper extends Projectile {
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

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Sniper(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Constructor with custom stats
    public Sniper(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Description: Draws the sniper bullet on the screen
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
    }

    // Description: Processes the sniper bullet's movement and interactions
    public void process() {
        // Move the sniper bullet and accelerate it and expire it
        super.process();
        velocity += ACCEL;

        // Check if the sniper bullet is out of the screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage * velocity / VELOCITY, knockback * velocity / VELOCITY, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage * velocity / VELOCITY;
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage * velocity / VELOCITY);
                die();
                ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage * velocity / VELOCITY;
            }

            // Boss projectiles
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

    // Static images
    public static BufferedImage ball;
    public static BufferedImage beam;

    // Constructor
    public Laser(Omegaman player, Coord coord, Coord size, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, 0, dir, damage, knockback, durability, frameCounter, canHitProj);
        hitBoxActive = false;
    }

    // Description: this method draws the laser beam and ball on the screen
    public void draw(Graphics2D g2) {
        double sizeY = size.y * Math.min(Math.min(frameCounter, LIFE - frameCounter), RESIZE_LEN) / RESIZE_LEN;
        if (frameCounter % (PULSE_HZ * 2) < PULSE_HZ) sizeY *= SIZE_Y_TO_PULSE_SIZE_Y;
        g2.drawImage(beam, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - sizeY / 2), (int) (size.x * Math.cos(dir)), (int) sizeY, null);
        g2.drawImage(ball, (int) (coord.x - (size.x / 2 + size.y * BEAM_SIZE_Y_TO_BALL_SIZE.x / 2) * Math.cos(dir)), (int) (coord.y - size.y * BEAM_SIZE_Y_TO_BALL_SIZE.y / 2), (int) (size.y * BEAM_SIZE_Y_TO_BALL_SIZE.x * Math.cos(dir)), (int) (size.y * BEAM_SIZE_Y_TO_BALL_SIZE.y), null);
    }

    // Description: this method processes the laser's movement and interactions
    public void process() {
        // Move the laser and expire it
        super.process();

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, BASE_KB_DIR + KB_DIR_TILT * Math.cos(dir), KB_SPREAD, SCREENSHAKE);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
                }

                // Enemy projectiles
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }

            // Boss projectiles
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    // Description: this method returns that the laser should never die to a projectile
    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Boomer extends Projectile {
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
    public static final int SKILL_PT_GAIN = 3;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = false;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;

    // Constructor with default stats
    public Boomer(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Constructor with custom stats
    public Boomer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    // Description: Draws the boomerang projectile on the screen
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
    }

    // Description: Processes the boomerang projectile's movement and interactions
    public void process() {
        // Move the boomerang and deccelerate it and expire it
        super.process();
        velocity += ACCEL;

        // Check if the boomerang is out of the screen
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    int multiplier = velocity < 0? 2: 1;
                    enemy.hurt(damage * multiplier, knockback * multiplier, coord, dir + multiplier / 2 * Math.PI, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).addSkillPts(SKILL_PT_GAIN * multiplier);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage * multiplier;
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                int multiplier = velocity < 0? 2: 1;
                boss.hurt(damage * multiplier);
                die();
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage * multiplier;
                ((Omegaman) character).addSkillPts(SKILL_PT_GAIN * multiplier);
            }

            // Boss projectiles
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
    // Size constants
    public static final double MIN_SIZE_PERCENT = 0.2;
    public static final Coord SIZE = new Coord(80, 80);

    // Damage constants
    public static final double DMG = 0.5 * (int) Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
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

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;
    public double rotation = 0;

    // Constructor
    public Bouncer(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    // Description: This method draws the bouncer projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }

    // Description: This method processes the bouncer projectile's movement and interactions
    public void process() {
        // Move the bouncer, rotate it, and expire it
        super.process();
        rotation = (rotation + ROTATION_SPEED * Math.cos(dir)) % ROTATION_MAX;

        // Check if the bouncer is out of the screen and bounce if so
        if (OmegaFight3.outOfScreen(coord, size)) {
            dir += Math.PI;
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
                }

                // Enemy projectiles
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }

            // Boss projectiles
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    // Description: This method returns that the bouncer should never die to a projectile
    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}

class Spike extends Projectile {
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

    // Instance variables
    public double rotation;
    public boolean dead;

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Spike(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE, VELOCITY, dir, DMG, KB, DURABILITY, LIFE, CAN_HIT_PROJ);
    }

    // Constructor with custom stats
    public Spike(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
    }

    // Description: This method draws the spike projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);    
    }

    // Description: This method processes the spike projectile's movement and interactions
    public void process() {
        // Move the spike and rotate it and expire it
        super.process();
        rotation = (rotation + ROTATION_SPEED * Math.cos(dir)) % ROTATION_MAX;

        // Check if the spike is out of the screen and die if so
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                die();
            }

            // Boss projectiles
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

    // Description: This method handles the death of the spike projectile
    public void die() {
        if (!dead) {
            dead = true;
            super.die();

            // Explodes into thorns
            for (int i = 0; i != NUM_THORNS; i++) {
                ((Omegaman) character).babyProjectiles.add(new Thorn((Omegaman) character, coord.copy(), Thorn.SIZE, Thorn.VELOCITY, i * Math.PI * 2 / NUM_THORNS, Thorn.DMG, Thorn.KB, Thorn.DURABILITY, Thorn.LIFE, CURVED_BABY_PROJS, Thorn.CAN_HIT_PROJ));
            }
        }
    }

    // Description: This method returns that the spike should die to any projectile
    public boolean shouldDieTo(double enemyDurability) {
        return true;
    }
}

class Thorn extends Projectile {
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

    // Instance variables
    public boolean curved;

    // Static image
    public static BufferedImage image;

    // Constructor
    public Thorn(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean curved, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        this.curved = curved;
    }

    // Draws the thorn projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);    
    }

    // Processes the thorn projectile's movement and interactions
    public void process() {
        // Move the thorn and turn it and expire it
        super.process();
        if (curved) dir += TURN_SPEED;

        // Check if the thorn is out of the screen and die if so
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    die();
                    ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
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

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                die();
                ((Omegaman) character).addSkillPts(SKILL_PT_GAIN);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }

            // Boss projectiles
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
    public static final double SPLIT_PROJS_START_ANGLE = (2 * Math.PI) / (PROJS_PER_SPLIT) / 2;
    public static final boolean CURVED_BABY_PROJS = false;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 30;

    // Misc constants
    public static final double MINIMUM_STAT_PERCENTAGE = 0.5;
    public static final int SCREENSHAKE = 0;
    public static final boolean CAN_HIT_PROJ = true;

    // Instance variables
    public BufferedImage image;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Constructor
    public Splitter(Omegaman player, Coord coord, Coord size, double velocity, double dir, double damage, double knockback, double durability, int frameCounter, boolean canHitProj) {
        super(player, coord, size, size, velocity, dir, damage, knockback, durability, frameCounter, canHitProj);
        image = images[player.playerNo];
    }

    // Description: Draws the splitter projectile on the screen
    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
    }

    // Description: Processes the splitter projectile's movement and interactions
    public void process() {
        // Move the splitter and expire it
        super.process();

        // Spit out thorns periodically
        if (frameCounter % (LIFE / NUM_SPLITS) == 0) {
            for (int i = 0; i != PROJS_PER_SPLIT; i++) {
                ((Omegaman) character).babyProjectiles.add(new Thorn((Omegaman) character, coord.copy(), Thorn.SIZE, Thorn.VELOCITY, SPLIT_PROJS_START_ANGLE + i * Math.PI * 2 / PROJS_PER_SPLIT, Thorn.DMG, Thorn.KB, Thorn.DURABILITY, (int) (Thorn.LIFE * ((double) frameCounter / LIFE + 1.0 / NUM_SPLITS)), CURVED_BABY_PROJS, Thorn.CAN_HIT_PROJ));
            }
        }

        // Check if the splitter is out of the screen and die if so
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
        }

        // Check for collisions with other characters and their projectiles
        for (Omegaman enemy: OmegaFight3.omegaman) {
            if (enemy != character) {
                // Enemy hitbox
                if (OmegaFight3.intersects(coord, hitBoxSize, enemy.coord, enemy.size, OmegaFight3.HITBOX_LEEWAY) && enemy.invCounter == Omegaman.VULNERABLE) {
                    enemy.hurt(damage, knockback, coord, dir, KB_SPREAD, SCREENSHAKE);
                    ((Omegaman) character).stats[Omegaman.DMG_TO_OMEGAMAN] += damage;
                }

                // Enemy projectiles
                if (canHitProj) {
                    for (Projectile proj: enemy.projectiles) {
                        if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, OmegaFight3.HITBOX_LEEWAY) && proj.hitBoxActive && proj.canHitProj) {
                            if (proj.shouldDieTo(durability)) proj.die();
                        }
                    }
                }
            }
        }

        // Check for collisions with bosses and their projectiles
        for (Boss boss: OmegaFight3.bosses) {
            // Boss hitbox
            if (OmegaFight3.intersects(coord, hitBoxSize, boss.coord, boss.size, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y))) {
                boss.hurt(damage);
                ((Omegaman) character).stats[Omegaman.DMG_TO_BOSS] += damage;
            }

            // Boss projectiles
            if (canHitProj) {
                for (Projectile proj: boss.projectiles) {
                    if (OmegaFight3.intersects(coord, hitBoxSize, proj.coord, proj.hitBoxSize, Boss.BOSS_HITBOX_LEEWAY * Math.min(boss.size.x, boss.size.y)) && proj.hitBoxActive && proj.canHitProj) {
                        if (proj.shouldDieTo(durability)) proj.die();
                    }
                }
            }
        }
    }

    // Description: This method returns that the splitter should never die to a projectile
    public boolean shouldDieTo(double enemyDurability) {
        return false;
    }
}