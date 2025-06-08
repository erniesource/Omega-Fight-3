package Version3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Battle {
    public static BufferedImage[] scoreBoard = new BufferedImage[1];
    public static final Font SCOREBOARD_FONT = new Font("Consolas", Font.BOLD, 30);
    public static final int SCOREBOARD_SPACING = 43;
    public static final Coord SCOREBOARD_SIZE = new Coord(600, 500);
    public static final Coord[] SCOREBOARD_COORD_TO_STATS_COORDS = {new Coord(314, 133), new Coord(457, 133)};
    public static final int BOSS_WIN = -1;
    public static final int BOTH_WIN = -2;
    public static final int NO_FORMATIVE_STATS = 3;
    public static final boolean[] IS_TRUNCATED = {false, true, false, true, false, false, false};
    public static final boolean[] IS_PERCENT = {false, false, true, false, false, true, true};
    public static final double FULL_STATS = 1.0;
    public static final int STAT_NUM_DECIMAL_PLACES = 1;

    // 2P V E grade calculation constants
    public static final double[] WEIGHTING = {2, 1, -1, 2};
    public static final double TOTAL_POS_WEIGHT = 5;
    public static final double[] MAX_SCORE = {3, 9, 50, 200};
    public static final double STATS_WEIGHT = 0.5;
    public static final Color[] RARITY_COLORS = {Color.WHITE, Color.GREEN, new Color(0, 112, 237), OmegaFight3.PURPLE};
    public static final Color GOLD = new Color(255, 186, 3);
    public static final int NUM_RARITIES = 4;
    public static final Coord SCOREBOARD_COORD_TO_GRADE_COORD = new Coord(310, 478);
    public static final int HUNDRED_FLASH_HZ = 10;

    public static int hundFlashCounter = 0;

    public String name;
    public int gameMode;
    public int winner; // -2: Both players, -1: Boss, 0+: Players
    public double[][] playerStats = new double[Omegaman.NUM_PLAYERS][Omegaman.NO_OF_STATS];
    public String result;
    public double grade;
    // Settings??? (difficulty, lives)

    public Battle(int gameMode, int winner, double[][] playerStats) {
        this.gameMode = gameMode;
        this.winner = winner;
        this.playerStats = playerStats;
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            for (int j = 0; j != Omegaman.NO_OF_STATS; j++) {
                if (IS_PERCENT[j]) playerStats[i][j] /= Math.pow(10, Omegaman.PERCENT_NUM_DECIMALS);
            }
            playerStats[i][Omegaman.SKILL_PTS_USED_NO] /= Omegaman.ONES_PER_SKILL_PT;
        }

        if (gameMode == OmegaFight3.TWOPVE) {
            boolean playerDied = false;
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                for (int j = NO_FORMATIVE_STATS; j != Omegaman.NO_OF_STATS; j++) {
                    grade += WEIGHTING[j - NO_FORMATIVE_STATS] * Math.min(1, playerStats[i][j] / MAX_SCORE[j - NO_FORMATIVE_STATS]);
                }
                if (playerStats[i][Omegaman.LIVES_LEFT_NO] <= 0) {
                    playerDied = true;
                }
            }
            grade *= STATS_WEIGHT / TOTAL_POS_WEIGHT / Omegaman.NUM_PLAYERS;
            if (!playerDied) grade += 1 - STATS_WEIGHT;
            grade = Math.round(grade * 100 * Math.pow(10, STAT_NUM_DECIMAL_PLACES)) / Math.pow(10, STAT_NUM_DECIMAL_PLACES);
            result = "" + (grade * 100) + "%";
        }
    }

    public void drawScoreBoard(Coord coord, double progress, Graphics g) {
        g.drawImage(scoreBoard[gameMode], (int) coord.x, (int) coord.y, null);
        g.setColor(Color.WHITE);
        g.setFont(SCOREBOARD_FONT);

        // DO IF FOR GAMEMODE HERE
        if (gameMode == OmegaFight3.TWOPVE) {
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                for (int j = 0; j != Omegaman.NO_OF_STATS; j++) {
                    String stringStat;
                    if (IS_TRUNCATED[j]) {
                        stringStat = (int) (playerStats[i][j] * progress) + "";
                    }
                    else {
                        stringStat = Math.round(playerStats[i][j] * progress * Math.pow(10, STAT_NUM_DECIMAL_PLACES)) / Math.pow(10, STAT_NUM_DECIMAL_PLACES) + "";
                    }
                    g.drawString(stringStat, (int) (coord.x + SCOREBOARD_COORD_TO_STATS_COORDS[i].x), (int) (coord.y + SCOREBOARD_COORD_TO_STATS_COORDS[i].y + (j + (j < NO_FORMATIVE_STATS? 0: 1)) * SCOREBOARD_SPACING));
                }
            }
            if (grade * progress == 100) {
                hundFlashCounter = (hundFlashCounter + 1) % (HUNDRED_FLASH_HZ * 2);
                if (hundFlashCounter < HUNDRED_FLASH_HZ) {
                    g.setColor(GOLD);
                }
                else {
                    g.setColor(OmegaFight3.PURPLE);
                }
            }
            else {
                g.setColor(RARITY_COLORS[(int) (grade * progress) / (100 / NUM_RARITIES)]);
            }
            g.drawString(progress == FULL_STATS? result: (Math.round(grade * progress * Math.pow(10, STAT_NUM_DECIMAL_PLACES)) / Math.pow(10, STAT_NUM_DECIMAL_PLACES) + "%"), (int) (coord.x + SCOREBOARD_COORD_TO_GRADE_COORD.x), (int) (coord.y + SCOREBOARD_COORD_TO_GRADE_COORD.y));
        }
    }
}
