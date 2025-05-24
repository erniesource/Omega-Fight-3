package Version2;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;
// Ernest Todo: sniper, laser
// Ernest Long term Todo: boomerang, bouncer, spike, splitter, ultimate, dash

public class OmegaFight3 extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Runnable {
    // Game States
    // The following are just examples of a game state legend
    // -1 <- Studio Animation
    // 0 <- Home Screen
    // 1 <- Credit/Tutorial Screen
    // 2 <- In-Game Screen
    // 3 <- Player win Screen
    public static int gameState = 2;

    // Players
    public static Omegaman[] omegaman = new Omegaman[Omegaman.NUM_PLAYERS];

    // Mouse/Keyboard Events
    public static int mouseX;
    public static int mouseY;
    public static boolean clicked;

    // Stage statistics
    public static int stageNo = 0;
    public static Stage[] stage = new Stage[2];

    // General game statistics
    public static int screenShakeCounter = 0;

    // Keyboard statistics
    public static HashSet<Integer> pressedKey = new HashSet<>();

    // Screen Settings
    public static final Coord SCREEN_SIZE = new Coord(1920, 960);
    public static final int FPS = 60;

    // Visual Effects
    public static final int SCREEN_SHAKE_HZ = 2;

    // Offset + Leeway
    public static final double HITBOX_LEEWAY = 5;

    // Color Settings
    public static final int MAX_RGB_VAL = 255;

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
        stage[0] = new Stage("battlefield", new Platform[] {new Platform(395, 1525, 610, true), new Platform(535, 820, 435, false), new Platform(1095, 1385, 435, false)},
        new Coord[] {new Coord(700, 435), new Coord(1260, 435)}, new int[] {Omegaman.RIGHT_SIGN, Omegaman.RIGHT_SIGN}, new int[] {1, 2});
        stage[1] = new Stage("final destination", new Platform[] {new Platform(245, 1675, 550, true)},
        new Coord[] {new Coord(700, 550), new Coord(1260, 550)}, new int[] {Omegaman.RIGHT_SIGN, Omegaman.RIGHT_SIGN}, new int[] {0, 0});

        // Player
        omegaman[0] = new Omegaman(0, stage[stageNo].spawnCoords[0].copy(), stage[stageNo].spawnSpriteSign[0], stage[stageNo].spawnPlatformNo[0], new int[] {KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S}, new int[] {KeyEvent.VK_C, KeyEvent.VK_V}, new int[] {0, 1});
        omegaman[1] = new Omegaman(1, stage[stageNo].spawnCoords[1].copy(), stage[stageNo].spawnSpriteSign[1], stage[stageNo].spawnPlatformNo[1], new int[] {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN}, new int[] {KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2}, new int[] {2, 3});

        // Weapon image importing
        Bullet.image = ImageIO.read(new File("player projectiles/bullet.png"));
        Shotgun.image = ImageIO.read(new File("player projectiles/shotgun.png"));
        Spammer.image = ImageIO.read(new File("player projectiles/spammer.png"));
        Sniper.image = ImageIO.read(new File("player projectiles/sniper.png"));
        Laser.ball = ImageIO.read(new File("player projectiles/ball.png"));
        Laser.beam = ImageIO.read(new File("player projectiles/beam.png"));
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            Rocket.images[i] = ImageIO.read(new File("player projectiles/" + i + "rocket.png"));
            Firework.images[i] = ImageIO.read(new File("player projectiles/" + i + "firework.png"));
            Firework.chargingImages[i] = ImageIO.read(new File("player projectiles/" + i + "fireworkCharge.png"));
            Missile.images[i] = ImageIO.read(new File("player projectiles/" + i + "missile.png"));
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
        else if(gameState == 0){
            
        }
        else if(gameState == 1){
         
        }
        else if(gameState == 2){
            stage[stageNo].drawStage(g);
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
        else if(gameState == 3){
         
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
        mouseX = e.getX();
        mouseY = e.getY();
    }
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        pressedKey.add(e.getKeyCode());
    }
    public void keyReleased(KeyEvent e) {
        pressedKey.remove(e.getKeyCode());
    }
}