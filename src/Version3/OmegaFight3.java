package Version3;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;
// Ernest Todo: menus, bosses
// Ernest Long term Todo: ultimate, dash, smoke

public class OmegaFight3 extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Runnable {
    // Screen Settings
    public static final Coord SCREEN_SIZE = new Coord(1920, 960);
    public static final int FPS = 60;
    public static final int SCREEN_SHAKE_HZ = 2;
    public static final int SPACING = 25;

    // Start menu constants
    public static final int TITLE_Y = 75;
    public static final Coord TITLE_SIZE = new Coord(753, 356);
    public static final int TITLE_NUM_Y = 455;
    public static final Coord TITLE_NUM_SIZE = new Coord(708, 250); 
    public static final int TITLE_NUM_CENTRE_Y = TITLE_NUM_Y + (int) TITLE_NUM_SIZE.y / 2;
    public static final int PRESS_START_Y = 724;
    public static final Coord PRESS_START_SIZE = new Coord(551, 31);
    public static final int PRESS_START_BLINK_HZ = 60;
    public static final int NUM_SLAM_SCREENSHAKE = 30;
    public static final Coord STUDIO_LOGO_SIZE = new Coord(256, 140);
    public static final int STUDIO_FADE_LEN = 6;
    public static final int BLACK_PAUSE = 1000;
    public static final int STUDIO_PAUSE = 2000;

    // Home menu Constants
    public static final Coord MENU_MAN_SIZE = new Coord(860, 900);
    public static final double MENU_MAN_SPD = 1;
    public static final int MENU_MAN_MIN_Y = (int) (SCREEN_SIZE.y - MENU_MAN_SIZE.y + MENU_MAN_SPD);
    public static final int MENU_MAN_MOVE_TIMES = 20;
    public static final int MENU_MAN_UP_PAUSE = 20;
    public static final int MENU_MAN_DOWN_PAUSE = 5;
    public static final int MENU_MAN_MOVE_HZ = 2;
    public static final Coord HOME_BUTTON_SIZE = new Coord(960, 100);
    public static final Font HOME_BUTTON_FONT = new Font("Consolas", Font.BOLD, 66);
    public static final int HOME_BUTTON_FIRST_Y = 570;
    public static final int HOME_BUTTON_SPACING = 150;

    // Choose Fight Menu Constants
    public static final int NOT_READY = -1;
    public static final int READY_ANIM_LEN = 10;
    public static final int BLACK_BAR_TOP = 100;
    public static final int BLACK_BAR_BOTTOM = 500;
    public static final int DIVIDER_RIGHT_X = 1645;
    public static final int LOADOUT_ICON_Y = 700;
    public static final int NO_WEAPON = -1;
    public static final Coord READY_BAR_SIZE = new Coord(1920, 240);

    // Pause menu constants
    public static final int PAUSE_KEY = KeyEvent.VK_ESCAPE;

    // Game end menu constants
    public static final double FLASH_ROTATIONS_PER_S = 1.0 / 6;
    public static final double FLASH_ROTATE_SPD = Math.PI * 2 / FPS * FLASH_ROTATIONS_PER_S;
    public static final Coord RESULTS_TITLE_SIZE = new Coord(1440, 130); 
    public static final int RESULTS_EDGE_SPACING = 100;
    public static final int RESULTS_SPACING = 40;
    public static final Coord BATTLE_NAME_BOX_SIZE = new Coord(405, 120);
    public static final int BATTLE_NAME_BOX_IDX = 0;

    // Battle log menu constants
    public static final Coord BATTLE_LOG_SCOREBOARD_COORD = new Coord(660, 350);
    public static final int BATTLE_NO_Y_COORD = 880;
    public static final Coord BATTLE_LOG_BATTLE_INFO_COORD = new Coord(582, 274);
    public static final int ARROW_BUTTON_SPACING = 10;
    public static final int HALF_BATTLE_NO_SIZE = 100;
    public static final int NO_SORTS = 2;
    public static final String BATTLE_LOG_FILE_NAME = "battle log";

    // Slideshow menu constants
    public static final int NUM_SLIDES = 5;

    // Direction Constants
    public static final int LEFT_SIGN = -1;
    public static final int RIGHT_SIGN = 1;

    // Stage constants
    public static final String[] STAGE_NAME = {"battlefield", "final destination"};
    public static final Platform[][] PLATFORMS = {{new Platform(395, 1525, 610, true), new Platform(535, 820, 435, false), new Platform(1095, 1385, 435, false)},
    {new Platform(245, 1675, 550, true)}};
    public static final Coord[][] SPAWN_COORDS = {{new Coord(700, 435), new Coord(1260, 435)},
    {new Coord(700, 550), new Coord(1260, 550)}};
    public static final int[][] SPAWN_SIGN = {{RIGHT_SIGN, RIGHT_SIGN}, {RIGHT_SIGN, RIGHT_SIGN}};
    public static final int[][] SPAWN_PLATFORM_NO = {{1, 2}, {0, 0}};
    public static final int[] STAGE_BUTTONO = {1, 2};
    public static final int FLASH_HZ = 10;
    public static final int FLASH_SIZE = 10;

    // Misc
    public static final double HITBOX_LEEWAY = 5;
    public static final int MAX_RGB_VAL = 255;

    // Gamemode constants
    public static final int NUM_GAMEMODES = 1;
    public static final int TWOPVE = 0;

    // Surge constants
    public static final int NUM_SURGE_IMAGES = 5;
    public static final int SURGE_FRAME_HZ = 6;
    public static final int SURGE_TIME = NUM_SURGE_IMAGES * SURGE_FRAME_HZ;
    public static final Coord SURGE_SIZE = new Coord(741, 949);
    public static final int SURGE_SPRITE_WIN_CHECK = 2;

    // Gamestates
    public static final int STUDIO_ANIM_GS = -1;
    public static final int HOME_GS = 0;
    public static final int CHOOSE_FIGHT_GS = 1;
    public static final int GAME_GS = 2;
    public static final int GAME_END_GS = 3;
    public static final int SLIDESHOW_GS = 4;
    public static final int BATTLE_LOG_GS = 5;

    // Button Numbers (Next avail: 31)
    public static final int NO_BUTTON_HIT = -1;

    // Choose your fight menu
    public static final int CHOOSE_BACK_BUTTONO = 0;
    public static final int READY_BUTTONO = 10;

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

    // Button constants
    public static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 40); 
    public static final Coord BUTTON_SIZE = new Coord(400, 50);
    public static final Coord MED_BUTTON_SIZE = new Coord(100, 50);
    public static final Coord SML_BUTTON_SIZE = new Coord(50, 50);
    public static final Font STAGE_FONT = new Font("Consolas", Font.BOLD, 25);
    public static final Coord STAGE_BUTTON_SIZE = new Coord(510, 255);
    public static final Coord WEAPON_ICON_SIZE = new Coord(100, 100);

    // Transition constants
    public static final int FADE_IN = 0;
    public static final int READY_FADE = 1;
    public static final int FADE_IN_LEN = 10;
    public static final int NO_TRANSITION = -1;
    public static final int READY_FADE_LEN = 30;
    public static final int COUNTDOWN_FADE_IN_LEN = 10;

    public static final int COUNTDOWN = 2;
    public static final int COUNTDOWN_LEN = 180;
    public static final int FIGHT_TRANSITION_LEN = 6;
    public static final int FIGHT_TEXT_LEN = 60;
    public static final int FIGHT_FLASH_HZ = 3;
    public static final int READY_TEXT = 2;
    public static final int FIGHT_TEXT_START = 0;
    public static final Coord FIGHT_SIZE = new Coord(986, 177);
    public static final Coord READY_SIZE = new Coord(1024, 174);

    public static final int GAME_SET = 3;
    public static final int GAME_OVER = 4;
    public static final int GAME_END_LEN = 180;
    public static final int GAME_END_TEXT_TRANSITION_LEN = 6;
    public static final int GAME_END_TEXT_LEN = 60;
    public static final Coord GAME_SET_SIZE = new Coord(538, 133);
    public static final Coord GAME_OVER_SIZE = new Coord(609, 130);

    public static final int RESULTS_COUNTING = 5;
    public static final int RESULTS_COUNTING_LEN = 60;
    public static final int RESULTS_FADE_LEN = 30;

    public static final int PAUSE = 6;

    public static final int START_ANIM = 7;
    public static final int LETTER_APPEAR_HZ = 4;
    public static final int TOTAL_LETTER_ANIM_LEN = (Letter.NUM_LETTERS - 1) * LETTER_APPEAR_HZ + Letter.LETTER_ANIM_LEN;
    public static final int NUM_SLAM_LEN = 8;
    public static final int STUDIO_PAUSE_LEN = 40;
    public static final int STUDIO_LEN = 180;
    public static final int START_ANIM_LEN = STUDIO_LEN + TOTAL_LETTER_ANIM_LEN + NUM_SLAM_LEN;

    public static final int FLASH = 8;
    public static final int FLASH_LEN = 6;

    public static final int FLASH_FADE = 9;
    public static final int FLASH_FADE_LEN = 70;
    public static final int TRUE_FLASH_FADE_LEN = 10;

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
    public static int gameState = STUDIO_ANIM_GS;

    // Players
    public static Omegaman[] omegaman = new Omegaman[Omegaman.NUM_PLAYERS];
    public static int[][] loadouts = {{NO_WEAPON, NO_WEAPON}, {NO_WEAPON, NO_WEAPON}};
    public static int[][] controls = {{KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S}, {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN}};
    public static int[][] shtKeys = {{KeyEvent.VK_C, KeyEvent.VK_V}, {KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2}};
    public static int[][] loadoutButtono = {{11, 12}, {13, 14}};

    // Boss
    public static ArrayList<Boss> bosses = new ArrayList<>();

    // Mouse/Keyboard Events
    public static Coord mouse = new Coord();
    public static boolean clicked;
    public static HashSet<Integer> pressedKey = new HashSet<>();

    // Stage statistics
    public static int stageNo = Stage.BATTLEFIELD_NO;
    public static Stage[] stage = new Stage[Stage.NO_OF_STAGES];
    public static int stageFlashCounter = 0;

    // Home menu buttons
    public static HashMap<Integer, Button> homeButtons = new HashMap<>();

    // Choose fight Buttons
    public static HashMap<Integer, Button> chooseButtons = new HashMap<>();
    public static int buttonPressed = NO_BUTTON_HIT;
    public static Button selectedIcon;
    public static HashMap<Integer, Integer> buttonoToWeaponNo = new HashMap<>();
    public static BufferedImage[] icon = new BufferedImage[Projectile.NO_OF_PLAYER_PROJECTILES];
    public static int iconFlashCounter = 0;
    public static int readyCounter = NOT_READY;

    // Pause buttons
    public static HashMap<Integer, Button> pauseButtons = new HashMap<>();

    // Game end Buttons
    public static HashMap<Integer, Button> gameEndButtons = new HashMap<>();
    public static ArrayList<TextBox> gameEndTextBoxes = new ArrayList<>();

    // Battle log buttons
    public static HashMap<Integer, Button> battleLogButtons = new HashMap<>();
    public static BufferedImage smlButtonImg;
    public static BufferedImage medButtonImg;

    // Slideshow buttons
    public static HashMap<Integer, Button> slideshowButtons = new HashMap<>();

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

    // Menu stats
    public static int transitionCounter = START_ANIM_LEN;
    public static int transitiono = START_ANIM;

    // Start menu
    public static LinkedList<Integer> letterOrder = new LinkedList<>();
    public static HashSet<Letter> letters = new HashSet<>();
    public static int pressStartCounter = 0;

    // Choose menu stats
    public static Battle battleDone;
    public static double flashRotation;

    // Main menu stats
    public static double menuManY = MENU_MAN_MIN_Y;
    public static int menuManCounter = 0;

    // Battle log stats
    public static ArrayList<Battle> battleLog = new ArrayList<>();
    public static int battleNo = 0;
    public static int sortNum = SortByTitle.NUM;
    public static boolean orderNormal = true;

    // Slideshow stats
    public static int slideNo;

    // General game statistics
    public static int screenShakeCounter = 0;
    public static BufferedImage placeHolder;
    public static int gameMode = HOME_GS;

    // Sounds
    public static Clip menuMusic;
    public static Clip endMusic;
    public static Clip superClick;
    public static Clip boom;
    public static Clip cheer;

    // Timer Settings
    public void run() {
        while(true) {
            repaint();
            try {
                Thread.sleep(1000/FPS); // 60 FPS
            }
            catch(Exception e) {}
        }
    }

    // JPanel Settings
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
    public static void main(String[] args) throws IOException {
        // Misc image imoprting
        placeHolder = ImageIO.read(new File("misc/placeholder.jpg"));

        // Start menu importing
        startBg = ImageIO.read(new File("menus/start.jpg"));
        titleNum = ImageIO.read(new File("menus/number.png"));
        pressAnyText = ImageIO.read(new File("menus/press start.png"));
        for (int i = 0; i != Letter.NUM_LETTERS; i++) {
            Letter.letters[i] = ImageIO.read(new File("menus/letter" + i + ".png"));
        }
        studioLogo = ImageIO.read(new File("menus/studio.png"));

        // Home menu image importing
        home = ImageIO.read(new File("menus/home.jpg"));
        homeButtonImg = ImageIO.read(new File("menus/home button.png"));
        menuMan = ImageIO.read(new File("menus/menuman.png"));

        // Paused images importing
        pausedBg = ImageIO.read(new File("menus/paused.png"));

        // Choose Menu image importing
        chooseMenu = ImageIO.read(new File("menus/choose.jpg"));
        buttonImg = ImageIO.read(new File("menus/button.jpg"));
        readyBar = ImageIO.read(new File("menus/ready.jpg"));
        countdownText[READY_TEXT] = ImageIO.read(new File("menus/ready text.png"));
        for (int i = 0; i != 2; i++) {
            countdownText[FIGHT_TEXT_START + i] = ImageIO.read(new File("menus/fight" + i + ".png"));
            gameOver[i] = ImageIO.read(new File("menus/game over" + i + ".png"));
            gameSet[i] = ImageIO.read(new File("menus/game set" + i + ".png"));
        }

        // Game end image importing
        for (int i = 0; i != 2 + Omegaman.NUM_PLAYERS; i++) {
            flash[i] = ImageIO.read(new File("menus/" + (i - 2) + "flash.jpg"));
        }
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            Battle.happyMan[i] = ImageIO.read(new File("menus/" + i + "happyMan.png"));
            Battle.sadMan[i] = ImageIO.read(new File("menus/" + i + "sadMan.png"));
        }
        resultsTitle = ImageIO.read(new File("menus/results title.png"));
        for (int i = 0; i != NUM_GAMEMODES; i++) {
            Battle.scoreBoard[i] = ImageIO.read(new File("menus/" + i + "scoreboard.jpg"));
        }
        battleNameBoxImg = ImageIO.read(new File("menus/battle name box.png"));

        // Battle log image importing
        battleLogBg = ImageIO.read(new File("menus/battle.jpg"));
        noBattle = ImageIO.read(new File("menus/no battle.jpg"));
        smlButtonImg = ImageIO.read(new File("menus/sml button.jpg"));
        medButtonImg = ImageIO.read(new File("menus/med button.jpg"));

        // Slideshow image importing
        for (int i = 0; i != NUM_SLIDES; i++) {
            slides[i] = ImageIO.read(new File("slideshow/slide" + i + ".jpg"));
        }

        // Smoke image importing
        for (int i = 0; i != Smoke.NUM_SMOKES; i++) {
            Smoke.smokes[i] = ImageIO.read(new File("player sprites/smoke" + i + ".png"));
        }
        
        // Player Weapon image importing
        addWeaponIcon = ImageIO.read(new File("menus/no weapon.png"));
        Bullet.image = ImageIO.read(new File("player projectiles/bullet.png"));
        icon[Omegaman.BULLET_WEAPON_NO] = ImageIO.read(new File("menus/bullet icon.png"));
        Shotgun.image = ImageIO.read(new File("player projectiles/shotgun.png"));
        icon[Omegaman.SHOTGUN_WEAPON_NO] = ImageIO.read(new File("menus/shotgun icon.png"));
        Spammer.image = ImageIO.read(new File("player projectiles/spammer.png"));
        icon[Omegaman.SPAMMER_WEAPON_NO] = ImageIO.read(new File("menus/spammer icon.png"));
        Sniper.image = ImageIO.read(new File("player projectiles/sniper.png"));
        icon[Omegaman.SNIPER_WEAPON_NO] = ImageIO.read(new File("menus/sniper icon.png"));
        Laser.ball = ImageIO.read(new File("player projectiles/ball.png"));
        Laser.beam = ImageIO.read(new File("player projectiles/beam.png"));
        icon[Omegaman.BOOMER_WEAPON_NO] = ImageIO.read(new File("menus/boomer icon.png"));
        Spike.image = ImageIO.read(new File("player projectiles/spike.png"));
        icon[Omegaman.SPIKE_WEAPON_NO] = ImageIO.read(new File("menus/spike icon.png"));
        Thorn.image = ImageIO.read(new File("player projectiles/thorn.png"));
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            Rocket.images[i] = ImageIO.read(new File("player projectiles/" + i + "rocket.png"));
            Firework.images[i] = ImageIO.read(new File("player projectiles/" + i + "firework.png"));
            Firework.chargingImages[i] = ImageIO.read(new File("player projectiles/" + i + "fireworkCharge.png"));
            Missile.images[i] = ImageIO.read(new File("player projectiles/" + i + "missile.png"));
            Boomer.images[i] = ImageIO.read(new File("player projectiles/" + i + "boomer.png"));
            Bouncer.images[i] = ImageIO.read(new File("player projectiles/" + i + "bouncer.png"));
            Splitter.images[i] = ImageIO.read(new File("player projectiles/" + i + "splitter.png"));
        }

        // Explosion image importing
        for (int i = 0; i != Rocket.NUM_EXPLOSION_IMAGES; i++) {
            Projectile.explosionImages[i] = ImageIO.read(new File("explosions/explosion" + i + ".png"));
        }

        // Boss surge image importing
        for (int i = 0; i != NUM_SURGE_IMAGES; i++) {
            Boss.surge[i] = ImageIO.read(new File("explosions/bsurge" + i + ".png"));
        }

        // Doctor projectile image importing
        Fastener.images[Fastener.NUT] = new BufferedImage[Fastener.NUM_SPRITES[Fastener.NUT]];
        for (int i = 0; i != Fastener.NUM_SPRITES[Fastener.NUT]; i++) {
            Fastener.images[Fastener.NUT][i] = ImageIO.read(new File("doctor projectiles/nut" + i + ".png"));
        }
        Fastener.images[Fastener.BOLT] = new BufferedImage[Fastener.NUM_SPRITES[Fastener.BOLT]];
        for (int i = 0; i != Fastener.NUM_SPRITES[Fastener.BOLT]; i++) {
            Fastener.images[Fastener.BOLT][i] = ImageIO.read(new File("doctor projectiles/bolt" + i + ".png"));
        }
        for (int i = 0; i != Energy.NO_OF_SPRITES; i++) {
            Energy.images[i] = ImageIO.read(new File("doctor projectiles/energy" + i + ".png"));
        }
        for (int i = 0; i != Pincer.NO_OF_SPRITES; i++) {
            Pincer.images[i] = ImageIO.read(new File("doctor projectiles/pincer" + i + ".png"));
        }
        for (int i = 0; i != Bombot.NO_OF_SPRITES; i++) {
            Bombot.images[i] = ImageIO.read(new File("doctor projectiles/bombot" + i + ".png"));
        }

        // Dragon projectile image importing
        for (int i = 0; i != Ring.NO_OF_SPRITES; i++) {
            Ring.images[i] = ImageIO.read(new File("dragon projectiles/ring" + i + ".png"));
        }
        for (int i = 0; i != Meteor.NO_OF_SPRITES; i++) {
            Meteor.images[i] = ImageIO.read(new File("dragon projectiles/meteor" + i + ".png"));
        }
        for (int i = 0; i != Bubble.NO_OF_SPRITES; i++) {
            Bubble.images[i] = ImageIO.read(new File("dragon projectiles/bubble" + i + ".png"));
        }
        for (int i = 0; i != Fire.NO_OF_SPRITES; i++) {
            Fire.images[i] = ImageIO.read(new File("dragon projectiles/fire" + i + ".png"));
        }

        // Stages
        for (int i = 0; i != Stage.NO_OF_STAGES; i++) {
            stage[i] = new Stage(STAGE_NAME[i], PLATFORMS[i], SPAWN_COORDS[i], SPAWN_SIGN[i], SPAWN_PLATFORM_NO[i], STAGE_BUTTONO[i]);
        }

        // Doctor image importing
        for (int i = 0; i != Doctor.STATE_NO_SPRITES[Boss.DEAD]; i++) {
            Doctor.sprite[Doctor.STATE_SPRITE_START[Boss.DEAD] + i] = ImageIO.read(new File("doctor/dead" + i + ".png"));
        }
        for (int i = 0; i != Doctor.STATE_NO_SPRITES[Boss.IDLE]; i++) {
            Doctor.sprite[Doctor.STATE_SPRITE_START[Boss.IDLE] + i] = ImageIO.read(new File("doctor/idle" + i + ".png"));
        }
        for (int i = 0; i != Doctor.STATE_NO_SPRITES[Doctor.SPIT]; i++) {
            Doctor.sprite[Doctor.STATE_SPRITE_START[Doctor.SPIT] + i] = ImageIO.read(new File("doctor/spit" + i + ".png"));
        }
        for (int i = 0; i != Doctor.STATE_NO_SPRITES[Doctor.LAUGH]; i++) {
            Doctor.sprite[Doctor.STATE_SPRITE_START[Doctor.LAUGH] + i] = ImageIO.read(new File("doctor/laugh" + i + ".png"));
        }

        // Dragon image importing
        for (int i = 0; i != Dragon.STATE_NO_SPRITES[Boss.DEAD]; i++) {
            Dragon.sprite[Dragon.STATE_SPRITE_START[Boss.DEAD] + i] = ImageIO.read(new File("dragon/dead" + i + ".png"));
        }
        for (int i = 0; i != Dragon.STATE_NO_SPRITES[Boss.IDLE]; i++) {
            Dragon.sprite[Dragon.STATE_SPRITE_START[Boss.IDLE] + i] = ImageIO.read(new File("dragon/idle" + i + ".png"));
        }
        for (int i = 0; i != Dragon.STATE_NO_SPRITES[Dragon.DIZZY]; i++) {
            Dragon.sprite[Dragon.STATE_SPRITE_START[Dragon.DIZZY] + i] = ImageIO.read(new File("dragon/dizzy" + i + ".png"));
        }
        for (int i = 0; i != Dragon.STATE_NO_SPRITES[Dragon.BARF]; i++) {
            Dragon.sprite[Dragon.STATE_SPRITE_START[Dragon.BARF] + i] = ImageIO.read(new File("dragon/barf" + i + ".png"));
        }

        // Buttons
        // Home menu buttons
        homeButtons.put(FIGHT_BUTTONO, new Button(homeButtonImg, HOME_BUTTON_FONT, new Coord(SCREEN_SIZE.x - HOME_BUTTON_SIZE.x / 2, HOME_BUTTON_FIRST_Y), HOME_BUTTON_SIZE.copy(), "FIGHT!!", FIGHT_BUTTONO, Button.SHADOW));
        homeButtons.put(SLIDESHOW_BUTTONO, new Button(homeButtonImg, HOME_BUTTON_FONT, new Coord(SCREEN_SIZE.x - HOME_BUTTON_SIZE.x / 2, HOME_BUTTON_FIRST_Y + HOME_BUTTON_SPACING), HOME_BUTTON_SIZE.copy(), "SLIDESHOW", SLIDESHOW_BUTTONO, Button.SHADOW));
        homeButtons.put(BATTLE_LOG_BUTTONO, new Button(homeButtonImg, HOME_BUTTON_FONT, new Coord(SCREEN_SIZE.x - HOME_BUTTON_SIZE.x / 2, HOME_BUTTON_FIRST_Y + HOME_BUTTON_SPACING * 2), HOME_BUTTON_SIZE.copy(), "BATTLE LOG", BATTLE_LOG_BUTTONO, Button.SHADOW));

        // Choose your fight menu buttons
        chooseButtons.put(CHOOSE_BACK_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SPACING + BUTTON_SIZE.x / 2, SPACING + BUTTON_SIZE.y / 2), BUTTON_SIZE.copy(), "BACK", CHOOSE_BACK_BUTTONO, Button.SHADOW));
        chooseButtons.put(stage[Stage.BATTLEFIELD_NO].buttono, new Button(stage[Stage.BATTLEFIELD_NO].image, STAGE_FONT, new Coord(SPACING + STAGE_BUTTON_SIZE.x / 2, (BLACK_BAR_TOP + BLACK_BAR_BOTTOM) / 2), STAGE_BUTTON_SIZE.copy(), stage[Stage.BATTLEFIELD_NO].stageName.toUpperCase(), stage[Stage.BATTLEFIELD_NO].buttono, Button.HIGHLIGHT)); // CHange size email Ms. Kim
        chooseButtons.put(stage[Stage.FINAL_DEST_NO].buttono, new Button(stage[Stage.FINAL_DEST_NO].image, STAGE_FONT, new Coord(SPACING * 2 + STAGE_BUTTON_SIZE.x * (1.0 / 2 + 1), (BLACK_BAR_TOP + BLACK_BAR_BOTTOM) / 2), STAGE_BUTTON_SIZE.copy(), stage[Stage.FINAL_DEST_NO].stageName.toUpperCase(), stage[Stage.FINAL_DEST_NO].buttono, Button.HIGHLIGHT));
        chooseButtons.put(3, new Button(placeHolder, STAGE_FONT, new Coord(SPACING * 3 + STAGE_BUTTON_SIZE.x * (1.0 / 2 + 2), (BLACK_BAR_TOP + BLACK_BAR_BOTTOM) / 2), STAGE_BUTTON_SIZE.copy(), "COMING IN 5-10 BUSINESS DAYS", 3, Button.HIGHLIGHT, true, false));
        chooseButtons.put(Bullet.BUTTONO, new Button(icon[Omegaman.BULLET_WEAPON_NO], new Coord(DIVIDER_RIGHT_X + SPACING + WEAPON_ICON_SIZE.x / 2, BLACK_BAR_TOP + SPACING + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), Bullet.BUTTONO));
        chooseButtons.put(Shotgun.BUTTONO, new Button(icon[Omegaman.SHOTGUN_WEAPON_NO], new Coord(DIVIDER_RIGHT_X + SPACING * 2 + WEAPON_ICON_SIZE.x * (1.0 / 2 + 1), BLACK_BAR_TOP + SPACING + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), Shotgun.BUTTONO));
        chooseButtons.put(Spammer.BUTTONO, new Button(icon[Omegaman.SPAMMER_WEAPON_NO], new Coord(DIVIDER_RIGHT_X + SPACING + WEAPON_ICON_SIZE.x / 2, BLACK_BAR_TOP + SPACING * 2 + WEAPON_ICON_SIZE.y * (1.0 / 2 + 1)), WEAPON_ICON_SIZE.copy(), Spammer.BUTTONO));
        chooseButtons.put(Sniper.BUTTONO, new Button(icon[Omegaman.SNIPER_WEAPON_NO], new Coord(DIVIDER_RIGHT_X + SPACING * 2 + WEAPON_ICON_SIZE.x * (1.0 / 2 + 1), BLACK_BAR_TOP + SPACING * 2 + WEAPON_ICON_SIZE.y * (1.0 / 2 + 1)), WEAPON_ICON_SIZE.copy(), Sniper.BUTTONO));
        chooseButtons.put(Boomer.BUTTONO, new Button(icon[Omegaman.BOOMER_WEAPON_NO], new Coord(DIVIDER_RIGHT_X + SPACING + WEAPON_ICON_SIZE.x / 2, BLACK_BAR_TOP + SPACING * 3 + WEAPON_ICON_SIZE.y * (1.0 / 2 + 2)), WEAPON_ICON_SIZE.copy(), Boomer.BUTTONO));
        chooseButtons.put(Spike.BUTTONO, new Button(icon[Omegaman.SPIKE_WEAPON_NO], new Coord(DIVIDER_RIGHT_X + SPACING * 2 + WEAPON_ICON_SIZE.x * (1.0 / 2 + 1), BLACK_BAR_TOP + SPACING * 3 + WEAPON_ICON_SIZE.y * (1.0 / 2 + 2)), WEAPON_ICON_SIZE.copy(), Spike.BUTTONO));
        chooseButtons.put(loadoutButtono[0][0], new Button(addWeaponIcon, new Coord(610 + WEAPON_ICON_SIZE.x / 2, LOADOUT_ICON_Y + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), loadoutButtono[0][0]));
        chooseButtons.put(loadoutButtono[0][1], new Button(addWeaponIcon, new Coord(610 + SPACING + WEAPON_ICON_SIZE.x * (1.0 / 2 + 1), LOADOUT_ICON_Y + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), loadoutButtono[0][1]));
        chooseButtons.put(loadoutButtono[1][0], new Button(addWeaponIcon, new Coord(1490 + WEAPON_ICON_SIZE.x / 2, LOADOUT_ICON_Y + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), loadoutButtono[1][0]));
        chooseButtons.put(loadoutButtono[1][1], new Button(addWeaponIcon, new Coord(1490 + SPACING + WEAPON_ICON_SIZE.x * (1.0 / 2 + 1), LOADOUT_ICON_Y + WEAPON_ICON_SIZE.y / 2), WEAPON_ICON_SIZE.copy(), loadoutButtono[1][1]));
        chooseButtons.put(READY_BUTTONO, new Button(readyBar, new Coord(0, SCREEN_SIZE.y / 2), READY_BAR_SIZE, READY_BUTTONO, false, false));

        buttonoToWeaponNo.put(Bullet.BUTTONO, Omegaman.BULLET_WEAPON_NO);
        buttonoToWeaponNo.put(Shotgun.BUTTONO, Omegaman.SHOTGUN_WEAPON_NO);
        buttonoToWeaponNo.put(Spammer.BUTTONO, Omegaman.SPAMMER_WEAPON_NO);
        buttonoToWeaponNo.put(Sniper.BUTTONO, Omegaman.SNIPER_WEAPON_NO);
        buttonoToWeaponNo.put(Boomer.BUTTONO, Omegaman.BOOMER_WEAPON_NO);
        buttonoToWeaponNo.put(Spike.BUTTONO, Omegaman.SPIKE_WEAPON_NO);

        // Pause buttons
        pauseButtons.put(RESUME_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x / 2, (SCREEN_SIZE.y + BUTTON_SIZE.y) / 2 + SPACING), BUTTON_SIZE.copy(), "RESUME", RESUME_BUTTONO, Button.SHADOW));
        pauseButtons.put(QUIT_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x / 2, SCREEN_SIZE.y / 2 + SPACING * 2 + BUTTON_SIZE.y * 1.5), BUTTON_SIZE.copy(), "QUIT", QUIT_BUTTONO, Button.SHADOW));

        // Game end buttons
        gameEndTextBoxes.add(new TextBox(battleNameBoxImg, BUTTON_FONT, new Coord((SCREEN_SIZE.x - BATTLE_NAME_BOX_SIZE.x - RESULTS_SPACING) / 2, RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING * 2 + Battle.SCOREBOARD_SIZE.y + BATTLE_NAME_BOX_SIZE.y / 2), BATTLE_NAME_BOX_SIZE, Button.SHADOW));
        gameEndButtons.put(NEXT_BATTLE_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord((SCREEN_SIZE.x + BUTTON_SIZE.x + RESULTS_SPACING) / 2, RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING * 2 + Battle.SCOREBOARD_SIZE.y + BATTLE_NAME_BOX_SIZE.y / 2), BUTTON_SIZE.copy(), "NEXT BATTLE!", NEXT_BATTLE_BUTTONO, Button.SHADOW));
        
        // Battle log buttons
        battleLogButtons.put(BATTLE_LOG_BACK_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SPACING + BUTTON_SIZE.x / 2, SPACING + BUTTON_SIZE.y / 2), BUTTON_SIZE.copy(), "BACK", BATTLE_LOG_BACK_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_BACK_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x / 2 - HALF_BATTLE_NO_SIZE - ARROW_BUTTON_SPACING - SML_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), "<", BATTLE_NO_BACK_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_ALL_BACK_BUTTONO, new Button(medButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x / 2 - HALF_BATTLE_NO_SIZE - ARROW_BUTTON_SPACING * 2 - SML_BUTTON_SIZE.x - MED_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), MED_BUTTON_SIZE.copy(), "<<", BATTLE_NO_ALL_BACK_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_NEXT_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x / 2 + HALF_BATTLE_NO_SIZE + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), ">", BATTLE_NO_NEXT_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_ALL_NEXT_BUTTONO, new Button(medButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x / 2 + HALF_BATTLE_NO_SIZE + ARROW_BUTTON_SPACING * 2 + SML_BUTTON_SIZE.x + MED_BUTTON_SIZE.x / 2, BATTLE_LOG_SCOREBOARD_COORD.y + Battle.SCOREBOARD_SIZE.y + ARROW_BUTTON_SPACING + SML_BUTTON_SIZE.y / 2), MED_BUTTON_SIZE.copy(), ">>", BATTLE_NO_ALL_NEXT_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_SORT_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING - BUTTON_SIZE.x / 2, SPACING + BUTTON_SIZE.y / 2), BUTTON_SIZE.copy(), "SORT BY: " + getSortName(), BATTLE_NO_SORT_BUTTONO, Button.SHADOW));
        battleLogButtons.put(BATTLE_NO_ORDER_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING - BUTTON_SIZE.x / 2, SPACING * 2 + BUTTON_SIZE.y * 1.5), BUTTON_SIZE.copy(), "ORDER BY: " + getOrderName(), BATTLE_NO_ORDER_BUTTONO, Button.SHADOW));

        // Pause buttons
        slideshowButtons.put(SLIDESHOW_BACK_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SPACING + BUTTON_SIZE.x / 2, SPACING + BUTTON_SIZE.y / 2), BUTTON_SIZE.copy(), "BACK", SLIDESHOW_BACK_BUTTONO, Button.SHADOW));
        slideshowButtons.put(SLIDE_NO_BACK_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING * 2 - SML_BUTTON_SIZE.x * 1.5, SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), "<", SLIDE_NO_BACK_BUTTONO, Button.SHADOW));
        slideshowButtons.put(SLIDE_NO_NEXT_BUTTONO, new Button(smlButtonImg, BUTTON_FONT, new Coord(SCREEN_SIZE.x - SPACING - SML_BUTTON_SIZE.x / 2, SPACING + SML_BUTTON_SIZE.y / 2), SML_BUTTON_SIZE.copy(), ">", SLIDE_NO_NEXT_BUTTONO, Button.SHADOW));

        // Battle log text file reading
        BufferedReader br = new BufferedReader(new FileReader("menus/" + BATTLE_LOG_FILE_NAME + ".txt"));
        int numBattles = Integer.parseInt(br.readLine());
        String stageName, stringStats[];
        int gameMode, winner;
        double[][] stats;
        for (int i = 0; i != numBattles; i++) {
            stageName = br.readLine();
            gameMode = Integer.parseInt(br.readLine());
            winner = Integer.parseInt(br.readLine());
            stats = new double[Omegaman.NUM_PLAYERS][Omegaman.NO_OF_STATS];
            for (int j = 0; j != Omegaman.NUM_PLAYERS; j++) {
                stringStats = br.readLine().split(" ");
                for (int k = 0; k != Omegaman.NO_OF_STATS; k++) {
                    stats[j][k] = Double.parseDouble(stringStats[k]);
                }
            }
            battleLog.add(new Battle(stageName, gameMode, winner, stats));
            battleLog.get(i).name = br.readLine();
        }
        br.close();
        resortLog();

        try {
            menuMusic = AudioSystem.getClip();
            menuMusic.open(AudioSystem.getAudioInputStream(new File("music/menu music.wav").toURI().toURL()));
            menuMusic.setFramePosition(0);

            endMusic = AudioSystem.getClip();
            endMusic.open(AudioSystem.getAudioInputStream(new File("music/end music.wav").toURI().toURL()));
            endMusic.setFramePosition(0);

            superClick = AudioSystem.getClip();
            superClick.open(AudioSystem.getAudioInputStream(new File("SFX/super click.wav").toURI().toURL()));
            superClick.setFramePosition(0);

            Button.click = AudioSystem.getClip();
            Button.click.open(AudioSystem.getAudioInputStream(new File("SFX/click.wav").toURI().toURL()));
            Button.click.setFramePosition(0);

            Button.hover = AudioSystem.getClip();
            Button.hover.open(AudioSystem.getAudioInputStream(new File("SFX/hover.wav").toURI().toURL()));
            Button.hover.setFramePosition(0);

            boom = AudioSystem.getClip();
            boom.open(AudioSystem.getAudioInputStream(new File("SFX/boom.wav").toURI().toURL()));
            boom.setFramePosition(0);

            cheer = AudioSystem.getClip();
            cheer.open(AudioSystem.getAudioInputStream(new File("SFX/cheer.wav").toURI().toURL()));
            cheer.setFramePosition(0);
        }
        catch (Exception e) {}

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
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("HUD/0face.png"));
    }


    // Draw Screen
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        if (screenShakeCounter != 0) {
            screenShakeCounter--;
            if (screenShakeCounter % SCREEN_SHAKE_HZ == 0) {
                Stage.coord.x = randomSign() * screenShakeCounter / 2 * Math.random();
                Stage.coord.y = randomSign() * screenShakeCounter / 2 * Math.random();
            }
            g.translate((int) Stage.coord.x, (int) Stage.coord.y);
        }
        if (gameState == STUDIO_ANIM_GS) {
            g.drawImage(startBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            if (transitiono != START_ANIM) g.drawImage(titleNum, (int) (SCREEN_SIZE.x - TITLE_NUM_SIZE.x) / 2, TITLE_NUM_Y, null);
            setOpacity(0.5 * (Math.sin(Math.PI * 2 / (PRESS_START_BLINK_HZ * 2) * (pressStartCounter - PRESS_START_BLINK_HZ / 2)) + 1), g2);
            g.drawImage(pressAnyText, (int) (SCREEN_SIZE.x - PRESS_START_SIZE.x) / 2, PRESS_START_Y, null);
            setOpacity(1, g2);
            if (transitiono != NO_TRANSITION) {
                drawTransition(g2);
                transition();
            }
            else {
                pressStartCounter = (pressStartCounter + 1) % (PRESS_START_BLINK_HZ * 2);
                if (pressedKey.size() != 0) {
                    transitiono = FLASH;
                    transitionCounter = FLASH_LEN;
                    superClick.stop();
                    superClick.setFramePosition(0);
                    superClick.start();
                }
            }
        }
        else if(gameState == HOME_GS) {
            g.drawImage(home, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            g.drawImage(menuMan, (int) (SCREEN_SIZE.x / 4 - MENU_MAN_SIZE.x / 2), (int) menuManY, null);

            drawButtons(homeButtons.values(), g);

            if (transitiono != NO_TRANSITION) {
                drawTransition(g2);
                transition();
            }
            else {
                actionPerformed();
                if (transitiono == NO_TRANSITION) {
                    processButtons(homeButtons.values());

                    menuManCounter = (menuManCounter + 1) % (MENU_MAN_MOVE_TIMES * MENU_MAN_MOVE_HZ * 2 + MENU_MAN_DOWN_PAUSE + MENU_MAN_UP_PAUSE);
                    if (menuManCounter % MENU_MAN_MOVE_HZ == 0) {
                        if (menuManCounter < MENU_MAN_MOVE_TIMES * MENU_MAN_MOVE_HZ) {
                            menuManY += MENU_MAN_SPD;
                        }
                        else if (menuManCounter >= MENU_MAN_MOVE_TIMES * MENU_MAN_MOVE_HZ + MENU_MAN_DOWN_PAUSE && menuManCounter < MENU_MAN_MOVE_TIMES * MENU_MAN_MOVE_HZ * 2 + MENU_MAN_DOWN_PAUSE) {
                            menuManY -= MENU_MAN_SPD;
                        }
                    }
                }
            }
        }
        else if(gameState == CHOOSE_FIGHT_GS) {
            g.drawImage(chooseMenu, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);

            // Flashing of selected stage
            stageFlashCounter = (stageFlashCounter + 1) % (FLASH_HZ * 2);
            if (stageFlashCounter < FLASH_HZ) g.setColor(PURPLE);
            else g.setColor(Color.YELLOW);
            Button stageButton = chooseButtons.get(stage[stageNo].buttono);
            Coord stageButtonSize = stageButton.size[stageButton.state];
            g.fillRect((int) (stageButton.coord.x - stageButtonSize.x / 2 - FLASH_SIZE), (int) (stageButton.coord.y - stageButtonSize.y / 2 - FLASH_SIZE),
            (int) (stageButtonSize.x + FLASH_SIZE * 2), (int) (stageButtonSize.y + FLASH_SIZE * 2));

            // Each weapon has their own icon
            if (selectedIcon != null) {
                iconFlashCounter = (iconFlashCounter + 1) % (FLASH_HZ * 2);
                if (iconFlashCounter < FLASH_HZ) g.setColor(PURPLE);
                else g.setColor(Color.YELLOW);
                Coord selectedIconSize = selectedIcon.size[selectedIcon.state];
                g.fillOval((int) (selectedIcon.coord.x - selectedIconSize.x / 2 - FLASH_SIZE), (int) (selectedIcon.coord.y - selectedIconSize.y / 2 - FLASH_SIZE),
                (int) (selectedIconSize.x + FLASH_SIZE * 2), (int) (selectedIconSize.y + FLASH_SIZE * 2));
            }

            drawButtons(chooseButtons.values(), g);

            if (transitiono != NO_TRANSITION) {
                drawTransition(g2);
                transition();
            }
            else {
                actionPerformed();
                if (transitiono == NO_TRANSITION) {
                    processButtons(chooseButtons.values());

                    Button readyButton = chooseButtons.get(READY_BUTTONO); 
                    if (readyCounter == NOT_READY) {
                        if (isReady()) {
                            readyCounter = 0;
                            // Set ready button to visible
                            readyButton.canSee = true;
                            readyButton.coord.x = -READY_BAR_SIZE.x / 2;
                        }
                    }
                    else if (readyCounter != READY_ANIM_LEN) {
                        readyCounter++;
                        readyButton.coord.x = lerp(-READY_BAR_SIZE.x / 2, READY_BAR_SIZE.x / 2, (double) readyCounter / READY_ANIM_LEN);
                        if (readyCounter == READY_ANIM_LEN) {
                            readyButton.canUse = true;
                        }
                    }
                    else {
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
        else if(gameState == GAME_GS) {
            stage[stageNo].drawStage(g);

            for (Boss boss: bosses) {
                if (boss.state != Boss.DEAD || boss.coord.y <= SCREEN_SIZE.y + boss.size.y / 2) boss.draw(g);
                else boss.drawSurge(g);
            }

            for (Omegaman omega: omegaman) {
                omega.drawHUD(g);
                omega.drawSmokes(g2);
                if (omega.state == Omegaman.ALIVE_STATE) {
                    omega.draw(g);
                    omega.drawCharge(g);
                    omega.drawPercent(g);
                }
            }

            for (Boss boss: bosses) {
                boss.drawProjectiles(g2);
            }
            
            for (Omegaman omega: omegaman) {
                omega.drawProjectiles(g2);
            }
            
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

            if ((transitiono != NO_TRANSITION && transitiono != COUNTDOWN && transitiono != GAME_OVER && transitiono != GAME_SET) ||
            (transitiono == COUNTDOWN && transitionCounter >= FIGHT_TEXT_LEN) || 
            (transitiono == GAME_OVER && transitionCounter >= GAME_END_LEN - GAME_END_TEXT_LEN - GAME_END_TEXT_TRANSITION_LEN) ||
            (transitiono == GAME_SET && transitionCounter >= GAME_END_LEN - GAME_END_TEXT_LEN - GAME_END_TEXT_TRANSITION_LEN)) {
                drawTransition(g2);
                transition();
            }
            else {
                if (transitiono != GAME_OVER && transitiono != GAME_SET) {
                    checkPause();
                    checkWin();
                }

                for (Omegaman omega: omegaman) {
                    omega.addbabyProjectiles();
                }
                for (Omegaman omega: omegaman) {
                    omega.processProjectiles();
                }
                for (Omegaman omega: omegaman) {
                    omega.deleteDeadProjectiles();
                }
                for (Boss boss: bosses) {
                    boss.addbabyProjectiles();
                }
                for (Boss boss: bosses) {
                    boss.processProjectiles();
                }
                for (Boss boss: bosses) {
                    boss.deleteDeadProjectiles();
                }

                for (Omegaman omega: omegaman) {
                    omega.processSmokes();
                    if (omega.state == Omegaman.ALIVE_STATE) {
                        if (omega.stunCounter == Omegaman.NOT_STUNNED) {
                            omega.controlX(pressedKey.contains(omega.lftKey), pressedKey.contains(omega.ritKey));
                            omega.controlY(pressedKey.contains(omega.upKey), pressedKey.contains(omega.dwnKey));
                            omega.controlShoot(pressedKey);
                            omega.moveAerial(pressedKey.contains(omega.upKey));
                        }
                        else omega.knockback();

                        omega.move();
                        omega.checkState();
                        omega.checkBossHitbox();
                        omega.countInv();
                        omega.regenSkillPts();
                        omega.shakePercent();
                    }
                }
                
                for (Omegaman omega: omegaman) {
                    if (omega.state != Omegaman.ALIVE_STATE) {
                        omega.frameCounter++;
                        if (omega.frameCounter == SURGE_TIME) omega.prepareForRespawn();
                        else if (omega.frameCounter >= SURGE_TIME + Omegaman.RESPAWN_PAUSE) {
                            omega.respawn(pressedKey.contains(omega.dwnKey));
                            omega.countInv();
                        }
                    }
                }

                for (Boss boss: bosses) {
                    if (boss.state != Boss.DEAD) {
                        if (boss.transitionTo != Boss.NO_TRANSITION) {
                            boss.transition();
                        }
                        else boss.attack();
                        boss.backgroundAttack();
                    }
                    else {
                        if (boss.coord.y <= SCREEN_SIZE.y + boss.size.y / 2) {
                            boss.fall();
                        }
                        else {
                            boss.surge();
                        }
                    }
                }

                if (transitiono == COUNTDOWN || transitiono == GAME_OVER || transitiono == GAME_SET) {
                    drawTransition(g2);
                    transition();
                }
            }
        }
        else if(gameState == GAME_END_GS) {
            flashRotation = (flashRotation + FLASH_ROTATE_SPD) % (Math.PI * 2);
            g2.rotate(flashRotation, SCREEN_SIZE.x / 2, SCREEN_SIZE.y / 2);
            g2.drawImage(flash[battleDone.winner + 2], (int) (SCREEN_SIZE.x - Math.ceil(Math.hypot(SCREEN_SIZE.x, SCREEN_SIZE.y))) / 2, (int) (SCREEN_SIZE.y - Math.ceil(Math.hypot(SCREEN_SIZE.x, SCREEN_SIZE.y))) / 2, null);
            g2.rotate(-flashRotation, SCREEN_SIZE.x / 2, SCREEN_SIZE.y / 2);

            g.drawImage(resultsTitle, (int) (SCREEN_SIZE.x - RESULTS_TITLE_SIZE.x) / 2, RESULTS_EDGE_SPACING, null);

            battleDone.drawEmoMan(g);

            if (transitiono != RESULTS_COUNTING) battleDone.drawScoreBoard(new Coord((SCREEN_SIZE.x - Battle.SCOREBOARD_SIZE.x) / 2, RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING), Battle.FULL_STATS, g);

            drawButtons(gameEndButtons.values(), g);
            drawTextBoxes(gameEndTextBoxes, g);

            if (transitiono != NO_TRANSITION) {
                drawTransition(g2);
                transition();
            }
            else {
                actionPerformed();
                if (transitiono == NO_TRANSITION) {
                    processButtons(gameEndButtons.values());
                    processTextBoxes(gameEndTextBoxes, g);
                }
            }
        }
        else if (gameState == SLIDESHOW_GS) {
            g.drawImage(slides[slideNo], 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);

            drawButtons(slideshowButtons.values(), g);

            if (transitiono != NO_TRANSITION) {
                drawTransition(g2);
                transition();
            }
            else {
                actionPerformed();
                if (transitiono == NO_TRANSITION) {
                    processButtons(slideshowButtons.values());
                }
            }
        }
        else if (gameState == BATTLE_LOG_GS) {
            g.drawImage(battleLogBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);

            if (battleLog.size() == 0) {
                g.drawImage(noBattle, (int) BATTLE_LOG_SCOREBOARD_COORD.x, (int) BATTLE_LOG_SCOREBOARD_COORD.y, null);
            }
            else {
                battleLog.get(battleNo).drawScoreBoard(BATTLE_LOG_SCOREBOARD_COORD, Battle.FULL_STATS, g);
                battleLog.get(battleNo).drawEmoMan(g);
                battleLog.get(battleNo).drawBattleInfo(BATTLE_LOG_BATTLE_INFO_COORD, g);
            }

            g.setColor(Color.WHITE);
            g.setFont(Battle.SCOREBOARD_FONT);
            g.drawString((battleNo + 1) + "/" + battleLog.size(), (int) (SCREEN_SIZE.x - g.getFontMetrics().stringWidth((battleNo + 1) + "/" + battleLog.size())) / 2, BATTLE_NO_Y_COORD);

            drawButtons(battleLogButtons.values(), g);

            if (transitiono != NO_TRANSITION) {
                drawTransition(g2);
                transition();
            }
            else {
                actionPerformed();
                if (transitiono == NO_TRANSITION) {
                    processButtons(battleLogButtons.values());
                }
            }
        }
    }

    public static double lerp(double orig, double goal, double alpha) {
        return orig + (goal - orig) * alpha;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double normalizeAngle(double angle) {
        angle = ((angle + Math.PI) % (2 * Math.PI));
        if (angle < 0) angle += 2 * Math.PI;
        return angle - Math.PI;
    }

    public static double signToRadians(int sign) {
        return Math.PI * (1 - (sign + 1) / 2);
    }

    public static int randomSign() {
        return (int) (Math.random() + 0.5) * 2 - 1;
    }

    public static boolean intersects(Coord coord1, Coord size1, Coord coord2, Coord size2, double leeway) {
        return Math.abs(coord1.x - coord2.x) < (size1.x + size2.x) / 2 - leeway && Math.abs(coord1.y - coord2.y) < (size1.y + size2.y) / 2 - leeway; 
    }

    public static void setOpacity(double alpha, Graphics2D g2) {
        try {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
        } catch (Exception e) {}
    }

    public static void resetButtons(Collection<Button> buttons) {
        for (Button button : buttons) {
            button.state = Button.NOPRESSED;
        }
    }

    public static void resetTextBoxes(Collection<TextBox> textBoxes) {
        for (TextBox textBox : textBoxes) {
            textBox.state = TextBox.NOPRESSED;
        }
    }

    public static void processButtons(Collection<Button> buttons) {
        buttonPressed = NO_BUTTON_HIT;
        for (Button button: buttons) {
            if (button.process(mouse, clicked)) {
                buttonPressed = button.num;
            }
        }
    }

    public static void processTextBoxes(Collection<TextBox> textBoxes, Graphics g) {
        for (TextBox textBox: textBoxes) {
            textBox.process(mouse, clicked, g);
        }
    }

    public static void drawButtons(Collection<Button> buttons, Graphics g) {
        for (Button button: buttons) {
            button.draw(g);
        }
    }

    public static void drawTextBoxes(Collection<TextBox> textBoxes, Graphics g) {
        for (TextBox textBox: textBoxes) {
            textBox.draw(g);
        }
    }

    public static String getSortName() {
        if (sortNum == SortByTitle.NUM) {
            return SortByTitle.NAME;
        }
        else if (sortNum == SortByGrade.NUM) {
            return SortByGrade.NAME;
        }
        return null;
    }

    public static String getOrderName() {
        return orderNormal? "LOWEST": "HIGHEST";
    }

    public static Comparator<Battle> getComparator() {
        if (sortNum == SortByTitle.NUM) {
            return new SortByTitle();
        }
        else if (sortNum == SortByGrade.NUM) {
            return new SortByGrade();
        }
        return null;
    }

    public static void resortLog() {
        if (orderNormal) battleLog.sort(getComparator());
        else battleLog.sort(Collections.reverseOrder(getComparator()));
    }

    public static void transition() {
        transitionCounter--;
        if (transitiono == FADE_IN) {
            if (transitionCounter == 0) {
                transitiono = NO_TRANSITION;
            }
        }
        if (transitiono == READY_FADE) {
            if (transitionCounter == 0) {
                gameState = GAME_GS;

                for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                    try {
                        omegaman[i] = new Omegaman(i, stage[stageNo].spawnCoords[i].copy(), stage[stageNo].spawnSpriteSign[i], stage[stageNo].spawnPlatformNo[i], controls[i], shtKeys[i], loadouts[i].clone(), loadoutButtono[i]);
                    }
                    catch (IOException e) {}
                    for (int j = 0; j != omegaman[i].loadout.length; j++) {
                        loadouts[i][j] = NO_WEAPON;
                        chooseButtons.get(omegaman[i].loadoutButtono[j]).image = addWeaponIcon;
                    }
                }

                readyCounter = -1;
                stageFlashCounter = 0;
                iconFlashCounter = 0;
                chooseButtons.get(READY_BUTTONO).canUse = false;
                chooseButtons.get(READY_BUTTONO).canSee = false;

                if (gameMode == TWOPVE) {
                    if (stageNo == Stage.BATTLEFIELD_NO) {
                        bosses.add(new Doctor(1));
                    }
                    else if (stageNo == Stage.FINAL_DEST_NO) {
                        bosses.add(new Dragon(1));
                    }
                }

                transitionCounter = COUNTDOWN_LEN;
                transitiono = COUNTDOWN;
                stage[stageNo].music.stop();
                stage[stageNo].music.setFramePosition(0);
                stage[stageNo].music.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        else if (transitiono == COUNTDOWN) {
            if (transitionCounter == 0) {
                transitiono = NO_TRANSITION;
            }
        }
        else if (transitiono == GAME_OVER) {
            if (transitionCounter == 0) {
                gameState = GAME_END_GS;
                transitiono = RESULTS_COUNTING;
                transitionCounter = RESULTS_COUNTING_LEN;
                if (gameMode == TWOPVE) {
                    double[][] stats = getStatsAndClearChars();
                    battleDone = new Battle(stage[stageNo].stageName, TWOPVE, Battle.BOSS_WIN, stats);
                }
            }
        }
        else if (transitiono == GAME_SET) {
            if (transitionCounter == 0) {
                gameState = GAME_END_GS;
                transitiono = RESULTS_COUNTING;
                transitionCounter = RESULTS_COUNTING_LEN;
                if (gameMode == TWOPVE) {
                    double[][] stats = getStatsAndClearChars();
                    battleDone = new Battle(stage[stageNo].stageName, TWOPVE, Battle.BOTH_WIN, stats);
                }
            }
        }
        else if (transitiono == RESULTS_COUNTING) {
            if (transitionCounter == 0) {
                transitiono = NO_TRANSITION;
            }
        }
        else if (transitiono == PAUSE) {
            actionPerformed();
            if (transitiono == PAUSE) {
                processButtons(pauseButtons.values());
            }
        }
        else if (transitiono == START_ANIM) {
            if (transitionCounter >= NUM_SLAM_LEN && transitionCounter <= START_ANIM_LEN - STUDIO_LEN) {
                if (transitionCounter >= NUM_SLAM_LEN + Letter.LETTER_ANIM_LEN) {
                    if ((transitionCounter - (NUM_SLAM_LEN + Letter.LETTER_ANIM_LEN)) % LETTER_APPEAR_HZ == 0) {
                        int letterNo = letterOrder.removeFirst();
                        letters.add(new Letter(Letter.LETTER_COORDS[letterNo], Letter.LETTER_SIZE[letterNo], Letter.letters[letterNo], Math.random() < 0.5));
                    }
                }
                else if (transitionCounter == NUM_SLAM_LEN) {
                    menuMusic.stop();
                    menuMusic.setFramePosition(0);
                    menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
                for (Letter letter: letters) {
                    letter.process();
                }
            }
            if (transitionCounter == 0) {
                transitiono = NO_TRANSITION;
                screenShakeCounter += NUM_SLAM_SCREENSHAKE;
            }
        }
        else if (transitiono == FLASH) {
            if (transitionCounter == 0) {
                gameState = HOME_GS;
                transitiono = FLASH_FADE;
                transitionCounter = FLASH_FADE_LEN;
            }
        }
        else if (transitiono == FLASH_FADE) {
            if (transitionCounter == 0) {
                transitiono = NO_TRANSITION;
            }
        }
    }

    public static void writeFile() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("menus/" + BATTLE_LOG_FILE_NAME + ".txt"));
            pw.println(battleLog.size());
            for (Battle b: battleLog) {
                pw.printf("%s\n%d\n%d\n", b.stageName, b.gameMode, b.winner);
                for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                    for (int j = 0; j != Omegaman.NO_OF_STATS; j++) {
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

    public static double[][] getStatsAndClearChars() {
        double[][] stats = new double[Omegaman.NUM_PLAYERS][];
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            stats[i] = omegaman[i].stats;
            stats[i][Omegaman.LIVES_LEFT_NO] = omegaman[i].livesLeft;
            omegaman[i] = null;
            for (int j = 0; j != Omegaman.NO_OF_STATS; j++) {
                if (Battle.IS_PERCENT[j]) stats[i][j] /= Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
            }
            stats[i][Omegaman.SKILL_PTS_USED_NO] /= Omegaman.ONES_PER_SKILL_PT;
        }
        bosses.clear();
        return stats;
    }

    public static void drawTransition(Graphics2D g2) {
        if (transitiono == FADE_IN) {
            drawFade((double) transitionCounter / FADE_IN_LEN, g2);
        }
        else if (transitiono == READY_FADE) {
            drawFade(1 - (double) transitionCounter / READY_FADE_LEN, g2);
        }
        else if (transitiono == COUNTDOWN) {
            if (transitionCounter > COUNTDOWN_LEN - COUNTDOWN_FADE_IN_LEN) drawFade((double) (transitionCounter - (COUNTDOWN_LEN - COUNTDOWN_FADE_IN_LEN)) / COUNTDOWN_FADE_IN_LEN, g2);
            if (transitionCounter >= FIGHT_TEXT_LEN) {
                double progress = Math.log(COUNTDOWN_LEN - transitionCounter + 1) / Math.log(COUNTDOWN_LEN - FIGHT_TEXT_LEN + 1);
                g2.drawImage(countdownText[READY_TEXT], (int) (SCREEN_SIZE.x / 2 - READY_SIZE.x * progress / 2), (int) (SCREEN_SIZE.y / 2 - READY_SIZE.y * progress / 2),
                (int) (READY_SIZE.x * progress), (int) (READY_SIZE.y * progress), null);
            }
            else if (transitionCounter >= FIGHT_TEXT_LEN - FIGHT_TRANSITION_LEN) {
                double progress = (double) (transitionCounter - (FIGHT_TEXT_LEN - FIGHT_TRANSITION_LEN)) / FIGHT_TRANSITION_LEN;
                g2.drawImage(countdownText[FIGHT_TEXT_START], (int) (SCREEN_SIZE.x / 2 - lerp(FIGHT_SIZE.y, SCREEN_SIZE.y, progress) * FIGHT_SIZE.x / FIGHT_SIZE.y / 2), (int) (SCREEN_SIZE.y / 2 - lerp(FIGHT_SIZE.y, SCREEN_SIZE.y, progress) / 2),
                (int) (lerp(FIGHT_SIZE.y, SCREEN_SIZE.y, progress) * FIGHT_SIZE.x / FIGHT_SIZE.y), (int) lerp(FIGHT_SIZE.y, SCREEN_SIZE.y, progress), null);
            }
            else if (transitionCounter >= FIGHT_TRANSITION_LEN) {
                g2.drawImage(countdownText[FIGHT_TEXT_START + transitionCounter % (FIGHT_FLASH_HZ * 2) / FIGHT_FLASH_HZ], (int) (SCREEN_SIZE.x / 2 - FIGHT_SIZE.x / 2), (int) (SCREEN_SIZE.y / 2 - FIGHT_SIZE.y / 2),
                (int) (FIGHT_SIZE.x), (int) (FIGHT_SIZE.y), null);
            }
            else {
                double progress = (double) (transitionCounter) / FIGHT_TRANSITION_LEN;
                g2.drawImage(countdownText[FIGHT_TEXT_START], (int) (SCREEN_SIZE.x / 2 - lerp(SCREEN_SIZE.y, FIGHT_SIZE.y, progress) * FIGHT_SIZE.x / FIGHT_SIZE.y / 2), (int) (SCREEN_SIZE.y / 2 - lerp(SCREEN_SIZE.y, FIGHT_SIZE.y, progress) / 2),
                (int) (lerp(SCREEN_SIZE.y, FIGHT_SIZE.y, progress) * FIGHT_SIZE.x / FIGHT_SIZE.y), (int) lerp(SCREEN_SIZE.y, FIGHT_SIZE.y, progress), null);
            }
        }
        else if (transitiono == GAME_OVER) {
            if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN) {
                double progress = (double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN)) / GAME_END_TEXT_TRANSITION_LEN;
                g2.drawImage(gameOver[0], (int) lerp(SCREEN_SIZE.x / 2 - GAME_OVER_SIZE.x, -GAME_OVER_SIZE.x, progress), (int) (SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                g2.drawImage(gameOver[1], (int) lerp(SCREEN_SIZE.x / 2, SCREEN_SIZE.x, progress), (int) (SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN - GAME_END_TEXT_LEN) {
                g2.drawImage(gameOver[0], (int) (SCREEN_SIZE.x / 2 - GAME_OVER_SIZE.x), (int) (SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                g2.drawImage(gameOver[1], (int) (SCREEN_SIZE.x / 2), (int) (SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN) {
                setOpacity((double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN)) / GAME_END_TEXT_TRANSITION_LEN, g2);
                g2.drawImage(gameOver[0], (int) (SCREEN_SIZE.x / 2 - GAME_OVER_SIZE.x), (int) (SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                g2.drawImage(gameOver[1], (int) (SCREEN_SIZE.x / 2), (int) (SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                setOpacity(1, g2);
            }
            else {
                drawFade(1 - (double) (transitionCounter) / (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN), g2);
            }
        }
        else if (transitiono == GAME_SET) {
            if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN) {
                double progress = (double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN)) / GAME_END_TEXT_TRANSITION_LEN;
                g2.drawImage(gameSet[0], (int) lerp(SCREEN_SIZE.x / 2 - GAME_SET_SIZE.x, -GAME_SET_SIZE.x, progress), (int) (SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                g2.drawImage(gameSet[1], (int) lerp(SCREEN_SIZE.x / 2, SCREEN_SIZE.x, progress), (int) (SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN - GAME_END_TEXT_LEN) {
                g2.drawImage(gameSet[0], (int) (SCREEN_SIZE.x / 2 - GAME_SET_SIZE.x), (int) (SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                g2.drawImage(gameSet[1], (int) (SCREEN_SIZE.x / 2), (int) (SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN) {
                setOpacity((double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN)) / GAME_END_TEXT_TRANSITION_LEN, g2);
                g2.drawImage(gameSet[0], (int) (SCREEN_SIZE.x / 2 - GAME_SET_SIZE.x), (int) (SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                g2.drawImage(gameSet[1], (int) (SCREEN_SIZE.x / 2), (int) (SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                setOpacity(1, g2);
            }
            else {
                drawFade(1 - (double) (transitionCounter) / (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN), g2);
            }
        }
        else if (transitiono == RESULTS_COUNTING) {
            battleDone.drawScoreBoard(new Coord((SCREEN_SIZE.x - Battle.SCOREBOARD_SIZE.x) / 2, RESULTS_EDGE_SPACING + RESULTS_TITLE_SIZE.y + RESULTS_SPACING), 1 - (double) transitionCounter / RESULTS_COUNTING_LEN, g2);
            if (transitionCounter >= RESULTS_COUNTING_LEN - RESULTS_FADE_LEN) {
                drawFade((double) (transitionCounter - (RESULTS_COUNTING_LEN - RESULTS_FADE_LEN)) / RESULTS_FADE_LEN, g2);
            }
        }
        else if (transitiono == PAUSE) {
            g2.drawImage(pausedBg, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            drawButtons(pauseButtons.values(), g2);
        }
        else if (transitiono == START_ANIM) {
            if (transitionCounter >= NUM_SLAM_LEN) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y);
                if (transitionCounter >= START_ANIM_LEN - STUDIO_LEN) {
                    if (transitionCounter >= START_ANIM_LEN - STUDIO_LEN + STUDIO_PAUSE_LEN && transitionCounter < START_ANIM_LEN - STUDIO_PAUSE_LEN) {
                        if (transitionCounter >= START_ANIM_LEN - STUDIO_PAUSE_LEN - STUDIO_FADE_LEN) {
                            setOpacity(1 - (double) (transitionCounter - (START_ANIM_LEN - STUDIO_PAUSE_LEN - STUDIO_FADE_LEN)) / STUDIO_FADE_LEN, g2);
                        }
                        else if (transitionCounter < START_ANIM_LEN - STUDIO_LEN + STUDIO_PAUSE_LEN + STUDIO_FADE_LEN) {
                            setOpacity((double) (transitionCounter - (START_ANIM_LEN - STUDIO_LEN + STUDIO_PAUSE_LEN)) / STUDIO_FADE_LEN, g2);
                        }
                        g2.drawImage(studioLogo, (int) (SCREEN_SIZE.x - STUDIO_LOGO_SIZE.x) / 2, (int) (SCREEN_SIZE.y - STUDIO_LOGO_SIZE.y) / 2, null);
                        setOpacity(1.0, g2);
                    }
                }
                else {
                    for (Letter letter: letters) {
                        letter.draw(g2);
                    }
                }
            }
            else {
                double opactiy = (double) transitionCounter / NUM_SLAM_LEN;
                drawFade(opactiy, g2);
                setOpacity(opactiy, g2);
                for (Letter letter: letters) {
                    letter.draw(g2);
                }
                setOpacity(1.0, g2);
                double progress = 1 - (double) (transitionCounter) / NUM_SLAM_LEN;
                Coord curSize = new Coord(lerp(TITLE_NUM_SIZE.x * SCREEN_SIZE.y / TITLE_NUM_SIZE.y, TITLE_NUM_SIZE.x, progress), lerp(SCREEN_SIZE.y, TITLE_NUM_SIZE.y, progress));
                g2.drawImage(titleNum, (int) (SCREEN_SIZE.x - curSize.x) / 2, (int) (TITLE_NUM_CENTRE_Y - curSize.y / 2), (int) curSize.x, (int) curSize.y, null);
            }
        }
        else if (transitiono == FLASH) {
            drawFlash(1 - (double) transitionCounter / FLASH_LEN, g2);
        }
        else if (transitiono == FLASH_FADE) {
            drawFlash(Math.min(1.0, (double) transitionCounter / TRUE_FLASH_FADE_LEN), g2);
        }
    }

    public static void drawFade(double amt, Graphics g) {
        g.setColor(new Color(0, 0, 0, (int) (MAX_RGB_VAL * amt)));
        g.fillRect(0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y);
    }

    public static void drawFlash(double amt, Graphics g) {
        g.setColor(new Color(255, 255, 255, (int) (MAX_RGB_VAL * amt)));
        g.fillRect(0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y);
    }

    public static boolean isReady() {
        for (int[] loadout : loadouts) {
            for (int weapon : loadout) {
                if (weapon == -1) return false;
            }
        }
        return true;
    }

    public void checkPause() {
        if (pressedKey.contains(PAUSE_KEY)) {
            transitiono = PAUSE;
        }
    }

    public void checkWin() {
        if (gameMode == TWOPVE) {
            for (Omegaman omega: omegaman) {
                if (omega.livesLeft <= 0 && omega.frameCounter == SURGE_FRAME_HZ * SURGE_SPRITE_WIN_CHECK) {
                    transitionCounter = GAME_END_LEN;
                    transitiono = GAME_OVER;
                    stage[stageNo].music.stop();
                    endMusic.stop();
                    endMusic.setFramePosition(0);
                    endMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }

            boolean bossDead = true;
            for (Boss boss: bosses) {
                if (boss.health > 0 || boss.frameCounter < SURGE_FRAME_HZ * SURGE_SPRITE_WIN_CHECK) {
                    bossDead = false;
                }
            }
            if (bossDead) {
                transitionCounter = GAME_END_LEN;
                transitiono = GAME_SET;
                stage[stageNo].music.stop();
                endMusic.stop();
                endMusic.setFramePosition(0);
                endMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public static void selectWeapon(Button weaponButton) {
        if ((weaponButton.image == selectedIcon.image && weaponButton.image != addWeaponIcon) || weaponButton == selectedIcon) {
            selectedIcon = null;
            return;
        }

        for (int i = 0; i < loadouts.length; i++) {
            for (int j = 0; j < loadouts[i].length; j++) {
                if (selectedIcon.num == loadoutButtono[i][j]) {
                    int weaponNo = buttonoToWeaponNo.get(weaponButton.num);
                    // Check if weapon is already in any loadout
                    for (int[] loadout : loadouts) {
                        for (int selectedWeapon : loadout) {
                            if (selectedWeapon == weaponNo) {
                                loadouts[i][j] = NO_WEAPON;
                                selectedIcon.image = addWeaponIcon;
                                selectedIcon = null;
                                return;
                            }
                        }
                    }
                    loadouts[i][j] = weaponNo;
                    selectedIcon.image = weaponButton.image;
                    selectedIcon = null;
                    return;
                }
            }
        }

        selectedIcon = weaponButton;
    }

    public static void selectLoadout(Button loadoutButton, int playerNo, int loadoutSlot) {
        if (loadoutButton.image == selectedIcon.image) {
            selectedIcon = null;
            return;
        }

        for (int i = 0; i < loadouts.length; i++) {
            for (int j = 0; j < loadouts[i].length; j++) {
                // Swap
                if (selectedIcon.num == loadoutButtono[i][j]) {
                    int temp0 = loadouts[playerNo][loadoutSlot];
                    loadouts[playerNo][loadoutSlot] = loadouts[i][j];
                    loadouts[i][j] = temp0;
                    BufferedImage temp1 = selectedIcon.image;
                    selectedIcon.image = loadoutButton.image;
                    loadoutButton.image = temp1;
                    selectedIcon = null;
                    return;
                }
            }
        }

        int weaponNo = buttonoToWeaponNo.get(selectedIcon.num);
        for (int[] loadout : loadouts) {
            for (int selectedWeapon : loadout) {
                if (selectedWeapon == weaponNo) {
                    loadouts[playerNo][loadoutSlot] = NO_WEAPON;
                    loadoutButton.image = addWeaponIcon;
                    selectedIcon = null;
                    return;
                }
            }
        }
        loadouts[playerNo][loadoutSlot] = weaponNo;
        loadoutButton.image = icon[weaponNo];
        selectedIcon = null;
    }

    public static void actionPerformed() {
        // Check button pressed with button num and do stuff CHECK ALL STATES here????
        if (buttonPressed != NO_BUTTON_HIT && !clicked) {
            if (gameState == HOME_GS) {
                if (buttonPressed == FIGHT_BUTTONO) {
                    gameState = CHOOSE_FIGHT_GS;
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    menuManY = MENU_MAN_MIN_Y;
                    menuManCounter = 0;
                    resetButtons(homeButtons.values());
                }
                else if (buttonPressed == SLIDESHOW_BUTTONO) {
                    gameState = SLIDESHOW_GS;
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    menuManY = MENU_MAN_MIN_Y;
                    menuManCounter = 0;
                    resetButtons(homeButtons.values());
                }
                else if (buttonPressed == BATTLE_LOG_BUTTONO) {
                    gameState = BATTLE_LOG_GS;
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    menuManY = MENU_MAN_MIN_Y;
                    menuManCounter = 0;
                    resetButtons(homeButtons.values());
                }
            }
            else if (gameState == CHOOSE_FIGHT_GS) {
                if (buttonPressed == CHOOSE_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    // transition counter to fade
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(chooseButtons.values());
                }
                else if (buttonPressed == stage[Stage.BATTLEFIELD_NO].buttono) {
                    stageNo = Stage.BATTLEFIELD_NO;
                }
                else if (buttonPressed == stage[Stage.FINAL_DEST_NO].buttono) {
                    stageNo = Stage.FINAL_DEST_NO;
                }
                else if (buttonPressed == READY_BUTTONO) {
                    transitiono = READY_FADE;
                    transitionCounter = READY_FADE_LEN;
                    menuMusic.stop();
                    superClick.stop();
                    superClick.setFramePosition(0);
                    superClick.start();
                    resetButtons(chooseButtons.values());
                }
                else {
                    for (int buttono: buttonoToWeaponNo.keySet()) {
                        if (buttonPressed == buttono) {
                            if (selectedIcon != null) {
                                selectWeapon(chooseButtons.get(buttono));
                            }
                            else {
                                selectedIcon = chooseButtons.get(buttono);
                            }
                        }
                    }
                    for (int i = 0; i != loadouts.length; i++) {
                        for (int j = 0; j != loadouts[i].length; j++) {
                            int loadoutBtn = loadoutButtono[i][j];
                            if (buttonPressed == loadoutBtn) {
                                if (selectedIcon != null) {
                                    selectLoadout(chooseButtons.get(loadoutBtn), i, j);
                                    break;
                                } else {
                                    selectedIcon = chooseButtons.get(loadoutBtn);
                                }
                            }
                        }
                    }
                }
            }
            else if (gameState == GAME_GS) {
                if (buttonPressed == RESUME_BUTTONO) {
                    transitiono = COUNTDOWN;
                    transitionCounter = COUNTDOWN_LEN - COUNTDOWN_FADE_IN_LEN / 2;
                    resetButtons(pauseButtons.values());
                }
                else if (buttonPressed == QUIT_BUTTONO) {
                    transitiono = GAME_OVER;
                    transitionCounter = GAME_END_LEN - GAME_END_TEXT_LEN - GAME_END_TEXT_TRANSITION_LEN * 2;
                    resetButtons(pauseButtons.values());
                    stage[stageNo].music.stop();
                    endMusic.stop();
                    endMusic.setFramePosition(0);
                    endMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
            else if (gameState == GAME_END_GS) {
                if (buttonPressed == NEXT_BATTLE_BUTTONO) {
                    gameState = CHOOSE_FIGHT_GS;
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    battleDone.name = gameEndTextBoxes.get(BATTLE_NAME_BOX_IDX).text == ""? "THE UNKNOWN BATTLE": gameEndTextBoxes.get(BATTLE_NAME_BOX_IDX).text;
                    gameEndTextBoxes.get(BATTLE_NAME_BOX_IDX).text = "";
                    battleLog.add(battleDone); 
                    writeFile();
                    resortLog();
                    battleDone = null;
                    flashRotation = 0;
                    resetButtons(gameEndButtons.values());
                    resetTextBoxes(gameEndTextBoxes);
                    endMusic.stop();
                    menuMusic.stop();
                    menuMusic.setFramePosition(0);
                    menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
            else if (gameState == SLIDESHOW_GS) {
                if (buttonPressed == SLIDESHOW_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(slideshowButtons.values());
                }
                else if (buttonPressed == SLIDE_NO_NEXT_BUTTONO) {
                    slideNo = (slideNo + 1) % NUM_SLIDES;
                }
                else if (buttonPressed == SLIDE_NO_BACK_BUTTONO) {
                    slideNo = (slideNo - 1 + NUM_SLIDES) % NUM_SLIDES;
                }
            }
            else if (gameState == BATTLE_LOG_GS) {
                if (buttonPressed == BATTLE_LOG_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
                    resetButtons(battleLogButtons.values());
                }
                else if (buttonPressed == BATTLE_NO_SORT_BUTTONO) {
                    sortNum = (sortNum + 1) % NO_SORTS;
                    battleLogButtons.get(BATTLE_NO_SORT_BUTTONO).text = "SORT BY: " + getSortName();
                    resortLog();
                    battleNo = 0;
                }
                else if (buttonPressed == BATTLE_NO_ORDER_BUTTONO) {
                    orderNormal = !orderNormal;
                    battleLogButtons.get(BATTLE_NO_ORDER_BUTTONO).text = "ORDER BY: " + getOrderName();
                    resortLog();
                    battleNo = 0;
                }
                else if (battleLog.size() != 0) {
                    if (buttonPressed == BATTLE_NO_BACK_BUTTONO) {
                        battleNo = (battleNo - 1 + battleLog.size()) % battleLog.size();
                    }
                    else if (buttonPressed == BATTLE_NO_ALL_BACK_BUTTONO) {
                        battleNo = 0;
                    }
                    else if (buttonPressed == BATTLE_NO_NEXT_BUTTONO) {
                        battleNo = (battleNo + 1) % battleLog.size();
                    }
                    else if (buttonPressed == BATTLE_NO_ALL_NEXT_BUTTONO) {
                        battleNo = battleLog.size() - 1;
                    }
                }
            }
            buttonPressed = NO_BUTTON_HIT;
        }
    }

    // Mouse and Keyboard Methods
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        clicked = true;
        // System.out.println(mouse);
    }
    public void mouseReleased(MouseEvent e) {
        clicked = false;
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        mouse.x = e.getX();
        mouse.y = e.getY();
    }
    public void mouseMoved(MouseEvent e) {
        mouse.x = e.getX();
        mouse.y = e.getY();
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        pressedKey.add(e.getKeyCode());
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
                if (transitiono == NO_TRANSITION) {
                    for (TextBox textBox: gameEndTextBoxes) {
                        textBox.backspace();
                    }
                }
            }
            else if (gameState == SLIDESHOW_GS) {
                
            }
            else if (gameState == BATTLE_LOG_GS) {

            }
        }
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
                if (transitiono == NO_TRANSITION) {
                    for (TextBox textBox: gameEndTextBoxes) {
                        textBox.addChar(e.getKeyChar());
                    }
                }
            }
            else if (gameState == SLIDESHOW_GS) {
                
            }
            else if (gameState == BATTLE_LOG_GS) {
                
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        pressedKey.remove(e.getKeyCode());
    }
}