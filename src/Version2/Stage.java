package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Stage {
    public BufferedImage image;
    public Platform[] platforms;
    public Coord[] spawnCoords;
    public int[] spawnSpriteSign;
    public int[] spawnPlatformNo;
    
    public static Coord coord = new Coord();

    public Stage(String stageName, Platform[] platforms,Coord[] spawnCoords, int[] spawnSpriteSign, int[] spawnPlatformNo) throws IOException {
        image = ImageIO.read(new File("stages/" + stageName + ".jpg"));
        this.platforms = platforms;
        this.spawnCoords = spawnCoords;
        this.spawnSpriteSign = spawnSpriteSign;
        this.spawnPlatformNo = spawnPlatformNo;
    }

    public void drawStage(Graphics g) {
        g.drawImage(image, (int) coord.x, (int) coord.y, null);
    }
}

class Platform {
    public boolean isMain;
    public int leftX;
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

    public Coord() {}

    public Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coord copy() {
        return new Coord(x, y);
    }
}