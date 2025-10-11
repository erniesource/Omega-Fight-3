package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Omegaman extends Char {
    // Sprite Constants
    public static final int IDLE_SPRITE = 0;
    public static final int FRST_RUN_SPRITE = 1;
    public static final int NUM_RUN_SPRITE = 3;
    public static final int LAST_RUN_SPRITE = FRST_RUN_SPRITE + NUM_RUN_SPRITE - 1;
    public static final int JUMP_SPRITE = 4;
    public static final int HURT_SPRITE = 5;
    public static final int DASH_SPRITE = 6;
    public static final Coord SIZE = new Coord(100);
    public static final double SIZE_TO_HITBOX = 0.5;
    public static final double SIZE_TO_HURTBOX = 0.9;
    public static final double SIZE_TO_FIRE = 0.8;

    // Skill Points Constants
    public static final int ONES_PER_SKILL_PT = 60;
    public static final int MAX_SKILL_PTS = 3 * ONES_PER_SKILL_PT;
    public static final int SKILL_PT_REGEN_HZ = OmegaFight3.DEV_MODE? 1: 10;
    public static final double PVP_SKILL_PT_BASIC_MULT = 2;

    // Movement Constants
    public static final int AIRBORNE = -1;
    public static final int JUMP_COOLDOWN = 3;
    public static final int JUMPS_ALLOWED = 2;
    public static final int COYOTE_TIME = 5;
    public static final double MOVE_DECEL_DEC = 0.75;
    public static final double MIN_ACCEL_DWN = 0.5;
    public static final int NUM_DASH_DIRS = 2;
    public static final int TAPS_TO_DASH = 2;
    public static final int DASH_TAP_TIME = 8;
    public static final int NOT_DASHING = -1;
    public static final int DASH_LFT = 0;
    public static final int DASH_RIT = 1;
    public static final double DASH_SKILL_PTS = 5.0 / 12;
    public static final int DASH_TIME = 15;
    public static final double DASH_SPD = 16;
    public static final int DASH_SMOKE_HZ = 5;
    public static final double SIZE_TO_DASH_SMOKE = 0.5;
    public static final double SOFT_COLLISION_ACCEL = 2;

    // Shooting Constants
    public static final int BULLET_WEAPON_NO = 0;
    public static final int SHOTGUN_WEAPON_NO = 1;
    public static final int SPAMMER_WEAPON_NO = 2;
    public static final int SNIPER_WEAPON_NO = 3;
    public static final int BOOMER_WEAPON_NO = 4;
    public static final int SPIKE_WEAPON_NO = 5;
    public static final int FIREBALL_WEAPON_NO = 6;
    public static final int GLUE_BOMB_WEAPON_NO = 7;
    public static final int[] BASIC_SHOT_HEAT = {20, 30, 60, 60, 20, 30, 35, 35};
    public static final int[] CHARGED_SHOT_HEAT = {50, 50, 35, 80, 100, 50, 80, 100};
    public static final int[] CHARGE_TIME = {50, 60, 50, 100, 100, 50, 70, 100};
    public static final double[] CHARGE_MIN_PERC = {0.4, 0.2, 0.5, 0.2, 0.3, 0.5, 0.4, 0.4};
    public static final int BASIC_SHOOT_TIME_LIMIT = 20;
    public static final int LOADOUT_NUM_WEAPONS = 2;
    public static final int MAX_SHOOT_CHARGE = 300;
    public static final int NOT_CHARGING = -1;
    public static final int NO_COOLING_WEAPON = -1;

    // Projectile Offsets
    public static final int IDLE_PROJ_Y_OFFSET = 7;
    public static final int JUMP_PROJ_Y_OFFSET = -13;
    public static final int PROJ_SPAWN_X_OFFSET = 10;

    // KB Constants
    public static final int NOT_STUNNED = 0;
    public static final int KB_COORD_Y_OFFSET = 50;
    public static final double KB_GRAVITY = 1.25;
    public static final double BOUNCE_MIN_VEL_Y = 5;
    public static final double STUN_REDUCTION = 0.9;
    public static final double DIR_INP_VEL = 2;

    // Kamikaze constants
    public static final double KAMIKAZE_DMG = 1 * Math.pow(10, Omegaman.PERC_NUM_DECIMALS);
    public static final double KAMIKAZE_KB = 10;

    // HUD Constants
    public static final int PERC_DISPLAY_Y_COORD = 790;
    public static final Coord PERC_DISPLAY_SIZE = new Coord(400, 150);
    public static final int PERC_NUM_DECIMALS = 1;
    public static final int PERC_MULT = (int) Math.pow(10, PERC_NUM_DECIMALS);
    public static final int MAX_PERC_COLOR_THRESHOLD = 150 * PERC_MULT;
    public static final int PERC_SHAKE_HZ = 2;
    public static final int PERC_SHADOW_OFFSET = 3;
    public static final int NUM_PLAYERS = 2;
    public static final int PERC_DISPLAY_DIST = (int) ((OmegaFight3.SCREEN_SIZE.x - PERC_DISPLAY_SIZE.x * NUM_PLAYERS) / (NUM_PLAYERS + 1));
    public static final Coord PERC_DISPLAY_SIZE_TO_SKILL_PTS_COORD = PERC_DISPLAY_SIZE.scaledBy(new Coord(0.5, 19.0 / 30));
    public static final double SKILL_PTS_SPACE = 0.0125 * PERC_DISPLAY_SIZE.x;
    public static final Coord SKILL_PTS_SIZE = new Coord((0.475 * PERC_DISPLAY_SIZE.x - SKILL_PTS_SPACE * (MAX_SKILL_PTS / ONES_PER_SKILL_PT - 1)) / (MAX_SKILL_PTS / ONES_PER_SKILL_PT), (1.0 / 6.0) * PERC_DISPLAY_SIZE.y);
    public static final Coord PERC_DISPLAY_SIZE_TO_PERC_COORD = PERC_DISPLAY_SIZE.scaledBy(0.5);
    public static final int PERC_SHAKE_TIME = 24;
    public static final double PERC_SHAKE_MULT = 2;
    public static final Coord FACE_SIZE = new Coord(42, 46);
    public static final int FACE_SPACING = 10;

    // Respawn Constants
    public static final int RESPAWN_PAUSE = 120;
    public static final int RESPAWN_INIT_VELOCITY = 25;
    public static final int RESPAWN_TIME_LIMIT = 120;

    // Surge Constants
    public static final int ALIVE_STATE = 0;
    public static final int DIED_BOT = 1;
    public static final int DIED_LFT = 3;
    public static final int DIED_TOP = 5;
    public static final int DIED_RIT = 7;
    public static final int DIED_BOTLFT = 2;
    public static final int DIED_TOPLFT = 4;
    public static final int DIED_TOPRIT = 6;
    public static final int DIED_BOTRIT = 8;
    public static final int MAX_DIST_TO_DIAGONAL = 50;

    // Stat reset constants
    public static final int DIED_STAT_RESET = 0;
    public static final int STUN_STAT_RESET = 1;
    public static final int DASH_STAT_RESET = 2;
    public static final int DIE_SCREENSHAKE = 30;

    // Fonts and Colors
    public static final Font BIG_PERC_FONT = new Font("Impact", Font.PLAIN, (int) (PERC_DISPLAY_SIZE.y * 8 / 15));
    public static final Font SML_PERC_FONT = new Font("Impact", Font.PLAIN, BIG_PERC_FONT.getSize() * 2 / 5);
    public static final Color MAX_PERC_COLOR = new Color(205, 46, 43);

    // Invincibility Constants
    public static final int INV_BLINK_CYCLE_LEN = 10;
    public static final int INV_BLINK_LEN = 3;
    public static final int RESPAWN_INV_LEN = 60;
    public static final int VULNERABLE = 0;

    // Stat constants
    public static final int NUM_STATS = 7;
    public static final int DIST_MOVED_NO = 0;
    public static final int TIMES_JUMPED_NO = 1;
    public static final int MAX_PERC_NO = 2;
    public static final int LIVES_LEFT_NO = 3;
    public static final int SKILL_PTS_USED_NO = 4;
    public static final int DMG_TO_OMEGAMAN = 5;
    public static final int DMG_TO_BOSS = 6;

    // Directory constants
    public static final String HUD_DIR = "HUD/";

    // Movement stats
    public int runSign = 1; // 1: Positive, -1: Negative
    public int lftKey, ritKey, upKey, dwnKey, shtKeys[];
    public Coord maxVelocity = new Coord(8, 22);
    public Coord accel = new Coord(1, 2);
    public int runFrameFreq = 6;

    // Jumping stats
    public int jumpCounter; // Jump cooldown (3 frames or 1/20 of a second)
    public int coyoteCounter; // Counter for Coyote time
    public int jumpState = 1; // Even number (Pressing on the i / 2 - th jump), Odd number (Not pressing on the i / 2 - th jump)
    public int onPlatform; // -1: Not on plaform (airborne), 0+ (which number platform player is on)
    public Deque<Wake> wakeQ = new LinkedList<>();

    // Dash stats
    public int[] dashButtonCounter = new int[NUM_DASH_DIRS];
    public int[] dashState = new int[NUM_DASH_DIRS];
    public int dashing = NOT_DASHING;
    public int dashCounter;

    // Weapon stats
    public int shootCharge;
    public int heatCounter;
    public int chargingWeapon = NOT_CHARGING;
    public int coolingWeapon = NO_COOLING_WEAPON;
    public int[] loadout;

    // Skill point stats
    public int skillPts = ONES_PER_SKILL_PT * 3 / 2;
    public int skillPtCounter;

    // KB stats
    public int stunCounter;

    // Combat stats
    public int livesLeft;
    public int percent;
    public int percentShakeCounter;
    public Coord[] percentDigitShake = new Coord[4];
    public int percentDisplayX;
    public int invCounter;

    // Images
    public static BufferedImage[][] sprite = new BufferedImage[NUM_PLAYERS][7];
    public static BufferedImage[] percentDisplay = new BufferedImage[NUM_PLAYERS];
    public static BufferedImage[] face = new BufferedImage[NUM_PLAYERS];
    public static BufferedImage[][] surge = new BufferedImage[NUM_PLAYERS][OmegaFight3.NUM_SURGE_IMAGES];

    // Other stats
    public double[] stats = new double[NUM_STATS];
    public int playerNo;

    // Constructor
    public Omegaman(int playerNo, Coord coord, Coord size, int spriteSign, int onPlatform, int livesLeft,int[] controls, int[] shtKeys, int[] loadout) throws IOException {
        // Initialize character variables
        super(coord, size, size.scaledBy(SIZE_TO_HITBOX), size.scaledBy(SIZE_TO_HURTBOX), SIZE_TO_FIRE, IDLE_SPRITE, spriteSign, 0, ALIVE_STATE);

        // Initialize player variables
        this.playerNo = playerNo;
        this.onPlatform = onPlatform;
        this.livesLeft = livesLeft;
        this.loadout = loadout;
        this.shtKeys = shtKeys;

        // Assign control keys
        lftKey = controls[0];
        ritKey = controls[1];
        upKey = controls[2];
        dwnKey = controls[3];

        // Percent related stuff
        percentDisplayX = PERC_DISPLAY_DIST + (int) (PERC_DISPLAY_SIZE.x + PERC_DISPLAY_DIST) * playerNo;
    }

    // Description: This methods calculates the control in the x-direction
    public void controlX(boolean lftPressed, boolean ritPressed) {
        // Left moving
        if (lftPressed) {
            // Deccelaration
            if (ritPressed) decelerateRun();

            // Acceleration
            else {
                if (velocity.x > -maxVelocity.x) velocity.x = Math.max(-maxVelocity.x, velocity.x - accel.x);
                else if (velocity.x < -maxVelocity.x) decelerate(MOVE_DECEL_DEC * accel.x);

                animateRun();

                // Change to correct direction
                if (spriteSign == OmegaFight3.RIT_SIGN) spriteSign = OmegaFight3.LFT_SIGN;
            }
        }

        // Right moving
        else if (ritPressed) {
            // Acceleration
            if (velocity.x < maxVelocity.x) velocity.x = Math.min(maxVelocity.x, velocity.x + accel.x);
            else if (velocity.x > maxVelocity.x) decelerate(MOVE_DECEL_DEC * accel.x);

            animateRun();

            // Change to correct direction
            if (spriteSign == OmegaFight3.LFT_SIGN) spriteSign = OmegaFight3.RIT_SIGN;
        }

        // Idle
        else decelerateRun();
    }

    public void controlDash(boolean[] pressed) {
        if (dashing == NOT_DASHING) {
            for (int i = 0; i != NUM_DASH_DIRS; i++) {
                int temp = controlDashDir(i, pressed[i]);
                if (temp != NOT_DASHING) {
                    if (dashing != NOT_DASHING) {
                        dashing = NOT_DASHING;
                    }
                    else {
                        dashing = temp;
                    }
                }
            }
            if (dashing != NOT_DASHING) {
                if (skillPts >= DASH_SKILL_PTS * ONES_PER_SKILL_PT && stunCounter == NOT_STUNNED) {
                    if (dashing == DASH_LFT) {
                        spriteSign = OmegaFight3.LFT_SIGN;
                        velocity = new Coord(DASH_SPD * OmegaFight3.LFT_SIGN, 0);
                    }
                    else if (dashing == DASH_RIT) {
                        spriteSign = OmegaFight3.RIT_SIGN;
                        velocity = new Coord(DASH_SPD * OmegaFight3.RIT_SIGN, 0);
                    }
                    resetStats(DASH_STAT_RESET);
                    skillPts -= (int) (DASH_SKILL_PTS * ONES_PER_SKILL_PT);
                    addToStat(SKILL_PTS_USED_NO, DASH_SKILL_PTS);
                    spriteNo = DASH_SPRITE;
                    dashCounter = DASH_TIME;
                    invCounter = DASH_TIME;
                    OmegaFight3.play(Boomer.whoosh);
                }
                else {
                    dashing = NOT_DASHING;
                }
            }
        }
    }

    private int controlDashDir(int idx, boolean pressed) {
        if (!pressed) {
            if (dashState[idx] % 2 == 1) {
                updateDashState(idx);
            }
        }
        else {
            if (dashState[idx] % 2 == 0) {
                updateDashState(idx);
            }
            if (dashState[idx] == TAPS_TO_DASH * 2 - 1) {
                dashState[idx] = 0;
                dashButtonCounter[idx] = 0;
                return idx;
            }
        }
        if (dashState[idx] != 0) dashButtonCounter[idx]++;
        return NOT_DASHING;
    }

    private void updateDashState(int idx) {
        if (dashButtonCounter[idx] <= DASH_TAP_TIME) {
            dashState[idx]++;
        }
        else {
            dashState[idx] = 0;
        }
        dashButtonCounter[idx] = 0;
    }

    public void dash() {
        // smoke
        if (dashCounter % DASH_SMOKE_HZ == 0) smokeQ.add(new Smoke(coord.copy(), size.scaledBy(SIZE_TO_DASH_SMOKE))); // random-ish smoke?
        dashCounter--;
        if (dashCounter == 0) {
            if (dashing == DASH_LFT) {
                velocity.x = maxVelocity.x * OmegaFight3.LFT_SIGN;
            }
            else if (dashing == DASH_RIT) {
                velocity.x = maxVelocity.x * OmegaFight3.RIT_SIGN;
            }
            jumpState = 3;
            dashing = NOT_DASHING;
            recover();
        }
    }

    // Description: This method calcates the controls in the Y-direction
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
            if (jumpState != 1) {
                wakeQ.add(new Wake(new Coord(coord.x, coord.y + size.y / 2 + velocity.y), size.x));
            }
            jumpState++;
            addToStat(TIMES_JUMPED_NO, 1);
        }

        // Dropping
        else if (dwnPressed && jumpCounter == JUMP_COOLDOWN && onPlatform != AIRBORNE && !OmegaFight3.stage[OmegaFight3.stageNo].platforms[onPlatform].isMain) {
            coord.y++;
            spriteNo = JUMP_SPRITE;
            onPlatform = AIRBORNE;
        }
    }

    // Description: This method passively regenerates skill points
    public void regenSkillPts() {
        if (skillPts != MAX_SKILL_PTS) {
            skillPtCounter = (skillPtCounter + 1) % SKILL_PT_REGEN_HZ;
            if (skillPtCounter == 0) skillPts++;
        }
    }

    // Description: This method calculates the shooting controls
    public void controlShoot(HashSet<Integer> pressedKeys) {
        Coord newProjCoord = new Coord(coord.x + (size.x / 2 - PROJ_SPAWN_X_OFFSET) * spriteSign, coord.y + (onPlatform == -1? JUMP_PROJ_Y_OFFSET : IDLE_PROJ_Y_OFFSET));

        // Blaster cooling down (can't shoot)
        if (heatCounter != 0) {
            heatCounter--;
            if (coolingWeapon == SPAMMER_WEAPON_NO * 2) {
                // Fire spammer based on Hz

                if (heatCounter > Spammer.LAST_PROJ_HEAT && (heatCounter - Spammer.LAST_PROJ_HEAT) % Spammer.PROJ_HZ == 0)
                OmegaFight3.projectiles.add(new Spammer(this, newProjCoord, OmegaFight3.signToRadians(spriteSign) - Spammer.SPREAD + Math.random() * Spammer.SPREAD * 2));
            }
            if (heatCounter == 0) {
                coolingWeapon = NO_COOLING_WEAPON;
            }
        }

        // Can shoot
        else {
            for (int i = 0; i != LOADOUT_NUM_WEAPONS; i++) {
                if ((chargingWeapon == NOT_CHARGING) || chargingWeapon == i) {
                    // If released key or time limit reach, fire weapon
                    if (!pressedKeys.contains(shtKeys[i]) || shootCharge == MAX_SHOOT_CHARGE) {
                        if (shootCharge != 0) {
                            // Fire basic
                            if (shootCharge <= BASIC_SHOOT_TIME_LIMIT || skillPts < ONES_PER_SKILL_PT) {
                                // Bullet
                                if (loadout[i] == BULLET_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new Bullet(this, newProjCoord, OmegaFight3.signToRadians(spriteSign)));
                                    
                                    // Sound effect
                                    OmegaFight3.play(Bullet.pew);
                                }

                                // SHotgun
                                else if (loadout[i] == SHOTGUN_WEAPON_NO) {
                                    for (int j = 0; j != Shotgun.NUM_SHOTS; j++) {
                                        OmegaFight3.projectiles.add(new Shotgun(this, newProjCoord.copy(), OmegaFight3.signToRadians(spriteSign) - Shotgun.SPREAD + j * (Shotgun.SPREAD * 2 / (Shotgun.NUM_SHOTS - 1))));
                                    }

                                    // Sound effect
                                    OmegaFight3.play(Shotgun.bang);
                                }

                                else if (loadout[i] == SPAMMER_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new Spammer(this, newProjCoord, OmegaFight3.signToRadians(spriteSign) - Spammer.SPREAD + Math.random() * Spammer.SPREAD * 2));
                                    
                                    // Sound effect
                                    OmegaFight3.play(Spammer.ratatat);
                                }

                                // SNiper
                                else if (loadout[i] == SNIPER_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new Sniper(this, newProjCoord, OmegaFight3.signToRadians(spriteSign)));
                                    
                                    // Recoil
                                    recoil(Sniper.RECOIL);

                                    // Sound effect
                                    OmegaFight3.play(Sniper.baw);
                                }

                                // Boomer
                                else if (loadout[i] == BOOMER_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new Boomer(this, newProjCoord, OmegaFight3.signToRadians(spriteSign)));

                                    // Sound effect
                                    OmegaFight3.play(Boomer.whoosh);
                                }

                                // Spike
                                else if (loadout[i] == SPIKE_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new Spike(this, newProjCoord, OmegaFight3.signToRadians(spriteSign)));
                                }

                                else if (loadout[i] == FIREBALL_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new Fireball(this, newProjCoord, OmegaFight3.signToRadians(spriteSign)));

                                    // Sound effect
                                    OmegaFight3.play(Fireball.foom);
                                }

                                else if (loadout[i] == GLUE_BOMB_WEAPON_NO) {
                                    OmegaFight3.projectiles.add(new GlueBomb(this, newProjCoord, OmegaFight3.signToRadians(spriteSign)));
                                }

                                // update shoot cooldown
                                heatCounter = BASIC_SHOT_HEAT[loadout[i]];
                                coolingWeapon = loadout[i] * 2;
                            }

                            // Fire charged
                            else {
                                double percentCharged = 0;

                                // Rocket
                                if (loadout[i] == BULLET_WEAPON_NO) {
                                    percentCharged = getPercentCharged(BULLET_WEAPON_NO);
                                    OmegaFight3.projectiles.add(new Rocket(this, newProjCoord, OmegaFight3.signToRadians(spriteSign), percentCharged));
                                
                                    // Recoil
                                    recoil(Rocket.RECOIL * percentCharged);
                                }

                                // Firework
                                else if (loadout[i] == SHOTGUN_WEAPON_NO) {
                                    percentCharged = getPercentCharged(SHOTGUN_WEAPON_NO);
                                    for (int j = 0; j != Firework.NUM_SHOTS; j++) {
                                        OmegaFight3.projectiles.add(new Firework(this, newProjCoord.copy(), Math.PI * 2 / Firework.NUM_SHOTS * j, percentCharged));
                                    }

                                    // Sound effect
                                    OmegaFight3.play(Firework.pow);
                                }

                                // Missile
                                else if (loadout[i] == SPAMMER_WEAPON_NO) {
                                    percentCharged = getPercentCharged(SPAMMER_WEAPON_NO);
                                    OmegaFight3.projectiles.add(new Missile(this, newProjCoord, OmegaFight3.signToRadians(spriteSign), spriteSign, percentCharged));
                                    
                                    // Recoil
                                    recoil(Missile.RECOIL * percentCharged);

                                    // Sound effect
                                    OmegaFight3.play(Missile.fwoosh);
                                }

                                // Laser
                                else if (loadout[i] == SNIPER_WEAPON_NO) {
                                    percentCharged = getPercentCharged(SNIPER_WEAPON_NO);
                                    OmegaFight3.projectiles.add(new Laser(this, new Coord((newProjCoord.x + OmegaFight3.SCREEN_SIZE.x * (spriteSign + 1) / 2) / 2, newProjCoord.y), Math.abs(newProjCoord.x - OmegaFight3.SCREEN_SIZE.x * (spriteSign + 1) / 2), OmegaFight3.signToRadians(spriteSign), percentCharged));

                                    // Recoil
                                    recoil(Laser.RECOIL * percentCharged);

                                    // Sound effect
                                    OmegaFight3.play(Laser.bew);
                                }

                                // Bouncer
                                else if (loadout[i] == BOOMER_WEAPON_NO) {
                                    percentCharged = getPercentCharged(BOOMER_WEAPON_NO);
                                    OmegaFight3.projectiles.add(new Bouncer(this, newProjCoord, OmegaFight3.signToRadians(spriteSign), percentCharged));

                                    // Sound effect
                                    OmegaFight3.play(Bouncer.vrrr);
                                }

                                // Splitter
                                else if (loadout[i] == SPIKE_WEAPON_NO) {
                                    percentCharged = getPercentCharged(SPIKE_WEAPON_NO);
                                    OmegaFight3.projectiles.add(new Splitter(this, newProjCoord, OmegaFight3.signToRadians(spriteSign), percentCharged));
                                }

                                // Phoenix
                                else if (loadout[i] == FIREBALL_WEAPON_NO) {
                                    percentCharged = getPercentCharged(FIREBALL_WEAPON_NO);
                                    OmegaFight3.projectiles.add(new Phoenix(this, OmegaFight3.signToRadians(spriteSign), percentCharged));

                                    // Sound effect
                                    OmegaFight3.play(Phoenix.caw);
                                }

                                // Nani
                                else if (loadout[i] == GLUE_BOMB_WEAPON_NO) {
                                    percentCharged = getPercentCharged(GLUE_BOMB_WEAPON_NO);
                                    for (int j = 0; j != Spark.NUM_SHOTS; j++) {
                                        OmegaFight3.projectiles.add(new Spark(this, newProjCoord.copy(), OmegaFight3.signToRadians(spriteSign), percentCharged, j));
                                    }

                                    // Recoil
                                    recoil(Spark.RECOIL * percentCharged);
                                }

                                // Update stats
                                skillPts -= (percentCharged < 0.5? ONES_PER_SKILL_PT / 2: ONES_PER_SKILL_PT);
                                addToStat(SKILL_PTS_USED_NO, (percentCharged < 0.5? 0.5: 1));
                                heatCounter = CHARGED_SHOT_HEAT[loadout[i]];
                                coolingWeapon = loadout[i] * 2 + 1;
                            }

                            // Update some more stats
                            shootCharge = 0;
                            chargingWeapon = NOT_CHARGING;
                        }
                    }

                    // Charge weapon
                    else if (skillPts >= ONES_PER_SKILL_PT || shootCharge != BASIC_SHOOT_TIME_LIMIT) {
                        if (chargingWeapon == NOT_CHARGING) chargingWeapon = i;
                        shootCharge++;
                    }
                }
            }
        }
    }

    public void checkBulletCombo() {
        if (Bullet.combo[playerNo] != 0) {
            Bullet.comboEndCounter[playerNo]++;
            if (Bullet.comboEndCounter[playerNo] == Bullet.TIME_TO_COMBO) {
                Bullet.combo[playerNo] = 0;
                Bullet.comboEndCounter[playerNo] = 0;
            }
        }
    }

    public void processWakes() {
        // Process smoke trails
        for (Wake wake: wakeQ) {
            wake.frameCounter++;
        }

        // Delete dead smoke
        while (!wakeQ.isEmpty() && wakeQ.getFirst().frameCounter == Wake.WAKE_LEN) {
            wakeQ.removeFirst();
        }
    }

    // Description: This method draws the smoke trails of the player
    public void drawWakes(Graphics2D g2) {
        for (Wake wake: wakeQ) {
            wake.draw(g2);
        }
    }

    // Description: This method draws the charging weapon on the player's blaster
    public void drawCharge(Graphics g) {
        if (shootCharge > BASIC_SHOOT_TIME_LIMIT) {
            Coord chargeCoord = new Coord(coord.x + size.x / 2 * spriteSign, coord.y + (onPlatform == -1? JUMP_PROJ_Y_OFFSET: IDLE_PROJ_Y_OFFSET));
            Coord chargeSize;

            // Rocket
            if (loadout[chargingWeapon] == BULLET_WEAPON_NO) {
                chargeSize = Rocket.SIZE.scaledBy(getPercentCharged(BULLET_WEAPON_NO));
                chargeCoord = chargeCoord.add(chargeSize.scaledBy(-0.5));
                g.drawImage(Rocket.images[playerNo], (int) (chargeCoord.x), (int) (chargeCoord.y), (int) chargeSize.x, (int) chargeSize.y, null);
            }

            // Firework
            else if (loadout[chargingWeapon] == SHOTGUN_WEAPON_NO) {
                chargeSize = Firework.CHARGE_SIZE.scaledBy(getPercentCharged(SHOTGUN_WEAPON_NO));
                chargeCoord = chargeCoord.add(chargeSize.scaledBy(-0.5));
                g.drawImage(Firework.chargingImages[playerNo], (int) (chargeCoord.x), (int) (chargeCoord.y), (int) chargeSize.x, (int) chargeSize.y, null);
            }

            // Missile
            else if (loadout[chargingWeapon] == SPAMMER_WEAPON_NO) {
                chargeSize = Missile.SIZE.scaledBy(getPercentCharged(SPAMMER_WEAPON_NO));
                g.drawImage(Missile.images[playerNo], (int) (chargeCoord.x - chargeSize.x / 2 * spriteSign), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x * spriteSign, (int) chargeSize.y, null);
            }

            // Laser
            else if (loadout[chargingWeapon] == SNIPER_WEAPON_NO) {
                chargeSize = (new Coord(Laser.SIZE_Y * Laser.BEAM_SIZE_Y_TO_BALL.x, Laser.SIZE_Y * Laser.BEAM_SIZE_Y_TO_BALL.y)).scaledBy(getPercentCharged(SNIPER_WEAPON_NO));
                g.drawImage(Laser.ball, (int) (chargeCoord.x - chargeSize.x / 2 * spriteSign), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x * spriteSign, (int) chargeSize.y, null);
            }

            // Bouncer
            else if (loadout[chargingWeapon] == BOOMER_WEAPON_NO) {
                chargeSize = Bouncer.SIZE.scaledBy(getPercentCharged(BOOMER_WEAPON_NO));
                chargeCoord = chargeCoord.add(chargeSize.scaledBy(-0.5));
                g.drawImage(Bouncer.images[playerNo], (int) (chargeCoord.x), (int) (chargeCoord.y), (int) chargeSize.x, (int) chargeSize.y, null);
            }

            // Splitter
            else if (loadout[chargingWeapon] == SPIKE_WEAPON_NO) {
                chargeSize = Splitter.SIZE.scaledBy(getPercentCharged(SPIKE_WEAPON_NO));
                g.drawImage(Splitter.images[playerNo], (int) (chargeCoord.x - chargeSize.x / 2 * spriteSign), (int) (chargeCoord.y - chargeSize.y / 2), (int) chargeSize.x * spriteSign, (int) chargeSize.y, null);
            }

            // Phoenix
            else if (loadout[chargingWeapon] == FIREBALL_WEAPON_NO) {
                chargeSize = Phoenix.CHARGE_SIZE.scaledBy(getPercentCharged(FIREBALL_WEAPON_NO));
                chargeCoord = chargeCoord.add(chargeSize.scaledBy(-0.5));
                g.drawImage(Phoenix.chargingImage, (int) (chargeCoord.x), (int) (chargeCoord.y), (int) chargeSize.x, (int) chargeSize.y, null);
            }

            // Spark (reticle?)
            else if (loadout[chargingWeapon] == GLUE_BOMB_WEAPON_NO) {
                chargeSize = Spark.SIZE.scaledBy(getPercentCharged(GLUE_BOMB_WEAPON_NO)); // CHANGE GET PERCENT CHARGED TO DO ACTUALLY WHAT IT'S NAME IS
                chargeCoord = chargeCoord.add(chargeSize.scaledBy(-0.5));
                g.drawImage(Spark.image, (int) (chargeCoord.x), (int) (chargeCoord.y), (int) chargeSize.x, (int) chargeSize.y, null);
                Coord reticleSize = chargeSize.scaledBy(Spark.EXPLOSION_SIZE_MULT);
                Coord reticleCoord = chargeCoord.add(reticleSize.scaledBy(-0.5));
                reticleCoord.x += (Spark.VELOCITY * (Spark.LIFE - 1) * getPercentCharged(GLUE_BOMB_WEAPON_NO) - PROJ_SPAWN_X_OFFSET) * spriteSign;
                reticleCoord.y += chargeSize.y / 2;
                g.drawImage(Spark.reticleImg, (int) (reticleCoord.x), (int) (reticleCoord.y), (int) reticleSize.x, (int) reticleSize.y, null);
            }
        }
    }

    // Description: Helper method for getting the percent charged (from 0-1) given the weapon number
    private double getPercentCharged(int weaponNo) {
        return OmegaFight3.lerp(CHARGE_MIN_PERC[weaponNo], 1, (double) Math.min(shootCharge - BASIC_SHOOT_TIME_LIMIT, CHARGE_TIME[weaponNo]) / (CHARGE_TIME[weaponNo]));
    }

    // Description: This method calculates the movement in the y-direction of the player
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
            if (upPressed && velocity.y < 0) velocity.y = Math.min(maxVelocity.y, velocity.y + MIN_ACCEL_DWN - velocity.y / maxVelocity.y);
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
            int platformNo = checkPlatforms();
            // Falling
            if (platformNo == AIRBORNE) {
                spriteNo = JUMP_SPRITE;
                onPlatform = AIRBORNE;
                coyoteCounter = COYOTE_TIME;
            }
            else {
                velocity.y = 0;
            }
        }
    }

    // Description: This method moves the player according to their velocity
    public void move() {
        if (velocity.x != 0) coord.x += velocity.x;
        if (velocity.y != 0) coord.y += velocity.y;
        addToStat(DIST_MOVED_NO, Math.hypot(velocity.x, velocity.y));
    }

    // Description: This method calculates the sprite change for a running player
    private void animateRun() {
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

    // Description: This method decelerates the player and changes the sprite of the player
    private void decelerateRun() {
        if (onPlatform != AIRBORNE && spriteNo != IDLE_SPRITE) spriteNo = IDLE_SPRITE;
        decelerate(accel.x);
    }

    // Description: This method calcuates the deceleration of the player
    public void decelerate(double decel) {
        if (velocity.x < 0) velocity.x = Math.min(0, velocity.x + decel);
        else if (velocity.x != 0) velocity.x = Math.max(0, velocity.x - decel);
    }

    // Description: This method return which platform number the player is landing on and AIRBORNE if they are not landing
    public int checkPlatforms() {
        for (int i = 0; i != OmegaFight3.stage[OmegaFight3.stageNo].platforms.length; i++) if (OmegaFight3.stage[OmegaFight3.stageNo].platforms[i].landed(coord.x, size.y, coord.y, coord.y + velocity.y)) return i;
        return AIRBORNE;
    }

    // Description: This method checks if the player is colliding with the boss and hurts them if so
    public void checkBossHitbox() {
        for (Boss boss: OmegaFight3.bosses) {
            if (OmegaFight3.intersects(coord, hurtBoxSize, boss.coord, boss.hitBoxSize)) {
                hurt(KAMIKAZE_DMG, KAMIKAZE_KB, boss.coord, true);
            }
        }
    }

    public void checkPlayerHitBox() {
        if (invCounter == VULNERABLE) {
            for (Omegaman omega: OmegaFight3.omegaman) {
                if (omega != this && OmegaFight3.intersects(coord, hurtBoxSize, omega.coord, omega.hitBoxSize) && omega.invCounter == VULNERABLE) {
                    double angle = Math.atan2(coord.y - omega.coord.y, coord.x - omega.coord.x);
                    velocity.x += SOFT_COLLISION_ACCEL * Math.cos(angle);
                    velocity.y += SOFT_COLLISION_ACCEL * Math.sin(angle);
                    omega.velocity.x += SOFT_COLLISION_ACCEL * -Math.cos(angle);
                    omega.velocity.y += SOFT_COLLISION_ACCEL * -Math.sin(angle);
                }
            }
        }
    }

    // Description: This method checks if the player has died and calculates in which direction they have died
    public void checkState() { // CHECK FOR DIAGONAL DEATHS
        // Bottom
        if (coord.y > OmegaFight3.SCREEN_SIZE.y + size.y) {
            if (coord.x < MAX_DIST_TO_DIAGONAL) {
                state = DIED_BOTLFT;
            }
            else if (coord.x > OmegaFight3.SCREEN_SIZE.x - MAX_DIST_TO_DIAGONAL) {
                state = DIED_BOTRIT;
            }
            else {
                state = DIED_BOT;
            }
            resetStats(DIED_STAT_RESET);
        }

        // Left
        else if (coord.x < -size.x) {
            if (coord.y < MAX_DIST_TO_DIAGONAL) {
                state = DIED_TOPLFT;
            }
            else if (coord.y > OmegaFight3.SCREEN_SIZE.y - MAX_DIST_TO_DIAGONAL) {
                state = DIED_BOTLFT;
            }
            else {
                state = DIED_LFT;
            }
            resetStats(DIED_STAT_RESET);
        }

        // Top
        else if (coord.y < -size.y) {
            if (coord.x < MAX_DIST_TO_DIAGONAL) {
                state = DIED_TOPLFT;
            }
            else if (coord.x > OmegaFight3.SCREEN_SIZE.x - MAX_DIST_TO_DIAGONAL) {
                state = DIED_TOPRIT;
            }
            else {
                state = DIED_TOP;
            }
            resetStats(DIED_STAT_RESET);
        }

        // Right
        else if (coord.x > OmegaFight3.SCREEN_SIZE.x + size.x) {
            if (coord.y < MAX_DIST_TO_DIAGONAL) {
                state = DIED_TOPRIT;
            }
            else if (coord.y > OmegaFight3.SCREEN_SIZE.y - MAX_DIST_TO_DIAGONAL) {
                state = DIED_BOTRIT;
            }
            else {
                state = DIED_RIT;
            }
            resetStats(DIED_STAT_RESET);
        }
    }

    // Description: This method handles the various large scale stat resets that I might need to do
    public void resetStats(int type) {
        // General stat resets
        frameCounter = 0;
        jumpCounter = 0;
        coyoteCounter = 0;
        shootCharge = 0;
        chargingWeapon = NOT_CHARGING;
        coolingWeapon = NO_COOLING_WEAPON;
        heatCounter = 0;
        runSign = 1;

        // Died stats resets
        if (type == DIED_STAT_RESET) {
            if (OmegaFight3.transitiono != OmegaFight3.GAME_SET && OmegaFight3.transitiono != OmegaFight3.GAME_OVER && livesLeft != OmegaFight3.INF_LIVES) livesLeft--;
            jumpState = 3;
            spriteNo = IDLE_SPRITE;
            spriteSign = OmegaFight3.stage[OmegaFight3.stageNo].spawnSpriteSign[playerNo];
            velocity.x = 0;
            velocity.y= RESPAWN_INIT_VELOCITY;
            onPlatform = AIRBORNE;
            OmegaFight3.screenShakeCounter += DIE_SCREENSHAKE;
            percentShakeCounter = 0;
            skillPts = 0;
            skillPtCounter = 0;
            stunCounter = 0;
            invCounter = RESPAWN_INIT_VELOCITY + RESPAWN_TIME_LIMIT + 2;
            Bullet.combo[playerNo] = 0;
            Bullet.comboEndCounter[playerNo] = 0;
            fireCounter = 0;
            OmegaFight3.play(OmegaFight3.boosh);
        }
        if (type != DASH_STAT_RESET) {
            dashing = NOT_DASHING;
            dashCounter = 0;
        }
        if (type != STUN_STAT_RESET) {
            for (int i = 0; i != NUM_DASH_DIRS; i++) {
                dashButtonCounter[i] = 0;
                dashState[i] = 0;
            }
        }
    }

    // Description: This method prepares the player for respawn and plays the cheer sound effect
    public void prepareForRespawn() {
        coord.x = OmegaFight3.stage[OmegaFight3.stageNo].spawnCoords[playerNo].x;
        coord.y = -size.y;
        percent = 0;
        OmegaFight3.play(OmegaFight3.cheer);
    }

    // Description: This method draws the surge of the dying player
    public void drawSurge(Graphics2D g2) {
        double rotation = Math.PI / 4 * (state - 1);
        BufferedImage surgeImage = surge[playerNo][frameCounter / OmegaFight3.SURGE_FRAME_HZ];
        Coord rotCoord;

        // Bottom death
        if (state == DIED_BOT) {
            rotCoord = new Coord(coord.x, OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y / 2);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (coord.x - OmegaFight3.SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }

        // Left death
        else if (state == DIED_LFT) {
            rotCoord = new Coord(OmegaFight3.SURGE_SIZE.y / 2, coord.y);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) ((OmegaFight3.SURGE_SIZE.y - OmegaFight3.SURGE_SIZE.x) / 2), (int) (coord.y - OmegaFight3.SURGE_SIZE.y / 2), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }

        // Top death
        else if (state == DIED_TOP) {
            rotCoord = new Coord(coord.x, OmegaFight3.SURGE_SIZE.y / 2);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (coord.x - OmegaFight3.SURGE_SIZE.x / 2), 0, null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }

        // Right death
        else if (state == DIED_RIT) {
            rotCoord = new Coord(OmegaFight3.SCREEN_SIZE.x - OmegaFight3.SURGE_SIZE.y / 2, coord.y);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (OmegaFight3.SCREEN_SIZE.x - (OmegaFight3.SURGE_SIZE.x + OmegaFight3.SURGE_SIZE.y) / 2), (int) (coord.y - OmegaFight3.SURGE_SIZE.y / 2), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }

        else if (state == DIED_BOTLFT) {
            rotCoord = new Coord(0, OmegaFight3.SCREEN_SIZE.y);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (-OmegaFight3.SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }
        
        else if (state == DIED_TOPLFT) {
            rotCoord = new Coord();
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (-OmegaFight3.SURGE_SIZE.x / 2), (int) (-OmegaFight3.SURGE_SIZE.y), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }

        else if (state == DIED_TOPRIT) {
            rotCoord = new Coord(OmegaFight3.SCREEN_SIZE.x, 0);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (OmegaFight3.SCREEN_SIZE.x - OmegaFight3.SURGE_SIZE.x / 2), (int) (-OmegaFight3.SURGE_SIZE.y), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }

        else if (state == DIED_BOTRIT) {
            rotCoord = new Coord(OmegaFight3.SCREEN_SIZE.x, OmegaFight3.SCREEN_SIZE.y);
            g2.rotate(rotation, rotCoord.x, rotCoord.y);
            g2.drawImage(surgeImage, (int) (OmegaFight3.SCREEN_SIZE.x - OmegaFight3.SURGE_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - OmegaFight3.SURGE_SIZE.y), null);
            g2.rotate(-rotation, rotCoord.x, rotCoord.y);
        }
    }

    // Description: This method draws the dying/falling percent of the player
    public void drawDiePercent(Graphics g) {
        drawStillPercent((int) OmegaFight3.lerp(PERC_DISPLAY_Y_COORD + (int) PERC_DISPLAY_SIZE_TO_PERC_COORD.y, OmegaFight3.SCREEN_SIZE.y + BIG_PERC_FONT.getSize(), (double) frameCounter / OmegaFight3.SURGE_TIME), g);
    }

    // Description: This method draws teh respawning/rising percent of the player
    public void drawRespawnPercent(Graphics g) {
        drawStillPercent((int) OmegaFight3.lerp(PERC_DISPLAY_Y_COORD + (int) PERC_DISPLAY_SIZE_TO_PERC_COORD.y, OmegaFight3.SCREEN_SIZE.y + BIG_PERC_FONT.getSize(), velocity.y / RESPAWN_INIT_VELOCITY), g);
    }

    // Description: This method calculates teh respawn of the player
    public void respawn(boolean dwnPressed)  {
        // Bring down player from bottom of screen
        if (velocity.y != 0) {
            velocity.y--;
            coord.y += velocity.y;
        }

        // Check for respawn button press and respawn if so
        else if (dwnPressed || frameCounter == OmegaFight3.SURGE_TIME + RESPAWN_PAUSE + RESPAWN_INIT_VELOCITY + RESPAWN_TIME_LIMIT) {
            state = ALIVE_STATE;
            frameCounter = 0;
            spriteNo = JUMP_SPRITE;
            onPlatform = AIRBORNE;
            invCounter = RESPAWN_INV_LEN;
        }
    }

    // Description: This method adds skillPts if the player hasn't reached the max amount of skill points yet
    public void addSkillPts(int amt) {
        skillPts = Math.min(skillPts + (int) (amt * (OmegaFight3.gameMode == OmegaFight3.PVP? PVP_SKILL_PT_BASIC_MULT: 1)), MAX_SKILL_PTS);
    }

    // Description: This method recoils the player based on the direction they're facing
    public void recoil(double amount) {
        if (spriteSign == OmegaFight3.LFT_SIGN) velocity.x += amount;
        else if (spriteSign == OmegaFight3.RIT_SIGN) velocity.x -= amount;
    }

    // Description: This method hurts the player and shakes their percent but doesn't knock them back
    public double hurt(double damage) {
        if (invCounter == VULNERABLE && state == ALIVE_STATE) {
            percentShakeCounter = PERC_SHAKE_TIME;
            percent += damage;
            setStat(MAX_PERC_NO, Math.max(stats[MAX_PERC_NO], percent));
            return damage;
        }
        return 0;
    }

    // Description: This method hurts the player and prepares them for knockback
    private void hurtWithKb(double damage, double knockback, Coord enemyCoord) {
        if (invCounter == VULNERABLE && state == ALIVE_STATE) {
            hurt(damage);

            // Ensure no clipping through platforms
            int platformNo = checkPlatforms();
            if (platformNo != AIRBORNE) coord.y = getPlatformY(platformNo);

            // Sprite change
            spriteNo = HURT_SPRITE;
            
            // Stat changes and screenshake
            resetStats(STUN_STAT_RESET);
        }
    }

    private void hurtSpriteSign() {
        int potSpriteSign = (int) -Math.signum(velocity.x);
        if (potSpriteSign != 0) spriteSign = potSpriteSign;
    }

    public double hurt(double damage, double knockback, Coord enemyCoord, double dir, double kbSpread) {
        return hurt(damage, knockback, enemyCoord, dir, kbSpread, false);
    }

    // Description: This method hurts the player and knocks them back in the directions specified with the amount of spread specified based on the enemy coordinates
    public double hurt(double damage, double knockback, Coord enemyCoord, double dir, double kbSpread, boolean setKB) {
        if (invCounter == VULNERABLE && state == ALIVE_STATE) {
            hurtWithKb(damage, knockback, enemyCoord);

            // knockback calculations. Can't put this with hurtWithKb cuz knockback is a local variable
            if (!setKB) knockback *= (percent / Math.pow(10, PERC_NUM_DECIMALS) / 100 + 1);
            stunCounter = (int) Math.pow(knockback, STUN_REDUCTION);

            // Angle and speed calculations
            double angle = Math.atan2(coord.y - KB_COORD_Y_OFFSET - enemyCoord.y, coord.x - enemyCoord.x);
            double minAngle = dir - kbSpread;
            double dMinAngle = OmegaFight3.normalizeAngle(minAngle - angle);
            double maxAngle = dir + kbSpread;
            double dMaxAngle = OmegaFight3.normalizeAngle(maxAngle - angle);
            if (dMinAngle > 0 || dMaxAngle < 0) {
                if (Math.abs(dMinAngle) < Math.abs(dMaxAngle)) angle = minAngle;
                else angle = maxAngle;
            }
            velocity = velocity.scaledBy(0.25);
            velocity.x += Math.cos(angle) * knockback;
            velocity.y += Math.sin(angle) * knockback;

            // Sprite direction change
            hurtSpriteSign();

            return damage;
        }
        return 0;
    }

    public double hurt(double damage, double knockback, Coord enemyCoord) {
        return hurt(damage, knockback, enemyCoord, false);
    }

    // Description: This method hurts the player and knocks them back in any direction based on the enemy coordinates
    public double hurt(double damage, double knockback, Coord enemyCoord, boolean setKB) {
        if (invCounter == VULNERABLE && state == ALIVE_STATE) {
            hurtWithKb(damage, knockback, enemyCoord);

            // knockback calculations
            if (!setKB) knockback *= (percent / Math.pow(10, PERC_NUM_DECIMALS) / 100 + 1);
            stunCounter = (int) Math.pow(knockback, STUN_REDUCTION);

            // ANgle and speed calculations
            double angle = Math.atan2(coord.y - KB_COORD_Y_OFFSET - enemyCoord.y, coord.x - enemyCoord.x);
            velocity = velocity.scaledBy(0.25);
            velocity.x += Math.cos(angle) * knockback;
            velocity.y += Math.sin(angle) * knockback;

            // Sprite direction change
            hurtSpriteSign();

            return damage;
        }
        return 0;
    }

    // Description: THis method calculates the knockback of the player
    public void knockback(HashSet<Integer> pressedKeys) {
        decelerate(accel.x);
        
        // velocity calculations
        int platformNo;
        velocity.y += KB_GRAVITY;
        if (pressedKeys.contains(upKey)) {
            coord.y -= DIR_INP_VEL;
        }
        else if (pressedKeys.contains(lftKey)) {
            coord.x -= DIR_INP_VEL;
        }
        else if (pressedKeys.contains(dwnKey)) { // Check clipping thru platforms before doing this (make temp variable for velocity?)
            double velocitY = velocity.y;
            velocity.y = DIR_INP_VEL;
            platformNo = checkPlatforms();
            if (platformNo == AIRBORNE) coord.y += DIR_INP_VEL;
            else coord.y = getPlatformY(platformNo);
            velocity.y = velocitY;
        }
        else if (pressedKeys.contains(ritKey)) {
            coord.x += DIR_INP_VEL;
        }

        // Platform collision and Bounce
        platformNo = checkPlatforms();
        if (platformNo != AIRBORNE) {
            coord.y = getPlatformY(platformNo);
            if (velocity.y >= BOUNCE_MIN_VEL_Y) {
                hurt(velocity.y - BOUNCE_MIN_VEL_Y);
                velocity.y *= -1;
            }
            else velocity.y = 0;
        }

        // Kb time calculations
        stunCounter--;
        if (stunCounter <= 0) {
            recover();
        }

        // Smoke trails
        smokeQ.add(new Smoke(coord.copy(), Smoke.SMOKE_SIZE.scaledBy(Math.pow(Math.hypot(velocity.x, velocity.y), Smoke.SMOKE_VEL_SCALE) * Math.min(size.x, size.y) / Smoke.SMOKE_DESIGNED_SIZE)));
    }

    private void recover() {
        int platformNo = checkPlatforms();
        // Airborne recovery
        if (platformNo == AIRBORNE) {
            spriteNo = JUMP_SPRITE;
            onPlatform = AIRBORNE;
            velocity.y = Math.min(velocity.y, maxVelocity.y);
            if (jumpState % 2 == 0) jumpState++;
            if (jumpState == 1) jumpState = 3;
        }

        // Ground recovery
        else {
            spriteNo = IDLE_SPRITE;
            onPlatform = platformNo;
            velocity.y = 0;
            jumpState = 1;
        }
    }

    // Description: This method calculates the invincibility frames of the player
    public void countInv() {
        if (invCounter != VULNERABLE) invCounter--;
    }

    // Description: This method gets the y-coordinate of the platform number specified
    private double getPlatformY(int platformNo) {
        return OmegaFight3.stage[OmegaFight3.stageNo].platforms[platformNo].y - size.y / 2;
    }

    public void addToStat(int statNo, double amt) {
        if (OmegaFight3.transitiono != OmegaFight3.GAME_SET && OmegaFight3.transitiono != OmegaFight3.GAME_OVER) stats[statNo] += amt;
    }

    public void setStat(int statNo, double val) {
        if (OmegaFight3.transitiono != OmegaFight3.GAME_SET && OmegaFight3.transitiono != OmegaFight3.GAME_OVER) stats[statNo] = val;
    }

    // Description: This method calculates the shaking percent of the player
    public void shakePercent() {
        if (percentShakeCounter != 0) {
            if (percentShakeCounter % PERC_SHAKE_HZ == 0) {
                String percStr = (percent / PERC_MULT) + "." + (percent % PERC_MULT) + "%";
                if (percentDigitShake.length < percStr.length())
                    percentDigitShake = new Coord[percStr.length()];
                double curPercShakeMult = percentShakeCounter * PERC_SHAKE_MULT;
                for (int i = 0; i < percentDigitShake.length; i++) {
                    percentDigitShake[i] = new Coord(OmegaFight3.randomSign() * Math.random() * curPercShakeMult,
                    OmegaFight3.randomSign() * Math.random() * curPercShakeMult);
                }
            }
            percentShakeCounter--;
        }
    }

    // Description: This method draws a skill point for the player
    public void drawSkillPt(int skillPtNo, double amt, Graphics g) {
        g.fillRect(percentDisplayX + (int) (PERC_DISPLAY_SIZE_TO_SKILL_PTS_COORD.x + skillPtNo * (SKILL_PTS_SPACE + SKILL_PTS_SIZE.x)),
            PERC_DISPLAY_Y_COORD + (int) (PERC_DISPLAY_SIZE_TO_SKILL_PTS_COORD.y), (int) (SKILL_PTS_SIZE.x * amt), (int) (SKILL_PTS_SIZE.y));
    }

    // Description: This method draws the bigger text portion of the player's still percent
    public int drawBigPercent(int offSet, int y, Graphics g) {
        g.setFont(BIG_PERC_FONT);
        g.drawString((percent / PERC_MULT) + "", percentDisplayX + (int) PERC_DISPLAY_SIZE_TO_PERC_COORD.x + offSet, y + offSet);
        return g.getFontMetrics().stringWidth((percent / PERC_MULT) + "");
    }

    // Description: This method draws the smaller text portion of the player's still percent
    public void drawSmlPercent(int offSet, int bigPercentWidth, int y, Graphics g) {
        g.setFont(SML_PERC_FONT);
        g.drawString("." + (percent % PERC_MULT) + "%", percentDisplayX + (int) PERC_DISPLAY_SIZE_TO_PERC_COORD.x + offSet + bigPercentWidth, y + offSet);
    }

    // Description: This method draws the entire still percent of the player
    public void drawStillPercent(int y, Graphics g) {
        // Text Shadow
        g.setColor(Color.BLACK);
        int bigPercentWidth = drawBigPercent(PERC_SHADOW_OFFSET, y, g);
        drawSmlPercent(PERC_SHADOW_OFFSET, bigPercentWidth, y, g);

        // Actual percent
        double percentColor = Math.min(percent, (double) MAX_PERC_COLOR_THRESHOLD) / MAX_PERC_COLOR_THRESHOLD;
        g.setColor(new Color(OmegaFight3.MAX_RGB_VAL - (int) ((OmegaFight3.MAX_RGB_VAL - MAX_PERC_COLOR.getRed()) * percentColor),
        OmegaFight3.MAX_RGB_VAL - (int) ((OmegaFight3.MAX_RGB_VAL - MAX_PERC_COLOR.getGreen()) * percentColor),
        OmegaFight3.MAX_RGB_VAL - (int) ((OmegaFight3.MAX_RGB_VAL - MAX_PERC_COLOR.getBlue()) * percentColor)));
        drawBigPercent(0, y, g);
        drawSmlPercent(0, bigPercentWidth, y, g);
    }

    // Description: This method draws the entire shaking percent of the player
    public void drawShakingPercent(String bigPercent, String smlPercent, Graphics g) { // Maybe do try catch and calculate shake if doesn work?
        g.setColor(Color.RED);
        int percentWidth = 0;

        // Big text part of percent
        g.setFont(BIG_PERC_FONT);
        for (int i = 0; i != bigPercent.length(); i++) {
            Coord digitCoord = PERC_DISPLAY_SIZE_TO_PERC_COORD.add(percentDigitShake[i]);
            String digitStr = bigPercent.substring(i, i + 1);
            g.drawString(digitStr, percentDisplayX + (int) (digitCoord.x) + percentWidth,
            PERC_DISPLAY_Y_COORD + (int) (digitCoord.y));
            percentWidth += g.getFontMetrics().stringWidth(digitStr);
        }

        // Small text part of percent
        g.setFont(SML_PERC_FONT);
        for (int i = bigPercent.length(); i != bigPercent.length() + smlPercent.length(); i++) {
            Coord digitCoord = PERC_DISPLAY_SIZE_TO_PERC_COORD.add(percentDigitShake[i]);
            String digitStr = smlPercent.substring(i - bigPercent.length(), i + 1 - bigPercent.length());
            g.drawString(digitStr, percentDisplayX + (int) (digitCoord.x) + percentWidth,
            PERC_DISPLAY_Y_COORD + (int) (digitCoord.y));
            percentWidth += g.getFontMetrics().stringWidth(digitStr);
        }
    }

    // Description: This method draws the HUD that displays all of the player's information at the bottom of the screen
    public void drawHUD(Graphics g) {
        // HUD
        g.drawImage(percentDisplay[playerNo], percentDisplayX, PERC_DISPLAY_Y_COORD, null);
        
        // Full skill points
        g.setColor(Color.WHITE);
        int skillPtNo = 0;
        for (; skillPtNo != skillPts / ONES_PER_SKILL_PT; skillPtNo++) drawSkillPt(skillPtNo, 1.0, g);

        // Incomplete skill point
        g.setColor(Color.GRAY);
        drawSkillPt(skillPtNo, skillPts % ONES_PER_SKILL_PT / ((double) ONES_PER_SKILL_PT), g);

        // Lives
        if (livesLeft != OmegaFight3.INF_LIVES) {
            for (int i = 0; i != livesLeft; i++) {
                g.drawImage(face[playerNo], percentDisplayX + (i * (int) (FACE_SIZE.x + FACE_SPACING)), PERC_DISPLAY_Y_COORD - FACE_SPACING - (int) FACE_SIZE.y, null);
            }
        }
    }

    // Description: THis method calculates which type of percent to draw and draws it
    public void drawPercent(Graphics g) {
        if (percentShakeCounter == 0) {
            drawStillPercent(PERC_DISPLAY_Y_COORD + (int) PERC_DISPLAY_SIZE_TO_PERC_COORD.y, g);
        }
        else {
            drawShakingPercent((percent / PERC_MULT) + "", "." + (percent % PERC_MULT) + "%", g);
        }
    }
    
    // Description: This method draws the player
    public void draw(Graphics g) {
        if ((invCounter % INV_BLINK_CYCLE_LEN < INV_BLINK_CYCLE_LEN - INV_BLINK_LEN) || dashing != NOT_DASHING) {
            g.drawImage(sprite[playerNo][spriteNo], (int) (coord.x - size.x / 2 * spriteSign), (int) (coord.y - size.y / 2), (int) (size.x * spriteSign), (int) (size.y), null);
        }
        super.draw(g);
    }
}