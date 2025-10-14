package Version4;

// Name:
// Ernest Li
// Date:
// June 13, 2025
// Description
// Omega Fight 3 is a 2D platform fighting game where players can choose their owners, weapons, and stages to battle against against AI bosses.
// The game features various menus for navigation, a home screen, a choose fight screen, an in-game screen, a game end screen, and a battle log screen.
// Players can also view a slideshow of the how to play the game and view credits.

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;
import javafx.util.Pair;
// Ernest Todo: ultimate, changing controls

public class OmegaFight3 extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Runnable {
    // Screen Settings
    public static final Coord SCREEN_SIZE = new Coord(1920, 960);
    public static final Coord SCREEN_CENTER = SCREEN_SIZE.scaledBy(0.5);
    public static final int SPACING = 25;

    // Frame Settings
    public static final int MAX_TICK_RATE = 60;

    // Screen Shake Settings
    public static final int SCREEN_SHAKE_HZ = 2;
    public static final double SCREEN_SHAKE_MULT = 0.75;
    public static final int SCREEN_SHAKE_MAX = 60;

    // Cheat constants
    public static final boolean DEV_MODE = false;
    public static final boolean CHEATS = DEV_MODE;
    public static final int KILL_KEY = KeyEvent.VK_K;
    public static final double KILL_DMG = 2 * Omegaman.PERC_MULT;
    public static final int FPS_CNT_KEY = KeyEvent.VK_F;
    public static final int FPS_TEXT_SPACING = 7;
    public static final Font FPS_FONT = new Font("Courier New", Font.PLAIN, 15);
    public static final Color CHEAT_COLOR = new Color(65, 190, 64);
    public static final int RANDOM_READY_KEY = KeyEvent.VK_R;
    public static final int HITBOX_KEY = KeyEvent.VK_H;
    public static final int SLOW_MODE_KEY = KeyEvent.VK_S;
    public static final int SLOW_MODE_TICK_RATE = 20;

    // Sound settings
    public static final boolean SOUND_ON = !DEV_MODE;

    // Gamestates
    public static final int STUDIO_ANIM_GS = -1;
    public static final int HOME_GS = 0;
    public static final int CHOOSE_FIGHT_GS = 1;
    public static final int GAME_GS = 2;
    public static final int GAME_END_GS = 3;
    public static final int SLIDESHOW_GS = 4;
    public static final int BATTLE_LOG_GS = 5;

    // Button Numbers (Next avail: 52)
    public static final int NO_BUTTON_HIT = -1;

    // Choose your fight menu
    public static final int CHOOSE_BACK_BUTTONO = 0;
    public static final int READY_BUTTONO = 10;
    public static final int RANDOM_STAGE_BUTTONO = 31;
    public static final int RANDOM_WEAPON_BUTTONO = 32;
    public static final int RULES_BUTTONO = 33;

    // Rules sub menu
    public static final int RULES_BACK_BUTTONO = 34;
    public static final int LIVES_BUTTONO = 35;
    public static final int BOSS_BUTTONO = 36;
    public static final int GAMEMODE_BUTTONO = 37;

    // Pause menu
    public static final int RESUME_BUTTONO = 26;
    public static final int QUIT_BUTTONO = 27;

    // Game end menu
    public static final int NEXT_BATTLE_BUTTONO = 15;
    public static final int FIGHT_BUTTONO = 16;
    public static final int SLIDESHOW_BUTTONO = 17;
    public static final int BATTLE_LOG_BUTTONO = 18;

    // Battle log menu
    public static final int BATTLE_LOG_BACK_BUTTONO = 19;
    public static final int BATTLE_NO_BACK_BUTTONO = 20;
    public static final int BATTLE_NO_ALL_BACK_BUTTONO = 21;
    public static final int BATTLE_NO_NEXT_BUTTONO = 22;
    public static final int BATTLE_NO_ALL_NEXT_BUTTONO = 23;
    public static final int BATTLE_NO_SORT_BUTTONO = 24;
    public static final int BATTLE_NO_ORDER_BUTTONO = 25;

    // Slideshow menu
    public static final int SLIDESHOW_BACK_BUTTONO = 28;
    public static final int SLIDE_NO_BACK_BUTTONO = 29;
    public static final int SLIDE_NO_NEXT_BUTTONO = 30;

    // Controls
    public static final int[][] CONTROLS_BUTTONO = {{40, 41, 42, 43}, {44, 45, 46, 47}};
    public static final int[][] SHOOT_BUTTONO = {{48, 49}, {50, 51}};

    // Button constants
    public static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 40); 
    public static final Coord BUTTON_SIZE = new Coord(400, 50);
    public static final Coord MED_BUTTON_SIZE = new Coord(100, 50);
    public static final Coord SML_BUTTON_SIZE = new Coord(50);
    public static final Font STAGE_FONT = new Font("Consolas", Font.BOLD, 25);
    public static final Coord STAGE_BUTTON_SIZE = new Coord(350, 175);
    public static final Coord WEAPON_ICON_SIZE = new Coord(100);

    // Start menu constants
    public static final int TITLE_Y = 75;
    public static final Coord TITLE_SIZE = new Coord(753, 356);
    public static final int TITLE_NUM_Y = 455;
    public static final Coord TITLE_NUM_SIZE = new Coord(708, 250); 
    public static final int TITLE_NUM_CENTRE_Y = TITLE_NUM_Y + (int) TITLE_NUM_SIZE.y / 2;
    public static final int PRESS_START_Y = 724;
    public static final Coord PRESS_START_SIZE = new Coord(551, 31);
    public static final int PRESS_START_BLINK_HZ = 60;
    public static final int NUM_SLAM_SCREENSHAKE = 45;
    public static final Coord STUDIO_LOGO_SIZE = new Coord(202, 126);
    public static final int STUDIO_FADE_LEN = 6;

    // Home menu Constants
    public static final Coord MENU_MAN_SIZE = new Coord(860, 900);
    public static final int MENU_MAN_ANIM_LEN = 120;
    public static final int MENU_MAN_ANIM_DIST = 40;
    public static final Coord HOME_BUTTON_SIZE = new Coord(960, 100);
    public static final double HOME_BUTTON_X = SCREEN_SIZE.x - HOME_BUTTON_SIZE.x / 2;
    public static final Font HOME_BUTTON_FONT = new Font("Consolas", Font.BOLD, 66);
    public static final int HOME_BUTTON_FIRST_Y = 570;
    public static final int HOME_BUTTON_SPACING = 150;
    public static final int HOME_NUM_BUTTONS = 3;
    public static final int[] HOME_BUTTONO = {FIGHT_BUTTONO, SLIDESHOW_BUTTONO, BATTLE_LOG_BUTTONO};
    public static final String[] HOME_BUTTON_TEXT = {"FIGHT!!", "SLIDESHOW", "BATTLE LOG"};

    // Choose Fight Menu Constants
    public static final int NOT_READY = -1;
    public static final int READY_ANIM_LEN = 6;
    public static final int BLACK_BAR_TOP = 125;
    public static final int BLACK_BAR_BOTTOM = 525;
    public static final int DIVIDER_RIT_X = 1525;
    public static final int LOADOUT_ICON_Y = 700;
    public static final int NO_WEAPON = -1;
    public static final Coord READY_BAR_SIZE = new Coord(1920, 240);
    public static final int[] LOADOUT_ICON_X = {610, 1490};
    public static final int STAGE_SPACING = 22;
    public static final int TOT_NUM_WEAPONS = 8;
    public static final int RANDOM_WEAPON = TOT_NUM_WEAPONS;
    public static final int[] WEAPON_BUTTONO = {Bullet.BUTTONO, Shotgun.BUTTONO, Spammer.BUTTONO, Sniper.BUTTONO, Boomer.BUTTONO, Spike.BUTTONO, Fireball.BUTTONO, GlueBomb.BUTTONO};
    public static final int WEAPON_ICONS_PER_ROW = 3;

    // Rules menu constants
    public static final String[] GAMEMODE_DESC = {"WORK TOGETHER AND OBLITERATE THE BOSS!", "DUKE IT OUT AND BE THE LAST ONE STANDING!", "BE THE ONE WHO DEALS THE MOST DAMAGE TO THE BOSS!"};
    public static final int GAMEMODE_DESC_BAR_Y = 787;
    public static final int GAMEMODE_DESC_BAR_SIZE_Y = 85;
    public static final int GAMEMODE_DESC_END_SIZE_X = 80;
    public static final int GAMEMODE_DESC_MID_Y = 835;
    public static final Font GAMEMODE_DESC_FONT = new Font("Consolas", Font.BOLD, 48);
    public static final Coord RULES_BUTTON_SIZE = new Coord(1080, 100);
    public static final Font RULES_BUTTON_FONT = HOME_BUTTON_FONT;
    public static final int RULES_BUTTON_MID_Y = 503; 
    public static final Coord RULES_BUTTON_SPACING = new Coord(40, 10);
    public static final int RULES_BUTTON_START_Y = (int) (RULES_BUTTON_MID_Y - RULES_BUTTON_SIZE.y * 1 - RULES_BUTTON_SPACING.y);
    public static final int RULES_BUTTON_DIST_Y = (int) (RULES_BUTTON_SIZE.y + RULES_BUTTON_SPACING.y);

    // Pause menu constants
    public static final int PAUSE_KEY = KeyEvent.VK_ESCAPE;
    public static final int PAUSE_NUM_BUTTONS = 2;
    public static final int[] PAUSE_BUTTONO = {RESUME_BUTTONO, QUIT_BUTTONO};
    public static final String[] PAUSE_BUTTON_TEXT = {"RESUME", "QUIT"};

    // Game end menu constants
    public static final double FLASH_ROTS_PER_S = 1.0 / 6;
    public static final double FLASH_ROTATE_SPD = Math.PI * 2 / MAX_TICK_RATE * FLASH_ROTS_PER_S;
    public static final Coord FLASH_COORD = SCREEN_CENTER.add(-Math.hypot(SCREEN_CENTER.x, SCREEN_CENTER.y));
    public static final Coord RESULTS_TITLE_SIZE = new Coord(1494, 134); 
    public static final int RESULTS_EDGE_SPACING = 100;
    public static final int RESULTS_SPACING = 40;
    public static final Coord BATTLE_NAME_BOX_SIZE = new Coord(405, 120);
    public static final int BATTLE_NAME_BOX_IDX = 0;
    public static final double GAME_END_BUTTON_Y = RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING * 2 + Battle.SCOREBOARD_SIZE.y + BATTLE_NAME_BOX_SIZE.y / 2;

    // Battle log menu constants
    public static final Coord BATTLE_LOG_SCOREBOARD_COORD = new Coord(660, 350);
    public static final int BATTLE_NO_Y_COORD = 880;
    public static final Coord BATTLE_LOG_BATTLE_INFO_COORD = new Coord(582, 274);
    public static final int ARROW_BUTTON_SPACING = 10;
    public static final int HALF_BATTLE_NO_SIZE = 100;
    public static final int NUM_SORTS = 2;
    public static final String BATTLE_LOG_FILE_NAME = "battle log";
    public static final String[] BATTLE_LOG_SORT_NAME = {SortByTitle.NAME, SortByGrade.NAME};

    // Direction Constants
    public static final int LFT_SIGN = -1;
    public static final int RIT_SIGN = 1;

    // Stage constants
    public static final int NUM_STAGES = 3;
    public static final int RANDOM_STAGE = NUM_STAGES;
    public static final int BATTLEFIELD_NO = 0;
    public static final int FINAL_DEST_NO = 1;
    public static final int NORTH_CAVE_NO = 2;
    public static final String[] STAGE_NAME = {"battlefield", "final destination", "northern cave"};
    public static final Platform[][] PLATFORMS = {{new Platform(395, 1525, 660, true), new Platform(535, 820, 485, false), new Platform(1095, 1385, 485, false)},
    {new Platform(245, 1675, 600, true)},
    {new Platform(187, 1709, 735, true), new Platform(90, 440, 500, false), new Platform(1480, 1830, 500, false)}};
    public static final Coord[][] SPAWN_COORDS = {{new Coord(700, 485), new Coord(1260, 485)},
    {new Coord(700, 600), new Coord(1260, 600)},
    {new Coord(810, 735), new Coord(1110, 735)}};
    public static final int[][] SPAWN_SIGN = {{RIT_SIGN, RIT_SIGN}, {RIT_SIGN, RIT_SIGN}, {RIT_SIGN, RIT_SIGN}};
    public static final int[][] SPAWN_PLATFORM_NO = {{1, 2}, {0, 0}, {0, 0}};
    public static final int[] STAGE_BUTTONO = {1, 2, 3};
    public static final int FLASH_HZ = 10;
    public static final int FLASH_SIZE = 10;

    // Slideshow menu constants
    public static final int NUM_SLIDES = 6;
    public static final int BOSS_SLIDE_NO = 3;
    public static final int DOCTOR_ANIM_STATE = Doctor.LAUGH;
    public static final int DRAGON_ANIM_STATE = Dragon.BARF;
    public static final int BIRD_ANIM_STATE = Bird.TWEAK;
    public static final int DOCTOR_STILL_SPRITE_NO = 3;
    public static final int DRAGON_STILL_SPRITE_NO = 2;
    public static final int BIRD_STILL_SPRITE_NO = 2;
    public static final Coord DOCTOR_ANIM_COORD = new Coord(34, 95);
    public static final Coord DRAGON_ANIM_COORD = new Coord(681, 59);
    public static final Coord BIRD_ANIM_COORD = new Coord(1276, 59);
    public static final Coord DOCTOR_ANIM_SIZE = new Coord(441, 578);
    public static final Coord DRAGON_ANIM_SIZE = new Coord(490, 603);
    public static final Coord BIRD_ANIM_SIZE = new Coord(612, 578);
    public static final Coord DOCTOR_ANIM_MID_COORD = DOCTOR_ANIM_COORD.add(DOCTOR_ANIM_SIZE.scaledBy(0.5));
    public static final Coord DRAGON_ANIM_MID_COORD = DRAGON_ANIM_COORD.add(DRAGON_ANIM_SIZE.scaledBy(0.5));
    public static final Coord BIRD_ANIM_MID_COORD = BIRD_ANIM_COORD.add(BIRD_ANIM_SIZE.scaledBy(0.5));
    public static final int THANKS_SLIDE_NO = 5;
    public static final Coord CHROMATIC_SIZE = new Coord(993.0 / 960 * SCREEN_SIZE.y);
    public static final Coord CHROMATIC_MID_COORD = new Coord(SCREEN_CENTER.x, 777.0 / 960 * SCREEN_SIZE.y);
    public static final Coord CHROMATIC_COORD = CHROMATIC_MID_COORD.add(CHROMATIC_SIZE.scaledBy(-0.5));
    public static final int CHROMATIC_ROT_LEN = MAX_TICK_RATE * 3;
    public static final int CONTROLS_SLIDE_NO = 0;

    // Controls constants
    public static final int CONTROLS_BUTTON_X_START = (int) SCREEN_CENTER.x;
    public static final int CONTROLS_BUTTON_SPACING_X = 506;
    public static final int[] CONTROLS_BUTTON_Y = {352, 473, 583, 691};
    public static final int[] SHOOT_BUTTON_Y = {787, 870};
    public static final Coord CONTROLS_BUTTON_SIZE = new Coord(482, 62);
    public static final int CONTROLS_KEY = 0;
    public static final int SHOOT_KEY = 1;

    // Boss constants
    public static final int NUM_DIFFICULTY = 6;
    public static final double[] DIFFICULTY_MULT = {0, 0.5, 0.65, 1.0, 1.35, 1.5, 2};
    public static final String[] DIFFICULTY_NAME = {"WHAT BOSS?", "V. EZ", "EZ", "MED", "HARD", "V. HARD", "GOD ITSELF"};
    public static final int NO_DIFFICULTY = 0;
    public static final int DEFAULT_DIFFICULTY = 3;

    // Misc
    public static final int MAX_RGB_VAL = 255;
    public static final double EPSILON = 1e-15;
    public static BufferedImage placeHolder;

    // Gamemode constants
    public static final int NUM_GAMEMODES = 3;
    public static final int ALLPVE = 0;
    public static final int PVP = 1;
    public static final int PVPVE = 2;
    public static final String[] GAMEMODE_NAME = {"ALL PVE", "PVP", "PVPVE"};

    // Lives constants
    public static final int MAX_LIVES = 8;
    public static final int INF_LIVES = MAX_LIVES + 1;
    public static final int DEFAULT_LIVES = 3;

    // Surge constants
    public static final int NUM_SURGE_IMAGES = 5;
    public static final int SURGE_FRAME_HZ = 6;
    public static final int SURGE_TIME = NUM_SURGE_IMAGES * SURGE_FRAME_HZ;
    public static final Coord SURGE_SIZE = new Coord(741, 949);
    public static final int SURGE_SPRITE_WIN_CHECK = 2;

    // Directory constants
    public static final String MISC_DIR = "misc/";
    public static final String MENUS_DIR = "menus/";
    public static final String SLIDESHOW_DIR = "slideshow/";
    public static final String PLAYER_SPRITES_DIR = "player sprites/";
    public static final String PLAYER_PROJS_DIR = "player projectiles/";
    public static final String EXPLOSIONS_DIR = "explosions/";
    public static final String DOCTOR_PROJS_DIR = "doctor projectiles/";
    public static final String DRAGON_PROJS_DIR = "dragon projectiles/";
    public static final String BIRD_PROJS_DIR = "bird projectiles/";
    public static final String PUNK_PROJS_DIR = "punk projectiles/";
    public static final String DOCTOR_DIR = "doctor/";
    public static final String DRAGON_DIR = "dragon/";
    public static final String BIRD_DIR = "bird/";
    public static final String PUNK_DIR = "punk/";
    public static final String MUSIC_DIR = "music/";
    public static final String SFX_DIR = "sfx/";

    // Transition constants (Next avail: 12)
    public static final int NO_TRANS = -1;
    // Fade in
    public static final int FADE_IN = 0;
    public static final int FADE_IN_LEN = 10;

    // Ready Fade out
    public static final int READY_FADE = 1;
    public static final int READY_FADE_LEN = 30;

    // Countdown
    public static final int COUNTDOWN = 2;
    public static final int COUNTDOWN_FADE_IN_LEN = 10;
    public static final int COUNTDOWN_LEN = 180;
    public static final int FIGHT_TRANS_LEN = 6;
    public static final int FIGHT_TEXT_LEN = 60;
    public static final int FIGHT_FLASH_HZ = 3;
    public static final int READY_TEXT = 2;
    public static final int FIGHT_TEXT_START = 0;
    public static final Coord FIGHT_SIZE = new Coord(986, 177);
    public static final Coord READY_SIZE = new Coord(1024, 174);

    // Game end
    public static final int GAME_SET = 3;
    public static final int GAME_OVER = 4;
    public static final int GAME_END_LEN = 180;
    public static final int GAME_END_TEXT_TRANS_LEN = 6;
    public static final int GAME_END_TEXT_LEN = 60;
    public static final Coord GAME_SET_SIZE = new Coord(538, 133);
    public static final Coord GAME_OVER_SIZE = new Coord(609, 130);

    // Results stats counting up
    public static final int RESULTS_COUNTING = 5;
    public static final int RESULTS_COUNTING_LEN = 60;
    public static final int RESULTS_FADE_LEN = 30;

    // In-game Pause
    public static final int PAUSE = 6;
    public static final int PAUSE_FADE_LEN = 6;

    // Start animation
    public static final int START_ANIM = 7;
    public static final int LETTER_APPEAR_HZ = 4;
    public static final int TOT_LETTER_ANIM_LEN = (Letter.NUM_LETTERS - 1) * LETTER_APPEAR_HZ + Letter.LETTER_ANIM_LEN;
    public static final int NUM_SLAM_LEN = 8;
    public static final int STUDIO_PAUSE_LEN = 60;
    public static final int STUDIO_LEN = 240;
    public static final int START_ANIM_LEN = STUDIO_LEN + TOT_LETTER_ANIM_LEN + NUM_SLAM_LEN;

    // White fade out
    public static final int FLASH = 8;
    public static final int FLASH_LEN = 6;

    // White fade in
    public static final int FLASH_FADE = 9;
    public static final int FLASH_FADE_LEN = 70;
    public static final int TRUE_FLASH_FADE_LEN = 10;

    // Rules
    public static final int RULES = 10;
    public static final int RULES_FADE_LEN = 6;

    // Change key bind
    public static final int CHANGE_KEY_BIND = 11;
    public static final int CHANGE_KEY_BIND_FADE_LEN = 6;

    // Color constants
    public static final Color PURPLE = new Color(186, 122, 255);

    // Game States
    // -1 <- Studio Animation + Start menu
    // 0 <- Home Screen
    // 1 <- Choose your fight screen
    // 2 <- In-Game Screen
    // 3 <- Game End Screen
    // 4 <- Credit/Tutorial Screen
    // 5 <- Battle Log Screen
    public static int gameState = DEV_MODE? CHOOSE_FIGHT_GS: STUDIO_ANIM_GS;

    // Players
    public static Omegaman[] omegaman = new Omegaman[Omegaman.NUM_PLAYERS];
    public static int[][] loadouts = {{NO_WEAPON, NO_WEAPON}, {NO_WEAPON, NO_WEAPON}};
    public static int[][] controls = {{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D}, {KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT}};
    public static int[][] shtKeys = {{KeyEvent.VK_C, KeyEvent.VK_V}, {KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2}};
    public static int[][] loadoutButtono = {{11, 12}, {13, 14}};

    // Boss
    public static LinkedList<Pair<Integer, Boss>> babyBosses = new LinkedList<>();
    public static LinkedList<Boss> bosses = new LinkedList<>();
    public static LinkedList<Boss> deadBosses = new LinkedList<>();

    // Settings
    public static int gameMode = DEV_MODE? PVP: ALLPVE;
    public static int difficulty = DEV_MODE? NO_DIFFICULTY: DEFAULT_DIFFICULTY;
    public static int lives = (DEV_MODE || gameMode == PVPVE)? INF_LIVES: DEFAULT_LIVES;

    // Explosions
    public static Deque<Explosion> explosionQ = new LinkedList<>();

    // Projectiles
    public static HashSet<Projectile> babyProjectiles = new HashSet<>();
    public static HashSet<Projectile> projectiles = new HashSet<>();
    public static HashSet<Projectile> deadProjectiles = new HashSet<>();

    // Mouse/Keyboard Events
    public static Coord mouse = new Coord();
    public static boolean clicked;
    public static HashSet<Integer> pressedKeys = new HashSet<>();

    // Cheat stats
    public static int tickRate = MAX_TICK_RATE;
    public static boolean fpsCntVis = false;
    public static boolean fpsCntKeyPressed = false;
    public static boolean hitBoxVis = false;
    public static boolean hitBoxKeyPressed = false;
    public static boolean slowMode = false;
    public static boolean slowModeKeyPressed = false;

    // Stage stats
    public static int stageNo = RANDOM_STAGE;
    public static Stage[] stage = new Stage[NUM_STAGES];
    public static int stageFlashCounter = 0;

    // Home menu buttons
    public static HashMap<Integer, Button> homeButtons = new HashMap<>();

    // Choose fight Buttons
    public static HashMap<Integer, Button> chooseButtons = new HashMap<>();
    public static int buttonPressed = NO_BUTTON_HIT;
    public static Button selectedIcon;
    public static BufferedImage[] weaponIcon = new BufferedImage[TOT_NUM_WEAPONS];
    public static int weaponIconFlashCounter = 0;
    public static int readyCounter = NOT_READY;

    // Pause buttons
    public static HashMap<Integer, Button> pauseButtons = new HashMap<>();

    // Rules buttons
    public static HashMap<Integer, Button> rulesButtons = new HashMap<>();

    // Game end Buttons
    public static HashMap<Integer, Button> gameEndButtons = new HashMap<>();
    public static ArrayList<TextBox> gameEndTextBoxes = new ArrayList<>();

    // Battle log buttons
    public static HashMap<Integer, Button> battleLogButtons = new HashMap<>();
    public static BufferedImage smlButtonImg;
    public static BufferedImage medButtonImg;

    // Slideshow buttons
    public static HashMap<Integer, Button> slideshowButtons = new HashMap<>();

    // Controls buttons
    public static HashMap<Integer, Button> controlsButtons = new HashMap<>();
    public static BufferedImage controlsButtonImg;
    public static BufferedImage bindKeyOverlay;
    public static int playerToChangeKey;
    public static int typeToChangeKey;
    public static int numToChangeKey;

    // Start menu images
    public static BufferedImage startBg;
    public static BufferedImage titleNum;
    public static BufferedImage pressAnyText;
    public static BufferedImage studioLogo;

    // Home menu images
    public static BufferedImage home;
    public static BufferedImage homeButtonImg;
    public static BufferedImage menuMan;

    // Choose menu images
    public static BufferedImage chooseMenu;
    public static BufferedImage buttonImg;
    public static BufferedImage addWeaponIcon;
    public static BufferedImage readyBar;
    public static BufferedImage[] countdownText = new BufferedImage[3];
    public static BufferedImage[] gameOver = new BufferedImage[2];
    public static BufferedImage[] gameSet = new BufferedImage[2];
    public static BufferedImage randomStageIcon;
    public static BufferedImage randomWeaponIcon;

    // Rules images
    public static BufferedImage rulesBg;
    public static BufferedImage gameModeDescBar;
    public static BufferedImage gameModeDescEnd;
    public static BufferedImage rulesButtonImg;

    // Pause images
    public static BufferedImage pausedBg;

    // Game end images
    public static BufferedImage[] flash = new BufferedImage[2 + Omegaman.NUM_PLAYERS];
    public static BufferedImage resultsTitle;
    public static BufferedImage battleNameBoxImg;

    // Battle log images
    public static BufferedImage battleLogBg;
    public static BufferedImage noBattle;

    // Slideshow images
    public static BufferedImage[] slides = new BufferedImage[NUM_SLIDES];
    public static BufferedImage chromatic;

    // Menu stats
    public static int transCounter = DEV_MODE? 0: START_ANIM_LEN;
    public static int transitiono = DEV_MODE? NO_TRANS: START_ANIM;

    // Start menu
    public static LinkedList<Integer> letterOrder = new LinkedList<>();
    public static HashSet<Letter> letters = new HashSet<>();
    public static int pressStartCounter = 0;

    // Battle end menu stats
    public static Battle battleDone;
    public static double flashRot;

    // Main menu stats
    public static int menuManCounter = 0;

    // Battle log stats
    public static ArrayList<Battle> battleLog = new ArrayList<>();
    public static int battleNo = 0;
    public static int sortNum = SortByTitle.NUM;
    public static boolean orderNormal = true;

    // Slideshow stats
    public static int slideNo;
    public static int slideCounter;

    // General game stats
    public static int screenShakeCounter = 0;
    public static Coord screenCoord = new Coord();

    // Frame stats
    public static int frameCounter;
    public static long lastFPSReset = System.currentTimeMillis();
    public static int curFPS;

    // Sounds
    public static Clip menuMusic;
    public static Clip endMusic;
    public static Clip superClick;
    public static Clip boosh;
    public static Clip cheer;
    public static Clip shing;

    // Timer Settings
    // Parameters: None
    // Return: None
    // Description: This is the thread that continuosly loops and repaints the panel
    public void run() {
        while(true) {
            repaint();
            try {
                Thread.sleep(1000 / tickRate);
            }
            catch(Exception e) {}
        }
    }

    // JPanel Settings
    // Parameters: None
    // Return: None
    // Description: THis is the constructor for the driver that sets up all the inputs and starts the thread
    public OmegaFight3(){
        setPreferredSize(new Dimension((int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y));
        // Adding  KeyListener, MouseListener, and MouseMotionListener
        this.setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        // Adding Timer
        Thread thread = new Thread(this);
        thread.start();
    }

    // Creating Game Window
    // Parameters:
    // args: Smth that java uses
    // Return: None
    // Description this is the main method where everything is initalized. This includes images and sounds
    public static void main(String[] args) throws IOException {
        // Misc image imoprting
        placeHolder = ImageIO.read(new File(MISC_DIR + "placeholder.jpg"));

        // Start menu importing
        startBg = ImageIO.read(new File(MENUS_DIR + "start.jpg"));
        titleNum = ImageIO.read(new File(MENUS_DIR + "number.png"));
        pressAnyText = ImageIO.read(new File(MENUS_DIR + "press start.png"));
        for (int i = 0; i != Letter.NUM_LETTERS; i++) {
            Letter.letters[i] = ImageIO.read(new File(MENUS_DIR + "letter" + i + ".png"));
        }
        studioLogo = ImageIO.read(new File(MENUS_DIR + "studio.png"));

        // Home menu image importing
        home = ImageIO.read(new File(MENUS_DIR + "home.jpg"));
        homeButtonImg = ImageIO.read(new File(MENUS_DIR + "home button.png"));
        menuMan = ImageIO.read(new File(MENUS_DIR + "menuman.png"));

        // Paused images importing
        pausedBg = ImageIO.read(new File(MENUS_DIR + "paused.png"));

        // Choose Menu image importing
        chooseMenu = ImageIO.read(new File(MENUS_DIR + "choose fight.jpg"));
        buttonImg = ImageIO.read(new File(MENUS_DIR + "button.jpg"));
        readyBar = ImageIO.read(new File(MENUS_DIR + "ready.jpg"));
        countdownText[READY_TEXT] = ImageIO.read(new File(MENUS_DIR + "ready text.png"));
        for (int i = 0; i != 2; i++) {
            countdownText[FIGHT_TEXT_START + i] = ImageIO.read(new File(MENUS_DIR + "fight" + i + ".png"));
            gameOver[i] = ImageIO.read(new File(MENUS_DIR + "game over" + i + ".png"));
            gameSet[i] = ImageIO.read(new File(MENUS_DIR + "game set" + i + ".png"));
        }
        randomStageIcon = ImageIO.read(new File(MENUS_DIR + "random stage.jpg"));
        randomWeaponIcon = ImageIO.read(new File(MENUS_DIR + "random weapon icon.png"));
        rulesBg = ImageIO.read(new File(MENUS_DIR + "rules.png"));

        // Rules sub menu importing
        gameModeDescBar = ImageIO.read(new File(MENUS_DIR + "game mode desc.jpg"));
        gameModeDescEnd = ImageIO.read(new File(MENUS_DIR + "game mode desc end.png"));
        rulesButtonImg = ImageIO.read(new File(MENUS_DIR + "rules button.png"));

        // Game end image importing
        for (int i = 0; i != 2 + Omegaman.NUM_PLAYERS; i++) {
            flash[i] = ImageIO.read(new File(MENUS_DIR + (i - 2) + "flash.jpg"));
        }
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            Battle.happyMan[i] = ImageIO.read(new File(MENUS_DIR + i + "happyMan.png"));
            Battle.sadMan[i] = ImageIO.read(new File(MENUS_DIR + i + "sadMan.png"));
        }
        resultsTitle = ImageIO.read(new File(MENUS_DIR + "results title.png"));
        for (int i = 0; i != NUM_GAMEMODES; i++) {
            Battle.scoreBoard[i] = ImageIO.read(new File(MENUS_DIR + i + "scoreboard.jpg"));
        }
        battleNameBoxImg = ImageIO.read(new File(MENUS_DIR + "battle name box.png"));

        // Battle log image importing
        battleLogBg = ImageIO.read(new File(MENUS_DIR + "battle.jpg"));
        noBattle = ImageIO.read(new File(MENUS_DIR + "no battle.jpg"));
        smlButtonImg = ImageIO.read(new File(MENUS_DIR + "sml button.jpg"));
        medButtonImg = ImageIO.read(new File(MENUS_DIR + "med button.jpg"));

        // Slideshow image importing
        for (int i = 0; i != NUM_SLIDES; i++) {
            slides[i] = ImageIO.read(new File(SLIDESHOW_DIR + "slide" + i + (i == THANKS_SLIDE_NO? ".png": ".jpg")));
        }
        chromatic = ImageIO.read(new File(SLIDESHOW_DIR + "chromatic.jpg"));

        // Controls image importing
        controlsButtonImg = ImageIO.read(new File(SLIDESHOW_DIR + "controls button.jpg"));
        bindKeyOverlay = ImageIO.read(new File(SLIDESHOW_DIR + "bind key overlay.png"));

        // Smoke image importing
        for (int i = 0; i != Smoke.NUM_SMOKES; i++) {
            Smoke.smokes[i] = ImageIO.read(new File(EXPLOSIONS_DIR + "smoke" + i + ".png"));
        }
        
        // Player Weapon image importing
        addWeaponIcon = ImageIO.read(new File(MENUS_DIR + "no weapon.png"));
        Bullet.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "bullet.png"));
        weaponIcon[Omegaman.BULLET_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "bullet icon.png"));
        Shotgun.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "shotgun.png"));
        weaponIcon[Omegaman.SHOTGUN_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "shotgun icon.png"));
        Spammer.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "spammer.png"));
        weaponIcon[Omegaman.SPAMMER_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "spammer icon.png"));
        Sniper.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "sniper.png"));
        weaponIcon[Omegaman.SNIPER_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "sniper icon.png"));
        Laser.ball = ImageIO.read(new File(PLAYER_PROJS_DIR + "ball.png"));
        Laser.beam = ImageIO.read(new File(PLAYER_PROJS_DIR + "beam.png"));
        weaponIcon[Omegaman.BOOMER_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "boomer icon.png"));
        Spike.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "spike.png"));
        weaponIcon[Omegaman.SPIKE_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "spike icon.png"));
        Thorn.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "thorn.png"));
        weaponIcon[Omegaman.FIREBALL_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "fireball icon.png"));
        for (int i = 0; i != Fireball.NUM_SPRITES; i++) {
            Fireball.image[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + "fireball" + i + ".png"));
        }
        for (int i = 0; i != Phoenix.NUM_SPRITES; i++) {
            Phoenix.image[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + "phoenix" + i + ".png"));
        }
        Phoenix.chargingImage = ImageIO.read(new File(PLAYER_PROJS_DIR + "phoenix egg.png"));
        Star.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "star.png"));
        for (int i = 0; i != GlueBomb.NUM_STATES; i++) {
            GlueBomb.glueImage[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "glue.png"));
        }
        for (int i = 0; i != GlueBomb.BOMB_NUM_SPRITES; i++) {
            GlueBomb.bombImage[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + "C4" + i + ".png"));
        }
        weaponIcon[Omegaman.GLUE_BOMB_WEAPON_NO] = ImageIO.read(new File(MENUS_DIR + "glue bomb icon.png"));
        Spark.image = ImageIO.read(new File(PLAYER_PROJS_DIR + "spark.png"));
        Spark.reticleImg = ImageIO.read(new File(PLAYER_PROJS_DIR + "reticle.png"));
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            Rocket.images[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "rocket.png"));
            Firework.images[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "firework.png"));
            Firework.chargingImages[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "fireworkCharge.png"));
            Missile.images[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "missile.png"));
            Boomer.images[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "boomer.png"));
            Bouncer.images[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "bouncer.png"));
            Splitter.images[i] = ImageIO.read(new File(PLAYER_PROJS_DIR + i + "splitter.png"));
        }

        Char.fireImg = ImageIO.read(new File(MISC_DIR + "fire.png"));
        Char.FIRE_DMG *= Omegaman.PERC_MULT;

        // Player sprite importing
        for (int playerNo = 0; playerNo != Omegaman.NUM_PLAYERS; playerNo++) {
            Omegaman.percentDisplay[playerNo] = ImageIO.read(new File(Omegaman.HUD_DIR + playerNo + "percentdisplay.png"));

            // Load sprites
            Omegaman.sprite[playerNo][Omegaman.IDLE_SPRITE] = ImageIO.read(new File(PLAYER_SPRITES_DIR + playerNo + "idle.png"));
            for (int i = 0; i != Omegaman.NUM_RUN_SPRITE; i++) {
                Omegaman.sprite[playerNo][i + 1] = ImageIO.read(new File(PLAYER_SPRITES_DIR + playerNo + "run" + i + ".png"));
            }
            Omegaman.sprite[playerNo][Omegaman.JUMP_SPRITE] = ImageIO.read(new File(PLAYER_SPRITES_DIR + playerNo + "jump.png"));
            Omegaman.sprite[playerNo][Omegaman.HURT_SPRITE] = ImageIO.read(new File(PLAYER_SPRITES_DIR + playerNo + "hurt.png"));
            Omegaman.sprite[playerNo][Omegaman.DASH_SPRITE] = ImageIO.read(new File(PLAYER_SPRITES_DIR + playerNo + "dash.png"));

            // Load surge images
            for (int i = 0; i != NUM_SURGE_IMAGES; i++) {
                Omegaman.surge[playerNo][i] = ImageIO.read(new File(EXPLOSIONS_DIR + playerNo + "surge" + i + ".png"));
            }

            // Load Face
            Omegaman.face[playerNo] = ImageIO.read(new File(Omegaman.HUD_DIR + playerNo + "face.png"));
        }

        // Explosion image importing
        for (int i = 0; i != Explosion.NUM_EXPLOSIONS; i++) {
            Explosion.explosions[i] = ImageIO.read(new File(EXPLOSIONS_DIR + "explosion" + i + ".png"));
        }

        Wake.wakeImg = ImageIO.read(new File(PLAYER_SPRITES_DIR + "wake.png"));

        // Boss surge image importing
        for (int i = 0; i != NUM_SURGE_IMAGES; i++) {
            Boss.surge[i] = ImageIO.read(new File(EXPLOSIONS_DIR + "bsurge" + i + ".png"));
        }

        // Doctor projectile image importing
        for (int i = 0; i != Fastener.NUM_TYPES; i++) {
            Fastener.images[i] = new BufferedImage[Fastener.NUM_SPRITES[i]];
            for (int j = 0; j != Fastener.NUM_SPRITES[i]; j++) {
                Fastener.images[i][j] = ImageIO.read(new File(DOCTOR_PROJS_DIR + Fastener.TYPE_NAME[i] + j + ".png"));
            }
        }
        for (int i = 0; i != Energy.NUM_SPRITES; i++) {
            Energy.images[i] = ImageIO.read(new File(DOCTOR_PROJS_DIR + "energy" + i + ".png"));
        }
        for (int i = 0; i != Pincer.NUM_SPRITES; i++) {
            Pincer.images[i] = ImageIO.read(new File(DOCTOR_PROJS_DIR + "pincer" + i + ".png"));
        }
        for (int i = 0; i != Bombot.NUM_SPRITES; i++) {
            Bombot.images[i] = ImageIO.read(new File(DOCTOR_PROJS_DIR + "bombot" + i + ".png"));
        }

        // Dragon projectile image importing
        for (int i = 0; i != Ring.NUM_SPRITES; i++) {
            Ring.images[i] = ImageIO.read(new File(DRAGON_PROJS_DIR + "ring" + i + ".png"));
        }
        for (int i = 0; i != Meteor.NUM_SPRITES; i++) {
            Meteor.images[i] = ImageIO.read(new File(DRAGON_PROJS_DIR + "meteor" + i + ".png"));
        }
        for (int i = 0; i != Bubble.NUM_SPRITES; i++) {
            Bubble.images[i] = ImageIO.read(new File(DRAGON_PROJS_DIR + "bubble" + i + ".png"));
        }
        for (int i = 0; i != Fire.NUM_SPRITES; i++) {
            Fire.images[i] = ImageIO.read(new File(DRAGON_PROJS_DIR + "fire" + i + ".png"));
        }

        // Bird projectile image importing
        for (int i = 0; i != Egg.NUM_STATES; i++) {
            Egg.images[i] = new BufferedImage[Egg.NUM_TYPES[i]][Egg.NUM_SPRITES[i]];
            for (int j = 0 ; j != Egg.NUM_TYPES[i]; j++) {
                for (int k = 0; k != Egg.NUM_SPRITES[i]; k++) {
                    Egg.images[i][j][k] = ImageIO.read(new File(BIRD_PROJS_DIR + j + Egg.STATE_NAMES[i] + k + ".png"));
                }
            }
        }
        for (int i = 0; i != Feather.NUM_SPRITES; i++) {
            Feather.images[i] = ImageIO.read(new File(BIRD_PROJS_DIR + "feather" + i + ".png"));
        }
        for (int i = 0; i != Diver.NUM_SPRITES; i++) {
            Diver.images[i] = ImageIO.read(new File(BIRD_PROJS_DIR + "diver" + i + ".png"));
        }

        // Punk projectile image importing
        for (int i = 0; i != Plush.NUM_STATES; i++) {
            for (int j = 0; j != Plush.NUM_SPRITES; j++) {
                Plush.images[i][j] = ImageIO.read(new File(PUNK_PROJS_DIR + i + "plush" + j + ".png"));
            }
        }

        // Stages
        for (int i = 0; i != NUM_STAGES; i++) {
            stage[i] = new Stage(STAGE_NAME[i], PLATFORMS[i], SPAWN_COORDS[i], SPAWN_SIGN[i], SPAWN_PLATFORM_NO[i], STAGE_BUTTONO[i]);
        }

        // Boss Image importing
        for (int i = 0; i != Doctor.STATE_NUM_SPRITES[Boss.DEAD] + Doctor.STATE_NUM_SPRITES[Boss.IDLE]; i++) {
            Doctor.docSprite[i] = ImageIO.read(new File(DOCTOR_DIR + (i < Doctor.STATE_NUM_SPRITES[Boss.DEAD] ? "dead" + i : "idle" + (i - Doctor.STATE_NUM_SPRITES[Boss.DEAD])) +  ".png"));
        }
        for (int i = 0; i != Dragon.STATE_NUM_SPRITES[Boss.DEAD] + Dragon.STATE_NUM_SPRITES[Boss.IDLE]; i++) {
            Dragon.dragonSprite[i] = ImageIO.read(new File(DRAGON_DIR + (i < Dragon.STATE_NUM_SPRITES[Boss.DEAD] ? "dead" + i : "idle" + (i - Dragon.STATE_NUM_SPRITES[Boss.DEAD])) + ".png"));
        }
        for (int i = 0; i != Bird.STATE_NUM_SPRITES[Boss.DEAD] + Bird.STATE_NUM_SPRITES[Boss.IDLE]; i++) {
            Bird.birdSprite[i] = ImageIO.read(new File(BIRD_DIR + (i < Bird.STATE_NUM_SPRITES[Boss.DEAD] ? "dead" + i : "idle" + (i - Bird.STATE_NUM_SPRITES[Boss.DEAD])) + ".png"));
        }
        Bird.sign = ImageIO.read(new File(BIRD_DIR + "sign.png"));
        for (int i = 0; i != Punk.STATE_NUM_SPRITES[Boss.DEAD] + Punk.STATE_NUM_SPRITES[Boss.IDLE]; i++) {
            Punk.punkSprite[i] = ImageIO.read(new File(PUNK_DIR + (i < Punk.STATE_NUM_SPRITES[Boss.DEAD] ? "dead" + i : "idle" + (i - Punk.STATE_NUM_SPRITES[Boss.DEAD])) + ".png"));
        }

        // Boss state animation length calculation
        for (int i = 0; i != Doctor.NUM_STATES; i++) {
            Doctor.STATE_ANIM_LEN[i] = Doctor.STATE_NUM_SPRITES[i] * Doctor.STATE_SPRITE_HZ[i];
        }
        for (int i = 0; i != Dragon.NUM_STATES; i++) {
            Dragon.STATE_ANIM_LEN[i] = Dragon.STATE_NUM_SPRITES[i] * Dragon.STATE_SPRITE_HZ[i];
        }
        for (int i = 0; i != Bird.NUM_STATES; i++) {
            Bird.STATE_ANIM_LEN[i] = Bird.STATE_NUM_SPRITES[i] * Bird.STATE_SPRITE_HZ[i];
        }

        // Doctor image importing
        for (int i = 1; i != Doctor.NUM_STATES; i++) {
            Doctor.STATE_SPRITE_START[i] = Doctor.STATE_SPRITE_START[i - 1] + Doctor.STATE_NUM_SPRITES[i - 1];
        }
        for (int i = Doctor.STATE_SPRITE_START[Boss.IDLE] + Doctor.STATE_NUM_SPRITES[Boss.IDLE], j = 3; i != Doctor.TOT_NUM_SPRITES; i++) {
            if (j != Doctor.NUM_STATES && Doctor.STATE_SPRITE_START[j] == i) j++;
            Doctor.docSprite[i] = ImageIO.read(new File(DOCTOR_DIR + Doctor.STATE_NAME[j - 3] + (i - Doctor.STATE_SPRITE_START[j - 1]) + ".png"));
        }

        // Dragon image importing
        for (int i = 1; i != Dragon.NUM_STATES; i++) {
            Dragon.STATE_SPRITE_START[i] = Dragon.STATE_SPRITE_START[i - 1] + Dragon.STATE_NUM_SPRITES[i - 1];
        }
        for (int i = Dragon.STATE_SPRITE_START[Boss.IDLE] + Dragon.STATE_NUM_SPRITES[Boss.IDLE], j = 3; i != Dragon.TOT_NUM_SPRITES; i++) {
            if (j != Dragon.NUM_STATES && Dragon.STATE_SPRITE_START[j] == i) j++;
            Dragon.dragonSprite[i] = ImageIO.read(new File(DRAGON_DIR + Dragon.STATE_NAME[j - 3] + (i - Dragon.STATE_SPRITE_START[j - 1]) + ".png"));
        }

        // Bird image importing
        for (int i = 1; i != Bird.NUM_STATES; i++) {
            Bird.STATE_SPRITE_START[i] = Bird.STATE_SPRITE_START[i - 1] + Bird.STATE_NUM_SPRITES[i - 1];
        }
        for (int i = Bird.STATE_SPRITE_START[Boss.IDLE] + Bird.STATE_NUM_SPRITES[Boss.IDLE], j = 3; i != Bird.TOT_NUM_SPRITES; i++) {
            if (j != Bird.NUM_STATES && Bird.STATE_SPRITE_START[j] == i) j++;
            Bird.birdSprite[i] = ImageIO.read(new File(BIRD_DIR + Bird.STATE_NAME[j - 3] + (i - Bird.STATE_SPRITE_START[j - 1]) + ".png"));
        }

        // Punk image importing
        for (int i = 1; i != Punk.NUM_STATES; i++) {
            Punk.STATE_SPRITE_START[i] = Punk.STATE_SPRITE_START[i - 1] + Punk.STATE_NUM_SPRITES[i - 1];
        }

        // Buttons
        // Home menu buttons
        for (int i = 0 ; i != HOME_NUM_BUTTONS; i++) {
            homeButtons.put(HOME_BUTTONO[i], new Button(homeButtonImg, HOME_BUTTON_FONT, new Coord(HOME_BUTTON_X, HOME_BUTTON_FIRST_Y + HOME_BUTTON_SPACING * i), HOME_BUTTON_SIZE.copy(), HOME_BUTTON_TEXT[i], HOME_BUTTONO[i], Button.SHADOW));
        }

        // Choose your fight menu buttons 
        chooseButtons.put(CHOOSE_BACK_BUTTONO, newBackButton(CHOOSE_BACK_BUTTONO));
        int stageIconY = (BLACK_BAR_TOP + BLACK_BAR_BOTTOM) / 2;
        for (int i = 0; i != NUM_STAGES; i++) {
            chooseButtons.put(stage[i].buttono, new Button(stage[i].image, STAGE_FONT, new Coord(STAGE_SPACING * (i + 1) + STAGE_BUTTON_SIZE.x * (i + 0.5), stageIconY), STAGE_BUTTON_SIZE.copy(), stage[i].stageName.toUpperCase(), stage[i].buttono, Button.HIGHLIGHT));
        }
        chooseButtons.put(RANDOM_STAGE_BUTTONO, new Button(randomStageIcon, STAGE_FONT, new Coord(STAGE_SPACING * (NUM_STAGES + 1) + STAGE_BUTTON_SIZE.x * (NUM_STAGES + 0.5), stageIconY), STAGE_BUTTON_SIZE.copy(), "RANDOM STAGE", RANDOM_STAGE_BUTTONO, Button.HIGHLIGHT));
        
        for (int i = 0; i != TOT_NUM_WEAPONS; i++) {
            chooseButtons.put(WEAPON_BUTTONO[i], new Button(weaponIcon[i], new Coord(DIVIDER_RIT_X + SPACING * (i % WEAPON_ICONS_PER_ROW + 1) + WEAPON_ICON_SIZE.x * (0.5 + i % WEAPON_ICONS_PER_ROW),
            BLACK_BAR_TOP + SPACING * (i / WEAPON_ICONS_PER_ROW + 1) + WEAPON_ICON_SIZE.y * (0.5 + i / WEAPON_ICONS_PER_ROW)), WEAPON_ICON_SIZE.copy(), WEAPON_BUTTONO[i]));
        }
        chooseButtons.put(RANDOM_WEAPON_BUTTONO, new Button(randomWeaponIcon, new Coord(DIVIDER_RIT_X + SPACING * (TOT_NUM_WEAPONS % WEAPON_ICONS_PER_ROW + 1) + WEAPON_ICON_SIZE.x * (0.5 + TOT_NUM_WEAPONS % WEAPON_ICONS_PER_ROW),
            BLACK_BAR_TOP + SPACING * (TOT_NUM_WEAPONS / WEAPON_ICONS_PER_ROW + 1) + WEAPON_ICON_SIZE.y * (0.5 + TOT_NUM_WEAPONS / WEAPON_ICONS_PER_ROW)), WEAPON_ICON_SIZE.copy(), RANDOM_WEAPON_BUTTONO));
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                chooseButtons.put(loadoutButtono[i][j], new Button(addWeaponIcon, new Coord(LOADOUT_ICON_X[i] + WEAPON_ICON_SIZE.x * (0.5 + j) + SPACING * j, LOADOUT_ICON_Y + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), loadoutButtono[i][j]));
            }
        }
        chooseButtons.put(READY_BUTTONO, new Button(readyBar, new Coord(0, SCREEN_CENTER.y), READY_BAR_SIZE, READY_BUTTONO, false, false));
        Coord rulesAndBackButtonCoord = new Coord(SCREEN_SIZE.x - BUTTON_SIZE.x / 2 - SPACING, SPACING + BUTTON_SIZE.y / 2);
        chooseButtons.put(RULES_BUTTONO, new Button(buttonImg, BUTTON_FONT, rulesAndBackButtonCoord.copy(), BUTTON_SIZE.copy(), "RULES", RULES_BUTTONO, Button.SHADOW));

        // Rules buttons
        rulesButtons.put(RULES_BACK_BUTTONO, newBackButton(rulesAndBackButtonCoord.copy(), RULES_BACK_BUTTONO));
        rulesButtons.put(LIVES_BUTTONO, new Button(rulesButtonImg, RULES_BUTTON_FONT, new Coord(SCREEN_CENTER.x + RULES_BUTTON_SPACING.x * 1, RULES_BUTTON_START_Y + (RULES_BUTTON_DIST_Y) * 0), RULES_BUTTON_SIZE.copy(), "LIVES: " + (lives == INF_LIVES? "INFINITY": lives), LIVES_BUTTONO, Button.SHADOW));
        rulesButtons.put(BOSS_BUTTONO, new Button(rulesButtonImg, RULES_BUTTON_FONT, new Coord(SCREEN_CENTER.x + RULES_BUTTON_SPACING.x * 0, RULES_BUTTON_START_Y + (RULES_BUTTON_DIST_Y) * 1), RULES_BUTTON_SIZE.copy(), "BOSS: " + DIFFICULTY_NAME[difficulty], BOSS_BUTTONO, Button.SHADOW));
        rulesButtons.put(GAMEMODE_BUTTONO, new Button(rulesButtonImg, RULES_BUTTON_FONT, new Coord(SCREEN_CENTER.x + RULES_BUTTON_SPACING.x * -1, RULES_BUTTON_START_Y + (RULES_BUTTON_DIST_Y) * 2), RULES_BUTTON_SIZE.copy(), "GAMEMODE: " + GAMEMODE_NAME[gameMode], GAMEMODE_BUTTONO, Button.SHADOW));

        // Pause buttons
        for (int i = 0; i != PAUSE_NUM_BUTTONS; i++) {
            pauseButtons.put(PAUSE_BUTTONO[i], new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x, SCREEN_CENTER.y + BUTTON_SIZE.y * (i + 0.5) + SPACING * (i + 1)), BUTTON_SIZE.copy(), PAUSE_BUTTON_TEXT[i], PAUSE_BUTTONO[i], Button.SHADOW));
        }

        // Game end buttons
        gameEndTextBoxes.add(new TextBox(battleNameBoxImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x - (BATTLE_NAME_BOX_SIZE.x + RESULTS_SPACING) / 2, GAME_END_BUTTON_Y), BATTLE_NAME_BOX_SIZE.copy(), Button.SHADOW, TextBox.TEXT_BUFFER_X, false, false));
        gameEndButtons.put(NEXT_BATTLE_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x + (BUTTON_SIZE.x + RESULTS_SPACING) / 2, GAME_END_BUTTON_Y), BUTTON_SIZE.copy(), "NEXT BATTLE!", NEXT_BATTLE_BUTTONO, Button.SHADOW, false, false));
        
        // Battle log buttons
        battleLogButtons.put(BATTLE_LOG_BACK_BUTTONO, newBackButton(BATTLE_LOG_BACK_BUTTONO));
        battleLogButtons.put(BATTLE_NO_BACK_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x - HALF_BATTLE_NO_SIZE - ARROW_BUTTON_SPACING - SML_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), "<", BATTLE_NO_BACK_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_ALL_BACK_BUTTONO, new Button(medButtonImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x - HALF_BATTLE_NO_SIZE - ARROW_BUTTON_SPACING * 2 - SML_BUTTON_SIZE.x - MED_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), MED_BUTTON_SIZE.copy(), "<<", BATTLE_NO_ALL_BACK_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_NEXT_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x + HALF_BATTLE_NO_SIZE + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), ">", BATTLE_NO_NEXT_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_ALL_NEXT_BUTTONO, new Button(medButtonImg, BUTTON_FONT, new Coord(SCREEN_CENTER.x + HALF_BATTLE_NO_SIZE + ARROW_BUTTON_SPACING * 2 + SML_BUTTON_SIZE.x + MED_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), MED_BUTTON_SIZE.copy(), ">>", BATTLE_NO_ALL_NEXT_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_SORT_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING - BUTTON_SIZE.x / 2, SPACING + BUTTON_SIZE.y / 2), BUTTON_SIZE.copy(), "SORT BY: " + BATTLE_LOG_SORT_NAME[sortNum], BATTLE_NO_SORT_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_ORDER_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING - BUTTON_SIZE.x / 2, SPACING * 2 + BUTTON_SIZE.y * 1.5), BUTTON_SIZE.copy(), "ORDER BY: " + getOrderName(), BATTLE_NO_ORDER_BUTTONO, Button.SHADOW));

        // SLideshow buttons
        slideshowButtons.put(SLIDESHOW_BACK_BUTTONO, newBackButton(SLIDESHOW_BACK_BUTTONO));
        slideshowButtons.put(SLIDE_NO_BACK_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING * 2 - SML_BUTTON_SIZE.x * 1.5, SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), "<", SLIDE_NO_BACK_BUTTONO, Button.SHADOW));
        slideshowButtons.put(SLIDE_NO_NEXT_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING - SML_BUTTON_SIZE.x / 2, SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), ">", SLIDE_NO_NEXT_BUTTONO, Button.SHADOW));

        // Controls buttons
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            for (int j = 0; j != 4; j++) {
                controlsButtons.put(CONTROLS_BUTTONO[i][j], new Button(controlsButtonImg, BUTTON_FONT, new Coord(CONTROLS_BUTTON_X_START + i * CONTROLS_BUTTON_SPACING_X, CONTROLS_BUTTON_Y[j]), CONTROLS_BUTTON_SIZE.copy(), KeyEvent.getKeyText(controls[i][j]), CONTROLS_BUTTONO[i][j], Button.SHADOW));
            }
            for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                controlsButtons.put(SHOOT_BUTTONO[i][j], new Button(controlsButtonImg, BUTTON_FONT, new Coord(CONTROLS_BUTTON_X_START + i * CONTROLS_BUTTON_SPACING_X, SHOOT_BUTTON_Y[j]), CONTROLS_BUTTON_SIZE.copy(), KeyEvent.getKeyText(shtKeys[i][j]), SHOOT_BUTTONO[i][j], Button.SHADOW));
            }
        }

        // Battle log text file reading
        BufferedReader br = new BufferedReader(new FileReader(MENUS_DIR + BATTLE_LOG_FILE_NAME + ".txt"));
        int numBattles = Integer.parseInt(br.readLine());
        String stageName, stringStats[];
        String[] settings;
        int winner;
        double bossHealth, stats[][];
        for (int i = 0; i != numBattles; i++) {
            stageName = br.readLine();
            winner = Integer.parseInt(br.readLine());
            settings = br.readLine().split(" ");
            bossHealth = Double.parseDouble(br.readLine());
            stats = new double[Omegaman.NUM_PLAYERS][Omegaman.NUM_STATS];
            for (int j = 0; j != Omegaman.NUM_PLAYERS; j++) {
                stringStats = br.readLine().split(" ");
                for (int k = 0; k != Omegaman.NUM_STATS; k++) {
                    stats[j][k] = Double.parseDouble(stringStats[k]);
                }
            }
            battleLog.add(new Battle(stageName, winner, Integer.parseInt(settings[0]), Integer.parseInt(settings[1]), Integer.parseInt(settings[2]), bossHealth, stats));
            battleLog.get(i).name = br.readLine();
        }
        br.close();
        resortLog();

        // Import music and SFX
        try {
            menuMusic = loadClip(MUSIC_DIR + "menu music.wav");
            endMusic = loadClip(MUSIC_DIR + "end music.wav");
            superClick = loadClip(SFX_DIR + "super click.wav");
            Button.click = loadClip(SFX_DIR + "click.wav");
            Button.hover = loadClip(SFX_DIR + "hover.wav");
            for (int i = 0; i != Explosion.NUM_BOOMS; i++) {
                Explosion.boom[i] = loadClip(SFX_DIR + i + "boom.wav");
            }
            boosh = loadClip(SFX_DIR + "boosh.wav");
            cheer = loadClip(SFX_DIR + "cheer.wav");
            shing = loadClip(SFX_DIR + "shing.wav");
            Bullet.pew = loadClip(SFX_DIR + "pew.wav");
            Shotgun.bang = loadClip(SFX_DIR + "bang.wav");
            Firework.pow = loadClip(SFX_DIR + "pow.wav");
            Spammer.ratatat = loadClip(SFX_DIR + "ratatat.wav");
            Missile.fwoosh = loadClip(SFX_DIR + "fwoosh.wav");
            Sniper.baw = loadClip(SFX_DIR + "baw.wav");
            Laser.bew = loadClip(SFX_DIR + "bew.wav");
            Boomer.whoosh = loadClip(SFX_DIR + "whoosh.wav");
            Bouncer.vrrr = loadClip(SFX_DIR + "vrrr.wav");
            Spike.pop = loadClip(SFX_DIR + "pop.wav");
            Fireball.foom = loadClip(SFX_DIR + "foom.wav");
            Phoenix.caw = loadClip(SFX_DIR + "caw.wav");
            Dragon.donk = loadClip(SFX_DIR + "donk.wav");
            Dragon.roar = loadClip(SFX_DIR + "roar.wav");
            Bubble.bububup = loadClip(SFX_DIR + "bububup.wav");
            Fire.foosh = loadClip(SFX_DIR + "foosh.wav");
            Doctor.hahaha = loadClip(SFX_DIR + "hahaha.wav");
            Doctor.blblbl = loadClip(SFX_DIR + "blblbl.wav");
            Pincer.zzzClick = loadClip(SFX_DIR + "zzz click.wav");
            Bird.vrrrCaw = loadClip(SFX_DIR + "vrrr caw.wav");
            Bird.bacaw = loadClip(SFX_DIR + "bacaw.wav");
            Diver.fwoot = loadClip(SFX_DIR + "fwoot.wav");
            Punk.honk = loadClip(SFX_DIR + "honk.wav");
        }
        catch (Exception e) {}

        // Initialize the order at which the letters of the title will come in
        for (int i = 0; i != Letter.NUM_LETTERS; i++) {
            letterOrder.add((int) (letterOrder.size() * Math.random()), i);
        }
        
        // JFrame and JPanel
        JFrame frame = new JFrame("Omega Fight 3");
        OmegaFight3 panel = new OmegaFight3();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MISC_DIR + "icon.png"));
    }


    // Draw Screen
    // Parameters:
    // g: The Graphics object that I use to draw stuff
    // Return: None
    // Description:
    // THis is the method that is repeatedly called to draw the stuff on the panel
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        // Screen shake
        if (screenShakeCounter != 0) {
            screenShakeCounter = Math.min(SCREEN_SHAKE_MAX, screenShakeCounter);
            screenShakeCounter--;
            if (screenShakeCounter % SCREEN_SHAKE_HZ == 0) {
                screenCoord.x = randomSign() * Math.random();
                screenCoord.y = randomSign() * Math.random();
                screenCoord = screenCoord.scaledBy(screenShakeCounter * SCREEN_SHAKE_MULT);
            }
            g.translate((int) screenCoord.x, (int) screenCoord.y);
        }

        // Start gamestate
        if (gameState == STUDIO_ANIM_GS) {
            // Background
            g.drawImage(startBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            if (transitiono != START_ANIM) g.drawImage(titleNum, (int) (SCREEN_CENTER.x - TITLE_NUM_SIZE.x / 2), TITLE_NUM_Y, null);

            // Flash Press any button to start
            setOpacity((Math.sin(Math.PI * 2 / (PRESS_START_BLINK_HZ * 2) * (pressStartCounter - PRESS_START_BLINK_HZ / 2)) + 1) / 2, g2);
            g.drawImage(pressAnyText, (int) (SCREEN_CENTER.x - PRESS_START_SIZE.x / 2), PRESS_START_Y, null);
            resetOpacity(g2);

            // Transition
            if (transitiono != NO_TRANS) {
                drawTransition(g2);
                transition();
            }

            // No transition
            else {
                // Calculate press any button to start flashing
                pressStartCounter = (pressStartCounter + 1) % (PRESS_START_BLINK_HZ * 2);
                
                // User pressed start
                if (pressedKeys.size() != 0 || clicked) {
                    transitiono = FLASH;
                    transCounter = FLASH_LEN;
                    pressStartCounter = PRESS_START_BLINK_HZ;
                    play(superClick);
                }
            }
        }

        // Home gamestate
        else if(gameState == HOME_GS) {
            // Background
            g.drawImage(home, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            g.drawImage(menuMan, (int) (SCREEN_SIZE.x / 4 - MENU_MAN_SIZE.x / 2), (int) (SCREEN_SIZE.y - MENU_MAN_SIZE.y + Math.round((-Math.cos(Math.PI * 2 / MENU_MAN_ANIM_LEN * menuManCounter) + 1) / 2 * MENU_MAN_ANIM_DIST)), null);

            // Buttons
            drawButtons(homeButtons.values(), g);

            // Calculate menu omegamen's coordinates
            menuManCounter = (menuManCounter + 1) % MENU_MAN_ANIM_LEN;

            // Transition
            if (transitiono != NO_TRANS) {
                drawTransition(g2);
                transition();
            }

            // No transition
            else {
                // Check button actions
                actionPerformed();

                // If still not transitioning, process stuff
                if (transitiono == NO_TRANS) {
                    // Process buttons
                    processButtons(homeButtons.values());
                }
            }
        }

        // Choose your fight gamestate
        else if(gameState == CHOOSE_FIGHT_GS) {
            // Background
            g.drawImage(chooseMenu, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);

            // Flashing of selected stage
            stageFlashCounter = (stageFlashCounter + 1) % (FLASH_HZ * 2);
            if (stageFlashCounter < FLASH_HZ) g.setColor(PURPLE);
            else g.setColor(Color.YELLOW);
            Button stageButton;
            if (stageNo != RANDOM_STAGE) {
                stageButton = chooseButtons.get(stage[stageNo].buttono);
            }
            else {
                stageButton = chooseButtons.get(RANDOM_STAGE_BUTTONO);
            }
            Coord stageButtonSize = stageButton.size[stageButton.state];
            Coord selectCoord = stageButton.coord.add(stageButtonSize.scaledBy(-0.5)).add(-FLASH_SIZE);
            Coord selectSize = stageButtonSize.add(FLASH_SIZE * 2);
            g.fillRect((int) (selectCoord.x), (int) (selectCoord.y), (int) (selectSize.x), (int) (selectSize.y));

            // If user has selected loadout or weapon icon, flash icon
            if (selectedIcon != null) {
                weaponIconFlashCounter = (weaponIconFlashCounter + 1) % (FLASH_HZ * 2);
                if (weaponIconFlashCounter < FLASH_HZ) g.setColor(PURPLE);
                else g.setColor(Color.YELLOW);
                Coord selectedIconSize = selectedIcon.size[selectedIcon.state];
                selectCoord = selectedIcon.coord.add(selectedIconSize.scaledBy(-0.5)).add(-FLASH_SIZE);
                selectSize = selectedIconSize.add(FLASH_SIZE * 2);
                g.fillOval((int) (selectCoord.x), (int) (selectCoord.y), (int) (selectSize.x), (int) (selectSize.y));
            }

            // Draw buttons
            boolean canSeeReady = chooseButtons.get(READY_BUTTONO).canSee;
            if (canSeeReady) chooseButtons.get(READY_BUTTONO).canSee = false;
            drawButtons(chooseButtons.values(), g);
            if (canSeeReady) {
                chooseButtons.get(READY_BUTTONO).canSee = true;
                chooseButtons.get(READY_BUTTONO).draw(g);
            }

            // Transition
            if (transitiono != NO_TRANS) {
                drawTransition(g2);
                transition();
            }
            
            // No transition
            else {
                // Check button actions
                actionPerformed();

                // If still not transitioning, process stuff
                if (transitiono == NO_TRANS) {
                    // Process buttons
                    processButtons(chooseButtons.values());

                    // Calculate ready button
                    Button readyButton = chooseButtons.get(READY_BUTTONO); 
                    if (readyCounter == NOT_READY) {
                        // Players are now ready
                        if (isReady()) {
                            readyCounter = 0;
                            readyButton.canSee = true;
                            readyButton.coord.x = -READY_BAR_SIZE.x / 2;
                            play(shing);
                        }
                    }
                    else if (readyCounter != READY_ANIM_LEN) {
                        // Animate ready button
                        readyCounter++;
                        readyButton.coord.x = lerp(-READY_BAR_SIZE.x / 2, READY_BAR_SIZE.x / 2, (double) readyCounter / READY_ANIM_LEN);
                        if (readyCounter == READY_ANIM_LEN) {
                            readyButton.canUse = true;
                        }
                    }
                    else {
                        // Players are no longer ready
                        if (!isReady()) {
                            readyCounter = NOT_READY;
                            readyButton.canSee = false;
                            readyButton.canUse = false;
                            chooseButtons.get(READY_BUTTONO).state = Button.NOPRESSED;
                        }
                    }
                }
            }
        }

        // In-game gamestate
        else if(gameState == GAME_GS) {
            // Background (stage)
            stage[stageNo].drawStage(g);

            // Draw bottom projectiles
            drawProjectiles(false, g2);

            // Draw boss
            for (Boss boss: bosses) {
                boss.drawSmokes(g2);
                if (boss.state != Boss.DEAD || boss.coord.y <= SCREEN_SIZE.y + boss.size.y / 2) boss.draw(g);
                else boss.drawSurge(g);
            }

            // Draw players
            for (Omegaman omega: omegaman) {
                omega.drawHUD(g);
                omega.drawSmokes(g2);
                omega.drawWakes(g2);
                if (omega.state == Omegaman.ALIVE_STATE) {
                    omega.draw(g);
                    omega.drawCharge(g);
                    omega.drawPercent(g);
                }
            }

            // Draw top projectiles
            drawProjectiles(true, g2);
            
            // Draw explosions
            drawExplosions(g);
            
            // Draw dead players
            for (Omegaman omega: omegaman) {
                if (omega.state != Omegaman.ALIVE_STATE) {
                    if (omega.frameCounter < SURGE_TIME) {
                        omega.drawDiePercent(g);
                        omega.drawSurge(g2);
                    }
                    else if (omega.frameCounter >= SURGE_TIME + Omegaman.RESPAWN_PAUSE) {
                        omega.drawRespawnPercent(g);
                        omega.draw(g);
                    }
                }
            }

            // Transition
            if ((transitiono != NO_TRANS && transitiono != COUNTDOWN && transitiono != GAME_OVER && transitiono != GAME_SET) ||
            (transitiono == COUNTDOWN && transCounter >= FIGHT_TEXT_LEN) || 
            (transitiono == GAME_OVER && transCounter >= GAME_END_LEN - GAME_END_TEXT_LEN - GAME_END_TEXT_TRANS_LEN) ||
            (transitiono == GAME_SET && transCounter >= GAME_END_LEN - GAME_END_TEXT_LEN - GAME_END_TEXT_TRANS_LEN)) {
                drawTransition(g2);
                transition();
            }

            // No transition
            else {
                // Calculate boss hurt blinking
                for (Boss boss: bosses) {
                    boss.processHurt();
                }

                // Process alive players
                for (Omegaman omega: omegaman) {
                    omega.processFire();
                    omega.processSmokes();
                    omega.processWakes();
                    if (omega.state == Omegaman.ALIVE_STATE) {
                        if (omega.stunCounter != Omegaman.NOT_STUNNED) {
                            omega.knockback(pressedKeys);
                        }
                        else if (omega.dashing != Omegaman.NOT_DASHING) {
                            omega.dash();
                        }
                        else {
                            omega.controlX(pressedKeys.contains(omega.lftKey), pressedKeys.contains(omega.ritKey));
                            omega.controlY(pressedKeys.contains(omega.upKey), pressedKeys.contains(omega.dwnKey));
                            omega.controlShoot(pressedKeys);
                            omega.moveAerial(pressedKeys.contains(omega.upKey));
                        }

                        omega.controlDash(new boolean[] {pressedKeys.contains(omega.lftKey), pressedKeys.contains(omega.ritKey)});
                        omega.move();
                        omega.checkBulletCombo();
                        omega.checkState();
                        omega.checkBossHitbox();
                        omega.checkPlayerHitBox();
                        omega.countInv();
                        omega.regenSkillPts();
                    }
                }
                
                // Process dead players
                for (Omegaman omega: omegaman) {
                    if (omega.state != Omegaman.ALIVE_STATE) {
                        if (omega.frameCounter <= SURGE_TIME || omega.livesLeft > 0) omega.frameCounter++;
                        if (omega.frameCounter == SURGE_TIME) omega.prepareForRespawn();
                        else if (omega.frameCounter >= SURGE_TIME + Omegaman.RESPAWN_PAUSE) {
                            omega.respawn(pressedKeys.contains(omega.dwnKey));
                            omega.countInv();
                        }
                    }
                }

                for (Pair<Integer, Boss> boss: babyBosses) {
                    bosses.add(boss.getKey(), boss.getValue());
                }
                babyBosses.clear();

                // Process bosses
                for (Boss boss: bosses) {
                    boss.processFire();
                    boss.processSmokes();
                    // Process alive bosses
                    if (boss.state != Boss.DEAD) {
                        if (boss.transTo != Boss.NO_TRANS) {
                            boss.transition();
                        }
                        else boss.attack();
                        boss.backgroundAttack();
                    }
                    // Process dead bosses
                    else {
                        if (boss.coord.y <= SCREEN_SIZE.y + boss.size.y / 2) {
                            boss.fall();
                        }
                        else {
                            boss.surge();
                        }
                    }
                }

                for (Boss boss: deadBosses) {
                    bosses.remove(boss);
                }
                deadBosses.clear();

                // Process projectiles and explosions
                addbabyProjectiles();
                processProjectiles();
                deleteDeadProjectiles();
                processExplosions();

                for (Omegaman omega: omegaman) {
                    omega.shakePercent();
                }

                // If not transitioning to game end, check for pause and if there's a winner
                if (transitiono != GAME_OVER && transitiono != GAME_SET) {
                    checkPause();
                    checkWin();
                }

                // Process and draw game end transitions and countdown transitions
                if (transitiono == COUNTDOWN || transitiono == GAME_OVER || transitiono == GAME_SET) {
                    drawTransition(g2);
                    transition();
                }
            }
        }

        // Game end gamestate
        else if(gameState == GAME_END_GS) {
            // Calculate and draw rotating background
            flashRot = (flashRot + FLASH_ROTATE_SPD) % (Math.PI * 2);
            g2.rotate(flashRot, SCREEN_CENTER.x, SCREEN_CENTER.y);
            g2.drawImage(flash[battleDone.winner + 2], (int) (FLASH_COORD.x), (int) (FLASH_COORD.y), null);
            g2.rotate(-flashRot, SCREEN_CENTER.x, SCREEN_CENTER.y);

            // Draw Header
            g.drawImage(resultsTitle, (int) (SCREEN_CENTER.x - RESULTS_TITLE_SIZE.x / 2), RESULTS_EDGE_SPACING, null);

            // Draw emotional Omegamen
            battleDone.drawEmoMan(g);

            // Draw stats
            if (transitiono != RESULTS_COUNTING) battleDone.drawScoreBoard(new Coord((SCREEN_CENTER.x - Battle.SCOREBOARD_SIZE.x / 2), RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING), g);

            // Draw buttons and textBoxes
            drawButtons(gameEndButtons.values(), g);
            drawTextBoxes(gameEndTextBoxes, g);

            // Transition
            if (transitiono != NO_TRANS) {
                drawTransition(g2);
                transition();
            }

            // No transition
            else {
                // Button actions
                actionPerformed();

                // If still not transitioning, process stuff
                if (transitiono == NO_TRANS) {
                    // Process buttons and textboxes
                    processButtons(gameEndButtons.values());
                    processTextBoxes(gameEndTextBoxes, g);
                }
            }
        }

        // Slideshow gamestate
        else if (gameState == SLIDESHOW_GS) {
            if (slideNo == THANKS_SLIDE_NO) {
                slideCounter = (slideCounter + 1) % CHROMATIC_ROT_LEN;
                double rotAmt = Math.PI * 2 * slideCounter / CHROMATIC_ROT_LEN;
                g2.rotate(rotAmt, CHROMATIC_MID_COORD.x, CHROMATIC_MID_COORD.y);
                g.drawImage(chromatic, (int) CHROMATIC_COORD.x, (int) CHROMATIC_COORD.y, (int) CHROMATIC_SIZE.x, (int) CHROMATIC_SIZE.y, null);
                g2.rotate(-rotAmt, CHROMATIC_MID_COORD.x, CHROMATIC_MID_COORD.y);
            }
            
            // Draw slide
            g.drawImage(slides[slideNo], 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);

            if (slideNo == BOSS_SLIDE_NO) {
                boolean doctorHover = intersects(mouse, Coord.PT, DOCTOR_ANIM_MID_COORD, DOCTOR_ANIM_SIZE);
                boolean dragonHover = intersects(mouse, Coord.PT, DRAGON_ANIM_MID_COORD, DRAGON_ANIM_SIZE);
                boolean birdHover = intersects(mouse, Coord.PT, BIRD_ANIM_MID_COORD, BIRD_ANIM_SIZE);
                int doctorSpriteNo, dragonSpriteNo, birdSpriteNo;

                if (!doctorHover && !dragonHover && !birdHover && slideCounter != 0) {
                    slideCounter = 0;
                }

                if (doctorHover) {
                    doctorSpriteNo = Doctor.STATE_SPRITE_START[DOCTOR_ANIM_STATE] + (slideCounter % Doctor.STATE_ANIM_LEN[DOCTOR_ANIM_STATE]) / Doctor.STATE_SPRITE_HZ[DOCTOR_ANIM_STATE];
                    slideCounter = (slideCounter + 1) % Doctor.STATE_ANIM_LEN[DOCTOR_ANIM_STATE];
                }
                else {
                    doctorSpriteNo = Doctor.STATE_SPRITE_START[DOCTOR_ANIM_STATE] + DOCTOR_STILL_SPRITE_NO;
                }

                if (dragonHover) {
                    dragonSpriteNo = Dragon.STATE_SPRITE_START[DRAGON_ANIM_STATE] + (slideCounter % Dragon.STATE_ANIM_LEN[DRAGON_ANIM_STATE]) / Dragon.STATE_SPRITE_HZ[DRAGON_ANIM_STATE];
                    slideCounter = (slideCounter + 1) % Dragon.STATE_ANIM_LEN[DRAGON_ANIM_STATE];
                }
                else {
                    dragonSpriteNo = Dragon.STATE_SPRITE_START[DRAGON_ANIM_STATE] + DRAGON_STILL_SPRITE_NO;
                }

                if (birdHover) {
                    birdSpriteNo = Bird.STATE_SPRITE_START[BIRD_ANIM_STATE] + (slideCounter % Bird.STATE_ANIM_LEN[BIRD_ANIM_STATE]) / Bird.STATE_SPRITE_HZ[BIRD_ANIM_STATE];
                    slideCounter = (slideCounter + 1) % Bird.STATE_ANIM_LEN[BIRD_ANIM_STATE];
                }
                else {
                    birdSpriteNo = Bird.STATE_SPRITE_START[BIRD_ANIM_STATE] + BIRD_STILL_SPRITE_NO;
                }

                g.drawImage(Doctor.docSprite[doctorSpriteNo], (int) DOCTOR_ANIM_COORD.x, (int) DOCTOR_ANIM_COORD.y, (int) DOCTOR_ANIM_SIZE.x, (int) DOCTOR_ANIM_SIZE.y, null);
                g.drawImage(Dragon.dragonSprite[dragonSpriteNo], (int) DRAGON_ANIM_COORD.x, (int) DRAGON_ANIM_COORD.y, (int) DRAGON_ANIM_SIZE.x, (int) DRAGON_ANIM_SIZE.y, null);
                g.drawImage(Bird.birdSprite[birdSpriteNo], (int) BIRD_ANIM_COORD.x, (int) BIRD_ANIM_COORD.y, (int) BIRD_ANIM_SIZE.x, (int) BIRD_ANIM_SIZE.y, null);
            }
            else if (slideNo == CONTROLS_SLIDE_NO) {
                drawButtons(controlsButtons.values(), g);
            }

            // Draw buttons
            drawButtons(slideshowButtons.values(), g);

            // Transition
            if (transitiono != NO_TRANS) {
                drawTransition(g2);
                transition();
            }

            // No transition
            else {
                // Button actions
                actionPerformed();

                // If still not transitioning, process stuff
                if (transitiono == NO_TRANS) {
                    ArrayList<Button> buttonsToProcess = new ArrayList<>(slideshowButtons.values());
                    if (slideNo == CONTROLS_SLIDE_NO) {
                        buttonsToProcess.addAll(controlsButtons.values());
                    }

                    // Process buttons
                    processButtons(buttonsToProcess);
                }
            }
        }
        else if (gameState == BATTLE_LOG_GS) {
            // Draw background
            g.drawImage(battleLogBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);

            // Draw battle stats board
            if (battleLog.size() == 0) {
                g.drawImage(noBattle, (int) BATTLE_LOG_SCOREBOARD_COORD.x, (int) BATTLE_LOG_SCOREBOARD_COORD.y, null);
            }
            else {
                battleLog.get(battleNo).drawScoreBoard(BATTLE_LOG_SCOREBOARD_COORD, g);
                battleLog.get(battleNo).drawEmoMan(g);
                battleLog.get(battleNo).drawBattleInfo(BATTLE_LOG_BATTLE_INFO_COORD, g);
            }

            // Draw Battle number
            g.setColor(Color.WHITE);
            g.setFont(Battle.STATS_FONT);
            g.drawString((battleNo + 1) + "/" + battleLog.size(), (int) (SCREEN_SIZE.x - g.getFontMetrics().stringWidth((battleNo + 1) + "/" + battleLog.size())) / 2, BATTLE_NO_Y_COORD);

            // Draw buttons
            drawButtons(battleLogButtons.values(), g);

            // Transition
            if (transitiono != NO_TRANS) {
                drawTransition(g2);
                transition();
            }

            // No transition
            else {
                // Button actions
                actionPerformed();

                // If still not transitioning, process stuff
                if (transitiono == NO_TRANS) {
                    // Process buttons
                    processButtons(battleLogButtons.values());
                }
            }
        }

        if (CHEATS) cheatGame(g);
    }

    // ASK ABOUT METHODS AND DATA ENCAPSULATION
    // Parameters:
    // orig: Start
    // goal: End
    // alpha: Amt from start to end (decimal from 0 to 1)
    // Return:
    // Number that is alpha of the way from orig to goal
    // Description:
    // This is a linear interpolation method
    public static double lerp(double orig, double goal, double alpha) {
        return orig + (goal - orig) * alpha;
    }

    // Parameters:
    // value: Value to be clamped
    // min: Minimum value
    // max: Maximum value
    // Return:
    // Clamped value that is greater than min and smaller than max
    // Description:
    // This method clamps the value between min and max if it is outside the min-max range
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // Parameters:
    // angle: Angle to be normalized
    // Return:
    // normalized angle
    // This method takes in an angle and returns that angle normalized between the range -PI and PI
    public static double normalizeAngle(double angle) {
        angle = ((angle + Math.PI) % (2 * Math.PI));
        if (angle < 0) angle += 2 * Math.PI;
        return angle - Math.PI;
    }

    // Parameters:
    // sign: The sign of the number to convert to radians (-1 or 1)
    // Return:
    // Sign in radians
    // Description:
    // This method converts -1 to PI and 1 to 0
    public static double signToRadians(int sign) {
        return Math.PI * (1 - (sign + 1) / 2);
    }

    // Parameters: None
    // Return: Sign
    // Description:
    // THis method return -1 or 1
    public static int randomSign() {
        return (int) (Math.random() + 0.5) * 2 - 1;
    }

    public static double randomDir() {
        return Math.random() * Math.PI * 2;
    }

    // Parameters:
    // coord1: coordinate of object 1
    // size1: size of object 1
    // coord2: coordinate of object 2
    // size2: size of object 2
    // Return:
    // Whether or not they intersect
    // Description:
    // This method checks if two objects touch/intersect, given their coordinates and sizes
    public static boolean intersects(Coord coord1, Coord size1, Coord coord2, Coord size2, double leeway) {
        return Math.abs(coord1.x - coord2.x) < (size1.x + size2.x) / 2 - leeway && Math.abs(coord1.y - coord2.y) < (size1.y + size2.y) / 2 - leeway; 
    }
    public static boolean intersects(Coord coord1, Coord size1, Coord coord2, Coord size2) {
        return intersects(coord1, size1, coord2, size2, 0);
    }

    // Parameters:
    // coord: Coordinate of object
    // Return: Whether or not the object is out of the screen
    // Description: This method returns whether or not an object is completely out of the screen given it's center coordinate and size
    public static boolean outOfScreen(Coord coord, Coord size) {
        return !intersects(SCREEN_CENTER, SCREEN_SIZE, coord, size, 0);
    }

    public static Clip loadClip(String dir) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(dir).toURI().toURL()));
        clip.setFramePosition(0);
        return clip;
    }

    // Parameters:
    // alpha: Opacity value (0 to 1)
    // g2: Graphics2D object to set opacity on
    // Return: None
    // Description:
    // This method sets the opacity of the all objects draw using Graphics2D to the given alpha value
    public static void setOpacity(double alpha, Graphics2D g2) {
        try {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
        } catch (Exception e) {}
    }

    public static void resetOpacity(Graphics2D g2) {
        try {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
        } catch (Exception e) {}
    }

    // Parameters: None
    // Return: None
    // Description:
    // This method checks if cheats keys are pressed and cheats if so
    public static void cheatGame(Graphics g) {
        if (pressedKeys.contains(KeyEvent.VK_CONTROL)) {
            if (transitiono == NO_TRANS) {
                if (pressedKeys.contains(KILL_KEY) && transitiono == NO_TRANS) {
                    for (Boss boss: bosses) {
                        boss.hurt(KILL_DMG);
                    }
                }

                if (pressedKeys.contains(RANDOM_READY_KEY) && gameState == CHOOSE_FIGHT_GS) {
                    chooseButtons.get(READY_BUTTONO).coord = SCREEN_CENTER.copy();
                    chooseButtons.get(READY_BUTTONO).canSee = true;
                    for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                        for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                            if (loadouts[i][j] == NO_WEAPON) {
                                loadouts[i][j] = RANDOM_WEAPON;
                                chooseButtons.get(loadoutButtono[i][j]).image = randomWeaponIcon;
                            }
                        }
                    }
                    startReadyFade();
                }
            }

            if (!fpsCntKeyPressed && pressedKeys.contains(FPS_CNT_KEY)) {
                fpsCntVis = !fpsCntVis;
            }
            fpsCntKeyPressed = pressedKeys.contains(FPS_CNT_KEY);

            if (!hitBoxKeyPressed && pressedKeys.contains(HITBOX_KEY)) {
                hitBoxVis = !hitBoxVis;
            }
            hitBoxKeyPressed = pressedKeys.contains(HITBOX_KEY);

            if (!slowModeKeyPressed && pressedKeys.contains(SLOW_MODE_KEY)) {
                if (slowMode) {
                    slowMode = false;
                    tickRate = MAX_TICK_RATE;
                }
                else {
                    slowMode = true;
                    tickRate = SLOW_MODE_TICK_RATE;
                }
            }
            slowModeKeyPressed = pressedKeys.contains(SLOW_MODE_KEY);
        }

        if (fpsCntVis) {
            g.setFont(FPS_FONT);
            g.setColor(Color.BLACK);
            String fpsStr = "FPS: " + curFPS;
            g.fillRect(0, (int) (SCREEN_SIZE.y - FPS_FONT.getSize() - FPS_TEXT_SPACING * 2), g.getFontMetrics().stringWidth(fpsStr) + FPS_TEXT_SPACING * 2, FPS_FONT.getSize() + FPS_TEXT_SPACING * 2);
            g.setColor(CHEAT_COLOR);
            g.drawString(fpsStr, FPS_TEXT_SPACING, (int) (SCREEN_SIZE.y - FPS_TEXT_SPACING));

            frameCounter++;
            if (System.currentTimeMillis() - lastFPSReset > 1000) {
                curFPS = frameCounter;
                frameCounter = 0;
                lastFPSReset = System.currentTimeMillis();
            }
        }
    }

    // Description: This method adds all new Projectiles into the main projectiles HashSet
    public static void addbabyProjectiles() {
        for (Projectile proj: babyProjectiles) {
            projectiles.add(proj);
        }
        babyProjectiles.clear();
    }

    // Description: This method processes all projectiles in the main projectiles HashSet
    public static void processProjectiles() {
        HashSet<Projectile> processedProjectiles = new HashSet<>();
        for (Projectile proj: projectiles) {
            proj.process();
            // Check for collisions with other owners and their projectiles
            if (proj.hitBoxActive) {
                for (Omegaman enemy: omegaman) {
                    if (enemy != proj.owner) {
                        // Enemy hitbox
                        if (intersects(proj.coord, proj.hitBoxSize, enemy.coord, enemy.hurtBoxSize) && enemy.invCounter == Omegaman.VULNERABLE) {
                            if (proj.dieTo(enemy)) {
                                if (proj.size.x == proj.size.y) {
                                    proj.owner.smokeQ.add(new Smoke(proj.coord.copy(), proj.size.copy()));
                                }
                                else {
                                    proj.owner.smokeQ.add(new Smoke(proj.coord.copy(), proj.size.copy(), proj.dir));
                                }
                            }
                        }
                    }
                }

                // Check for collisions with bosses and their projectiles
                for (Boss boss: bosses) {
                    if (boss != proj.owner) {
                        // Boss hitbox
                        if (intersects(proj.coord, proj.hitBoxSize, boss.coord, boss.hurtBoxSize)) {
                            if (proj.dieTo(boss)) {
                                if (proj.size.x == proj.size.y) {
                                    proj.owner.smokeQ.add(new Smoke(proj.coord.copy(), proj.size.copy()));
                                }
                                else {
                                    proj.owner.smokeQ.add(new Smoke(proj.coord.copy(), proj.size.copy(), proj.dir));
                                }
                            }
                        }
                    }
                }

                if (proj.canHitProj) {
                    for (Projectile otherProj: processedProjectiles) {
                        if (intersects(proj.coord, proj.hitBoxSize, otherProj.coord, otherProj.hitBoxSize) && otherProj.canHitProj && proj.owner != otherProj.owner) {
                            boolean projDied = proj.dieTo(otherProj);
                            boolean otherProjDied = otherProj.dieTo(proj);
                            if (projDied) {
                                if (otherProjDied) {
                                    proj.owner.smokeQ.add(new Smoke(proj.coord.add(otherProj.coord).scaledBy(0.5), proj.size.add(otherProj.size).scaledBy(0.5)));
                                }
                                else {
                                    if (proj.size.x == proj.size.y) {
                                        proj.owner.smokeQ.add(new Smoke(proj.coord.copy(), proj.size.copy()));
                                    }
                                    else {
                                        proj.owner.smokeQ.add(new Smoke(proj.coord.copy(), proj.size.copy(), proj.dir));
                                    }
                                }
                            }
                            else if (otherProjDied) {
                                if (otherProj.size.x == otherProj.size.y) {
                                    otherProj.owner.smokeQ.add(new Smoke(otherProj.coord.copy(), otherProj.size.copy()));
                                }
                                else {
                                    otherProj.owner.smokeQ.add(new Smoke(otherProj.coord.copy(), otherProj.size.copy(), otherProj.dir));
                                }
                            }
                        }
                    }
                    processedProjectiles.add(proj);
                }
            }
        }
    }

    // Descrption: This method deletes all dead projectiles from the main projectiles HashSet
    public static void deleteDeadProjectiles() {
        for (Projectile proj: deadProjectiles) {
            projectiles.remove(proj);
        }
        deadProjectiles.clear();
    }

    // Description: This method draws all projectiles in the main projectiles HashSet
    public static void drawProjectiles(boolean isTop, Graphics2D g2) {
        for (Projectile proj: projectiles) {
            if (proj.isOnTop == isTop) proj.draw(g2);
        }
    }

    public static void processExplosions() { // Maybe make effects abstract class if needed in future
        // Process smoke trails
        for (Explosion explosion: explosionQ) {
            explosion.frameCounter--;
        }

        // Delete dead smoke
        while (!explosionQ.isEmpty() && explosionQ.getFirst().frameCounter == 0) {
            explosionQ.removeFirst();
        }
    }

    // Description: This method draws the smoke trails of the player
    public static void drawExplosions(Graphics g) {
        for (Explosion explosion: explosionQ) {
            explosion.draw(g);
        }
    }

    // Parameters:
    // buttons: Collection of Button objects to reset to NOPRESSED state
    // Return: None
    // Description:
    // This method resets the state of all buttons in the given collection to NOPRESSED
    public static void resetButtons(Collection<Button> buttons) {
        for (Button button : buttons) {
            button.state = Button.NOPRESSED;
        }
    }

    // Parameters:
    // textBoxes: Collection of TextBox objects to reset to NOPRESSED state
    // Return: None
    // Description:
    // This method resets the state of all text boxes in the given collection to NOPRESSED
    public static void resetTextBoxes(Collection<TextBox> textBoxes) {
        for (TextBox textBox : textBoxes) {
            textBox.state = TextBox.NOPRESSED;
            textBox.typing = false;
        }
    }

    // Parameters:
    // buttons: Collection of Button objects to process
    // Return: None
    // Description:
    // This method processes all buttons in the given collection, checking if they are pressed and updating the buttonPressed variable accordingly
    public static void processButtons(Collection<Button> buttons) {
        buttonPressed = NO_BUTTON_HIT;
        for (Button button: buttons) {
            if (button.process(mouse, clicked)) {
                buttonPressed = button.num;
            }
        }
    }

    // Parameters:
    // textBoxes: Collection of TextBox objects to process
    // g: Graphics object for textboxes to check for text sizes
    // Return: None
    // Description:
    // This method processes all text boxes in the given collection
    public static void processTextBoxes(Collection<TextBox> textBoxes, Graphics g) {
        for (TextBox textBox: textBoxes) {
            textBox.process(mouse, clicked, g);
        }
    }

    // Parameters:
    // buttons: Collection of Button objects to draw
    // g: Graphics object to draw the buttons on
    // Return: None
    // Description:
    // This method draws all buttons in the given collection using the provided Graphics object
    public static void drawButtons(Collection<Button> buttons, Graphics g) {
        for (Button button: buttons) {
            button.draw(g);
        }
    }

    // Parameters:
    // textBoxes: Collection of TextBox objects to draw
    // g: Graphics object to draw the text boxes on
    // Return: None
    // Description:
    // This method draws all text boxes in the given collection using the provided Graphics object
    public static void drawTextBoxes(Collection<TextBox> textBoxes, Graphics g) {
        for (TextBox textBox: textBoxes) {
            textBox.draw(g);
        }
    }

    public static Button newBackButton(int buttono) {
        return newBackButton(BUTTON_SIZE.scaledBy(0.5).add(SPACING), buttono);
    }

    public static Button newBackButton(Coord coord, int buttono) {
        return new Button(buttonImg, BUTTON_FONT, coord, BUTTON_SIZE.copy(), "BACK", buttono, Button.SHADOW);
    }

    // Parameters: None
    // Return: Name of the order
    // Description:
    // This method returns the name of the current order based on the orderNormal variable
    public static String getOrderName() {
        return orderNormal? "LOWEST": "HIGHEST";
    }

    // Parameters: None
    // Return: Comparator for Battle objects based on the current sortNum
    // Description:
    // This method returns a Comparator for Battle objects based on the current sortNum
    public static Comparator<Battle> getComparator() {
        if (sortNum == SortByTitle.NUM) {
            return new SortByTitle();
        }
        else if (sortNum == SortByGrade.NUM) {
            return new SortByGrade();
        }
        return null; // This line is just so it compiles; This line SHOULD never be reached
    }

    // Parameters: None
    // Return: None
    // Description:
    // This method resorts the battleLog based on the current sortNum and orderNormal
    // It sorts the battleLog in ascending order if orderNormal is true, otherwise in descending order
    public static void resortLog() {
        if (orderNormal) battleLog.sort(getComparator());
        else battleLog.sort(Collections.reverseOrder(getComparator()));
    }

    // Parameters: None
    // Return: None
    // Description:
    // This method writes the battle log to a file named "battle log.txt" in the "menus" directory CHANGE SO IT APPENDS?
    public static void writeFile() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(MENUS_DIR + BATTLE_LOG_FILE_NAME + ".txt"));
            pw.println(battleLog.size());
            for (Battle b: battleLog) {
                pw.printf("%s\n%d\n%d %d %d\n%.15f\n", b.stageName, b.winner, b.gameMode, b.lives, b.difficulty, b.bossHealth);
                for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                    for (int j = 0; j != Omegaman.NUM_STATS; j++) {
                        pw.print(b.playerStats[i][j] + " ");
                    }
                    pw.println();
                }
                pw.println(b.name);
            }
            pw.close();
        }
        catch (IOException e) {}
    }

    // Parameters: None
    // Return: 2D array of stats
    // Description:
    // This method retrieves the stats of all Omegamen and clears the Omegaman array
    // It also clears the bosses list
    private static double[][] getStatsAndClearEverything() {
        double[][] stats = new double[Omegaman.NUM_PLAYERS][];
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            stats[i] = omegaman[i].stats;
            stats[i][Omegaman.LIVES_LEFT_NO] = omegaman[i].livesLeft;
            omegaman[i] = null;
            for (int j = 0; j != Omegaman.NUM_STATS; j++) {
                if (Battle.IS_PERC[j]) stats[i][j] /= Omegaman.PERC_MULT;
            }
        }
        babyBosses.clear();
        bosses.clear();
        deadBosses.clear();
        explosionQ.clear();
        babyProjectiles.clear();
        projectiles.clear();
        deadProjectiles.clear();
        return stats;
    }

    private static double getBossHealth() {
        double bossHealth = 0;
        for (Boss boss: bosses) {
            bossHealth += Math.max(0, boss.health);
        }
        return bossHealth / Omegaman.PERC_MULT;
    }

    private static void startReadyFade() {
        transitiono = READY_FADE;
        transCounter = READY_FADE_LEN;
        menuMusic.stop();
        play(superClick);
        resetButtons(chooseButtons.values());
    }

    // Parameters: None
    // Return: None
    // Description:
    // This method transitions based on the current transitiono and transCounter
    public static void transition() {
        // Calculate frame counter for transitions
        transCounter--;

        // Fade in transition
        if (transitiono == FADE_IN) {
            if (transCounter == 0) {
                transitiono = NO_TRANS;
            }
        }

        // Ready fade out transition
        if (transitiono == READY_FADE) {
            // Transition to in-game gamestate
            if (transCounter == 0) {
                gameState = GAME_GS;

                if (stageNo == RANDOM_STAGE) stageNo = (int) (Math.random() * NUM_STAGES);

                // Initialize Omegamen
                for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                    for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                        if (loadouts[i][j] == RANDOM_WEAPON) {
                            int weapono;
                            do {
                                weapono = (int) (Math.random() * TOT_NUM_WEAPONS);
                            }
                            while (!validWeapon(weapono));
                            loadouts[i][j] = weapono;
                        }
                    }
                    try {
                        omegaman[i] = new Omegaman(i, new Coord(stage[stageNo].spawnCoords[i].x, stage[stageNo].spawnCoords[i].y - Omegaman.SIZE.y / 2), Omegaman.SIZE.copy(), stage[stageNo].spawnSpriteSign[i], stage[stageNo].spawnPlatformNo[i], lives, controls[i], shtKeys[i], loadouts[i].clone());
                    }
                    catch (IOException e) {}
                }
                for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                    for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                        loadouts[i][j] = NO_WEAPON;
                        chooseButtons.get(loadoutButtono[i][j]).image = addWeaponIcon;
                    }
                }

                // Reset choose your fight menu variables
                readyCounter = -1;
                stageFlashCounter = 0;
                weaponIconFlashCounter = 0;
                selectedIcon = null;
                chooseButtons.get(READY_BUTTONO).canUse = false;
                chooseButtons.get(READY_BUTTONO).canSee = false;

                // Initalize bosses
                if (gameMode == ALLPVE || gameMode == PVPVE) {
                    if (stageNo == BATTLEFIELD_NO) {
                        bosses.addLast(new Doctor());
                    }
                    else if (stageNo == FINAL_DEST_NO) {
                        bosses.addLast(new Dragon());
                    }
                    else if (stageNo == NORTH_CAVE_NO) {
                        bosses.addLast(new Bird());
                    }
                }

                // Initialize transition variables and start music
                transCounter = COUNTDOWN_LEN;
                transitiono = COUNTDOWN;
                try {
                    Thread.sleep(1000);
                }
                catch (Exception e) {}
                loop(stage[stageNo].music);
            }
        }

        // Countdown transition
        else if (transitiono == COUNTDOWN) {
            if (transCounter == 0) {
                transitiono = NO_TRANS;
            }
        }

        // Game over transition
        else if (transitiono == GAME_OVER) {
            // Transition to game end gamestate with game over
            if (transCounter == 0) {
                gameState = GAME_END_GS;
                transitiono = RESULTS_COUNTING;
                transCounter = RESULTS_COUNTING_LEN;

                // Create battle object make a temp winner var
                battleDone = new Battle(stage[stageNo].stageName, Battle.BOTH_LOSE, gameMode, lives, difficulty, getBossHealth(), getStatsAndClearEverything());
            }
        }

        // Game set transition
        else if (transitiono == GAME_SET) {
            // Transition to game end gamestate with game set
            if (transCounter == 0) {
                gameState = GAME_END_GS;
                transitiono = RESULTS_COUNTING;
                transCounter = RESULTS_COUNTING_LEN;

                // Create battle object
                if (gameMode == ALLPVE) {
                    battleDone = new Battle(stage[stageNo].stageName, Battle.BOTH_WIN, ALLPVE, lives, difficulty, getBossHealth(), getStatsAndClearEverything());
                }
                else if (gameMode == PVP) {
                    int winner = Battle.BOTH_LOSE;
                    for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                        if (omegaman[i].livesLeft > 0) {
                            winner = i;
                        }
                    }
                    battleDone = new Battle(stage[stageNo].stageName, winner, PVP, lives, difficulty, getBossHealth(), getStatsAndClearEverything());
                }
                else if (gameMode == PVPVE) {
                    int winner = 0;
                    double maxDmg = 0;
                    boolean allEqual = true;
                    for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                        double curDmg = omegaman[i].stats[Omegaman.DMG_TO_BOSS];
                        if (i != 0 && allEqual) allEqual = curDmg == omegaman[i - 1].stats[Omegaman.DMG_TO_BOSS];
                        if (curDmg > maxDmg) {
                            maxDmg = curDmg;
                            winner = i;
                        }
                    }
                    if (allEqual) winner = Battle.BOTH_WIN;
                    battleDone = new Battle(stage[stageNo].stageName, winner, PVPVE, lives, difficulty, getBossHealth(), getStatsAndClearEverything());
                }
            }
        }

        // Results counting transition
        else if (transitiono == RESULTS_COUNTING) {
            if (transCounter == 0) {
                transitiono = NO_TRANS;

                // Enable buttons and textboxes (because this is a long animation)
                for (Button button: gameEndButtons.values()) {
                    button.canSee = true;
                    button.canUse = true;
                }
                for (TextBox textbox: gameEndTextBoxes) {
                    textbox.canSee = true;
                    textbox.canUse = true;
                }
            }
        }

        // Pause transition
        else if (transitiono == PAUSE) {
            if (transCounter <= -1) {
                // Button actions
                actionPerformed();

                // If still pausing, process resume and quit buttons
                if (transitiono == PAUSE) {
                    processButtons(pauseButtons.values());
                }
                transCounter++;
            }
        }

        // Start animation transition
        else if (transitiono == START_ANIM) {
            // Animate letters blipping onto screen
            if (transCounter >= NUM_SLAM_LEN && transCounter <= START_ANIM_LEN - STUDIO_LEN) {
                // If it's time to add a letter, add a letter
                if (transCounter >= NUM_SLAM_LEN + Letter.LETTER_ANIM_LEN) {
                    if ((transCounter - (NUM_SLAM_LEN + Letter.LETTER_ANIM_LEN)) % LETTER_APPEAR_HZ == 0) {
                        int letterNo = letterOrder.removeFirst();
                        letters.add(new Letter(Letter.LETTER_COORDS[letterNo], Letter.LETTER_SIZE[letterNo], Letter.letters[letterNo], Math.random() < 0.5));
                    }
                }

                // If it's time to slam the number, start the music
                else if (transCounter == NUM_SLAM_LEN) {
                    loop(menuMusic);
                }

                // Process letters
                for (Letter letter: letters) {
                    letter.process();
                }
            }

            // Shake screen and change transitiono to NO_TRANS
            if (transCounter == 0) {
                transitiono = NO_TRANS;
                screenShakeCounter += NUM_SLAM_SCREENSHAKE;
            }
        }

        // Flash transition
        else if (transitiono == FLASH) {
            // transition to home gamestate
            if (transCounter == 0) {
                gameState = HOME_GS;
                transitiono = FLASH_FADE;
                transCounter = FLASH_FADE_LEN;
            }
        }

        // Flash fade transition
        else if (transitiono == FLASH_FADE) {
            if (transCounter == 0) {
                transitiono = NO_TRANS;
            }
        }

        // Rules
        else if (transitiono == RULES) {
            if (transCounter == -1) {
                // Button actions
                actionPerformed();

                // If still pausing, process resume and quit buttons
                if (transitiono == RULES && transCounter == -1) {
                    processButtons(rulesButtons.values());
                }
                transCounter++;
            }
            else if (transCounter == -RULES_FADE_LEN) {
                transitiono = NO_TRANS;
                transCounter = 0;
            }
        }

        else if (transitiono == CHANGE_KEY_BIND) {
            if (transCounter == -1) {
                transCounter++;
            }
            if (pressedKeys.size() == 1) {
                for (int key: pressedKeys) {
                    if (validKey(key)) {
                        if (typeToChangeKey == CONTROLS_KEY) {
                            controls[playerToChangeKey][numToChangeKey] = key;
                            controlsButtons.get(CONTROLS_BUTTONO[playerToChangeKey][numToChangeKey]).text = KeyEvent.getKeyText(key);
                        }
                        else if (typeToChangeKey == SHOOT_KEY) {
                            shtKeys[playerToChangeKey][numToChangeKey] = key;
                            controlsButtons.get(SHOOT_BUTTONO[playerToChangeKey][numToChangeKey]).text = KeyEvent.getKeyText(key);
                        }
                    }
                }
                for (Button button: slideshowButtons.values()) {
                    button.canUse = true;
                }
                for (Button button: controlsButtons.values()) {
                    button.canUse = true;
                }
                transitiono = NO_TRANS;
                transCounter = 0;
            }
        }
    }

    // Parameters:
    // g2: Graphics2D object to draw the transition on
    // Return: None
    // Description:
    // This method draws the transition based on the current transitiono and transCounter
    public static void drawTransition(Graphics2D g2) {
        // Fade in transition
        if (transitiono == FADE_IN) {
            drawFade((double) transCounter / FADE_IN_LEN, g2);
        }

        // Ready fade out transition
        else if (transitiono == READY_FADE) {
            drawFade(1 - (double) (transCounter - 1) / READY_FADE_LEN, g2);
        }

        // Countdown transition
        else if (transitiono == COUNTDOWN) {
            // Fade in
            if (transCounter > COUNTDOWN_LEN - COUNTDOWN_FADE_IN_LEN) drawFade((double) (transCounter - (COUNTDOWN_LEN - COUNTDOWN_FADE_IN_LEN)) / COUNTDOWN_FADE_IN_LEN, g2);
            
            // Draw ready?
            if (transCounter >= FIGHT_TEXT_LEN) {
                Coord curReadySize = READY_SIZE.scaledBy(Math.log(COUNTDOWN_LEN - transCounter + 1) / Math.log(COUNTDOWN_LEN - FIGHT_TEXT_LEN + 1));
                Coord readyCoord = SCREEN_CENTER.add(curReadySize.scaledBy(-0.5));
                g2.drawImage(countdownText[READY_TEXT], (int) (readyCoord.x), (int) (readyCoord.y), (int) (curReadySize.x), (int) (curReadySize.y), null);
            }

            // Draw FIGHT! slam
            else if (transCounter >= FIGHT_TEXT_LEN - FIGHT_TRANS_LEN) {
                Coord curFightSize = new Coord(lerp(FIGHT_SIZE.y, SCREEN_SIZE.y, (double) (transCounter - (FIGHT_TEXT_LEN - FIGHT_TRANS_LEN)) / FIGHT_TRANS_LEN)); curFightSize.x *= FIGHT_SIZE.x / FIGHT_SIZE.y;
                Coord fightCoord = SCREEN_CENTER.add(curFightSize.scaledBy(-0.5));
                g2.drawImage(countdownText[FIGHT_TEXT_START], (int) (fightCoord.x), (int) (fightCoord.y), (int) (curFightSize.x), (int) (curFightSize.y), null);
            }

            // Draw FIGHT! flashing
            else if (transCounter >= FIGHT_TRANS_LEN) {
                Coord fightCoord = SCREEN_CENTER.add(FIGHT_SIZE.scaledBy(-0.5));
                g2.drawImage(countdownText[FIGHT_TEXT_START + transCounter % (FIGHT_FLASH_HZ * 2) / FIGHT_FLASH_HZ], (int) (fightCoord.x), (int) (fightCoord.y),
                (int) (FIGHT_SIZE.x), (int) (FIGHT_SIZE.y), null);
            }

            // Draw FIGHT! slamming out
            else {
                Coord curFightSize = new Coord(lerp(SCREEN_SIZE.y, FIGHT_SIZE.y, (double) (transCounter) / FIGHT_TRANS_LEN)); curFightSize.x *= FIGHT_SIZE.x / FIGHT_SIZE.y;
                Coord fightCoord = SCREEN_CENTER.add(curFightSize.scaledBy(-0.5));
                g2.drawImage(countdownText[FIGHT_TEXT_START], (int) (fightCoord.x), (int) (fightCoord.y), (int) (curFightSize.x), (int) (curFightSize.y), null);
            }
        }

        // Game over transition
        else if (transitiono == GAME_OVER) {
            Coord gameOverCoord = SCREEN_CENTER.add(GAME_OVER_SIZE.scaledBy(-1)); gameOverCoord.y += GAME_OVER_SIZE.y / 2; 

            // Draw game over text coming in from the sides
            if (transCounter >= GAME_END_LEN - GAME_END_TEXT_TRANS_LEN) {
                double progress = (double) (transCounter - (GAME_END_LEN - GAME_END_TEXT_TRANS_LEN)) / GAME_END_TEXT_TRANS_LEN;
                g2.drawImage(gameOver[0], (int) lerp(gameOverCoord.x, -GAME_OVER_SIZE.x, progress), (int) (gameOverCoord.y), null);
                g2.drawImage(gameOver[1], (int) lerp(SCREEN_CENTER.x, SCREEN_SIZE.x, progress), (int) (gameOverCoord.y), null);
            }

            // Draw game over text staying in the middle
            else if (transCounter >= GAME_END_LEN - GAME_END_TEXT_TRANS_LEN - GAME_END_TEXT_LEN) {
                g2.drawImage(gameOver[0], (int) (gameOverCoord.x), (int) (gameOverCoord.y), null);
                g2.drawImage(gameOver[1], (int) (SCREEN_CENTER.x), (int) (gameOverCoord.y), null);
            }

            // Draw game over text fading out
            else if (transCounter >= GAME_END_LEN - GAME_END_TEXT_TRANS_LEN * 2 - GAME_END_TEXT_LEN) {
                setOpacity((double) (transCounter - (GAME_END_LEN - GAME_END_TEXT_TRANS_LEN * 2 - GAME_END_TEXT_LEN)) / GAME_END_TEXT_TRANS_LEN, g2);
                g2.drawImage(gameOver[0], (int) (gameOverCoord.x), (int) (gameOverCoord.y), null);
                g2.drawImage(gameOver[1], (int) (SCREEN_CENTER.x), (int) (gameOverCoord.y), null);
                resetOpacity(g2);
            }

            // Fade out
            else {
                drawFade(1 - (double) (transCounter) / (GAME_END_LEN - GAME_END_TEXT_TRANS_LEN * 2 - GAME_END_TEXT_LEN), g2);
            }
        }

        // Game set transition
        else if (transitiono == GAME_SET) {
            Coord gameSetCoord = SCREEN_CENTER.add(GAME_SET_SIZE.scaledBy(-1)); gameSetCoord.y += GAME_SET_SIZE.y / 2;
            // Draw game set text coming in from the sides
            if (transCounter >= GAME_END_LEN - GAME_END_TEXT_TRANS_LEN) {
                double progress = (double) (transCounter - (GAME_END_LEN - GAME_END_TEXT_TRANS_LEN)) / GAME_END_TEXT_TRANS_LEN;
                g2.drawImage(gameSet[0], (int) lerp(gameSetCoord.x, -GAME_SET_SIZE.x, progress), (int) (gameSetCoord.y), null);
                g2.drawImage(gameSet[1], (int) lerp(SCREEN_CENTER.x, SCREEN_SIZE.x, progress), (int) (gameSetCoord.y), null);
            }

            // Draw game set text staying in the middle
            else if (transCounter >= GAME_END_LEN - GAME_END_TEXT_TRANS_LEN - GAME_END_TEXT_LEN) {
                g2.drawImage(gameSet[0], (int) (gameSetCoord.x), (int) (gameSetCoord.y), null);
                g2.drawImage(gameSet[1], (int) (SCREEN_CENTER.x), (int) (gameSetCoord.y), null);
            }

            // Draw game set text fading out
            else if (transCounter >= GAME_END_LEN - GAME_END_TEXT_TRANS_LEN * 2 - GAME_END_TEXT_LEN) {
                setOpacity((double) (transCounter - (GAME_END_LEN - GAME_END_TEXT_TRANS_LEN * 2 - GAME_END_TEXT_LEN)) / GAME_END_TEXT_TRANS_LEN, g2);
                g2.drawImage(gameSet[0], (int) (gameSetCoord.x), (int) (gameSetCoord.y), null);
                g2.drawImage(gameSet[1], (int) (SCREEN_CENTER.x), (int) (gameSetCoord.y), null);
                resetOpacity(g2);
            }

            // Fade out
            else {
                drawFade(1 - (double) (transCounter) / (GAME_END_LEN - GAME_END_TEXT_TRANS_LEN * 2 - GAME_END_TEXT_LEN), g2);
            }
        }

        // Results counting up transition
        else if (transitiono == RESULTS_COUNTING) {
            // Draw stats
            battleDone.drawScoreBoard(new Coord(SCREEN_CENTER.x - Battle.SCOREBOARD_SIZE.x / 2, RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING), 1 - (double) transCounter / RESULTS_COUNTING_LEN, g2);
            
            // Fade in
            if (transCounter >= RESULTS_COUNTING_LEN - RESULTS_FADE_LEN) {
                drawFade((double) (transCounter - (RESULTS_COUNTING_LEN - RESULTS_FADE_LEN)) / RESULTS_FADE_LEN, g2);
            }
        }

        // Pause
        else if (transitiono == PAUSE) {
            // Draw paused background and paused buttons
            setOpacity(1 - (double) transCounter / PAUSE_FADE_LEN, g2);
            g2.drawImage(pausedBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            drawButtons(pauseButtons.values(), g2);
            resetOpacity(g2);
            
        }

        // Start animation
        else if (transitiono == START_ANIM) {
            // If number not slamming yet
            if (transCounter >= NUM_SLAM_LEN) {
                // Draw black background
                drawFade(1, g2);

                // Draw studio fade in, pause, and fade out
                int studioNameTime = START_ANIM_LEN - STUDIO_LEN;
                if (transCounter >= studioNameTime) {
                    int studioNameEndDrawTime = studioNameTime + STUDIO_PAUSE_LEN;
                    int studioNameStartDrawTime = START_ANIM_LEN - STUDIO_PAUSE_LEN;
                    if (transCounter >= studioNameEndDrawTime && transCounter < studioNameStartDrawTime) {
                        int studioNameFadeInTime = studioNameStartDrawTime - STUDIO_FADE_LEN;
                        if (transCounter >= studioNameFadeInTime) {
                            setOpacity(1 - (double) (transCounter - (studioNameFadeInTime)) / STUDIO_FADE_LEN, g2);
                        }
                        else if (transCounter < studioNameEndDrawTime + STUDIO_FADE_LEN) {
                            setOpacity((double) (transCounter - (studioNameEndDrawTime)) / STUDIO_FADE_LEN, g2);
                        }
                        Coord studioLogoCoord = SCREEN_CENTER.add(STUDIO_LOGO_SIZE.scaledBy(-0.5));
                        g2.drawImage(studioLogo, (int) (studioLogoCoord.x), (int) (studioLogoCoord.y), null);
                        resetOpacity(g2);
                    }
                }

                // Draw letters
                else {
                    for (Letter letter: letters) {
                        letter.draw(g2);
                    }
                }
            }

            // If number slamming
            else {
                // Fade into color title and background
                double opacity = (double) transCounter / NUM_SLAM_LEN;
                drawFade(opacity, g2);
                setOpacity(opacity, g2);
                for (Letter letter: letters) {
                    letter.draw(g2);
                }
                resetOpacity(g2);

                // Slam number
                double progress = 1 - (double) (transCounter) / NUM_SLAM_LEN;
                Coord curSize = new Coord(lerp(TITLE_NUM_SIZE.x * SCREEN_SIZE.y / TITLE_NUM_SIZE.y, TITLE_NUM_SIZE.x, progress), lerp(SCREEN_SIZE.y, TITLE_NUM_SIZE.y, progress));
                g2.drawImage(titleNum, (int) (SCREEN_CENTER.x - curSize.x / 2), (int) (TITLE_NUM_CENTRE_Y - curSize.y / 2), (int) curSize.x, (int) curSize.y, null);
            }
        }

        // Flash transition
        else if (transitiono == FLASH) {
            drawFlash(1 - (double) transCounter / FLASH_LEN, g2);
        }

        // Flash fade transition
        else if (transitiono == FLASH_FADE) {
            drawFlash(Math.min(1.0, (double) transCounter / TRUE_FLASH_FADE_LEN), g2);
        }

        else if (transitiono == RULES) {
            setOpacity(1 - (double) Math.abs(transCounter) / RULES_FADE_LEN, g2);

            g2.drawImage(rulesBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            drawButtons(rulesButtons.values(), g2);
            g2.setFont(GAMEMODE_DESC_FONT);
            int descBarSizeX = SPACING * 2 + g2.getFontMetrics().stringWidth(GAMEMODE_DESC[gameMode]);
            int descBarCoordX = (int) SCREEN_SIZE.x - descBarSizeX;
            g2.drawImage(gameModeDescBar, descBarCoordX, GAMEMODE_DESC_BAR_Y, descBarSizeX, GAMEMODE_DESC_BAR_SIZE_Y, null);
            g2.drawImage(gameModeDescEnd, descBarCoordX - GAMEMODE_DESC_END_SIZE_X, GAMEMODE_DESC_BAR_Y, null);
            g2.drawString(GAMEMODE_DESC[gameMode], descBarCoordX + SPACING, GAMEMODE_DESC_MID_Y + Button.getAccWordSize(GAMEMODE_DESC_FONT.getSize()) / 2);
            resetOpacity(g2);
        }

        else if (transitiono == CHANGE_KEY_BIND) {
            setOpacity(1 - (double) transCounter / CHANGE_KEY_BIND_FADE_LEN, g2);
            g2.drawImage(bindKeyOverlay, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            resetOpacity(g2);
        }
    }

    // Parameters:
    // amt: Amount of fade (0 to 1)
    // g: Graphics object to draw the fade on
    // Return: None
    // Description:
    // This method draws a black fade effect on the entire screen using the given Graphics object
    public static void drawFade(double amt, Graphics g) {
        g.setColor(new Color(0, 0, 0, (int) (MAX_RGB_VAL * amt)));
        g.fillRect(0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y);
    }

    // Parameters:
    // amt: Amount of flash (0 to 1)
    // g: Graphics object to draw the flash on
    // Return: None
    // Description:
    // This method draws a white flash effect on the entire screen using the given Graphics object
    public static void drawFlash(double amt, Graphics g) {
        g.setColor(new Color(255, 255, 255, (int) (MAX_RGB_VAL * amt)));
        g.fillRect(0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y);
    }

    // Parameters: None
    // Return: Whether all loadouts are ready
    // Description:
    // This method checks if all loadouts are ready by ensuring that no loadout contains NO_WEAPON
    private static boolean isReady() {
        for (int[] loadout : loadouts) {
            for (int weapon : loadout) {
                if (weapon == NO_WEAPON) return false;
            }
        }
        return true;
    }

    // Parameters: None
    // Return: None
    // Description:
    // This method checks if the pause key is pressed and sets the transitiono to PAUSE if it is
    public static void checkPause() {
        if (pressedKeys.contains(PAUSE_KEY)) {
            transitiono = PAUSE;
            transCounter = PAUSE_FADE_LEN;
        }
    }

    // Parameters: None
    // Return: None
    // Description:
    // This method checks if the game if all Omegamen have no lives left or if all bosses are dead and transitions to game end gamstate if so
    public static void checkWin() {
        if (gameMode == ALLPVE) {
            // check omegamen
            for (Omegaman omega: omegaman) {
                if (omega.livesLeft <= 0 && omega.frameCounter >= SURGE_FRAME_HZ * SURGE_SPRITE_WIN_CHECK) {
                    transCounter = GAME_END_LEN;
                    transitiono = GAME_OVER;
                    stage[stageNo].music.stop();
                    loop(endMusic);
                }
            }

            // Check bosses
            boolean allBossDead = checkBoss();
            if (allBossDead) {
                transCounter = GAME_END_LEN;
                transitiono = GAME_SET;
                stage[stageNo].music.stop();
                loop(endMusic);
            }
        }
        else if (gameMode == PVP) {
            // check omegamen
            int deadCount = 0;
            for (Omegaman omega: omegaman) {
                if (omega.livesLeft <= 0 && omega.frameCounter >= SURGE_FRAME_HZ * SURGE_SPRITE_WIN_CHECK) {
                    deadCount++;
                }
            }
            if (deadCount == Omegaman.NUM_PLAYERS) {
                transCounter = GAME_END_LEN;
                transitiono = GAME_OVER;
                stage[stageNo].music.stop();
                loop(endMusic);
            }
            else if (deadCount == Omegaman.NUM_PLAYERS - 1) {
                transCounter = GAME_END_LEN;
                transitiono = GAME_SET;
                stage[stageNo].music.stop();
                loop(endMusic);
            }
        }
        else if (gameMode == PVPVE) {
            // Check bosses
            boolean allBossDead = checkBoss();
            if (allBossDead) {
                transCounter = GAME_END_LEN;
                transitiono = GAME_SET;
                stage[stageNo].music.stop();
                loop(endMusic);
            }
        }
    }

    private static boolean checkBoss() {
        for (Boss boss: bosses) {
            if (boss.health > 0 || boss.frameCounter < SURGE_FRAME_HZ * SURGE_SPRITE_WIN_CHECK) {
                return false;
            }
        }
        return true;
    }

    private static boolean validKey(int key) {
        if (key == KeyEvent.VK_ESCAPE) return false;
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            for (int j = 0; j != 4; j++) {
                if (controls[i][j] == key) return false;
            }
            for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                if (shtKeys[i][j] == key) return false;
            }
        }
        return true;
    }

    private static boolean validWeapon(int weapono) {
        if (weapono == TOT_NUM_WEAPONS) return true;
        for (int[] loadout : loadouts) {
            for (int selectedWeapon : loadout) {
                if (selectedWeapon == weapono) {
                    return false;
                }
            }
        }
        return true;
    }

    // Parameters:
    // weaponButton: Button representing the weapon to select
    // Return: None
    // Description:
    // This method selects a weapon based on the weapon button pressed
    public static void selectWeapon(Button weaponButton) {
        // If the selected icon is the same as the weapon button, deselect it
        if ((weaponButton.image == selectedIcon.image && weaponButton.image != addWeaponIcon) || weaponButton == selectedIcon) {
            selectedIcon = null;
            return;
        }
        
        // Chekc if previous selected icon is loadout button
        for (int i = 0; i < Omegaman.NUM_PLAYERS; i++) {
            for (int j = 0; j < Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                if (selectedIcon.num == loadoutButtono[i][j]) {
                    int weaponNo;
                    if (weaponButton.num != RANDOM_WEAPON_BUTTONO) weaponNo = Arrays.binarySearch(WEAPON_BUTTONO, weaponButton.num);
                    else weaponNo = RANDOM_WEAPON;
                    // Check if weapon is already in any loadout and invalidate if so 
                    if (!validWeapon(weaponNo)) {
                        loadouts[i][j] = NO_WEAPON;
                        selectedIcon.image = addWeaponIcon;
                        selectedIcon = null;
                        return;
                    }

                    // Weapon select is valid
                    loadouts[i][j] = weaponNo;
                    selectedIcon.image = weaponButton.image;
                    selectedIcon = null;
                    return;
                }
            }
        }

        // Select icon if didn't select icon previously
        selectedIcon = weaponButton;
    }

    // Parameters:
    // loadoutButton: Button representing the loadout to select
    // playerNo: Player number of the loadout
    // loadoutSlot: Slot number of the loadout
    // Return: None
    // Description:
    // This method selects a loadout based on the loadout button pressed
    public static void selectLoadout(Button loadoutButton, int playerNo, int loadoutSlot) {
        // If the selected icon is the same as the loadout button, deselect it
        if (loadoutButton.image == selectedIcon.image) {
            if (loadoutButton.image == addWeaponIcon && loadoutButton != selectedIcon) {
                selectedIcon = loadoutButton;
            }
            else {
                selectedIcon = null;
            }
            return;
        }

        // If the selected icon is a loadout button, swap or select weapon
        for (int i = 0; i < Omegaman.NUM_PLAYERS; i++) {
            for (int j = 0; j < Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                // Swap loadouts
                if (selectedIcon.num == loadoutButtono[i][j]) {
                    int temp = loadouts[playerNo][loadoutSlot];
                    loadouts[playerNo][loadoutSlot] = loadouts[i][j];
                    loadouts[i][j] = temp;
                    BufferedImage temp1 = selectedIcon.image;
                    selectedIcon.image = loadoutButton.image;
                    loadoutButton.image = temp1;
                    selectedIcon = null;
                    return;
                }
            }
        }

        // If the selected icon is a weapon button, check if it is already in any player's loadout. Invalidate it if so
        int weaponNo;
        if (selectedIcon.num != RANDOM_WEAPON_BUTTONO) weaponNo = Arrays.binarySearch(WEAPON_BUTTONO, selectedIcon.num);
        else weaponNo = RANDOM_WEAPON;
        if (!validWeapon(weaponNo)) {
            loadouts[playerNo][loadoutSlot] = NO_WEAPON;
            loadoutButton.image = addWeaponIcon;
            selectedIcon = null;
            return;
        }

        // Loadout select is valid, set weapon
        loadouts[playerNo][loadoutSlot] = weaponNo;
        loadoutButton.image = selectedIcon.image;
        selectedIcon = null;
    }

    public static void play(Clip clip) {
        if (SOUND_ON) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public static void loop(Clip clip) {
        if (SOUND_ON) {
            clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Parameters: None
    // Return: None
    // Description:
    // This is my own actionPerformed method. It does almost the same thing as JButton's actionperformed
    public static void actionPerformed() {
        // If previous frame hit button but now not clicking, button released, so do something
        if (buttonPressed != NO_BUTTON_HIT && !clicked) {
            // Home gamestate buttons
            if (gameState == HOME_GS) {
                // FIGHT!! button
                if (buttonPressed == FIGHT_BUTTONO) {
                    gameState = CHOOSE_FIGHT_GS;
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(homeButtons.values());
                }

                // Slideshow button
                else if (buttonPressed == SLIDESHOW_BUTTONO) {
                    gameState = SLIDESHOW_GS;
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(homeButtons.values());
                }

                // Battle Log button
                else if (buttonPressed == BATTLE_LOG_BUTTONO) {
                    gameState = BATTLE_LOG_GS;
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(homeButtons.values());
                }
            }

            // Choose fight gamestate buttons
            else if (gameState == CHOOSE_FIGHT_GS) {
                // Back button
                if (buttonPressed == CHOOSE_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    // transition counter to fade
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;

                    // Reset loadouts
                    for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                        for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                            loadouts[i][j] = NO_WEAPON;
                            chooseButtons.get(loadoutButtono[i][j]).image = addWeaponIcon;
                        }
                    }

                    // Reset choose your fight menu variables
                    readyCounter = -1;
                    stageFlashCounter = 0;
                    weaponIconFlashCounter = 0;
                    selectedIcon = null;
                    chooseButtons.get(READY_BUTTONO).canUse = false;
                    chooseButtons.get(READY_BUTTONO).canSee = false;
                    resetButtons(chooseButtons.values());
                }

                // Ready button
                else if (buttonPressed == READY_BUTTONO) {
                    startReadyFade();
                }

                else if (buttonPressed == RANDOM_STAGE_BUTTONO) {
                    stageNo = RANDOM_STAGE;
                }

                else if (buttonPressed == RANDOM_WEAPON_BUTTONO) {
                    if (selectedIcon != null) {
                        selectWeapon(chooseButtons.get(RANDOM_WEAPON_BUTTONO));
                    }
                    else {
                        selectedIcon = chooseButtons.get(RANDOM_WEAPON_BUTTONO);
                    }
                }

                else if (buttonPressed == RULES_BUTTONO) {
                    transitiono = RULES;
                    transCounter = RULES_FADE_LEN;
                    resetButtons(chooseButtons.values());
                }

                else if (buttonPressed == RULES_BACK_BUTTONO) {
                    transCounter = -2;
                    resetButtons(rulesButtons.values());
                }

                else if (buttonPressed == LIVES_BUTTONO) {
                    if (gameMode != PVPVE) {
                        lives = lives % (MAX_LIVES + (DEV_MODE? 1: 0)) + 1;
                        rulesButtons.get(LIVES_BUTTONO).text = "LIVES: " + (lives == INF_LIVES? "INFINITY": lives);
                    }
                }

                else if (buttonPressed == BOSS_BUTTONO) {
                    if (gameMode != PVP) {
                        difficulty = difficulty % NUM_DIFFICULTY + 1;
                        rulesButtons.get(BOSS_BUTTONO).text = "BOSS: " + DIFFICULTY_NAME[difficulty];
                    }
                }

                else if (buttonPressed == GAMEMODE_BUTTONO) {
                    if (gameMode == PVP) {
                        difficulty = DEFAULT_DIFFICULTY;
                        rulesButtons.get(BOSS_BUTTONO).text = "BOSS: " + DIFFICULTY_NAME[difficulty];
                    }
                    else if (gameMode == PVPVE && !DEV_MODE) {
                        lives = DEFAULT_LIVES;
                        rulesButtons.get(LIVES_BUTTONO).text = "LIVES: " + (lives == INF_LIVES? "INFINITY": lives);
                    }
                    gameMode = (gameMode + 1) % NUM_GAMEMODES;
                    rulesButtons.get(GAMEMODE_BUTTONO).text = "GAMEMODE: " + GAMEMODE_NAME[gameMode];
                    if (gameMode == PVP) {
                        difficulty = 0;
                        rulesButtons.get(BOSS_BUTTONO).text = "BOSS: " + DIFFICULTY_NAME[difficulty];
                    }
                    else if (gameMode == PVPVE) {
                        lives = INF_LIVES;
                        rulesButtons.get(LIVES_BUTTONO).text = "LIVES: INFINITY";
                    }
                }

                // Loadout or weapon icon buttons
                else {
                    // Stage buttons
                    for (int i = 0; i != NUM_STAGES; i++) {
                        if (buttonPressed == stage[i].buttono) {
                            stageNo = i;
                            break;
                        }
                    }

                    // Weapon icon buttons
                    for (int buttono: WEAPON_BUTTONO) {
                        if (buttonPressed == buttono) {
                            if (selectedIcon != null) {
                                selectWeapon(chooseButtons.get(buttono));
                            }
                            else {
                                selectedIcon = chooseButtons.get(buttono);
                            }
                            break;
                        }
                    }

                    // Loadout icon buttons
                    for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                        for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                            int loadoutBtn = loadoutButtono[i][j];
                            if (buttonPressed == loadoutBtn) {
                                if (selectedIcon != null) {
                                    selectLoadout(chooseButtons.get(loadoutBtn), i, j);
                                } else {
                                    selectedIcon = chooseButtons.get(loadoutBtn);
                                }
                                break;
                            }
                        }
                    }
                }
            }

            // In-Game gamestate buttons
            else if (gameState == GAME_GS) {
                // Resume button
                if (buttonPressed == RESUME_BUTTONO) {
                    transitiono = COUNTDOWN;
                    transCounter = COUNTDOWN_LEN - COUNTDOWN_FADE_IN_LEN / 2;
                    resetButtons(pauseButtons.values());
                }

                // Quit button
                else if (buttonPressed == QUIT_BUTTONO) {
                    transitiono = GAME_OVER;
                    transCounter = (GAME_END_LEN - GAME_END_TEXT_LEN - GAME_END_TEXT_TRANS_LEN * 2) / 2;
                    resetButtons(pauseButtons.values());
                    stage[stageNo].music.stop();
                    loop(endMusic);
                }
            }

            // Game End gamestate buttons
            else if (gameState == GAME_END_GS) {
                // NExt battle button
                if (buttonPressed == NEXT_BATTLE_BUTTONO) {
                    gameState = CHOOSE_FIGHT_GS;
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    battleDone.name = gameEndTextBoxes.get(BATTLE_NAME_BOX_IDX).text.trim().length() == 0? "THE UNKNOWN BATTLE": gameEndTextBoxes.get(BATTLE_NAME_BOX_IDX).text;
                    ArrayList<Battle> sortedBattleLog = new ArrayList<>(battleLog);
                    sortedBattleLog.sort(new SortByTitle());
                    int i = Collections.binarySearch(sortedBattleLog, battleDone, new SortByTitle());
                    if (i >= 0) {
                        int num = 0;
                        i++;
                        while (i != sortedBattleLog.size() && sortedBattleLog.get(i).name.equals(battleDone.name + " " + num)) {
                            i++;
                            num++;
                        }
                        battleDone.name += " " + num;
                    }
                    battleLog.add(battleDone);
                    gameEndTextBoxes.get(BATTLE_NAME_BOX_IDX).text = "";
                    writeFile();
                    resortLog();
                    battleDone = null;
                    flashRot = 0;
                    resetButtons(gameEndButtons.values());
                    resetTextBoxes(gameEndTextBoxes);
                    for (Button button: gameEndButtons.values()) {
                        button.canSee = false;
                        button.canUse = false;
                    }
                    for (TextBox textbox: gameEndTextBoxes) {
                        textbox.canSee = false;
                        textbox.canUse = false;
                    }
                    endMusic.stop();
                    loop(menuMusic);
                }
            }

            // Slideshow gamestate buttons
            else if (gameState == SLIDESHOW_GS) {
                // Back button
                if (buttonPressed == SLIDESHOW_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(slideshowButtons.values());
                    if (slideNo == CONTROLS_SLIDE_NO) {
                        resetButtons(controlsButtons.values());
                    }
                }

                // Next button
                else if (buttonPressed == SLIDE_NO_NEXT_BUTTONO) {
                    if (slideNo == CONTROLS_SLIDE_NO) {
                        resetButtons(controlsButtons.values());
                    }
                    slideNo = (slideNo + 1) % NUM_SLIDES;
                    slideCounter = 0;
                }

                // Prev button
                else if (buttonPressed == SLIDE_NO_BACK_BUTTONO) {
                    if (slideNo == CONTROLS_SLIDE_NO) {
                        resetButtons(controlsButtons.values());
                    }
                    slideNo = (slideNo - 1 + NUM_SLIDES) % NUM_SLIDES;
                    slideCounter = 0;
                }

                else if (slideNo == CONTROLS_SLIDE_NO) {
                    for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                        for (int j = 0; j != 4; j++) {
                            if (buttonPressed == CONTROLS_BUTTONO[i][j]) {
                                playerToChangeKey = i;
                                typeToChangeKey = CONTROLS_KEY;
                                numToChangeKey = j;
                                for (Button button: slideshowButtons.values()) {
                                    button.canUse = false;
                                }
                                for (Button button: controlsButtons.values()) {
                                    button.canUse = false;
                                }
                                transitiono = CHANGE_KEY_BIND;
                                transCounter = CHANGE_KEY_BIND_FADE_LEN;
                            }
                        }
                        for (int j = 0; j != Omegaman.LOADOUT_NUM_WEAPONS; j++) {
                            if (buttonPressed == SHOOT_BUTTONO[i][j]) {
                                playerToChangeKey = i;
                                typeToChangeKey = SHOOT_KEY;
                                numToChangeKey = j;
                                for (Button button: slideshowButtons.values()) {
                                    button.canUse = false;
                                }
                                for (Button button: controlsButtons.values()) {
                                    button.canUse = false;
                                }
                                transitiono = CHANGE_KEY_BIND;
                                transCounter = CHANGE_KEY_BIND_FADE_LEN;
                            }
                        }
                    }
                }
            }

            // Battle log gamestate buttons
            else if (gameState == BATTLE_LOG_GS) {
                // Back buttons
                if (buttonPressed == BATTLE_LOG_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    transCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(battleLogButtons.values());
                }

                // Sort button
                else if (buttonPressed == BATTLE_NO_SORT_BUTTONO) {
                    sortNum = (sortNum + 1) % NUM_SORTS;
                    battleLogButtons.get(BATTLE_NO_SORT_BUTTONO).text = "SORT BY: " + BATTLE_LOG_SORT_NAME[sortNum];
                    resortLog();
                    battleNo = 0;
                }

                // Order button
                else if (buttonPressed == BATTLE_NO_ORDER_BUTTONO) {
                    orderNormal = !orderNormal;
                    battleLogButtons.get(BATTLE_NO_ORDER_BUTTONO).text = "ORDER BY: " + getOrderName();
                    resortLog();
                    battleNo = 0;
                }

                // battle log number buttons
                else if (battleLog.size() != 0) {
                    // Prev button
                    if (buttonPressed == BATTLE_NO_BACK_BUTTONO) {
                        battleNo = (battleNo - 1 + battleLog.size()) % battleLog.size();
                    }

                    // First button
                    else if (buttonPressed == BATTLE_NO_ALL_BACK_BUTTONO) {
                        battleNo = 0;
                    }

                    // NExt button
                    else if (buttonPressed == BATTLE_NO_NEXT_BUTTONO) {
                        battleNo = (battleNo + 1) % battleLog.size();
                    }

                    // Last button
                    else if (buttonPressed == BATTLE_NO_ALL_NEXT_BUTTONO) {
                        battleNo = battleLog.size() - 1;
                    }
                }
            }
            buttonPressed = NO_BUTTON_HIT;
        }
    }

    // Mouse Methods for MouseListener and MouseMotionListener
    public void mouseClicked(MouseEvent e) {}

    // Parameters:
    // e: The mouse clicked event
    // Return: None
    // Description: This method sets clicked to true when the mouse is pressed
    public void mousePressed(MouseEvent e) {
        clicked = true;
        // System.out.println(mouse);
    }

    // Parameters:
    // e: The mouse released event
    // Return: None
    // Description: This method sets clicked to false when the mouse is released
    public void mouseReleased(MouseEvent e) {
        clicked = false;
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    // Parameters:
    // e: The mouse dragged event
    // Return: None
    // Description: This method stores the coordinates of the mouse when the mouse is dragged
    public void mouseDragged(MouseEvent e) {
        mouse.x = e.getX();
        mouse.y = e.getY();
    }

    // Parameters:
    // e: The mouse moved event
    // Return: None
    // Description: This method stores the coordinates of the mouse when the mouse is moved
    public void mouseMoved(MouseEvent e) {
        mouse.x = e.getX();
        mouse.y = e.getY();
    }

    // Keyboard methods for KeyListener
    public void keyTyped(KeyEvent e) {}

    // Parameters:
    // e: The key pressed event
    // Return: None
    // Description: This method stores the key pressed in the pressedKeys HashSet and also types in the textboxes of each game state
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());

        // Text box calculation
        // Backspace
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (gameState == STUDIO_ANIM_GS) {

            }
            else if (gameState == HOME_GS) {

            }
            else if (gameState == CHOOSE_FIGHT_GS) {

            }
            else if (gameState == GAME_GS) {
                
            }
            else if (gameState == GAME_END_GS) {
                // Battle name text box backspace 
                if (transitiono == NO_TRANS) {
                    for (TextBox textBox: gameEndTextBoxes) {
                        if (textBox.typing) textBox.backspace();
                    }
                }
            }
            else if (gameState == SLIDESHOW_GS) {
                
            }
            else if (gameState == BATTLE_LOG_GS) {

            }
        }
        // Normal typing
        else {
            if (gameState == STUDIO_ANIM_GS) {

            }
            else if (gameState == HOME_GS) {

            }
            else if (gameState == CHOOSE_FIGHT_GS) {

            }
            else if (gameState == GAME_GS) {
                
            }
            else if (gameState == GAME_END_GS) {
                // Battle name text box typing
                if (transitiono == NO_TRANS) {
                    for (TextBox textBox: gameEndTextBoxes) {
                        if (textBox.typing) textBox.addChar(e.getKeyChar());
                    }
                }
            }
            else if (gameState == SLIDESHOW_GS) {
                
            }
            else if (gameState == BATTLE_LOG_GS) {
                
            }
        }
    }

    // Parameters:
    // e: The key released event
    // Return: None
    // Description: This method removes the key released from the pressedKeys HashSet
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }
}