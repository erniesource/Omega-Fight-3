package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Battle {
    // Stats board constants
    // Make these into arrays have for each gamemode
    public static BufferedImage[] scoreBoard = new BufferedImage[1];
    public static final Font SCORE_FONT = new Font("Consolas", Font.BOLD, 30);
    public static final int SCORE_SPACING = 43;
    public static final Coord[] SCORE_TO_STATS_COORDS = {new Coord(314, 133), new Coord(457, 133)}; // Maybe array? prolly...

    public static final int NUM_FORMATIVE_STATS = 3; // Specific to coop

    public static final Coord SCOREBOARD_SIZE = new Coord(600, 500);
    public static final boolean[] IS_TRUNC = {false, true, false, true, false, false, false};
    public static final boolean[] IS_PERC = {false, false, true, false, false, true, true};
    public static final int STAT_NUM_DECIMAL_PLACES = 1;
    public static final int BATTLE_INFO_SPACING = 35;
    public static final int BOSS_WIN = -1;
    public static final int BOTH_WIN = -2;

    // Emo man constants
    public static final int EMO_MAN_EDGE_OFFSET = 75;
    public static final Coord HAPPY_MAN_SIZE = new Coord(517, 700);
    public static final Coord SAD_MAN_SIZE = new Coord(326, 500);

    // 2P V E grade calculation constants
    public static final double[] WEIGHT = {2, 1, -1, 2};
    public static final double TOT_POS_WEIGHT = 5;
    public static final double[] MAX_SCORE = {3, 9, 50, 200};
    public static final double STATS_WEIGHT = 0.5;
    public static final Color[] RARITY_COLORS = {Color.WHITE, Color.GREEN, new Color(0, 112, 237), OmegaFight3.PURPLE};
    public static final Color GOLD = new Color(255, 186, 3);
    public static final int NUM_RARITIES = 4;
    public static final Coord SCOREBOARD_TO_GRADE_COORD = new Coord(310, 478);
    public static final int HUND_FLASH_HZ = 10;

    // Static flash counter for hundred percent grade
    public static int hundFlashCounter = 0;

    // Images
    public static BufferedImage[] happyMan = new BufferedImage[Omegaman.NUM_PLAYERS];
    public static BufferedImage[] sadMan = new BufferedImage[Omegaman.NUM_PLAYERS];

    // Instance variables
    public String name;
    public String stageName;
    public int gameMode;
    public int winner; // -2: Both players, -1: Boss, 0+: Players
    public double[][] playerStats = new double[Omegaman.NUM_PLAYERS][Omegaman.NO_OF_STATS];
    public double grade;
    public String result;
    // Settings (difficulty, lives)

    // Constructor
    public Battle(String stageName, int gameMode, int winner, double[][] playerStats) {
        // Initialize variables
        this.stageName = stageName;
        this.gameMode = gameMode;
        this.winner = winner;
        this.playerStats = playerStats;

        // Calculate grade
        if (gameMode == OmegaFight3.TWOPVE) {
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                for (int j = NUM_FORMATIVE_STATS; j != Omegaman.NO_OF_STATS; j++) {
                    grade += WEIGHT[j - NUM_FORMATIVE_STATS] * Math.min(1, playerStats[i][j] / MAX_SCORE[j - NUM_FORMATIVE_STATS]);
                }
            }
            grade *= STATS_WEIGHT / TOT_POS_WEIGHT / Omegaman.NUM_PLAYERS;
            if (winner != BOSS_WIN) grade += 1 - STATS_WEIGHT;
            grade = roundStat(grade * 100);
            result = grade + "%";
        }
    }

    // Description:
    // This method draws the scoreboard for the battle.
    public void drawScoreBoard(Coord coord, double progress, Graphics g) {
        // Draw board
        g.drawImage(scoreBoard[gameMode], (int) coord.x, (int) coord.y, null);
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);

        if (gameMode == OmegaFight3.TWOPVE) {
            // Draw each stat (truncate it if needed)
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                for (int j = 0; j != Omegaman.NO_OF_STATS; j++) {
                    String stringStat;
                    if (IS_TRUNC[j]) {
                        stringStat = (int) (playerStats[i][j] * progress) + "";
                    }
                    else {
                        stringStat = roundStat(playerStats[i][j] * progress) + "";
                    }
                    g.drawString(stringStat, (int) (coord.x + SCORE_TO_STATS_COORDS[i].x), (int) (coord.y + SCORE_TO_STATS_COORDS[i].y + (j + (j < NUM_FORMATIVE_STATS? 0: 1)) * SCORE_SPACING));
                }
            }

            // Set grade color
            if (grade * progress == 100) {
                hundFlashCounter = (hundFlashCounter + 1) % (HUND_FLASH_HZ * 2);
                if (hundFlashCounter < HUND_FLASH_HZ) {
                    g.setColor(GOLD);
                }
                else {
                    g.setColor(OmegaFight3.PURPLE);
                }
            }
            else {
                g.setColor(RARITY_COLORS[(int) (grade * progress) / (100 / NUM_RARITIES)]);
            }
            
            // Draw result
            g.drawString(progress == 1.0? result: (roundStat(grade * progress) + "%"), (int) (coord.x + SCOREBOARD_TO_GRADE_COORD.x), (int) (coord.y + SCOREBOARD_TO_GRADE_COORD.y));
        }
    }

    private double roundStat(double stat) {
        return Math.round(stat * Math.pow(10, STAT_NUM_DECIMAL_PLACES)) / Math.pow(10, STAT_NUM_DECIMAL_PLACES);
    }

    public void drawScoreBoard(Coord coord, Graphics g) {
        drawScoreBoard(coord, 1, g);
    }

    // Description:
    // This method draws the emotional Omegamen on the two sides of the screen
    public void drawEmoMan(Graphics g) {
        // Both win
        if (winner == Battle.BOTH_WIN) {
            g.drawImage(happyMan[0], EMO_MAN_EDGE_OFFSET, (int) (OmegaFight3.SCREEN_SIZE.y - HAPPY_MAN_SIZE.y), null);
            g.drawImage(happyMan[1], (int) (OmegaFight3.SCREEN_SIZE.x - EMO_MAN_EDGE_OFFSET - HAPPY_MAN_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - HAPPY_MAN_SIZE.y), null);
        }
        
        // Boss win
        else if (winner == Battle.BOSS_WIN) {
            g.drawImage(sadMan[0], EMO_MAN_EDGE_OFFSET, (int) (OmegaFight3.SCREEN_SIZE.y - SAD_MAN_SIZE.y), null);
            g.drawImage(sadMan[1], (int) (OmegaFight3.SCREEN_SIZE.x - EMO_MAN_EDGE_OFFSET - SAD_MAN_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - SAD_MAN_SIZE.y), null);
        }

        // P0 win
        else if (winner == 0) {
            g.drawImage(happyMan[0], EMO_MAN_EDGE_OFFSET, (int) (OmegaFight3.SCREEN_SIZE.y - HAPPY_MAN_SIZE.y), null);
            g.drawImage(sadMan[1], (int) (OmegaFight3.SCREEN_SIZE.x - EMO_MAN_EDGE_OFFSET - SAD_MAN_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - SAD_MAN_SIZE.y), null);
        }

        // P1 win
        else if (winner == 1) {
            g.drawImage(sadMan[0], EMO_MAN_EDGE_OFFSET, (int) (OmegaFight3.SCREEN_SIZE.y - SAD_MAN_SIZE.y), null);
            g.drawImage(happyMan[1], (int) (OmegaFight3.SCREEN_SIZE.x - EMO_MAN_EDGE_OFFSET - HAPPY_MAN_SIZE.x), (int) (OmegaFight3.SCREEN_SIZE.y - HAPPY_MAN_SIZE.y), null);
        }
    }

    // Description:
    // This method draws the battle information (name and stage) at the specified coordinates using the provided Graphics object.
    public void drawBattleInfo(Coord coord, Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        g.drawString(name, (int) coord.x, (int) coord.y);
        g.drawString("ON " + stageName.toUpperCase(), (int) coord.x, (int) coord.y + BATTLE_INFO_SPACING);
    }
}
