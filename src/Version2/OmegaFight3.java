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
    public static Stage[] stage = new Stage[2];

    // Buttons
    public static Button[] chooseButtons = new Button[1]; // [14] Skip 1 for 3rd stage for now

    // Menu images
    public static BufferedImage chooseMenu;
    public static BufferedImage buttonImg;

    // General game statistics
    public static int screenShakeCounter = 0;

    // Screen Settings
    public static final Coord SCREEN_SIZE = new Coord(1920, 960);
    public static final int FPS = 60;
    public static final int SCREEN_SHAKE_HZ = 2;

    // Stage constants
    public static final int NO_OF_STAGES = 2;
    public static final int FINAL_DEST_NO = 0;
    public static final int BATTLEFIELD_NO = 1;
    public static final String[] STAGE_NAME = {"battlefield", "final destination"};
    public static final Platform[][] PLATFORMS = {{new Platform(395, 1525, 610, true), new Platform(535, 820, 435, false), new Platform(1095, 1385, 435, false)},
    {new Platform(245, 1675, 550, true)}};
    public static final Coord[][] SPAWN_COORDS = {{new Coord(700, 435), new Coord(1260, 435)},
    {new Coord(700, 550), new Coord(1260, 550)}};
    public static final int[][] SPAWN_SIGN = {{Omegaman.RIGHT_SIGN, Omegaman.RIGHT_SIGN}, {Omegaman.RIGHT_SIGN, Omegaman.RIGHT_SIGN}};
    public static final int[][] SPAWN_PLATFORM_NO = {{1, 2}, {0, 0}};

    // Offset + Leeway
    public static final double HITBOX_LEEWAY = 5;

    // Color Settings
    public static final int MAX_RGB_VAL = 255;

    // Gamestates
    public static final int STUDIO_ANIM_GS = -1;
    public static final int HOME_GS = 0;
    public static final int CHOOSE_FIGHT_GS = 1;
    public static final int GAME_GS = 2;
    public static final int WIN_GS = 3;
    public static final int SLIDESHOW_GS = 4;

    // Button Numbers
    // Choose your fight menu
    public static final int CHOOSE_BACK_BTNO = 0;
    public static final int BATTLEFIELD_BTNO = 1;
    public static final int FINAL_DEST_BTNO = 2;
    // Leave num for another stage?
    public static final int READY_BTNO = 10;

    public static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 40); 
    public static final Coord BUTTON_SIZE = new Coord(400, 50);

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
        for (int i = 0; i != NO_OF_STAGES; i++) {
            stage[i] = new Stage(STAGE_NAME[i], PLATFORMS[i], SPAWN_COORDS[i], SPAWN_SIGN[i], SPAWN_PLATFORM_NO[i]);
        }
        
        // Player
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            omegaman[i] = new Omegaman(i, stage[stageNo].spawnCoords[i].copy(), stage[stageNo].spawnSpriteSign[i], stage[stageNo].spawnPlatformNo[i], controls[i], shtKeys[i], loadouts[i], loadoutButtono[i]);
        }

        // Menu image importing
        chooseMenu = ImageIO.read(new File("menus/choose.jpg"));
        buttonImg = ImageIO.read(new File("menus/button.jpg"));

        // Buttons
        chooseButtons[CHOOSE_BACK_BTNO] = new Button(buttonImg, BUTTON_FONT, new Coord(225, 37.5), BUTTON_SIZE, "BACK", CHOOSE_BACK_BTNO, true, true);
        
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
        if (gameState == -1) {

        }
        else if(gameState == 0) {
            
        }
        else if(gameState == 1) {
            g.drawImage(chooseMenu, 0, 0, (int) SCREEN_SIZE.x, (int) SCREEN_SIZE.y, null);
            processButtons(chooseButtons);
            drawButtons(chooseButtons, g);
        }
        else if(gameState == 2) {
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
        else if(gameState == 3) {
         
        }
        else if (gameState == 4) {

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
    public static void processButtons(Button[] buttons) {
        for (Button button: buttons) {
            if (button.process()) {
                // change button pressed
                break;
            }
        }
    }

    public static void drawButtons(Button[] buttons, Graphics g) {
        for (Button button: buttons) {
            button.draw(g);
        }
    }

    public static void actionPerformed() {
        // Check button pressed with button num and do stuff CHECK ALL STATES here????
    }

    // Mouse and Keyboard Methods
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        clicked = true;
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