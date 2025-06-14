package Version2;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
// Ernest Todo: menus, bosses
// Ernest Long term Todo: ultimate, dash, smoke

public class OmegaFight3 extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Runnable {
    // Game States
    // -1 <- Studio Animation + Start menu
    // 0 <- Home Screen
    // 1 <- Choose your fight screen
    // 2 <- In-Game Screen
    // 3 <- Game End Screen
    // 4 <- Credit/Tutorial Screen
    public static int gameState = 1;

    // Players
    public static Omegaman[] omegaman = new Omegaman[Omegaman.NUM_PLAYERS];
    public static int[][] loadouts = {{-1, -1}, {-1, -1}};
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
    public static int stageNo = 0;
    public static Stage[] stage = new Stage[Stage.NO_OF_STAGES];
    public static int stageFlashCounter = 0;

    // Buttons
    public static HashMap<Integer, Button> chooseButtons = new HashMap<>(); // [14] Skip 1 for 3rd stage for now
    public static int buttonPressed = -1;
    public static Button selectedIcon;
    public static HashMap<Integer, Integer> buttonoToWeaponNo = new HashMap<>();
    public static BufferedImage[] icon = new BufferedImage[Projectile.NO_OF_PLAYER_PROJECTILES];
    public static int iconFlashCounter = 0;
    public static int readyCounter = -1;

    // Menu images
    public static BufferedImage chooseMenu;
    public static BufferedImage buttonImg;
    public static BufferedImage addWeaponIcon;
    public static BufferedImage readyBar;
    public static BufferedImage[] countdownText = new BufferedImage[3];
    public static BufferedImage[] gameOver = new BufferedImage[2];
    public static BufferedImage[] gameSet = new BufferedImage[2];

    // Menu stats
    public static int transitionCounter = 0;
    public static int transitiono = -1;

    // General game statistics
    public static int screenShakeCounter = 0;
    public static BufferedImage placeHolder;
    public static int gameMode = 0;

    // Choose Fight Menu Constants
    public static final int NOT_READY = -1;
    public static final int READY_ANIM_LEN = 10;
    public static final int BLACK_BAR_TOP = 100;
    public static final int BLACK_BAR_BOTTOM = 500;
    public static final int DIVIDER_RIGHT_X = 1645;
    public static final int LOADOUT_ICON_Y = 700;
    public static final int NO_WEAPON = -1;
    public static final Coord READY_BAR_SIZE = new Coord(1920, 240);

    // Screen Settings
    public static final Coord SCREEN_SIZE = new Coord(1920, 960);
    public static final int FPS = 60;
    public static final int SCREEN_SHAKE_HZ = 2;
    public static final int SPACING = 25;

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

    // Button Numbers (Next avail: 15)
    public static final int NO_BUTTON_HIT = -1;
    // Choose your fight menu
    public static final int CHOOSE_BACK_BUTTONO = 0;
    public static final int READY_BUTTONO = 10;

    // Button constants
    public static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 40); 
    public static final Font STAGE_FONT = new Font("Consolas", Font.BOLD, 25);
    public static final Coord BUTTON_SIZE = new Coord(400, 50);
    public static final Coord STAGE_BUTTON_SIZE = new Coord(510, 255);
    public static final Coord WEAPON_ICON_SIZE = new Coord(100, 100);

    // Transition constants
    public static final int FADE_IN = 0;
    public static final int READY_FADE = 1;
    public static final int FADE_IN_LEN = 10;
    public static final int NO_TRANSITION = -1;
    public static final int READY_FADE_LEN = 30;

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
    public static final Coord GAME_SET_SIZE = new Coord(747, 177);
    public static final Coord GAME_OVER_SIZE = new Coord(831, 174);

    // Color constants
    public static final Color PURPLE = new Color(186, 122, 255);

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

        // Menu image importing
        chooseMenu = ImageIO.read(new File("menus/choose.jpg"));
        buttonImg = ImageIO.read(new File("menus/button.jpg"));
        readyBar = ImageIO.read(new File("menus/ready.jpg"));
        countdownText[READY_TEXT] = ImageIO.read(new File("menus/ready text.png"));
        for (int i = 0; i != 2; i++) {
            countdownText[FIGHT_TEXT_START + i] = ImageIO.read(new File("menus/fight" + i + ".png"));
            gameOver[i] = ImageIO.read(new File("menus/game over" + i + ".png"));
            gameSet[i] = ImageIO.read(new File("menus/game set" + i + ".png"));
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
        // Choose your fight menu buttons
        chooseButtons.put(CHOOSE_BACK_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SPACING + 400 / 2, SPACING + SPACING / 2), BUTTON_SIZE.copy(), "BACK", CHOOSE_BACK_BUTTONO, Button.SHADOW));
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
        
        // JFrame and JPanel
        JFrame frame = new JFrame("Omega Fight 3");
        OmegaFight3 panel = new OmegaFight3();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        }
        else if(gameState == HOME_GS) {
            
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
                transition();
                drawTransition(g2);
            }
            else {
                actionPerformed();
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
                    }
                }
            }
        }
        else if(gameState == GAME_GS) {
            stage[stageNo].drawStage(g);

            for (Boss boss: bosses) {
                if (boss.state != Boss.DEAD || boss.coord.y <= OmegaFight3.SCREEN_SIZE.y + boss.size.y / 2) boss.draw(g);
                else boss.drawSurge(g);
            }

            for (Omegaman omega: omegaman) {
                omega.drawHUD(g);
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
                transition();
                drawTransition(g2);
            }
            else {
                if (transitiono == COUNTDOWN || transitiono == GAME_OVER || transitiono == GAME_SET) {
                    transition();
                    drawTransition(g2);
                }
                if (transitiono != GAME_OVER && transitiono != GAME_SET) checkWin();

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
                        if (boss.coord.y <= OmegaFight3.SCREEN_SIZE.y + boss.size.y / 2) {
                            boss.fall();
                        }
                        else {
                            boss.surge();
                        }
                    }
                }
            }
        }
        else if(gameState == GAME_END_GS) {
         
        }
        else if (gameState == SLIDESHOW_GS) {

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

    // Static method for processing current menu buttons THINk ABT THis
    public static void processButtons(Collection<Button> buttons) {
        boolean hitButton = false;
        for (Button button: buttons) {
            if (button.process()) {
                buttonPressed = button.num;
                hitButton = true;
            }
        }
        if (!hitButton) buttonPressed = NO_BUTTON_HIT;
    }

    public static void drawButtons(Collection<Button> buttons, Graphics g) {
        for (Button button: buttons) {
            button.draw(g);
        }
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
            }
        }
        else if (transitiono == COUNTDOWN) {
            if (transitionCounter == 0) {
                transitiono = NO_TRANSITION;
            }
        }
        else if (transitiono == GAME_OVER) {
            if (transitionCounter == 0) {
                // Transfer to game finish gamestate (Prepare for next game)
                gameState = GAME_END_GS;
                transitionCounter = FADE_IN_LEN;
                transitiono = FADE_IN;
                // Make battle class to record stuff here
            }
        }
        else if (transitiono == GAME_SET) {
            if (transitionCounter == 0) {
                // Transfer to game finish gamestate (Prepare for next game)
                gameState = GAME_END_GS;
                transitionCounter = FADE_IN_LEN;
                transitiono = FADE_IN;
            }
        }
    }

    public static void drawTransition(Graphics2D g2) {
        if (transitiono == FADE_IN) {
            drawFade(transitionCounter / FADE_IN_LEN, g2);
        }
        else if (transitiono == READY_FADE) {
            drawFade(1 - (double) transitionCounter / READY_FADE_LEN, g2);
        }
        else if (transitiono == COUNTDOWN) {
            if (transitionCounter > COUNTDOWN_LEN - FADE_IN_LEN) drawFade((double) (transitionCounter - (COUNTDOWN_LEN - FADE_IN_LEN)) / FADE_IN_LEN, g2);
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
                g2.drawImage(gameOver[0], (int) OmegaFight3.lerp(SCREEN_SIZE.x / 2 - GAME_OVER_SIZE.x, -GAME_OVER_SIZE.x, progress), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                g2.drawImage(gameOver[1], (int) OmegaFight3.lerp(SCREEN_SIZE.x / 2, SCREEN_SIZE.x, progress), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN - GAME_END_TEXT_LEN) {
                g2.drawImage(gameOver[0], (int) (SCREEN_SIZE.x / 2 - GAME_OVER_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                g2.drawImage(gameOver[1], (int) (SCREEN_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN) {
                setOpacity((double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN)) / GAME_END_TEXT_TRANSITION_LEN, g2);
                g2.drawImage(gameOver[0], (int) (SCREEN_SIZE.x / 2 - GAME_OVER_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                g2.drawImage(gameOver[1], (int) (SCREEN_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_OVER_SIZE.y) / 2, null);
                setOpacity(1, g2);
            }
            else {
                drawFade(1 - (double) (transitionCounter) / (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN), g2);
            }
        }
        else if (transitiono == GAME_SET) {
            if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN) {
                double progress = (double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN)) / GAME_END_TEXT_TRANSITION_LEN;
                g2.drawImage(gameSet[0], (int) OmegaFight3.lerp(SCREEN_SIZE.x / 2 - GAME_SET_SIZE.x, -GAME_SET_SIZE.x, progress), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                g2.drawImage(gameSet[1], (int) OmegaFight3.lerp(SCREEN_SIZE.x / 2, SCREEN_SIZE.x, progress), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN - GAME_END_TEXT_LEN) {
                g2.drawImage(gameSet[0], (int) (SCREEN_SIZE.x / 2 - GAME_SET_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                g2.drawImage(gameSet[1], (int) (SCREEN_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
            }
            else if (transitionCounter >= GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN) {
                setOpacity((double) (transitionCounter - (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN)) / GAME_END_TEXT_TRANSITION_LEN, g2);
                g2.drawImage(gameSet[0], (int) (SCREEN_SIZE.x / 2 - GAME_SET_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                g2.drawImage(gameSet[1], (int) (SCREEN_SIZE.x / 2), (int) (OmegaFight3.SCREEN_SIZE.y - GAME_SET_SIZE.y) / 2, null);
                setOpacity(1, g2);
            }
            else {
                drawFade(1 - (double) (transitionCounter) / (GAME_END_LEN - GAME_END_TEXT_TRANSITION_LEN * 2 - GAME_END_TEXT_LEN), g2);
            }
        }
    }

    public static void drawFade(double amt, Graphics g) {
        g.setColor(new Color(0, 0, 0, (int) (MAX_RGB_VAL * amt)));
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

    public void checkWin() {
        if (gameMode == TWOPVE) {
            for (Omegaman omega: omegaman) {
                if (omega.livesLeft <= 0 && omega.frameCounter == SURGE_FRAME_HZ * SURGE_SPRITE_WIN_CHECK) {
                    transitionCounter = GAME_END_LEN;
                    transitiono = GAME_OVER;
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
            if (gameState == CHOOSE_FIGHT_GS) {
                if (buttonPressed == CHOOSE_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    // transition counter to fade
                    transitionCounter = FADE_IN_LEN;
                    transitiono = FADE_IN;
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
    }
    public void keyReleased(KeyEvent e) {
        pressedKey.remove(e.getKeyCode());
    }
}