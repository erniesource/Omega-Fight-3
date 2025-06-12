package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;

public class Button {
    public static final double DIFF_IN_SIZE_OF_STATES = 0.05;
    public static final int PRESSED = 0;
    public static final int NOPRESSED = 1;
    public static final int HOVERED = 2;
    public static final int NUM_STATES = 3;
    public static final int SHADOW = 0;
    public static final int HIGHLIGHT = 1;
    public static final int HIGHLIGHT_X_BUFFER = 10;
    public static final double LEEWAY = 0;
    public static final double SHADOW_OFFSET = 0.05;
    public static final int NO_BUTTON_NUM = -1;

    public static Clip hover;
    public static Clip click;

    public BufferedImage image;
    public Font[] font = new Font[NUM_STATES];
    public Coord coord;
    public Coord[] size = new Coord[NUM_STATES];
    public String text;
    public int state = NOPRESSED; // 0: Pressed, 1: Not Pressed, 2: Hovered
    public int num;
    public int style;
    public boolean canSee = true;
    public boolean canUse = true;

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
            g.drawImage(image, (int) (coord.x - (int) size[state].x / 2), (int) (coord.y - (int) size[state].y / 2), (int) size[state].x, (int) size[state].y, null);
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
    public boolean process(Coord mouse, boolean clicked) {
        if (canUse) {
            if (OmegaFight3.intersects(mouse, Coord.PT, coord, size[NOPRESSED], LEEWAY)) {
                if (clicked) {
                    if (state != PRESSED) {
                        state = PRESSED;
                        click.stop();
                        click.setFramePosition(0);
                        click.start();
                    }
                    return true;
                }
                else if (state != HOVERED) {
                    if (state != PRESSED) {
                        hover.stop();
                        hover.setFramePosition(0);
                        hover.start();
                    }
                    state = HOVERED;
                }
            }
            else if (state != NOPRESSED) state = NOPRESSED;
        }
        return false;
    }
}

class TextBox extends Button {
    public static final int CURSOR_HZ = 30;
    public static final int CURSOR_SPACING = 0;
    public static final int ALLOWED_DIST_FROM_EDGE = 10;

    public boolean typing;
    public boolean clickedOutside;
    public HashSet<Integer> prevPressedKeys;
    public int cursorCounter;
    public int allowedDistFromEdge;

    public TextBox(BufferedImage image, Font font, Coord coord, Coord size, int style) {
        super(image, font, coord, size, "", NO_BUTTON_NUM, style);
        allowedDistFromEdge = ALLOWED_DIST_FROM_EDGE;
        
    }

    public TextBox(BufferedImage image, Font font, Coord coord, Coord size, int style, int allowedDistFromEdge, boolean canSee, boolean canUse) {
        super(image, font, coord, size, "", NO_BUTTON_NUM, style, canSee, canUse);
        this.allowedDistFromEdge = allowedDistFromEdge;
    }

    // Draw method
    public void draw(Graphics g) {
        if (canSee) {
            g.drawImage(image, (int) (coord.x - size[state].x / 2), (int) (coord.y - size[state].y / 2), (int) size[state].x, (int) size[state].y, null);
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
            if (typing && cursorCounter % (CURSOR_HZ * 2) < CURSOR_HZ) {;
                g.drawString("|", (int) (coord.x + g.getFontMetrics().stringWidth(text) / 2) + CURSOR_SPACING, (int) (coord.y + Math.pow(font[state].getSize(), 0.9) / 2));
            }
        }
    }

    // Process method
    public void process(Coord mouse, boolean clicked, Graphics g) {
        if (canUse) {
            if (OmegaFight3.intersects(mouse, Coord.PT, coord, size[NOPRESSED], LEEWAY)) {
                if (clicked) {
                    if (state != PRESSED) {
                        state = PRESSED;
                        click.stop();
                        click.setFramePosition(0);
                        click.start();
                    }
                    if (clickedOutside) clickedOutside = false;
                }
                else if (state != HOVERED) {
                    if (state == PRESSED) {
                        typing = true;
                        cursorCounter = 0;
                    }
                    state = HOVERED;
                    hover.stop();
                    hover.setFramePosition(0);
                    hover.start();
                }
            }
            else {
                if (clicked) {
                    clickedOutside = true;
                }
                else {
                    if (clickedOutside) {
                        clickedOutside = false;
                        typing = false;
                    }
                }
                if (state != NOPRESSED) state = NOPRESSED;
            }

            g.setFont(font[NOPRESSED]);
            while (g.getFontMetrics().stringWidth(text) >= size[NOPRESSED].x - allowedDistFromEdge * 2) text = text.substring(0, text.length() - 1);

            if (typing) cursorCounter = (cursorCounter + 1) % (CURSOR_HZ * 2);
        }
    }

    public void addChar(char keyPressed) {
        if (keyPressed >= 32 && keyPressed < 127) {
            text += Character.toUpperCase(keyPressed);
            cursorCounter = 0;
        }
    }

    public void backspace() {
        if (text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
    }
}