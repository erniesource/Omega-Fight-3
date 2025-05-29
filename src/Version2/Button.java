package Version2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button {
    public BufferedImage image;
    public Font[] font = new Font[3];
    public Coord coord;
    public Coord[] size = new Coord[3];
    public String text;
    public int state = 1; // 0: Pressed, 1: Not Pressed, 2: Hovered
    public int num;
    public int style;
    public boolean canSee = true;
    public boolean canUse = true;

    public static final double DIFF_IN_SIZE_OF_STATES = 0.05;
    public static final int PRESSED = 0;
    public static final int NOPRESSED = 1;
    public static final int HOVERED = 2;
    public static final int SHADOW = 0;
    public static final int HIGHLIGHT = 1;
    public static final int HIGHLIGHT_X_BUFFER = 10;
    public static final double LEEWAY = 0;
    public static final double SHADOW_OFFSET = 0.05;

    public Button(BufferedImage image, Font font, Coord coord, Coord size, String text, int num, int style) {
        this(image, coord, size, num);
        this.font[PRESSED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 - DIFF_IN_SIZE_OF_STATES)));
        this.font[NOPRESSED] = font;
        this.font[HOVERED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 + DIFF_IN_SIZE_OF_STATES)));
        this.text = text;
        this.style = style;
    }
    
    public Button(BufferedImage image, Font font, Coord coord, Coord size, String text, int num, int style, boolean canSee, boolean canUse) {
        this(image, coord, size, num, canSee, canUse);
        this.font[PRESSED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 - DIFF_IN_SIZE_OF_STATES)));
        this.font[NOPRESSED] = font;
        this.font[HOVERED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 + DIFF_IN_SIZE_OF_STATES)));
        this.text = text;
        this.style = style;
    }

    public Button(BufferedImage image, Coord coord, Coord size, int num) {
        this.image = image;
        this.coord = coord;
        this.size[PRESSED] = size.scaledBy(1 - DIFF_IN_SIZE_OF_STATES);
        this.size[NOPRESSED] = size;
        this.size[HOVERED] = size.scaledBy(1 + DIFF_IN_SIZE_OF_STATES);
        this.num = num;
    }

    public Button(BufferedImage image, Coord coord, Coord size, int num, boolean canSee, boolean canUse) {
        this(image, coord, size, num);
        this.canSee = canSee;
        this.canUse = canUse;
    }

    // Draw method
    public void draw(Graphics g) {
        if (canSee) {
            g.drawImage(image, (int) (coord.x - size[state].x / 2), (int) (coord.y - size[state].y / 2), (int) size[state].x, (int) size[state].y, null);
            if (text != null) {
                g.setFont(font[state]);
                g.setColor(Color.WHITE);
                if (style == SHADOW) {
                    g.drawString(text, (int) (coord.x - g.getFontMetrics().stringWidth(text) / 2 + SHADOW_OFFSET * Math.pow(font[state].getSize(), 0.9)),
                    (int) (coord.y + Math.pow(font[state].getSize(), 0.9) / 2 + SHADOW_OFFSET * Math.pow(font[state].getSize(), 0.9)));
                }
                else if (style == HIGHLIGHT) {
                    g.fillRect((int) (coord.x - (g.getFontMetrics().stringWidth(text) + HIGHLIGHT_X_BUFFER) / 2), (int) (coord.y - font[state].getSize() / 2), g.getFontMetrics().stringWidth(text) + HIGHLIGHT_X_BUFFER, font[state].getSize());
                }
                g.setColor(Color.BLACK);
                g.drawString(text, (int) (coord.x - g.getFontMetrics().stringWidth(text) / 2), (int) (coord.y + Math.pow(font[state].getSize(), 0.9) / 2));
            }
        }
    }

    // Process method
    public boolean process() {
        if (canUse) {
            if (OmegaFight3.intersects(OmegaFight3.mouse, Coord.PT, coord, size[NOPRESSED], LEEWAY)) {
                if (OmegaFight3.clicked) {
                    if (state != PRESSED) state = PRESSED;
                    return true;
                }
                else if (state != HOVERED) state = HOVERED;
            }
            else if (state != NOPRESSED) state = NOPRESSED;
        }
        return false;
    }
}
