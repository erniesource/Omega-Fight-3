package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends Projectile {
    // Damage constants
    public static final double DMG = 2 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 5;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(25, 18);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 20;
    public static final int LIFE = 25;

    // Misc constants
    public static final int BUTTONO = 4;
    public static final int SKILL_PT_GAIN = 4;
    public static final int SKILL_PT_COST = 6;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static image
    public static BufferedImage image;

    // Constructor
    public Bullet(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    // Overloaded constructor with default stats
    public Bullet(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the bullet on the screen
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Rocket extends Projectile {
    // Size constants
    public static final double HITBOX_TO_SIZE = 2.5;
    public static final double MIN_PERC = 0.2;
    public static final Coord SIZE = new Coord(50, 50);

    // Damage constants
    public static final double DMG = 15 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 3;
    public static final double EXPLOSION_SIZE_MULT = 4;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 40;

    // Misc constants
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;

    // Constructor
    public Rocket(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    public Rocket(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(HITBOX_TO_SIZE), VELOCITY * percentCharged, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (LIFE * percentCharged), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description:
    // This overridden method makes the rocket explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
        }
        super.die();
    }

    // Description:
    // This overridden method draws the rocket or explosion image based on its state
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
    }

    // Description: This returns the fact that rocket dies to any projectile
    public boolean shouldDieTo(double enemyDura) {
        return true;
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Shotgun extends Projectile {
    // Damage constants
    public static final double DMG = 2 * Omegaman.PERC_MULT;
    public static final double DURA = 1;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(24, 20);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 18; 
    public static final int LIFE = 16;

    // Shot orientation constants
    public static final int NUM_SHOTS = 4;
    public static final double SPREAD = Math.PI / 6;

    // Misc constants
    public static final int BUTTONO = 5;
    public static final int SKILL_PT_GAIN = 1;
    public static final int SKILL_PT_COST = (int) (1.0 * NUM_SHOTS);
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Shotgun(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public Shotgun(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    // Description: Draws the shotgun bullet on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Firework extends Projectile {
    // Size constants
    public static final double MIN_PERC = 0.2;
    public static final Coord SIZE = new Coord(50, 50);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double DMG = 3.5 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 3;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 15;

    // Misc constants
    public static final int NUM_SHOTS = 8;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static BufferedImage[] chargingImages = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;

    // Constructor
    public Firework(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    public Firework(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(SIZE_TO_HITBOX), VELOCITY * percentCharged, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (LIFE * percentCharged), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the firework projectile
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }

    // This returns the fact that Firework dies to any projectile
    public boolean shouldDieTo(double enemyDura) {
        return false;
    }
}

class Spammer extends Projectile {
    // Damage constants
    public static final double DMG = 1.5 * Omegaman.PERC_MULT;
    public static final double DURA = 1;
    public static final double KB = 5;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(22, 18);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 19; 
    public static final int LIFE = 20;

    // Shot orientation constants
    public static final double SPREAD = Math.PI / 9;

    // Misc constants
    public static final int BUTTONO = 6;
    public static final int SKILL_PT_GAIN = 1;
    public static final int SKILL_PT_COST = 2;
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Spammer(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public Spammer(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    // Description: Draws the spammer bullet on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Missile extends Projectile {
    // Size constants
    public static final double HITBOX_TO_SIZE = 1.5;
    public static final double MIN_PERC = 0.2;
    public static final Coord SIZE = new Coord(60, 60);

    // Damage constants
    public static final double DMG = 12 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 3;
    public static final double EXPLOSION_SIZE_MULT = 3;

    // Velocity constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 70;
    public static final double TURN_SPD = Math.PI * (90.0 / LIFE / 180.0);

    // Misc constants
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;
    public int sign;

    // Constructor
    public Missile(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, int sign, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
        this.sign = sign;
    }

    public Missile(Omegaman player, Coord coord, double dir, int sign, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(HITBOX_TO_SIZE), VELOCITY * percentCharged, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (LIFE * percentCharged), sign, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description:
    // This overridden method makes the missile explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
        }
        super.die();
    }

    // Description: This method draws the missile or explosion image based on its state
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 * sign), (int) size.x, (int) size.y * sign, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: This method processes the missile's movement and interactions
    public void process() {
        // Move the missile and expire it
        super.process();

        // Homing
        // Loop thru all bosses and omegamen and find closest target
        Char target = null;
        double closestDist = Double.MAX_VALUE;
        for (Omegaman enemy : OmegaFight3.omegaman) {
            if (enemy != character && enemy.state == Omegaman.ALIVE_STATE) { // Make sure most loops check alive state
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
            if (Math.abs(angleDif) <= TURN_SPD) dir = targetDir;
            else dir += Math.signum(angleDif) * TURN_SPD;
        }
    }

    // Description: This returns the fact that missile dies to any projectile
    public boolean shouldDieTo(double enemyDura) {
        return true;
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Sniper extends Projectile {
    // Damage constants
    public static final double DMG = 2.5 * Omegaman.PERC_MULT;
    public static final double DURA = 3;
    public static final double KB = 7;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(41, 14);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 15; 
    public static final int LIFE = 30;
    public static final double ACCEL = 1;

    // Misc constants
    public static final int BUTTONO = 7;
    public static final int SKILL_PT_GAIN = 10;
    public static final int SKILL_PT_COST = 10;
    public static final double RECOIL = 4;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Sniper(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public Sniper(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
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
    }

    public void hitPlayer(Omegaman enemy) {
        double mult = velocity / VELOCITY;
        double trueDmg = enemy.hurt(damage * mult, knockback * mult, coord, dir, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double mult = velocity / VELOCITY;
        double trueDmg = boss.hurt(damage * mult);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Laser extends Projectile {
    // Size constants
    public static final double MIN_PERC = 0.2;
    public static final double SIZE_Y = 80; // x-size not impacted by charge, must be calculated
    public static final Coord BEAM_SIZE_Y_TO_BALL = new Coord(50 / SIZE_Y, 50 / SIZE_Y);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double DMG = 1 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 5;
    public static final double BASE_KB_DIR = Math.PI * 1.5;
    public static final double KB_DIR_TILT = Math.PI / 4;
    public static final double KB_SPREAD = Math.PI / 6;

    // Velocity constants
    public static final int LIFE = 20; // Not affected by charging

    // Animation constants
    public static final int RESIZE_LEN = 4;
    public static final double SIZE_Y_TO_PULSE = 1.1;
    public static final int PULSE_HZ = 2;

    // Misc constants
    public static final double RECOIL = 16;
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage ball;
    public static BufferedImage beam;

    // Constructor
    public Laser(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, 0, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    public Laser(Omegaman player, Coord coord, double sizeX, double dir, double percentCharged) {
        this(player, coord, new Coord(sizeX, SIZE_Y * percentCharged), (new Coord(sizeX, SIZE_Y * percentCharged)).scaledBy(SIZE_TO_HITBOX), dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: this method draws the laser beam and ball on the screen
    public void draw(Graphics2D g2) {
        double sizeY = size.y * Math.min(Math.min(frameCounter, LIFE - frameCounter), RESIZE_LEN) / RESIZE_LEN;
        if (frameCounter % (PULSE_HZ * 2) < PULSE_HZ) sizeY *= SIZE_Y_TO_PULSE;
        g2.drawImage(beam, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - sizeY / 2), (int) (size.x * Math.cos(dir)), (int) sizeY, null);
        g2.drawImage(ball, (int) (coord.x - (size.x / 2 + size.y * BEAM_SIZE_Y_TO_BALL.x / 2) * Math.cos(dir)), (int) (coord.y - size.y * BEAM_SIZE_Y_TO_BALL.y / 2), (int) (size.y * BEAM_SIZE_Y_TO_BALL.x * Math.cos(dir)), (int) (size.y * BEAM_SIZE_Y_TO_BALL.y), null);
    }

    // Description: this method returns that the laser should never die to a projectile
    public boolean shouldDieTo(double enemyDura) {
        return false;
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, BASE_KB_DIR + KB_DIR_TILT * Math.cos(dir), kbSpread);
        ((Omegaman) character).addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        ((Omegaman) character).addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Boomer extends Projectile {
    // Damage constants
    public static final double DMG = 3.5 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 7;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(30, 15);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 20;
    public static final double ACCEL = -2;
    public static final int LIFE = 36;

    // Misc constants
    public static final int BUTTONO = 8;
    public static final int SKILL_PT_GAIN = 3;
    public static final int SKILL_PT_COST = 5;
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;

    // Constructor with default stats
    public Boomer(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public Boomer(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    // Description: Draws the boomerang projectile on the screen
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
    }

    // Description: Processes the boomerang projectile's movement and interactions
    public void process() {
        // Move the boomerang and deccelerate it and expire it
        super.process();
        velocity += ACCEL;
    }

    public void hitPlayer(Omegaman enemy) {
        int mult = velocity < 0? 2: 1;
        double trueDmg = enemy.hurt(damage * mult, knockback * mult, coord, dir + mult / 2 * Math.PI, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN * mult);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        int mult = velocity < 0? 2: 1;
        double trueDmg = boss.hurt(damage * mult);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        omega.addSkillPts(SKILL_PT_GAIN * mult);
    }
}

class Bouncer extends Projectile {
    // Size constants
    public static final double MIN_PERC = 0.2;
    public static final Coord SIZE = new Coord(80, 80);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double DMG = 0.5 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 3;

    // Velocity constants
    public static final double VELOCITY = 25;
    public static final int LIFE = 160;
    public static final double ROT_HZ = 1;
    public static final double ROT_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROT_HZ;
    public static final double ROT_MAX = Math.PI / 2;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public BufferedImage image;
    public double rotation = 0;

    // Constructor
    public Bouncer(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    public Bouncer(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(SIZE_TO_HITBOX), VELOCITY * percentCharged, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (LIFE * percentCharged), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the bouncer projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
    }

    // Description: This method processes the bouncer projectile's movement and interactions
    public void process() {
        // Move the bouncer, rotate it, and expire it
        if (coord.x < 0) {
            coord.x = 0;
            dir = 0;
        }
        else if (coord.x > OmegaFight3.SCREEN_SIZE.x) {
            coord.x = OmegaFight3.SCREEN_SIZE.x;
            dir = Math.PI;
        }
        super.process();
        rotation = (rotation + ROT_SPEED * Math.cos(dir)) % ROT_MAX;
    }

    // Description: This method returns that the bouncer should never die to a projectile
    public boolean shouldDieTo(double enemyDura) {
        return false;
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        ((Omegaman) character).addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        ((Omegaman) character).addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Spike extends Projectile {
    // Damage constants
    public static final double THORN_DMG = 1 * Omegaman.PERC_MULT;
    public static final double DURA = 1;
    public static final double THORN_KB = 5;
    public static final int NUM_THORNS = 6;
    public static final boolean CURVED_BABY_PROJS = true;

    // Size constants
    public static final Coord SIZE = new Coord(50, 50);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 35;
    public static final double ROT_HZ = 1;
    public static final double ROT_SPEED = Math.PI * 2 / OmegaFight3.FPS * ROT_HZ;
    public static final double ROT_MAX = Math.PI * 2 / 3;

    // Misc constants
    public static final int BUTTONO = 9;
    public static final int SKILL_PT_COST = (int) (1.5 * NUM_THORNS * Thorn.SKILL_PT_GAIN);
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Instance variables
    public double rotation;

    // Static image
    public static BufferedImage image;

    // Constructor with default stats
    public Spike(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public Spike(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, 0, 0, 0, dura, frameCounter, canHitProj, isOnTop);
    }

    // Description: This method draws the spike projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);    
    }

    // Description: This method processes the spike projectile's movement and interactions
    public void process() {
        // Move the spike and rotate it and expire it
        super.process();
        rotation = (rotation + ROT_SPEED * Math.cos(dir)) % ROT_MAX;
    }

    // Description: This method handles the death of the spike projectile
    public void die() {
        if (!dead) {
            super.die();

            // Explodes into thorns
            for (int i = 0; i != NUM_THORNS; i++) {
                OmegaFight3.babyProjectiles.add(new Thorn((Omegaman) character, coord.copy(), i * Math.PI * 2 / NUM_THORNS, THORN_DMG, THORN_KB, Thorn.LIFE, CURVED_BABY_PROJS));
            }
        }
    }

    // Description: This method returns that the spike should die to any projectile
    public boolean shouldDieTo(double enemyDura) {
        return true;
    }

    public void hitPlayer(Omegaman enemy) {
        die();
    }

    public void hitBoss(Boss boss) {
        die();
    }
}

class Thorn extends Projectile {
    // Damage constants
    public static final double DURA = 1;
    public static final double KB_SPREAD = Math.PI / 3;

    // Size constants
    public static final Coord SIZE = new Coord(25, 20);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 20;
    public static final double TURN_AMT = Math.PI / 3;
    public static final double TURN_SPD = TURN_AMT / LIFE;

    // Misc constants
    public static final int SKILL_PT_GAIN = 1;
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Instance variables
    public boolean curved;

    // Static image
    public static BufferedImage image;

    // Constructor
    public Thorn(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean curved, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        this.curved = curved;
    }

    public Thorn(Omegaman player, Coord coord, double dir, double damage, double knockback, int frameCounter, boolean curved) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, damage, knockback, KB_SPREAD, DURA, frameCounter, curved, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Draws the thorn projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Processes the thorn projectile's movement and interactions
    public void process() {
        // Move the thorn and turn it and expire it
        super.process();
        if (curved) dir += TURN_SPD;
    }

    public void hitPlayer(Omegaman enemy) {
        double trueDmg = enemy.hurt(damage, knockback, coord, dir, kbSpread);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
    }

    public void hitBoss(Boss boss) {
        double trueDmg = boss.hurt(damage);
        die();
        Omegaman omega = ((Omegaman) character);
        omega.addSkillPts(SKILL_PT_GAIN);
        omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
    }
}

class Splitter extends Projectile {
    // Size constants
    public static final double MIN_PERC = 0.2;
    public static final Coord SIZE = new Coord(60, 50);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double THORN_DMG = 2 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double THORN_KB = 15;
    public static final int NUM_SPLITS = 3;
    public static final int PROJS_PER_SPLIT = 4;
    public static final double SPLIT_PROJS_START_ANGLE = (2 * Math.PI) / (PROJS_PER_SPLIT) / 2;
    public static final boolean CURVED_BABY_PROJS = false;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 30;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Instance variables
    public BufferedImage image;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Constructor
    public Splitter(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, 0, 0, 0, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    public Splitter(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(SIZE_TO_HITBOX), VELOCITY * percentCharged, dir, DURA, (int) (LIFE * percentCharged), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the splitter projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
    }

    // Description: Processes the splitter projectile's movement and interactions
    public void process() {
        // Move the splitter and expire it
        super.process();

        // Spit out thorns periodically
        if (frameCounter % (LIFE / NUM_SPLITS) == 0) {
            for (int i = 0; i != PROJS_PER_SPLIT; i++) { // Make these thorns not have skill pts?
                OmegaFight3.babyProjectiles.add(new Thorn((Omegaman) character, coord.copy(), SPLIT_PROJS_START_ANGLE + i * Math.PI * 2 / PROJS_PER_SPLIT, THORN_DMG, THORN_KB, (int) (Thorn.LIFE * ((double) frameCounter / LIFE + 1.0 / NUM_SPLITS)), CURVED_BABY_PROJS));
            }
        }
    }

    // Description: This method returns that the splitter should never die to a projectile
    public boolean shouldDieTo(double enemyDura) {
        return false;
    }

    public void hitPlayer(Omegaman enemy) {}
    public void hitBoss(Boss boss) {}
}