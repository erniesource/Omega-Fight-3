package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class Stage {
    // Instance variables
    public BufferedImage image;
    public Platform[] platforms; // Make an arraylist? not yet
    public Coord[] spawnCoords;
    public int[] spawnSpriteSign;
    public int[] spawnPlatformNo;
    public int buttono;
    public String stageName;
    public Clip music;

    // Constructor (Also imports music and background)
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

    // Description: This method draws the stage background
    public void drawStage(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}

class Platform {
    // Instance variables
    public boolean isMain; // Players can't drop through main platforms
    public int leftX;
    public int rightX;
    public int y;

    // Constructor
    public Platform(int leftX, int rightX, int y, boolean isMain) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.y = y;
        this.isMain = isMain;
    }

    // Description: THis method returns whther or not an object has landed on this platform given it's x coordinate, initial y coordinate and final y coordinate
    public boolean landed(double x, double sizeY, double initialY, double finalY) {
        return (leftX <= x && x <= rightX && initialY <= y - sizeY / 2 && finalY >= y - sizeY / 2);
    }
}

class Coord { // Note: Coord is actually more of a Vector, but Vector has already been taken as a class name soooo
    // Constants
    public static final Coord PT = new Coord();
    
    // Instance variables
    public double x;
    public double y;

    // Constructor that initializes origin
    public Coord() {}

    public Coord(double xy) {
        x = xy;
        y = xy;
    }

    // More specifid constructor
    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Description: This method returns a copy of this coord
    public Coord copy() {
        return new Coord(x, y);
    }

    // Description: This method returns a new Coord with it's x and y scaled by a specified amount
    public Coord scaledBy(double multiplier) {
        return new Coord(x * multiplier, y * multiplier);
    }

    public Coord scaledBy(Coord coord) {
        return new Coord(x * coord.x, y * coord.y);
    }

    // Description: This method is for printing out coords when debugging
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}