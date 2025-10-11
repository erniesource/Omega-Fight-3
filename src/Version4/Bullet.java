package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.sound.sampled.*;

public class Bullet extends Projectile {
    // Damage constants
    public static final double DMG = 1 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 5;
    public static final double KB_SPREAD = Math.PI / 2;
    public static final int MAX_COMBO = 5;
    public static final double DMG_POW = Math.pow(5, 1.0 / MAX_COMBO);
    public static final double SIZE_POW = Math.pow(2.5, 1.0 / MAX_COMBO);
    public static final int TIME_TO_COMBO = 300;

    // Size constants
    public static final Coord SIZE = new Coord(25, 18);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 20;
    public static final int LIFE = 25;

    // Misc constants
    public static final int BUTTONO = 4;
    public static final int SKILL_PT_GAIN = 4;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static variables
    public static BufferedImage image;
    public static int[] combo = new int[Omegaman.NUM_PLAYERS];
    public static int[] comboEndCounter = new int[Omegaman.NUM_PLAYERS];
    public static Clip pew;

    // Constructor
    public Bullet(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    // Overloaded constructor with default stats
    public Bullet(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.scaledBy(Math.pow(SIZE_POW, combo[player.playerNo])), SIZE.scaledBy(Math.pow(SIZE_POW, combo[player.playerNo]) * SIZE_TO_HITBOX), VELOCITY, dir, DMG * Math.pow(DMG_POW, combo[player.playerNo]), KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the bullet on the screen
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        super.draw(g2);
    }

    public void process() {
        move();
        Omegaman omega = (Omegaman) owner;
        frameCounter--;
        if (frameCounter == 0) {
            die();
            combo[omega.playerNo] = 0;
            comboEndCounter[omega.playerNo] = 0;
        }
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
            combo[omega.playerNo] = 0;
            comboEndCounter[omega.playerNo] = 0;
        }
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        
        if (trueDmg != 0) {
            combo[omega.playerNo] = Math.min(MAX_COMBO, combo[omega.playerNo] + 1);
            omega.addSkillPts(SKILL_PT_GAIN);
        }
        else {
            combo[omega.playerNo] = 0;
        }
        comboEndCounter[omega.playerNo] = 0;
        return true;
    }

    public boolean dieTo(Projectile proj) {
        if (dura <= proj.dura) {
            die();
            Omegaman omega = ((Omegaman) owner);
            combo[omega.playerNo] = Math.min(MAX_COMBO, combo[omega.playerNo] + 1);
            comboEndCounter[omega.playerNo] = 0;
            return true;
        }
        return false;
    }
}

class Rocket extends Projectile {
    // Size constants
    public static final double HITBOX_TO_SIZE = 2.5;
    public static final Coord SIZE = new Coord(50);

    // Damage constants
    public static final double DMG = 15 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 22;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final double EXPLOSION_SIZE_MULT = 4;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 40;
    public static final double LIFE_PERC_POW = 0.5;

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
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(HITBOX_TO_SIZE), VELOCITY, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (Math.pow(percentCharged, LIFE_PERC_POW) * LIFE), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description:
    // This overridden method makes the rocket explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
            super.die();
        }
    }

    public void process() {
        move();
        Omegaman omega = (Omegaman) owner;
        frameCounter--;
        if (frameCounter == 0) {
            die();
            Bullet.combo[omega.playerNo] = 0;
            Bullet.comboEndCounter[omega.playerNo] = 0;
        }
        if (OmegaFight3.outOfScreen(coord, size)) {
            die();
            Bullet.combo[omega.playerNo] = 0;
            Bullet.comboEndCounter[omega.playerNo] = 0;
        }
    }

    // Description:
    // This overridden method draws the rocket or explosion image based on its state
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        super.draw(g2);
    }

    public boolean dieTo(Projectile proj) {
        die();
        Omegaman omega = ((Omegaman) owner);
        Bullet.combo[omega.playerNo] = Math.min(Bullet.MAX_COMBO, Bullet.combo[omega.playerNo] + 1);
        Bullet.comboEndCounter[omega.playerNo] = 0;
        return true;
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        
        if (trueDmg != 0) {
            Bullet.combo[omega.playerNo] = Math.min(Bullet.MAX_COMBO, Bullet.combo[omega.playerNo] + 1);
            OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        }
        else {
            Bullet.combo[omega.playerNo] = 0;
        }
        Bullet.comboEndCounter[omega.playerNo] = 0;
        return true;
    }
}

class Shotgun extends Projectile {
    // Damage constants
    public static final double DMG = 2.5 * Omegaman.PERC_MULT;
    public static final double DURA = 1;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(24, 20);
    public static final double SIZE_TO_HITBOX = 0.9;

    // Movement constants
    public static final double VELOCITY = 18; 
    public static final int LIFE = 16;

    // Shot orientation constants
    public static final int NUM_SHOTS = 4;
    public static final double SPREAD = Math.PI / 6;

    // Misc constants
    public static final int BUTTONO = 5;
    public static final int SKILL_PT_GAIN = 2;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static variables
    public static BufferedImage image;
    public static Clip bang;

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
        super.draw(g2);
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }

        if (trueDmg != 0) omega.addSkillPts(SKILL_PT_GAIN);
        return true;
    }
}

class Firework extends Projectile {
    // Size constants
    public static final Coord SIZE = new Coord(48, 40);
    public static final Coord CHARGE_SIZE = new Coord(50);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double DMG = 4 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 4;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 15;
    public static final double LIFE_PERC_POW = 0.5;

    // Misc constants
    public static final int NUM_SHOTS = 8;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static BufferedImage[] chargingImages = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static Clip pow;

    // Instance variables
    public BufferedImage image;

    // Constructor
    public Firework(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    public Firework(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (Math.pow(percentCharged, LIFE_PERC_POW) * LIFE), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the firework projectile
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    public boolean dieTo(Projectile proj) {
        return false;
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        return true;
    }
}

class Spammer extends Projectile {
    // Damage constants
    public static final double DMG = 1.5 * Omegaman.PERC_MULT;
    public static final double DURA = 1;
    public static final double KB = 3;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(22, 18);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 20; 
    public static final int LIFE = 21;

    // Shot constants
    public static final double SPREAD = Math.PI / 9;
    public static final int PROJS_PER_BURST = 6;
    public static final int PROJ_HZ = 5;
    public static final int LAST_PROJ_HEAT = Omegaman.BASIC_SHOT_HEAT[Omegaman.SPAMMER_WEAPON_NO] - PROJS_PER_BURST * PROJ_HZ;

    // Misc constants
    public static final int BUTTONO = 6;
    public static final int SKILL_PT_GAIN = 1;
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Static image
    public static BufferedImage image;
    public static Clip ratatat;

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
        super.draw(g2);
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }

        if (trueDmg != 0) omega.addSkillPts(SKILL_PT_GAIN);
        return true;
    }
}

class Missile extends Projectile {
    // Size constants
    public static final double HITBOX_TO_SIZE = 1.5;
    public static final Coord SIZE = new Coord(60);

    // Damage constants
    public static final double DMG = 15 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final double EXPLOSION_SIZE_MULT = 3;

    // Velocity constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 70;
    public static final double TURN_SPD = Math.PI * (120.0 / LIFE / 180.0);
    public static final double LIFE_PERC_POW = 0.5;

    // Misc constants
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static images
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static Clip fwoosh;

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
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(HITBOX_TO_SIZE), VELOCITY, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (Math.pow(percentCharged, LIFE_PERC_POW) * LIFE), sign, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description:
    // This overridden method makes the missile explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
            super.die();
        }
    }

    // Description: This method draws the missile or explosion image based on its state
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        g2.drawImage(image, (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2 * sign), (int) size.x, (int) size.y * sign, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: This method processes the missile's movement and interactions
    public void process() {
        // Move the missile and expire it
        super.process();

        // Homing
        Char nearest = nearestChar();
        if (nearest != null) {
            home(nearest, TURN_SPD);
        }
    }

    // Description: This returns the fact that missile dies to any projectile
    public boolean dieTo(Projectile proj) {
        die();
        return true;
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        return true;
    }
}

class Sniper extends Projectile {
    // Damage constants
    public static final double DMG = 2.5 * Omegaman.PERC_MULT;
    public static final double DURA = 3;
    public static final double KB = 8;
    public static final double KB_SPREAD = Math.PI / 4;

    // Size constants
    public static final Coord SIZE = new Coord(41, 14);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Movement constants
    public static final double VELOCITY = 15; 
    public static final int LIFE = 30;
    public static final double ACCEL = 1;

    // Misc constants
    public static final int BUTTONO = 7;
    public static final int SKILL_PT_GAIN = 8;
    public static final double RECOIL = 4;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static variables
    public static BufferedImage image;
    public static Clip baw;

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
        super.draw(g2);
    }

    // Description: Processes the sniper bullet's movement and interactions
    public void process() {
        // Move the sniper bullet and accelerate it and expire it
        super.process();
        velocity += ACCEL;
    }

    public boolean dieTo(Char enemy) {
        die();
        double mult = velocity / VELOCITY;
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage * mult, knockback * mult, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage * mult);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }

        if (trueDmg != 0) omega.addSkillPts(SKILL_PT_GAIN);
        return true;
    }
}

class Laser extends Projectile {
    // Size constants
    public static final double SIZE_Y = 80; // x-size not impacted by charge, must be calculated
    public static final Coord BEAM_SIZE_Y_TO_BALL = new Coord(50 / SIZE_Y);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double DMG = 0.8 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double SOUR_KB = 2;
    public static final double SWEET_KB = 18;
    public static final double BASE_KB_DIR = Math.PI * 1.5;
    public static final double KB_DIR_TILT = Math.PI / 3;
    public static final double KB_SPREAD = Math.PI / 6;
    public static final int SOUR_TO_SWEET_TRANS = 5;

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
    public static Clip bew;

    // Constructor
    public Laser(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, 0, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    public Laser(Omegaman player, Coord coord, double sizeX, double dir, double percentCharged) {
        this(player, coord, new Coord(sizeX, SIZE_Y * percentCharged), (new Coord(sizeX, SIZE_Y * percentCharged)).scaledBy(SIZE_TO_HITBOX), dir, DMG * percentCharged, SOUR_KB * percentCharged, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: this method draws the laser beam and ball on the screen
    public void draw(Graphics2D g2) {
        double sizeY = size.y * Math.min(Math.min(frameCounter, LIFE - frameCounter), RESIZE_LEN) / RESIZE_LEN;
        if (frameCounter % (PULSE_HZ * 2) < PULSE_HZ) sizeY *= SIZE_Y_TO_PULSE;
        g2.drawImage(beam, (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - sizeY / 2), (int) (size.x * Math.cos(dir)), (int) sizeY, null);
        g2.drawImage(ball, (int) (coord.x - (size.x / 2 + size.y * BEAM_SIZE_Y_TO_BALL.x / 2) * Math.cos(dir)), (int) (coord.y - size.y * BEAM_SIZE_Y_TO_BALL.y / 2), (int) (size.y * BEAM_SIZE_Y_TO_BALL.x * Math.cos(dir)), (int) (size.y * BEAM_SIZE_Y_TO_BALL.y), null);
        super.draw(g2);
    }

    public void process() {
        super.process();
        if (frameCounter == SOUR_TO_SWEET_TRANS) knockback = SWEET_KB;
    }

    // Description: this method returns that the laser should never die to a projectile
    public boolean dieTo(Projectile proj) {
        return false;
    }

    public boolean dieTo(Char enemy) {
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, BASE_KB_DIR + KB_DIR_TILT * Math.cos(dir), kbSpread, knockback != SWEET_KB);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        return false;
    }
}

class Boomer extends Projectile {
    // Damage constants
    public static final double DMG = 2 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 7.5;
    public static final double KB_SPREAD = Math.PI / 4;

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
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;

    // Static variables
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static Clip whoosh;

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
        super.draw(g2);
    }

    // Description: Processes the boomerang projectile's movement and interactions
    public void process() {
        // Move the boomerang and deccelerate it and expire it
        move();
        expire();
        velocity += ACCEL;
    }

    public boolean dieTo(Char enemy) {
        die();
        int mult = velocity < 0? 2: 1;
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage * mult, knockback * mult, coord, dir + mult / 2 * Math.PI, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage * mult);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }

        if (trueDmg != 0) omega.addSkillPts(SKILL_PT_GAIN);
        return true;
    }
}

class Bouncer extends Projectile {
    // Size constants
    public static final Coord SIZE = new Coord(80);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double DMG = 0.75 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 4;

    // Velocity constants
    public static final double VELOCITY = 25;
    public static final int LIFE = 160;
    public static final double ROT_HZ = 1;
    public static final double ROT_SPD = Math.PI * 2 / 60 * ROT_HZ;
    public static final double ROT_MAX = Math.PI / 2;
    public static final double LIFE_PERC_POW = 0.5;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static variables
    public static BufferedImage[] images = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static Clip vrrr;

    // Instance variables
    public BufferedImage image;
    public double rotation = 0;

    // Constructor
    public Bouncer(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        image = images[player.playerNo];
    }

    public Bouncer(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (Math.pow(percentCharged, LIFE_PERC_POW) * LIFE), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: This method draws the bouncer projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
        super.draw(g2);
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
        rotation = (rotation + ROT_SPD * Math.cos(dir)) % ROT_MAX;
    }

    // Description: This method returns that the bouncer should never die to a projectile
    public boolean dieTo(Projectile proj) {
        return false;
    }

    public boolean dieTo(Char enemy) {
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        return false;
    }
}

class Spike extends Projectile {
    // Damage constants
    public static final double THORN_DMG = 1.5 * Omegaman.PERC_MULT;
    public static final double DURA = 1;
    public static final double THORN_KB = 10;
    public static final int NUM_THORNS = 6;
    public static final boolean CURVED_BABY_PROJS = true;
    public static final double THORN_PERC_CHARGED = 1;

    // Size constants
    public static final Coord SIZE = new Coord(50);
    public static final double SIZE_TO_HITBOX = 0.9;

    // Movement constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 35;
    public static final double ROT_HZ = 1;
    public static final double ROT_SPD = Math.PI * 2 / OmegaFight3.MAX_TICK_RATE * ROT_HZ;
    public static final double ROT_MAX = Math.PI * 2 / 3;

    // Misc constants
    public static final int BUTTONO = 9;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;

    // Static variables
    public static BufferedImage image;
    public static Clip pop;

    // Instance variables
    public double rotation;

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
        super.draw(g2);
    }

    // Description: This method processes the spike projectile's movement and interactions
    public void process() {
        // Move the spike and rotate it and expire it
        super.process();
        rotation = (rotation + ROT_SPD * Math.cos(dir)) % ROT_MAX;
    }

    // Description: This method handles the death of the spike projectile
    public void die() {
        if (!dead) {
            super.die();

            // Explodes into thorns
            for (int i = 0; i != NUM_THORNS; i++) {
                OmegaFight3.babyProjectiles.add(new Thorn((Omegaman) owner, coord.copy(), i * Math.PI * 2 / NUM_THORNS, THORN_DMG, THORN_KB, THORN_PERC_CHARGED, Thorn.LIFE, CURVED_BABY_PROJS));
            }

            OmegaFight3.play(pop);
        }
    }

    // Description: This method returns that the spike should die to any projectile
    public boolean dieTo(Projectile proj) {
        super.die();
        return true;
    }
    
    public boolean dieTo(Char enemy) {
        die();
        return true;
    }
}

class Thorn extends Projectile {
    // Damage constants
    public static final double DURA = 1;
    public static final double KB_SPREAD = Math.PI / 4;

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

    public Thorn(Omegaman player, Coord coord, double dir, double damage, double knockback, double percentCharged, int frameCounter, boolean curved) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged * SIZE_TO_HITBOX), VELOCITY, dir, damage * percentCharged, knockback * percentCharged, KB_SPREAD, DURA, (int) (frameCounter * percentCharged), curved, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Draws the thorn projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Processes the thorn projectile's movement and interactions
    public void process() {
        // Move the thorn and turn it and expire it
        super.process();
        if (curved) dir += TURN_SPD;
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }

        if (trueDmg != 0) omega.addSkillPts(SKILL_PT_GAIN);
        return true;
    }
}

class Splitter extends Projectile {
    // Size constants
    public static final Coord SIZE = new Coord(60, 50);
    public static final double SIZE_TO_HITBOX = 1.0;

    // Damage constants
    public static final double THORN_DMG = 3 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double THORN_KB = 20;
    public static final int NUM_SPLITS = 3;
    public static final int PROJS_PER_SPLIT = 4;
    public static final double SPLIT_PROJS_START_ANGLE = (2 * Math.PI) / (PROJS_PER_SPLIT) / 2;
    public static final boolean CURVED_BABY_PROJS = false;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 30;
    public static final double LIFE_PERC_POW = 0.5;

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
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DURA, (int) (Math.pow(percentCharged, LIFE_PERC_POW) * LIFE), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the splitter projectile on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    // Description: Processes the splitter projectile's movement and interactions
    public void process() {
        // Move the splitter and expire it
        super.process();

        // Spit out thorns periodically
        if (frameCounter % (LIFE / NUM_SPLITS) == 0) {
            for (int i = 0; i != PROJS_PER_SPLIT; i++) { // Make these thorns not have skill pts?
                OmegaFight3.babyProjectiles.add(new Thorn((Omegaman) owner, coord.copy(), SPLIT_PROJS_START_ANGLE + i * Math.PI * 2 / PROJS_PER_SPLIT, THORN_DMG, THORN_KB, size.x / SIZE.x, (int) (Thorn.LIFE * ((double) frameCounter / LIFE + 1.0 / NUM_SPLITS)), CURVED_BABY_PROJS));
            }

            OmegaFight3.play(Spike.pop);
        }
    }

    // Description: This method returns that the splitter should never die to a projectile
    public boolean dieTo(Projectile proj) {
        return false;
    }
    public boolean dieTo(Char enemy) {
        return false;
    }
}

class Fireball extends Projectile {
    // Damage constants
    public static final double DMG = 1 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 10;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final int FIRE_TIME = 40;

    // Size constants
    public static final Coord SIZE = new Coord(75, 30);
    public static final double SIZE_TO_HITBOX = 0.9;
    public static final Coord SIZE_TO_EXPLOSION = new Coord(1.2, 3);

    // Movement constants
    public static final double VELOCITY = 10;
    public static final int LIFE = 50;

    // Misc constants
    public static final int BUTTONO = 38;
    public static final int SKILL_PT_GAIN = 8;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_SPRITES = 3;
    public static final int SPRITE_CHANGE_HZ = 7;

    // Static variables
    public static BufferedImage[] image = new BufferedImage[NUM_SPRITES];
    public static Clip foom;

    // Constructor
    public Fireball(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    // Overloaded constructor with default stats
    public Fireball(Omegaman player, Coord coord, double dir) {
        this(player, coord, SIZE.copy(), SIZE.scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, KB_SPREAD, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description: Draws the bullet on the screen
    public void draw(Graphics2D g2) {
        g2.drawImage(image[(frameCounter % (NUM_SPRITES * SPRITE_CHANGE_HZ)) / SPRITE_CHANGE_HZ], (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
        super.draw(g2);
    }

    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(SIZE_TO_EXPLOSION)));
            super.die();
        }
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
            if (trueDmg != 0) omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, FIRE_TIME / Char.FIRE_HURT_HZ * Char.FIRE_DMG);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
            if (trueDmg != 0) omega.addToStat(Omegaman.DMG_TO_BOSS, FIRE_TIME / Char.FIRE_HURT_HZ * Char.FIRE_DMG);
        }
        
        if (trueDmg != 0) {
            enemy.setFire(FIRE_TIME);
            omega.addSkillPts(SKILL_PT_GAIN);
        }

        return true;
    }
}

class Phoenix extends Projectile {
    // Size constants
    public static final double HITBOX_TO_SIZE = 0.9;
    public static final Coord SIZE = new Coord(60);
    public static final Coord CHARGE_SIZE = new Coord(50, 65);
    public static final Coord COORD_TO_STAR = new Coord(0, -SIZE.y / 6); // FIX THIS

    // Damage constants
    public static final double DMG = 2 * Omegaman.PERC_MULT;
    public static final double DURA = 3;
    public static final double KB = 22;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final double EXPLOSION_SIZE_MULT = 2;
    public static final int FIRE_TIME = 100;
    public static final int NUM_STARS = 3;

    // Velocity constants
    public static final int LIFE = 300;
    public static final double LIFE_PERC_POW = 0.5;

    // Misc constants
    public static final int SCREENSHAKE = 15;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_SPRITES = 4;
    public static final int SPRITE_CHANGE_HZ = 5;
    public static final double ROT_SPD = Math.PI * 2 / 100;

    // Static variables
    public static BufferedImage[] image = new BufferedImage[NUM_SPRITES];
    public static BufferedImage chargingImage;
    public static Clip caw;

    // Instance variables
    public double rotation;

    // Constructor
    public Phoenix(Omegaman player, Coord size, Coord hitBoxSize, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, new Coord(), size, hitBoxSize, 0, 0, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        rotation = dir;
        func();
    }

    public Phoenix(Omegaman player, double dir, double percentCharged) {
        this(player, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(HITBOX_TO_SIZE), dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (Math.pow(percentCharged, LIFE_PERC_POW) * LIFE), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Description:
    // This overridden method makes the pheonix explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
            super.die();
        }
    }

    public void process() {
        expire();
        checkLeave();
        Omegaman omega = ((Omegaman) owner);
        if (omega.state != Omegaman.ALIVE_STATE) die();
        if (frameCounter % (LIFE / NUM_STARS) == 0) {
            double targetDir = 0;
            Char nearest = nearestChar();
            if (nearest != null) targetDir = Math.atan2(nearest.coord.y - coord.y, nearest.coord.x - coord.x); 
            OmegaFight3.babyProjectiles.add(new Star(omega, coord.add(COORD_TO_STAR), targetDir, size.x / SIZE.x));
        }
        rotation = (rotation + ROT_SPD) % (Math.PI * 2);
        func();
    }

    public void func() {
        Omegaman omega = ((Omegaman) owner);
        Coord omegaToPhoenixCoord = new Coord((new Coord()).disto(omega.size.scaledBy(0.5)));
        omegaToPhoenixCoord.x *= Math.cos(rotation);
        omegaToPhoenixCoord.y *= Math.sin(rotation);
        coord = omega.coord.add(omegaToPhoenixCoord);
        dir = Math.atan2(omegaToPhoenixCoord.y, omegaToPhoenixCoord.x) + Math.PI / 2;
    }

    // Description:
    // This overridden method draws the phoenix image based on its state
    public void draw(Graphics2D g2) {
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image[(frameCounter % (NUM_SPRITES * SPRITE_CHANGE_HZ)) / SPRITE_CHANGE_HZ], (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        super.draw(g2);
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
            if (trueDmg != 0) omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, FIRE_TIME / Char.FIRE_HURT_HZ * Char.FIRE_DMG);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
            if (trueDmg != 0) omega.addToStat(Omegaman.DMG_TO_BOSS, FIRE_TIME / Char.FIRE_HURT_HZ * Char.FIRE_DMG);
        }
        
        if (trueDmg != 0) enemy.setFire(FIRE_TIME);
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        return true;
    }
}

class Star extends Projectile {
    // Damage constants
    public static final double DMG = 2 * Omegaman.PERC_MULT;
    public static final double DURA = 2;
    public static final double KB = 20;
    public static final double KB_SPREAD = Math.PI / 4;
    public static final int FIRE_TIME = 60;

    // Size constants
    public static final Coord SIZE = new Coord(37);
    public static final double SIZE_TO_HITBOX = 0.9;
    public static final double EXPLOSION_SIZE_MULT = 2;

    // Movement constants
    public static final double VELOCITY = 12; 
    public static final int LIFE = 60;
    public static final double ROT_SPD = (Math.PI * 2) / 30;

    // Misc constants
    public static final boolean CAN_HIT_PROJ = false;
    public static final boolean IS_ON_TOP = true;
    public static final int SCREENSHAKE = 15;

    // Static image
    public static BufferedImage image;

    // Instance variables
    public double rotation;

    // Constructor with default stats
    public Star(Omegaman player, Coord coord, double dir, double percentCharged) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged * SIZE_TO_HITBOX), VELOCITY, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (LIFE * percentCharged), CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public Star(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
    }

    // Description:
    // This overridden method makes the star explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
            super.die();
        }
    }

    // Description: Draws the star on the screen
    public void draw(Graphics2D g2) {
        g2.rotate(rotation, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-rotation, coord.x, coord.y);
        super.draw(g2);
    }

    public void process() {
        super.process();
        rotation = (rotation + ROT_SPD) % (Math.PI % 2);
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
            if (trueDmg != 0) omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, FIRE_TIME / Char.FIRE_HURT_HZ * Char.FIRE_DMG);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
            if (trueDmg != 0) omega.addToStat(Omegaman.DMG_TO_BOSS, FIRE_TIME / Char.FIRE_HURT_HZ * Char.FIRE_DMG);
        }
        
        if (trueDmg != 0) enemy.setFire(FIRE_TIME);
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        return true;
    }
}

class GlueBomb extends Projectile {
    // Damage constants
    public static final double DMG = 4 * Omegaman.PERC_MULT;
    public static final double DURA = INF_DURA;
    public static final double KB = 10;

    // Size constants
    public static final Coord[] GLUE_SIZE = {new Coord(70, 28), new Coord(60, 53)};
    public static final Coord BOMB_SIZE = new Coord(32, 50);
    public static final double SIZE_TO_HITBOX = 0.1;
    public static final Coord EXPLOSION_SIZE_MULT = new Coord(3, 2);

    // Movement constants
    public static final double VELOCITY = 8; 
    public static final int LIFE = 120;
    public static final int MIN_BOMB_TIME = 30;

    // Misc constants
    public static final int BUTTONO = 39;
    public static final int SKILL_PT_GAIN = 12;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int NUM_STATES = 2;
    public static final int GLOBBING = 0;
    public static final int SPLATTING = 1;
    public static final int BOMB_NUM_SPRITES = 2;
    public static final int SCREENSHAKE = 10;

    // Static variables
    public static BufferedImage[] glueImage = new BufferedImage[NUM_STATES];
    public static BufferedImage[] bombImage = new BufferedImage[BOMB_NUM_SPRITES];

    public int state;
    public Char victim;
    public Coord coordToVictim;

    // Constructor with default stats
    public GlueBomb(Omegaman player, Coord coord, double dir) {
        this(player, coord, GLUE_SIZE[GLOBBING].copy(), GLUE_SIZE[GLOBBING].scaledBy(SIZE_TO_HITBOX), VELOCITY, dir, DMG, KB, DURA, LIFE, CAN_HIT_PROJ, IS_ON_TOP);
    }

    // Constructor with custom stats
    public GlueBomb(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double dura, int frameCounter, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, 0, dura, frameCounter, canHitProj, isOnTop);
    }

    // Description: Draws the shotgun bullet on the screen
    public void draw(Graphics2D g2) {
        g2.drawImage(glueImage[state], (int) (coord.x - size.x / 2 * Math.cos(dir)), (int) (coord.y - size.y / 2), (int) (size.x * Math.cos(dir)), (int) size.y, null);
        Coord bombSize = new Coord(size.x * BOMB_SIZE.x / GLUE_SIZE[state].x, size.y * BOMB_SIZE.y / GLUE_SIZE[state].y);
        Coord bombCoord = coord.add(bombSize.scaledBy(-0.5));
        g2.drawImage(bombImage[(int) (Math.sin(Math.PI * 2 * LIFE / frameCounter) / 2 + 1)], (int) (bombCoord.x), (int) (bombCoord.y), (int) (bombSize.x), (int) (bombSize.y), null);
        super.draw(g2);
    }

    public void process() {
        if (state == SPLATTING) {
            coord = victim.coord.add(new Coord(coordToVictim.x * victim.spriteSign * victim.size.x, coordToVictim.y * victim.size.y));
        }
        else {
            move();
        }
        expire();
        checkLeave();
    }

    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, (new Coord(size.x / GLUE_SIZE[state].x, size.y / GLUE_SIZE[state].y)).scaledBy(BOMB_SIZE).scaledBy(EXPLOSION_SIZE_MULT)));
            super.die();
            Omegaman omega = ((Omegaman) owner);
            double trueDmg = 0;
            if (victim instanceof Omegaman) {
                trueDmg = ((Omegaman) victim).hurt(damage, knockback, coord);
                omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
            }
            else if (victim instanceof Boss) {
                trueDmg = ((Boss) victim).hurt(damage);
                omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
            }
            
            if (trueDmg != 0) {
                omega.addSkillPts(SKILL_PT_GAIN);
                OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / GLUE_SIZE[SPLATTING].x));
            }
        }
    }

    public boolean dieTo(Char enemy) {
        if (state == GLOBBING) {
            state = SPLATTING;
            victim = enemy;
            coordToVictim = new Coord((coord.x - enemy.coord.x) / enemy.size.x * enemy.spriteSign, (coord.y - enemy.coord.y) / enemy.size.y);
            size = new Coord(size.x * GLUE_SIZE[SPLATTING].x / GLUE_SIZE[GLOBBING].x, size.y * GLUE_SIZE[SPLATTING].y / GLUE_SIZE[GLOBBING].y);
            velocity = 0;
            frameCounter = Math.max(frameCounter, MIN_BOMB_TIME);
        }
        return false;
    }

    public boolean dieTo(Projectile proj) {
        die();
        return true;
    }
}

class Spark extends Projectile {
    // Size constants
    public static final double HITBOX_TO_SIZE = 0.9;
    public static final Coord SIZE = new Coord(50, 45);

    public static final Coord EXPLOSION_SIZE_MULT = new Coord(3, 30.0 / 9);

    // Damage constants
    public static final double DMG = 8 * Omegaman.PERC_MULT;
    public static final double DURA = 3;
    public static final double KB = 15;
    public static final double KB_SPREAD = Math.PI / 4;

    // Velocity constants
    public static final double VELOCITY = 15;
    public static final int LIFE = 45;

    // Shot orientation constants
    public static final int NUM_SHOTS = 3;
    public static final double MAX_DIST_TO_CENTER = 1;
    public static final double[] INIT_DIR = {-Math.atan2(MAX_DIST_TO_CENTER, 0.5), Math.atan2(MAX_DIST_TO_CENTER, 0.5), 0};
    public static final double[] HALF_DIR = {Math.atan2(MAX_DIST_TO_CENTER, 0.5), -Math.atan2(MAX_DIST_TO_CENTER, 0.5), 0};

    // Misc constants
    public static final double RECOIL = 8;
    public static final int SCREENSHAKE = 10;
    public static final boolean CAN_HIT_PROJ = true;
    public static final boolean IS_ON_TOP = true;
    public static final int TOP = 0;
    public static final int BOTM = 1;
    public static final int CENTR = 2;

    // Static image
    public static BufferedImage image;
    public static BufferedImage reticleImg;

    public int type;
    public double trueDir;
    public Coord origin;
    public int totalLife;

    // Constructor
    public Spark(Omegaman player, Coord coord, Coord size, Coord hitBoxSize, double velocity, double dir, double damage, double knockback, double kbSpread, double dura, int frameCounter, int type, boolean canHitProj, boolean isOnTop) {
        super(player, coord, size, hitBoxSize, velocity, dir, damage, knockback, kbSpread, dura, frameCounter, canHitProj, isOnTop);
        trueDir = dir;
        this.type = type;
        this.dir = INIT_DIR[type] + trueDir;
        origin = coord.copy();
        totalLife = frameCounter;
    }

    public Spark(Omegaman player, Coord coord, double dir, double percentCharged, int type) {
        this(player, coord, SIZE.scaledBy(percentCharged), SIZE.scaledBy(percentCharged).scaledBy(HITBOX_TO_SIZE), VELOCITY, dir, DMG * percentCharged, KB * percentCharged, KB_SPREAD, DURA, (int) (LIFE * percentCharged), type, CAN_HIT_PROJ, IS_ON_TOP);
    }

    public void process() {
        frameCounter--;
        coord.x = origin.x + (totalLife - frameCounter) * velocity * Math.cos(trueDir);
        if (type == TOP) {
            coord.y = origin.y - (double) (totalLife / 2 - Math.abs(totalLife / 2 - frameCounter)) / (totalLife / 2) * (MAX_DIST_TO_CENTER * totalLife / 2 * velocity) * Math.cos(trueDir);
        }
        else if (type == BOTM) {
            coord.y = origin.y + (double) (totalLife / 2 - Math.abs(totalLife / 2 - frameCounter)) / (totalLife / 2) * (MAX_DIST_TO_CENTER * totalLife / 2 * velocity) * Math.cos(trueDir);
        }
        if (frameCounter == 0) {
            die();
            hitBoxSize = hitBoxSize.scaledBy(EXPLOSION_SIZE_MULT);
        }
        else if (frameCounter == totalLife / 2) {
            dir = HALF_DIR[type] + trueDir;
        }
    }

    // Description:
    // This overridden method makes the rocket explode
    public void die() {
        if (!dead) {
            OmegaFight3.explosionQ.add(new Explosion(coord, size.scaledBy(EXPLOSION_SIZE_MULT)));
            super.die();
        }
    }

    // Description:
    // This overridden method draws the rocket or explosion image based on its state
    public void draw(Graphics2D g2) {
        g2.rotate(dir, coord.x, coord.y);
        Coord drawCoord = coord.add(size.scaledBy(-0.5));
        g2.drawImage(image, (int) (drawCoord.x), (int) (drawCoord.y), (int) size.x, (int) size.y, null);
        g2.rotate(-dir, coord.x, coord.y);
        super.draw(g2);
    }

    public boolean dieTo(Char enemy) {
        die();
        Omegaman omega = ((Omegaman) owner);
        double trueDmg = 0;
        if (enemy instanceof Omegaman) {
            trueDmg = ((Omegaman) enemy).hurt(damage, knockback, coord, dir, kbSpread);
            omega.addToStat(Omegaman.DMG_TO_OMEGAMAN, trueDmg);
        }
        else if (enemy instanceof Boss) {
            trueDmg = ((Boss) enemy).hurt(damage);
            omega.addToStat(Omegaman.DMG_TO_BOSS, trueDmg);
        }
        
        OmegaFight3.screenShakeCounter += (int) (SCREENSHAKE * (size.x / SIZE.x));
        return true;
    }
}