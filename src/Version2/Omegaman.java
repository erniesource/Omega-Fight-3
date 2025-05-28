package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class Omegaman extends Char {
    // Movement stats
    public Coord velocity = new Coord();
    public int runSign = 1; // 1: Positive, -1: Negative
    public int lftKey, ritKey, upKey, dwnKey, shtKeys[], swtKey;
    public Coord maxVelocity = new Coord(8, 22);
    public Coord accel = new Coord(1, 2);
    public int runFrameFreq = 6;

    // Jumping stats
    public int jumpCounter; // Jump cooldown (3 frames or 1/20 of a second)
    public int coyoteCounter; // Counter for Coyote time
    public int jumpState = 1; // Even number (Pressing on the i / 2 - th jump), Odd number (Not pressing on the i / 2 - th jump)
    public int onPlatform; // -1: Not on plaform (airborne), 0+ (which number platform player is on)

    // Weapon statistics
    public int shootCharge;
    public int heatCounter;
    public int chargingWeapon = -1;
    public int[] loadout;
    public int[] loadoutButtono;
    public Deque<Integer> stalenessQ = new LinkedList<>(); // To be used

    // Skill point statistics
    public int skillPts = ONES_PER_SKILL_PT * 3 / 2;
    public int skillPtCounter;

    // KB statistics
    public int stunCounter;

    // Combat statistics
    public int percent;
    public int percentShakeCounter;
    public Coord[] percentDigitShake = new Coord[4];
    public int percentDisplayX;
    public int invCounter;

    // Other stats
    public int playerNo;
    
    // Sprite Constants
    public static final int IDLE_SPRITE = 0;
    public static final int FRST_RUN_SPRITE = 1;
    public static final int LAST_RUN_SPRITE = 3;
    public static final int JUMP_SPRITE = 4;
    public static final int HURT_SPRITE = 5;

    // Direction Constants
    public static final int LEFT_SIGN = -1;
    public static final int RIGHT_SIGN = 1;

    // Movement Constants
    public static final int AIRBORNE = -1;
    public static final int JUMP_COOLDOWN = 3;
    public static final int JUMPS_ALLOWED = 2;
    public static final int COYOTE_TIME = 5;

    // Shooting Constants
    public static final int BULLET_WEAPON_NO = 0;
    public static final int SHOTGUN_WEAPON_NO = 1;
    public static final int SPAMMER_WEAPON_NO = 2;
    public static final int SNIPER_WEAPON_NO = 3;
    public static final int BOOMER_WEAPON_NO = 4;
    public static final int SPIKE_WEAPON_NO = 5;
    public static final int[] BASIC_SHOT_HEAT = {10, 30, 5, 60, 10, 30};
    public static final int[] CHARGED_SHOT_HEAT = {20, 60, 50, 80, 100, 60};
    public static final int[] CHARGE_TIME = {50, 90, 80, 100, 120, 60};
    public static final int BASIC_SHOOT_TIME_LIMIT = 20;
    public static final int LOADOUT_NUM_WEAPONS = 2;
    public static final int MAX_SHOOT_CHARGE = 300;
    public static final int NOT_CHARGING = -1;

    // Projectile Offsets
    public static final int IDLE_PROJ_Y_OFFSET = 7;
    public static final int JUMP_PROJ_Y_OFFSET = -13;
    public static final int PROJ_SPAWN_X_OFFSET = 6;

    // Skill Points Constants
    public static final int ONES_PER_SKILL_PT = 60;
    public static final int MAX_SKILL_PTS = 3 * ONES_PER_SKILL_PT;
    public static final int SKILL_PT_REGEN_HZ = 10;

    // KB Constants
    public static final int NOT_STUNNED = 0;
    public static final int KB_COORD_Y_OFFSET = 50;
    public static final double KB_GRAVITY = 1.25;
    public static final double BOUNCE_MIN_VEL_Y = 20;
    public static final double STUN_REDUCTION = 0.9;

    // HUD Constants
    public static final int PERCENT_DISPLAY_Y_COORD = 790;
    public static final Coord PERCENT_DISPLAY_SIZE = new Coord(400, 150);
    public static final int PERCENT_NUM_DECIMALS = 1;
    public static final int MAX_PERCENT_COLOR_THRESHOLD = 150 * (int) Math.pow(10, PERCENT_NUM_DECIMALS);
    public static final int PERCENT_SHAKE_HZ = 2;
    public static final int PERCENT_SHADOW_OFFSET = 3;
    public static final int NUM_PLAYERS = 2;
    public static final int PERCENT_DISPLAY_DIST = (int) ((OmegaFight3.SCREEN_SIZE.x - PERCENT_DISPLAY_SIZE.x * NUM_PLAYERS) / (NUM_PLAYERS + 1));
    public static final Coord PERCENT_DISPLAY_SIZE_TO_SKILL_PTS_COORD = new Coord(0.5 * PERCENT_DISPLAY_SIZE.x, 19.0 / 30.0 * PERCENT_DISPLAY_SIZE.y);
    public static final double SKILL_PTS_SPACE = 0.0125 * PERCENT_DISPLAY_SIZE.x;
    public static final Coord SKILL_PTS_SIZE = new Coord((0.475 * PERCENT_DISPLAY_SIZE.x - SKILL_PTS_SPACE * (MAX_SKILL_PTS / ONES_PER_SKILL_PT - 1)) / (MAX_SKILL_PTS / ONES_PER_SKILL_PT), (1.0 / 6.0) * PERCENT_DISPLAY_SIZE.y);
    public static final Coord PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD = new Coord(0.5 * PERCENT_DISPLAY_SIZE.x, 0.5 * PERCENT_DISPLAY_SIZE.y);
    public static final int PERCENT_SHAKE_TIME = 24;
    public static final double PERCENT_SHAKE_MULTIPLIER = 2;

    // Respawn Constants
    public static final int RESPAWN_PAUSE = 120;
    public static final int RESPAWN_INITIAL_VELOCITY = 25;
    public static final int RESPAWN_TIME_LIMIT = 120;

    // Death States
    public static final int ALIVE_STATE = 0;
    public static final int DIED_BOTTOM = 1;
    public static final int DIED_LEFT = 2;
    public static final int DIED_TOP = 3;
    public static final int DIED_RIGHT = 4;

    // Stat reset constants
    public static final int DIED_STAT_RESET = 0;
    public static final int GENERAL_STAT_RESET = -1;

    // Surge Constants
    public static final int NUM_SURGE_IMAGES = 5;
    public static final int SURGE_FRAME_HZ = 6;
    public static final int SURGE_TIME = NUM_SURGE_IMAGES * SURGE_FRAME_HZ;
    public static final Coord SURGE_SIZE = new Coord(741, 949);

    // Fonts and Colors
    public static final Font BIG_PERCENT_FONT = new Font("Impact", Font.PLAIN, (int) (PERCENT_DISPLAY_SIZE.y * 8 / 15));
    public static final Font SML_PERCENT_FONT = new Font("Impact", Font.PLAIN, BIG_PERCENT_FONT.getSize() * 2 / 5);
    public static final Color MAX_PERCENT_COLOR = new Color(205, 46, 43);

    // Invincibility Constants
    public static final int INV_BLINK_CYCLE_LEN = 10;
    public static final int INV_BLINK_LEN = 3;
    public static final int RESPAWN_INV_LEN = 60;
    public static final int VULNERABLE = 0;

    // Images
    public BufferedImage[] sprite = new BufferedImage[6];
    public BufferedImage percentDisplay;
    public BufferedImage[] surge = new BufferedImage[NUM_SURGE_IMAGES];

    public Omegaman(int playerNo, Coord coord, int spriteSign, int onPlatform, int[] controls, int[] shtKeys, int[] loadout, int[] loadoutButtono) throws IOException {
        // Initialize character variables
        super(coord, IDLE_SPRITE, spriteSign, 0, new Coord(100, 100), ALIVE_STATE);

        // Initialize player variables
        this.playerNo = playerNo;
        this.onPlatform = onPlatform;
        this.loadout = loadout;
        this.shtKeys = shtKeys;
        this.loadoutButtono = loadoutButtono;

        // Assign control keys
        lftKey = controls[0];
        ritKey = controls[1];
        upKey = controls[2];
        dwnKey = controls[3];

        // Percent related stuff
        percentDisplayX = PERCENT_DISPLAY_DIST + (int) (PERCENT_DISPLAY_SIZE.x + PERCENT_DISPLAY_DIST) * playerNo;
        percentDisplay = ImageIO.read(new File("HUD/" + playerNo + "percentdisplay.png"));

        // Load sprites
        sprite[IDLE_SPRITE] = ImageIO.read(new File("player sprites/" + playerNo + "idle.png"));
        for (int i = 0; i != LAST_RUN_SPRITE - FRST_RUN_SPRITE + 1; i++) {
            sprite[i + 1] = ImageIO.read(new File("player sprites/" + playerNo + "run" + i + ".png"));
        }
        sprite[JUMP_SPRITE] = ImageIO.read(new File("player sprites/" + playerNo + "jump.png"));
        sprite[HURT_SPRITE] = ImageIO.read(new File("player sprites/" + playerNo + "hurt.png"));

        // Load surge images
        for (int i = 0; i != NUM_SURGE_IMAGES; i++) {
            surge[i] = ImageIO.read(new File("explosions/" + playerNo + "surge" + i + ".png"));
        }
    }

    public void controlX(boolean lftPressed, boolean ritPressed) {
        // Left moving
        if (lftPressed) {
            // Deccelaration
            if (ritPressed) decelerateRun();

            // Acceleration
            else {
                if (velocity.x > -maxVelocity.x) velocity.x = Math.max(-maxVelocity.x, velocity.x - accel.x);
                else if (velocity.x < -maxVelocity.x) velocity.x = Math.min(-maxVelocity.x, velocity.x + accel.x);

                animateRun();

                // Change to correct direction
                if (spriteSign == RIGHT_SIGN) spriteSign = LEFT_SIGN;
            }
        }

        // Right moving
        else if (ritPressed) {
            // Acceleration
            if (velocity.x < maxVelocity.x) velocity.x = Math.min(maxVelocity.x, velocity.x + accel.x);
            else if (velocity.x > maxVelocity.x) velocity.x = Math.max(maxVelocity.x, velocity.x - accel.x);

            animateRun();

            // Change to correct direction
            if (spriteSign == LEFT_SIGN) spriteSign = RIGHT_SIGN;
        }

        // Idle
        else decelerateRun();
    }

    public void controlY(boolean upPressed, boolean dwnPressed) {
        // Jumping
        if (upPressed && jumpState % 2 == 1 && jumpState < JUMPS_ALLOWED * 2 && jumpCounter == JUMP_COOLDOWN) {
            // Teleport player to platform if about to land, but still count it as double jump since they bypassed the jump cooldown
            int platformNo = checkPlatforms();
            if (platformNo != AIRBORNE) coord.y = getPlatformY(platformNo);
            velocity.y = -maxVelocity.y;
            spriteNo = JUMP_SPRITE;
            onPlatform = AIRBORNE;
            jumpCounter = 0;
            jumpState++;
        }

        // Dropping
        else if (dwnPressed && jumpCounter == JUMP_COOLDOWN && onPlatform != AIRBORNE && !OmegaFight3.stage[OmegaFight3.stageNo].platforms[onPlatform].isMain) {
            coord.y++;
            spriteNo = JUMP_SPRITE;
            onPlatform = AIRBORNE;
        }
    }

    public void regenSkillPts() {
        if (skillPts != MAX_SKILL_PTS) {
            skillPtCounter = (skillPtCounter + 1) % SKILL_PT_REGEN_HZ;
            if (skillPtCounter == 0) skillPts++;
        }
    }

    public void controlShoot(HashSet<Integer> pressedKey) {
        if (heatCounter != 0) heatCounter--;
        else {
            for (int i = 0; i != LOADOUT_NUM_WEAPONS; i++) {
                if ((chargingWeapon == NOT_CHARGING) || chargingWeapon == i) {
                    if (!pressedKey.contains(shtKeys[i]) || shootCharge == MAX_SHOOT_CHARGE) {
                        if (shootCharge != 0) {
                            Coord newProjCoord = new Coord(coord.x + (size.x / 2 - PROJ_SPAWN_X_OFFSET) * spriteSign, coord.y + (onPlatform == -1? JUMP_PROJ_Y_OFFSET : IDLE_PROJ_Y_OFFSET));
                            
                            // Fire basic
                            if (shootCharge <= BASIC_SHOOT_TIME_LIMIT || skillPts < ONES_PER_SKILL_PT) {
                                if (loadout[i] == BULLET_WEAPON_NO) projectiles.add(new Bullet(this, newProjCoord, Bullet.SIZE, Bullet.VELOCITY, OmegaFight3.signToRadians(spriteSign), Bullet.DMG, Bullet.KB, Bullet.DURABILITY, Bullet.LIFE));
                                else if (loadout[i] == SHOTGUN_WEAPON_NO) {
                                    // Shotgun
                                    for (int j = 0; j != Shotgun.NUM_SHOTS; j++) {
                                        projectiles.add(new Shotgun(this, newProjCoord.copy(), Shotgun.SIZE, Shotgun.VELOCITY, OmegaFight3.signToRadians(spriteSign) - Shotgun.SPREAD + j * (Shotgun.SPREAD * 2 / (Shotgun.NUM_SHOTS - 1)), Shotgun.DMG, Shotgun.KB, Shotgun.DURABILITY, Shotgun.LIFE));
                                    }
                                }
                                else if (loadout[i] == SPAMMER_WEAPON_NO) {
                                    projectiles.add(new Spammer(this, newProjCoord, Spammer.SIZE, Spammer.VELOCITY, OmegaFight3.signToRadians(spriteSign) - Spammer.SPREAD + Math.random() * Spammer.SPREAD * 2, Spammer.DMG, Spammer.KB, Spammer.DURABILITY, Spammer.LIFE));
                                }
                                else if (loadout[i] == SNIPER_WEAPON_NO) {
                                    projectiles.add(new Sniper(this, newProjCoord, Sniper.SIZE, Sniper.VELOCITY, OmegaFight3.signToRadians(spriteSign), Sniper.DMG, Sniper.KB, Sniper.DURABILITY, Sniper.LIFE));
                                    
                                    // Recoil
                                    recoil(Sniper.RECOIL);
                                }
                                else if (loadout[i] == BOOMER_WEAPON_NO) {
                                    projectiles.add(new Boomer(this, newProjCoord, Boomer.SIZE, Boomer.VELOCITY, OmegaFight3.signToRadians(spriteSign), Boomer.DMG, Boomer.KB, Boomer.DURABILITY, Boomer.LIFE));
                                }
                                else if (loadout[i] == SPIKE_WEAPON_NO) {
                                    projectiles.add(new Spike(this, newProjCoord, Spike.SIZE, Spike.VELOCITY, OmegaFight3.signToRadians(spriteSign), Spike.DMG, Spike.KB, Spike.DURABILITY, Spike.LIFE));
                                }

                                heatCounter = BASIC_SHOT_HEAT[loadout[i]];
                            }

                            // Fire charged
                            else {
                                double percentCharged = 0;
                                if (loadout[i] == BULLET_WEAPON_NO) {
                                    percentCharged = OmegaFight3.lerp(Rocket.MIN_SIZE_PERCENT, 1, getPercentCharged(BULLET_WEAPON_NO));
                                    projectiles.add(new Rocket(this, newProjCoord, Rocket.SIZE.scaledBy(percentCharged), Rocket.VELOCITY * percentCharged, OmegaFight3.signToRadians(spriteSign), Rocket.DMG * percentCharged, Rocket.KB * percentCharged, Rocket.DURABILITY, (int) (Rocket.LIFE * percentCharged)));
                                
                                    // Recoil
                                    recoil(Rocket.RECOIL * percentCharged);
                                }
                                else if (loadout[i] == SHOTGUN_WEAPON_NO) {
                                    percentCharged = OmegaFight3.lerp(Rocket.MIN_SIZE_PERCENT, 1, getPercentCharged(SHOTGUN_WEAPON_NO));
                                    for (int j = 0; j != Firework.NUM_SHOTS; j++) {
                                        projectiles.add(new Firework(this, newProjCoord.copy(), Firework.SIZE.scaledBy(percentCharged), Firework.VELOCITY * percentCharged, Math.PI * 2 / Firework.NUM_SHOTS * j, Firework.DMG * percentCharged, Firework.KB * percentCharged, Firework.DURABILITY, (int) (Firework.LIFE * percentCharged)));
                                    }
                                }
                                else if (loadout[i] == SPAMMER_WEAPON_NO) {
                                    percentCharged = OmegaFight3.lerp(Missile.MIN_SIZE_PERCENT, 1, getPercentCharged(SPAMMER_WEAPON_NO));
                                    projectiles.add(new Missile(this, newProjCoord, Missile.SIZE.scaledBy(percentCharged), Missile.VELOCITY * percentCharged, OmegaFight3.signToRadians(spriteSign), Missile.DMG * percentCharged, Missile.KB * percentCharged, Missile.DURABILITY, (int) (Missile.LIFE * percentCharged), spriteSign));
                                    
                                    // Recoil
                                    recoil(Missile.RECOIL * percentCharged);
                                }
                                else if (loadout[i] == SNIPER_WEAPON_NO) {
                                    percentCharged = OmegaFight3.lerp(Laser.MIN_SIZE_PERCENT, 1, getPercentCharged(SNIPER_WEAPON_NO));
                                    projectiles.add(new Laser(this, new Coord((newProjCoord.x + OmegaFight3.SCREEN_SIZE.x * (spriteSign + 1) / 2) / 2, newProjCoord.y), new Coord(Math.abs(newProjCoord.x - OmegaFight3.SCREEN_SIZE.x * (spriteSign + 1) / 2), Laser.SIZE_Y * percentCharged), OmegaFight3.signToRadians(spriteSign), Laser.DMG * percentCharged, Laser.KB * percentCharged, Laser.DURABILITY, Laser.LIFE));

                                    // Recoil
                                    recoil(Laser.RECOIL * percentCharged);
                                }
                                else if (loadout[i] == BOOMER_WEAPON_NO) {
                                    percentCharged = OmegaFight3.lerp(Bouncer.MIN_SIZE_PERCENT, 1, getPercentCharged(BOOMER_WEAPON_NO));
                                    projectiles.add(new Bouncer(this, newProjCoord, Bouncer.SIZE.scaledBy(percentCharged), Bouncer.VELOCITY * percentCharged, OmegaFight3.signToRadians(spriteSign), Bouncer.DMG * percentCharged, Bouncer.KB * percentCharged, Bouncer.DURABILITY, (int) (Bouncer.LIFE * percentCharged)));
                                }
                                else if (loadout[i] == SPIKE_WEAPON_NO) {
                                    percentCharged = OmegaFight3.lerp(Splitter.MIN_SIZE_PERCENT, 1, getPercentCharged(SPIKE_WEAPON_NO));
                                    projectiles.add(new Splitter(this, newProjCoord, Splitter.SIZE.scaledBy(percentCharged), Splitter.VELOCITY * percentCharged, OmegaFight3.signToRadians(spriteSign), Splitter.DMG, Splitter.KB, Splitter.DURABILITY, (int) (Splitter.LIFE * percentCharged)));
                                }

                                // Update stats
                                skillPts -= (percentCharged < 0.5? ONES_PER_SKILL_PT / 2: ONES_PER_SKILL_PT);
                                heatCounter = CHARGED_SHOT_HEAT[loadout[i]];
                            }
                            shootCharge = 0;
                            chargingWeapon = NOT_CHARGING;
                        }
                    }
                    else if (skillPts >= ONES_PER_SKILL_PT || shootCharge != BASIC_SHOOT_TIME_LIMIT) {
                        if (chargingWeapon == NOT_CHARGING) chargingWeapon = i;
                        shootCharge++;
                    }
                }
            }
        }
    }

    public void drawCharge(Graphics g) {
        if (shootCharge > BASIC_SHOOT_TIME_LIMIT) {
            Coord chargeCoord = new Coord(coord.x + size.x / 2 * spriteSign, coord.y + (onPlatform == -1? JUMP_PROJ_Y_OFFSET: IDLE_PROJ_Y_OFFSET));
            Coord chargeSize;
            if (loadout[chargingWeapon] == BULLET_WEAPON_NO) {
                chargeSize = Rocket.SIZE.scaledBy(OmegaFight3.lerp(Rocket.MIN_SIZE_PERCENT, 1, getPercentCharged(BULLET_WEAPON_NO)));
                g.drawImage(Rocket.images[playerNo], (int) (chargeCoord.x - chargeSize.x / 2), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x, (int) chargeSize.y, null);
            }
            else if (loadout[chargingWeapon] == SHOTGUN_WEAPON_NO) {
                chargeSize = Firework.SIZE.scaledBy(OmegaFight3.lerp(Firework.MIN_SIZE_PERCENT, 1, getPercentCharged(SHOTGUN_WEAPON_NO)));
                g.drawImage(Firework.chargingImages[playerNo], (int) (chargeCoord.x - chargeSize.x / 2), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x, (int) chargeSize.y, null);
            }
            else if (loadout[chargingWeapon] == SPAMMER_WEAPON_NO) {
                chargeSize = Missile.SIZE.scaledBy(OmegaFight3.lerp(Missile.MIN_SIZE_PERCENT, 1, getPercentCharged(SPAMMER_WEAPON_NO)));
                g.drawImage(Missile.images[playerNo], (int) (chargeCoord.x - chargeSize.x / 2 * spriteSign), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x * spriteSign, (int) chargeSize.y, null);
            }
            else if (loadout[chargingWeapon] == SNIPER_WEAPON_NO) {
                chargeSize = (new Coord(Laser.SIZE_Y * Laser.BEAM_SIZE_Y_TO_BALL_SIZE.x, Laser.SIZE_Y * Laser.BEAM_SIZE_Y_TO_BALL_SIZE.y)).scaledBy(OmegaFight3.lerp(Laser.MIN_SIZE_PERCENT, 1, getPercentCharged(SNIPER_WEAPON_NO)));
                g.drawImage(Laser.ball, (int) (chargeCoord.x - chargeSize.x / 2 * spriteSign), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x * spriteSign, (int) chargeSize.y, null);
            }
            else if (loadout[chargingWeapon] == BOOMER_WEAPON_NO) {
                chargeSize = Bouncer.SIZE.scaledBy(OmegaFight3.lerp(Bouncer.MIN_SIZE_PERCENT, 1, getPercentCharged(BOOMER_WEAPON_NO)));
                g.drawImage(Bouncer.images[playerNo], (int) (chargeCoord.x - chargeSize.x / 2), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x, (int) chargeSize.y, null);
            }
            else if (loadout[chargingWeapon] == SPIKE_WEAPON_NO) {
                chargeSize = Splitter.SIZE.scaledBy(OmegaFight3.lerp(Splitter.MIN_SIZE_PERCENT, 1, getPercentCharged(SPIKE_WEAPON_NO)));
                g.drawImage(Splitter.images[playerNo], (int) (chargeCoord.x - chargeSize.x / 2 * spriteSign), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x * spriteSign, (int) chargeSize.y, null);
            }
        }
    }

    private double getPercentCharged(int weaponNo) {
        return (double) Math.min(shootCharge - BASIC_SHOOT_TIME_LIMIT, CHARGE_TIME[weaponNo]) / (CHARGE_TIME[weaponNo]);
    }

    public void moveAerial(boolean upPressed) {
        // Jumping Eligibility
        if (!upPressed && jumpState % 2 == 0) jumpState++;

        // Jump Cooldown
        if (jumpCounter != JUMP_COOLDOWN) jumpCounter++;

        if (onPlatform == AIRBORNE) {
            // Jump leeway
            if (coyoteCounter != 0) {
                coyoteCounter--;
                if (coyoteCounter == 0 && jumpState < 2) jumpState += 2;
            }

            // Acceleration with speed limit
            if (upPressed && velocity.y < 0) velocity.y = Math.min(maxVelocity.y, velocity.y + 0.5 - velocity.y / maxVelocity.y);
            else velocity.y = Math.min(maxVelocity.y, velocity.y + accel.y);

            // Platform Collision
            int platformNo = checkPlatforms();
            if (platformNo != AIRBORNE) {
                spriteNo = IDLE_SPRITE;
                velocity.y = 0;
                jumpCounter = 0;
                jumpState %= 2;
                onPlatform = platformNo;
                coord.y = getPlatformY(platformNo);
            }
        }
        else {
            // Falling
            if (checkPlatforms() == AIRBORNE) {
                spriteNo = JUMP_SPRITE;
                onPlatform = AIRBORNE;
                coyoteCounter = COYOTE_TIME;
            }
        }
    }

    public void move() {
        if (velocity.x != 0) coord.x += velocity.x;
        if (velocity.y != 0) coord.y += velocity.y;
    }

    public void animateRun() {
        // Change of Sprites
        if (onPlatform != AIRBORNE) {
            frameCounter++;
            // Cycle through running sprites and change to running sprites if not already
            if (spriteNo < FRST_RUN_SPRITE || spriteNo > LAST_RUN_SPRITE) {
                spriteNo = FRST_RUN_SPRITE;
                frameCounter = 0;
            }
            else if (frameCounter >= runFrameFreq) {
                frameCounter = 0;
                if (spriteNo == FRST_RUN_SPRITE) runSign = 1;
                else if (spriteNo== LAST_RUN_SPRITE) runSign = -1;
                spriteNo += runSign;
            }
        }
    }

    public void decelerateRun() {
        if (onPlatform != AIRBORNE && spriteNo != IDLE_SPRITE) spriteNo = IDLE_SPRITE;
        decelerate();
    }

    public void decelerate() {
        if (velocity.x < 0) velocity.x = Math.min(0, velocity.x + 1);
        else if (velocity.x != 0) velocity.x = Math.max(0, velocity.x - 1);
    }

    public int checkPlatforms() {
        for (int i = 0; i != OmegaFight3.stage[OmegaFight3.stageNo].platforms.length; i++) if (OmegaFight3.stage[OmegaFight3.stageNo].platforms[i].landed(coord.x, coord.y, coord.y + velocity.y)) return i;
        return AIRBORNE;
    }

    public void checkState() {
        // Bottom
        if (coord.y > OmegaFight3.SCREEN_SIZE.y + size.y / 2) {
            state = DIED_BOTTOM;
            resetStats(DIED_STAT_RESET);
        }

        // Left
        else if (coord.x < -size.x / 2) {
            state = DIED_LEFT;
            resetStats(DIED_STAT_RESET);
        }

        // Top
        else if (coord.y < -size.y / 2) {
            state = DIED_TOP;
            resetStats(DIED_STAT_RESET);
        }

        // Right
        else if (coord.x > OmegaFight3.SCREEN_SIZE.x + size.x / 2) {
            state = DIED_RIGHT;
            resetStats(DIED_STAT_RESET);
        }
    }

    public void resetStats(int type) {
        frameCounter = 0;
        jumpCounter = 0;
        coyoteCounter = 0;
        shootCharge = 0;
        chargingWeapon = NOT_CHARGING;
        heatCounter = 0;
        runSign = 1;
        if (type == DIED_STAT_RESET) {
            jumpState = 3;
            spriteNo = IDLE_SPRITE;
            spriteSign = OmegaFight3.stage[OmegaFight3.stageNo].spawnSpriteSign[playerNo];
            velocity.x = 0;
            velocity.y= RESPAWN_INITIAL_VELOCITY;
            onPlatform = AIRBORNE;
            OmegaFight3.screenShakeCounter += 30;
            percentShakeCounter = 0;
            skillPts = 0;
            skillPtCounter = 0;
            stunCounter = 0;
            invCounter = RESPAWN_INITIAL_VELOCITY + RESPAWN_TIME_LIMIT + 2;
            // fireCounter = 0;
            // iFramesCounter = 60;
        }
    }

    public void prepareForRespawn() {
        coord.x = OmegaFight3.stage[OmegaFight3.stageNo].spawnCoords[playerNo].x;
        coord.y = -size.y;
        percent = 0;
    }

    public void drawSurge(Graphics2D g2) {
        double rotation = Math.PI / 2 * (state - 1);
        BufferedImage surgeImage = surge[frameCounter / SURGE_FRAME_HZ];
        if (state == DIED_BOTTOM) {
            g2.rotate(rotation, coord.x, OmegaFight3.SCREEN_SIZE.y - SURGE_SIZE.y / 2);
            g2.drawImage(surgeImage, (int) (coord.x - SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - SURGE_SIZE.y), null);
            g2.rotate(-rotation, coord.x, OmegaFight3.SCREEN_SIZE.y - SURGE_SIZE.y / 2);
        }
        else if (state == DIED_LEFT) {
            g2.rotate(rotation, SURGE_SIZE.y / 2, coord.y);
            g2.drawImage(surgeImage, (int) ((SURGE_SIZE.y - SURGE_SIZE.x) / 2), (int) (coord.y - SURGE_SIZE.y / 2), null);
            g2.rotate(-rotation, SURGE_SIZE.y / 2, coord.y);
        }
        else if (state == DIED_TOP) {
            g2.rotate(rotation, coord.x, SURGE_SIZE.y / 2);
            g2.drawImage(surgeImage, (int) (coord.x - SURGE_SIZE.x / 2), 0, null);
            g2.rotate(-rotation, coord.x, SURGE_SIZE.y / 2);
        }
        else if (state == DIED_RIGHT) {
            g2.rotate(rotation, OmegaFight3.SCREEN_SIZE.x - SURGE_SIZE.y / 2, coord.y);
            g2.drawImage(surgeImage, (int) (OmegaFight3.SCREEN_SIZE.x - (SURGE_SIZE.x + SURGE_SIZE.y) / 2), (int) (coord.y - SURGE_SIZE.y / 2), null);
            g2.rotate(-rotation, OmegaFight3.SCREEN_SIZE.x - SURGE_SIZE.y / 2, coord.y);
        }
    }

    public void diePercent(Graphics g) {
        drawStillPercent((int) OmegaFight3.lerp(PERCENT_DISPLAY_Y_COORD + (int) PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.y, OmegaFight3.SCREEN_SIZE.y + BIG_PERCENT_FONT.getSize(), (double) frameCounter / SURGE_TIME), g);
    }

    public void respawnPercent(Graphics g) {
        drawStillPercent((int) OmegaFight3.lerp(PERCENT_DISPLAY_Y_COORD + (int) PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.y, OmegaFight3.SCREEN_SIZE.y + BIG_PERCENT_FONT.getSize(), velocity.y / RESPAWN_INITIAL_VELOCITY), g);
    }

    public void respawn(boolean dwnPressed)  {
        if (velocity.y != 0) {
            velocity.y--;
            coord.y += velocity.y;
        }
        else if (dwnPressed || frameCounter == SURGE_TIME + RESPAWN_PAUSE + RESPAWN_INITIAL_VELOCITY + RESPAWN_TIME_LIMIT) {
            state = ALIVE_STATE;
            frameCounter = 0;
            spriteNo = JUMP_SPRITE;
            onPlatform = AIRBORNE;
            invCounter = RESPAWN_INV_LEN;
        }
    }

    public void recoil(double amount) {
        if (spriteSign == LEFT_SIGN) velocity.x += amount;
        else if (spriteSign == RIGHT_SIGN) velocity.x -= amount;
    }

    public void hurt(double damage) {
        percentShakeCounter = PERCENT_SHAKE_TIME;
        percent += damage;
    }

    public void hurt(double damage, double knockback, Coord enemyCoord, double dir, double kbSpread, int screenShake) {
        hurt(damage);

        int platformNo = checkPlatforms();
        if (platformNo != AIRBORNE) coord.y = getPlatformY(platformNo);

        spriteNo = HURT_SPRITE;
        
        resetStats(GENERAL_STAT_RESET);
        OmegaFight3.screenShakeCounter += screenShake;

        // Speed calculations
        knockback *= (percent / Math.pow(10, PERCENT_NUM_DECIMALS) / 100 + 1);
        double angle = Math.atan2(coord.y - KB_COORD_Y_OFFSET - enemyCoord.y, coord.x - enemyCoord.x);
        double minAngle = dir - kbSpread;
        double dMinAngle = OmegaFight3.normalizeAngle(minAngle - angle);
        double maxAngle = dir + kbSpread;
        double dMaxAngle = OmegaFight3.normalizeAngle(maxAngle - angle);
        if (dMinAngle > 0 || dMaxAngle < 0) {
            if (Math.abs(dMinAngle) < Math.abs(dMaxAngle)) angle = minAngle;
            else angle = maxAngle;
        }
        velocity.x += Math.cos(angle) * knockback;
        velocity.y += Math.sin(angle) * knockback;
        stunCounter = (int) Math.pow(knockback, STUN_REDUCTION);

        if (enemyCoord.x != coord.x) {
            spriteSign = (int) -Math.signum(Math.cos(angle));
            if (spriteSign == 0) spriteSign = RIGHT_SIGN;
        }
    }

    public void knockback() {
        decelerate();
        
        velocity.y += KB_GRAVITY;
        int platformNo = checkPlatforms();
        if (platformNo != AIRBORNE) {
            coord.y = getPlatformY(platformNo);
            if (velocity.y >= BOUNCE_MIN_VEL_Y) {
                hurt(velocity.y);
                velocity.y *= -1;
            }
            else velocity.y = 0;
        }

        stunCounter--;
        if (stunCounter == 0) {
            if (platformNo == AIRBORNE) {
                spriteNo = JUMP_SPRITE;
                onPlatform = AIRBORNE;
                velocity.y = Math.min(velocity.y, maxVelocity.y);
                if (jumpState < 2) jumpState = 3;
            }
            else {
                spriteNo = IDLE_SPRITE;
                onPlatform = platformNo;
                jumpState = 1;
            }
        }
    }

    public void countInv() {
        if (invCounter != VULNERABLE) invCounter--;
    }

    public double getPlatformY(int platformNo) {
        return OmegaFight3.stage[OmegaFight3.stageNo].platforms[platformNo].y;
    }

    public void shakePercent() {
        if (percentShakeCounter != 0) {
            if (percentShakeCounter % PERCENT_SHAKE_HZ == 0) {
                if (percentDigitShake.length < ((percent / (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "." + (percent % (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "%").length())
                    percentDigitShake = new Coord[((percent / (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "." + (percent % (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "%").length()];
                for (int i = 0; i < percentDigitShake.length; i++) {
                    percentDigitShake[i] = new Coord(OmegaFight3.randomSign() * percentShakeCounter * Math.random() * PERCENT_SHAKE_MULTIPLIER,
                    OmegaFight3.randomSign() * percentShakeCounter * Math.random() * PERCENT_SHAKE_MULTIPLIER);
                }
            }
            percentShakeCounter--;
        }
    }

    public void drawSkillPt(int skillPtNo, double amt, Graphics g) {
        g.fillRect(percentDisplayX + (int) (PERCENT_DISPLAY_SIZE_TO_SKILL_PTS_COORD.x + skillPtNo * (SKILL_PTS_SPACE + SKILL_PTS_SIZE.x)),
            PERCENT_DISPLAY_Y_COORD + (int) (PERCENT_DISPLAY_SIZE_TO_SKILL_PTS_COORD.y), (int) (SKILL_PTS_SIZE.x * amt), (int) (SKILL_PTS_SIZE.y));
    }

    public int drawBigPercent(int offSet, int y, Graphics g) {
        g.setFont(BIG_PERCENT_FONT);
        g.drawString((percent / (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "", percentDisplayX + (int) PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.x + offSet, y + offSet);
        return g.getFontMetrics().stringWidth((percent / (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "");
    }

    public void drawSmlPercent(int offSet, int bigPercentWidth, int y, Graphics g) {
        g.setFont(SML_PERCENT_FONT);
        g.drawString("." + (percent % (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "%", percentDisplayX + (int) PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.x + offSet + bigPercentWidth, y + offSet);
    }

    public void drawStillPercent(int y, Graphics g) {
        // Text Shadow
        g.setColor(Color.BLACK);
        int bigPercentWidth = drawBigPercent(PERCENT_SHADOW_OFFSET, y, g);
        drawSmlPercent(PERCENT_SHADOW_OFFSET, bigPercentWidth, y, g);

        // Actual percent
        double percentColor = Math.min(percent, (double) MAX_PERCENT_COLOR_THRESHOLD) / MAX_PERCENT_COLOR_THRESHOLD;
        g.setColor(new Color(OmegaFight3.MAX_RGB_VAL - (int) ((OmegaFight3.MAX_RGB_VAL - MAX_PERCENT_COLOR.getRed()) * percentColor),
        OmegaFight3.MAX_RGB_VAL - (int) ((OmegaFight3.MAX_RGB_VAL - MAX_PERCENT_COLOR.getGreen()) * percentColor),
        OmegaFight3.MAX_RGB_VAL - (int) ((OmegaFight3.MAX_RGB_VAL - MAX_PERCENT_COLOR.getBlue()) * percentColor)));
        drawBigPercent(0, y, g);
        drawSmlPercent(0, bigPercentWidth, y, g);
    }

    public void drawShakingPercent(String bigPercent, String smlPercent, Graphics g) {
        g.setColor(Color.RED);
        int percentWidth = 0;
        g.setFont(BIG_PERCENT_FONT);
        for (int i = 0; i != bigPercent.length(); i++) {
            g.drawString(bigPercent.substring(i, i + 1), percentDisplayX + (int) (PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.x + percentDigitShake[i].x) + percentWidth,
            PERCENT_DISPLAY_Y_COORD + (int) (PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.y + percentDigitShake[i].y));
            percentWidth += g.getFontMetrics().stringWidth(bigPercent.substring(i, i + 1));
        }
        g.setFont(SML_PERCENT_FONT);
        for (int i = bigPercent.length(); i != bigPercent.length() + smlPercent.length(); i++) {
            g.drawString(smlPercent.substring(i - bigPercent.length(), i + 1 - bigPercent.length()), percentDisplayX + (int) (PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.x + percentDigitShake[i].x) + percentWidth,
            PERCENT_DISPLAY_Y_COORD + (int) (PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.y + percentDigitShake[i].y));
            percentWidth += g.getFontMetrics().stringWidth(smlPercent.substring(i - bigPercent.length(), i + 1 - bigPercent.length()));
        }
    }

    public void drawHUD(Graphics g) {
        g.drawImage(percentDisplay, percentDisplayX, PERCENT_DISPLAY_Y_COORD, null);
        
        // Full skill points
        g.setColor(Color.WHITE);
        int skillPtNo = 0;
        for (; skillPtNo != skillPts / ONES_PER_SKILL_PT; skillPtNo++) drawSkillPt(skillPtNo, 1.0, g);

        // Incomplete skill point
        g.setColor(Color.GRAY);
        drawSkillPt(skillPtNo, skillPts % ONES_PER_SKILL_PT / ((double) ONES_PER_SKILL_PT), g);
    }

    public void drawPercent(Graphics g) {
        if (percentShakeCounter == 0) {
            drawStillPercent(PERCENT_DISPLAY_Y_COORD + (int) PERCENT_DISPLAY_SIZE_TO_PERCENT_COORD.y, g);
        }
        else {
            drawShakingPercent((percent / (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "", "." + (percent % (int) Math.pow(10, PERCENT_NUM_DECIMALS)) + "%", g);
        }
    }
    
    public void draw(Graphics g) {
        if (invCounter % INV_BLINK_CYCLE_LEN < INV_BLINK_CYCLE_LEN - INV_BLINK_LEN) {
            if (spriteSign == 1) g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2), (int) (coord.y - size.y / 2), null);
            else g.drawImage(sprite[spriteNo], (int) (coord.x - size.x / 2 + size.x), (int) (coord.y - size.y / 2), (int) -size.x, (int) size.y, null);
        }
    }
}