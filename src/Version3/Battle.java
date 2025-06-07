package Version3;

import java.awt.*;

public class Battle {
    public String name;
    public int gameMode;
    public int winner; // -2: Both players, -1: Boss, 0+: Players
    public double[][] playerStats = new double[Omegaman.NUM_PLAYERS][Omegaman.NO_OF_STATS];
    // Settings??? (difficulty, lives)

    public static final int BOSS_WIN = -1;
    public static final int BOTH_WIN = -2;
    public static final int NO_FORMATIVE_STATS = 3;
    public static final boolean[] isTruncated = {false, true, false, true, false, false, false};
    public static final boolean[] isPercent = {false, false, true, false, false, true, true};

    public Battle(int gameMode, int winner, double[][] playerStats) {
        this.gameMode = gameMode;
        this.winner = winner;
        this.playerStats = playerStats;
    }

    public void draw(Font font, Coord coord, int spacing, int playerNo, double progress, Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(font);
        for (int i = 0; i != Omegaman.NO_OF_STATS; i++) {
            String stringStat;
            if (isTruncated[i]) {
                stringStat = (int) (playerStats[playerNo][i] * progress) + "";
                g.drawString(stringStat, (int) coord.x, (int) (coord.y + (i + (i < NO_FORMATIVE_STATS? 0: 1)) * spacing));
            }
            else {
                stringStat = (playerStats[playerNo][i] * progress) + "";
                g.drawString(stringStat.substring(0, stringStat.indexOf(".") + 2), (int) coord.x, (int) (coord.y + (i + (i < NO_FORMATIVE_STATS? 0: 1)) * spacing));
            }
        }
    }
}
