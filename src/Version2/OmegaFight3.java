package Version2;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
// Ernest Todo: 
// Ernest Long term Todo: menus, ultimate, dash

public class OmegaFight3 extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Runnable {
    // Game States
    // The following are just examples of a game state legend
    // -1 <- Studio Animation
    // 0 <- Home Screen
    // 1 <- Choose your fight screen
    // 2 <- In-Game Screen
    // 3 <- Player win Screen
    // 4 <- Credit/Tutorial Screen
    public static int gameState = 1;

    // Players
    public static Omegaman[] omegaman = new Omegaman[Omegaman.NUM_PLAYERS];
    public static int[][] loadouts = {{-1, -1}, {-1, -1}};
    public static int[][] controls = {{KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S}, {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN}};
    public static int[][] shtKeys = {{KeyEvent.VK_C, KeyEvent.VK_V}, {KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2}};
    public static int[][] loadoutButtono = {{11, 12}, {13, 14}};

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

    // Menu images
    public static BufferedImage chooseMenu;
    public static BufferedImage buttonImg;

    // General game statistics
    public static int screenShakeCounter = 0;

    // Misc
    public static BufferedImage placeHolder;

    // Screen Settings
    public static final Coord SCREEN_SIZE = new Coord(1920, 960);
    public static final int FPS = 60;
    public static final int SCREEN_SHAKE_HZ = 2;
    public static final int SPACING = 25;

    // Stage constants
    public static final String[] STAGE_NAME = {"battlefield", "final destination"};
    public static final Platform[][] PLATFORMS = {{new Platform(395, 1525, 610, true), new Platform(535, 820, 435, false), new Platform(1095, 1385, 435, false)},
    {new Platform(245, 1675, 550, true)}};
    public static final Coord[][] SPAWN_COORDS = {{new Coord(700, 435), new Coord(1260, 435)},
    {new Coord(700, 550), new Coord(1260, 550)}};
    public static final int[][] SPAWN_SIGN = {{Omegaman.RIGHT_SIGN, Omegaman.RIGHT_SIGN}, {Omegaman.RIGHT_SIGN, Omegaman.RIGHT_SIGN}};
    public static final int[][] SPAWN_PLATFORM_NO = {{1, 2}, {0, 0}};
    public static final int[] STAGE_BUTTONO = {1, 2};
    public static final int STAGE_FLASH_HZ = 10;
    public static final int FLASH_SIZE = 10;

    // Misc
    public static final double HITBOX_LEEWAY = 5;
    public static final int MAX_RGB_VAL = 255;

    // Gamestates
    public static final int STUDIO_ANIM_GS = -1;
    public static final int HOME_GS = 0;
    public static final int CHOOSE_FIGHT_GS = 1;
    public static final int GAME_GS = 2;
    public static final int WIN_GS = 3;
    public static final int SLIDESHOW_GS = 4;

    // Button Numbers (Next avail: 15)
    // Choose your fight menu
    public static final int CHOOSE_BACK_BUTTONO = 0;
    public static final int READY_BUTTONO = 10;

    // Button constants
    public static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 40); 
    public static final Font STAGE_FONT = new Font("Consolas", Font.BOLD, 25);
    public static final Coord BUTTON_SIZE = new Coord(400, 50);

    // Mouse and Keyboard constants
    public static final int NO_BUTTON_HIT = -1;

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
        // Stages
        for (int i = 0; i != Stage.NO_OF_STAGES; i++) {
            stage[i] = new Stage(STAGE_NAME[i], PLATFORMS[i], SPAWN_COORDS[i], SPAWN_SIGN[i], SPAWN_PLATFORM_NO[i], STAGE_BUTTONO[i]);
        }
        
        // Player
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            omegaman[i] = new Omegaman(i, stage[stageNo].spawnCoords[i].copy(), stage[stageNo].spawnSpriteSign[i], stage[stageNo].spawnPlatformNo[i], controls[i], shtKeys[i], loadouts[i], loadoutButtono[i]);
        }

        // Misc image imoprting
        placeHolder = ImageIO.read(new File("misc/placeholder.jpg"));

        // Menu image importing
        chooseMenu = ImageIO.read(new File("menus/choose.jpg"));
        buttonImg = ImageIO.read(new File("menus/button.jpg"));

        // Buttons
        chooseButtons.put(CHOOSE_BACK_BUTTONO, new Button(buttonImg, BUTTON_FONT, new Coord(SPACING + 400 / 2, SPACING + SPACING / 2), BUTTON_SIZE, "BACK", CHOOSE_BACK_BUTTONO, Button.SHADOW, true, true));
        chooseButtons.put(stage[Stage.BATTLEFIELD_NO].buttono, new Button(stage[Stage.BATTLEFIELD_NO].image, STAGE_FONT, new Coord(SPACING + 510 / 2, (100 + 500) / 2), new Coord(510, 255), stage[Stage.BATTLEFIELD_NO].stageName.toUpperCase(), stage[Stage.BATTLEFIELD_NO].buttono, Button.HIGHLIGHT, true, true)); // CHange size email Ms. Kim
        chooseButtons.put(stage[Stage.FINAL_DEST_NO].buttono, new Button(stage[Stage.FINAL_DEST_NO].image, STAGE_FONT, new Coord(SPACING * 2 + 510 * (1.0 / 2 + 1), (100 + 500) / 2), new Coord(510, 255), stage[Stage.FINAL_DEST_NO].stageName.toUpperCase(), stage[Stage.FINAL_DEST_NO].buttono, Button.HIGHLIGHT, true, true));
        chooseButtons.put(3, new Button(placeHolder, STAGE_FONT, new Coord(SPACING * 3 + 510 * (1.0 / 2 + 2), (100 + 500) / 2), new Coord(510, 255), "COMING IN 5-10 BUSINESS DAYS", 3, Button.HIGHLIGHT, true, false));
        
        // Weapon image importing
        Bullet.image = ImageIO.read(new File("player projectiles/bullet.png"));
        Shotgun.image = ImageIO.read(new File("player projectiles/shotgun.png"));
        Spammer.image = ImageIO.read(new File("player projectiles/spammer.png"));
        Sniper.image = ImageIO.read(new File("player projectiles/sniper.png"));
        Laser.ball = ImageIO.read(new File("player projectiles/ball.png"));
        Laser.beam = ImageIO.read(new File("player projectiles/beam.png"));
        Spike.image = ImageIO.read(new File("player projectiles/spike.png"));
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
        for (int i = 0; i != Rocket.NUM_EXPLOSION_IMAGES; i++) {
            Projectile.explosionImages[i] = ImageIO.read(new File("explosions/explosion" + i + ".png"));
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
            actionPerformed();
            processButtons(chooseButtons.values());

            // Flashing of selected stage
            stageFlashCounter = (stageFlashCounter + 1) % (STAGE_FLASH_HZ * 2);
            if (stageFlashCounter < STAGE_FLASH_HZ) g.setColor(PURPLE);
            else g.setColor(Color.YELLOW);
            Button stageButton = chooseButtons.get(stage[stageNo].buttono);
            Coord stageButtonSize = stageButton.size[stageButton.state];
            g.fillRect((int) (stageButton.coord.x - stageButtonSize.x / 2 - FLASH_SIZE), (int) (stageButton.coord.y - stageButtonSize.y / 2 - FLASH_SIZE),
            (int) (stageButtonSize.x + FLASH_SIZE * 2), (int) (stageButtonSize.y + FLASH_SIZE * 2));
            drawButtons(chooseButtons.values(), g);
            // Each wepon has their own icon
        }
        else if(gameState == GAME_GS) {
            stage[stageNo].drawStage(g);
            for (Omegaman omega: omegaman) {
                omega.addNewProjectiles();
            }
            for (Omegaman omega: omegaman) {
                omega.drawHUD(g);
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
                    omega.countInv();
                    omega.draw(g);
                    omega.drawCharge(g);
                    omega.regenSkillPts();
                    omega.shakePercent();
                    omega.drawPercent(g);
                }
                omega.processProjectiles(g2);
            }
            for (Omegaman omega: omegaman) {
                omega.deleteDeadProjectiles();
            }
            for (Omegaman omega: omegaman) {
                if (omega.state != Omegaman.ALIVE_STATE) {
                    omega.frameCounter++;
                    if (omega.frameCounter < Omegaman.SURGE_TIME) {
                        omega.diePercent(g);
                        omega.drawSurge(g2);
                    }
                    else if (omega.frameCounter == Omegaman.SURGE_TIME) omega.prepareForRespawn();
                    else if (omega.frameCounter >= Omegaman.SURGE_TIME + Omegaman.RESPAWN_PAUSE) {
                        omega.respawn(pressedKey.contains(omega.dwnKey));
                        omega.respawnPercent(g);
                        omega.countInv();
                        omega.draw(g);
                    }
                }
            }
        }
        else if(gameState == WIN_GS) {
         
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

    public static void actionPerformed() {
        // Check button pressed with button num and do stuff CHECK ALL STATES here????
        if (buttonPressed != NO_BUTTON_HIT && !clicked) {
            if (gameState == CHOOSE_FIGHT_GS) {
                if (buttonPressed == CHOOSE_BACK_BUTTONO) {
                    gameState = HOME_GS;
                    // transition counter to fade
                }
                else if (buttonPressed == stage[Stage.BATTLEFIELD_NO].buttono) {
                    stageNo = Stage.BATTLEFIELD_NO;
                }
                else if (buttonPressed == stage[Stage.FINAL_DEST_NO].buttono) {
                    stageNo = Stage.FINAL_DEST_NO;
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