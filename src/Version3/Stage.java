package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class Stage {
    public static final int NO_OF_STAGES = 2;
    public static final int BATTLEFIELD_NO = 0;
    public static final int FINAL_DEST_NO = 1;
    
    public BufferedImage image;
    public Platform[] platforms; // Make an arraylist?
    public Coord[] spawnCoords;
    public int[] spawnSpriteSign;
    public int[] spawnPlatformNo;
    public int buttono;
    public String stageName;
    public Clip music;
    
    public static Coord coord = new Coord();

    public Stage(String stageName, Platform[] platforms, Coord[] spawnCoords, int[] spawnSpriteSign, int[] spawnPlatformNo, int buttono) throws IOException {
        image = ImageIO.read(new File("stages/" + stageName + ".jpg"));
        this.stageName = stageName;
        this.platforms = platforms;
        this.spawnCoords = spawnCoords;
        this.spawnSpriteSign = spawnSpriteSign;
        this.spawnPlatformNo = spawnPlatformNo;
        this.buttono = buttono;

        try {
            music = AudioSystem.getClip();
            music.open(AudioSystem.getAudioInputStream(new File("music/" + stageName + " music.wav").toURI().toURL()));
            music.setFramePosition(0);
        }
        catch (Exception e) {}
    }

    public void drawStage(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}

class Platform {
    public boolean isMain;
    public int leftX; // Replace left with lft and right with rit
    public int rightX;
    public int y;

    public Platform(int leftX, int rightX, int y, boolean isMain) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.y = y;
        this.isMain = isMain;
    }

    public boolean landed(double x, double initialY, double finalY) {
        return (leftX <= x && x <= rightX && initialY <= y && finalY >= y);
    }

    public boolean onPlatform(double x) {
        return leftX <= x && x <= rightX;
    }
}

class Coord {
    public double x;
    public double y;

    public static final Coord PT = new Coord();

    public Coord() {}

    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coord copy() {
        return new Coord(x, y);
    }

    public Coord scaledBy(double multiplier) {
        return new Coord(x * multiplier, y * multiplier);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}