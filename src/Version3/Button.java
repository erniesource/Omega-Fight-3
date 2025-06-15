package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;

public class Button {
    // Constants
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

    // Sounds
    public static Clip hover;
    public static Clip click;

    // Instance variables
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

    // Constructors order from most customized to most general
    public Button(BufferedImage image, Font font, Coord coord, Coord size, String text, int num, int style, boolean canSee, boolean canUse) {
        this(image, coord, size, num, canSee, canUse);
        this.font[PRESSED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 - DIFF_IN_SIZE_OF_STATES)));
        this.font[NOPRESSED] = font;
        this.font[HOVERED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 + DIFF_IN_SIZE_OF_STATES)));
        this.text = text;
        this.style = style;
    }

    public Button(BufferedImage image, Font font, Coord coord, Coord size, String text, int num, int style) {
        this(image, coord, size, num);
        this.font[PRESSED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 - DIFF_IN_SIZE_OF_STATES)));
        this.font[NOPRESSED] = font;
        this.font[HOVERED] = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * (1 + DIFF_IN_SIZE_OF_STATES)));
        this.text = text;
        this.style = style;
    }

    public Button(BufferedImage image, Coord coord, Coord size, int num, boolean canSee, boolean canUse) {
        this(image, coord, size, num);
        this.canSee = canSee;
        this.canUse = canUse;
    }

    public Button(BufferedImage image, Coord coord, Coord size, int num) {
        this.image = image;
        this.coord = coord;
        this.size[PRESSED] = size.scaledBy(1 - DIFF_IN_SIZE_OF_STATES);
        this.size[NOPRESSED] = size;
        this.size[HOVERED] = size.scaledBy(1 + DIFF_IN_SIZE_OF_STATES);
        this.num = num;
    }

    // Description: This method draws the button on the screen based on its current state and instance variables
    public void draw(Graphics g) {
        if (canSee) {
            // Draw image
            g.drawImage(image, (int) (coord.x - (int) size[state].x / 2), (int) (coord.y - (int) size[state].y / 2), (int) size[state].x, (int) size[state].y, null);

            // Draw text
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

    // Description: This method processes mouse input to determine it's state and also plays sound
    public boolean process(Coord mouse, boolean clicked) {
        if (canUse) {
            if (OmegaFight3.intersects(mouse, Coord.PT, coord, size[NOPRESSED], LEEWAY)) {
                // Clicked
                if (clicked) {
                    if (state != PRESSED) {
                        state = PRESSED;
                        click.stop();
                        click.setFramePosition(0);
                        click.start();
                    }
                    return true;
                }

                // Hovered
                else if (state != HOVERED) {
                    if (state != PRESSED) {
                        hover.stop();
                        hover.setFramePosition(0);
                        hover.start();
                    }
                    state = HOVERED;
                }
            }

            // Not hovered or clicked
            else if (state != NOPRESSED) state = NOPRESSED;
        }
        return false;
    }
}

class TextBox extends Button {
    // Constants
    public static final int CURSOR_HZ = 20;
    public static final int CURSOR_SPACING = 0;
    public static final int ALLOWED_DIST_FROM_EDGE = 10;

    // Instance variables
    public boolean typing;
    public boolean clickedOutside;
    public HashSet<Integer> prevPressedKeys;
    public int cursorCounter;
    public int allowedDistFromEdge;

    // Constructors order from most customized to most general
    public TextBox(BufferedImage image, Font font, Coord coord, Coord size, int style, int allowedDistFromEdge, boolean canSee, boolean canUse) {
        super(image, font, coord, size, "", NO_BUTTON_NUM, style, canSee, canUse);
        this.allowedDistFromEdge = allowedDistFromEdge;
    }

    public TextBox(BufferedImage image, Font font, Coord coord, Coord size, int style) {
        super(image, font, coord, size, "", NO_BUTTON_NUM, style);
        allowedDistFromEdge = ALLOWED_DIST_FROM_EDGE;
        
    }

    // Description: This method draws the text box on the screen based on its current state and instance variables
    public void draw(Graphics g) {
        if (canSee) {
            // Draw image
            g.drawImage(image, (int) (coord.x - size[state].x / 2), (int) (coord.y - size[state].y / 2), (int) size[state].x, (int) size[state].y, null);
            
            // Draw text
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

            // Draw cursor
            if (typing && cursorCounter % (CURSOR_HZ * 2) < CURSOR_HZ) {;
                g.drawString("|", (int) (coord.x + g.getFontMetrics().stringWidth(text) / 2) + CURSOR_SPACING, (int) (coord.y + Math.pow(font[state].getSize(), 0.9) / 2));
            }
        }
    }

    // Description: This method processes mouse input to determine the text box's state, plays sound, and handles the cursor blinking
    public void process(Coord mouse, boolean clicked, Graphics g) {
        if (canUse) {
            if (OmegaFight3.intersects(mouse, Coord.PT, coord, size[NOPRESSED], LEEWAY)) {
                // Clicked
                if (clicked) {
                    if (state != PRESSED) {
                        state = PRESSED;
                        click.stop();
                        click.setFramePosition(0);
                        click.start();
                    }
                    if (clickedOutside) clickedOutside = false;
                }

                // Hovered
                else if (state != HOVERED) {
                    if (state == PRESSED) {
                        typing = true;
                        cursorCounter = 0;
                    }
                    else {
                        hover.stop();
                        hover.setFramePosition(0);
                        hover.start();
                    }
                    state = HOVERED;
                }
            }
            else {
                // Clicked outside
                if (clicked) {
                    clickedOutside = true;
                }

                // Released click outside
                else {
                    if (clickedOutside) {
                        clickedOutside = false;
                        typing = false;
                    }
                }

                // Not hovered nor pressed
                if (state != NOPRESSED) state = NOPRESSED;
            }

            // Check if text fits inside textbox
            g.setFont(font[NOPRESSED]);
            while (g.getFontMetrics().stringWidth(text) >= size[NOPRESSED].x - allowedDistFromEdge * 2) text = text.substring(0, text.length() - 1);

            // Calculate cursor blinking
            if (typing) cursorCounter = (cursorCounter + 1) % (CURSOR_HZ * 2);
        }
    }

    // Description: This method adds a character to the text box if it is a valid character and resets the cursor counter
    public void addChar(char keyPressed) {
        if (keyPressed >= 32 && keyPressed < 127) {
            text += Character.toUpperCase(keyPressed);
            cursorCounter = 0;
        }
    }

    // Description: This method deletes the last character from the text box if there is any text
    public void backspace() {
        if (text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
    }
}