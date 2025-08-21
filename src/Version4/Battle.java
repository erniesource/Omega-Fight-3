package Version4;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Battle {
    // Stats board constants
    // Make these into arrays have for each gamemode
    public static BufferedImage[] scoreBoard = new BufferedImage[OmegaFight3.NUM_GAMEMODES];
    public static final Font STATS_FONT = new Font("Consolas", Font.BOLD, 30);
    public static final int SCORE_SPACING = 43;
    public static final Coord[] SCOREBOARD_TO_STATS_COORDS = {new Coord(314, 90), new Coord(457, 90)}; // Maybe array? prolly...

    // scoreboard constants
    public static final int SKIP = 0;
    public static final int FORMATIVE = 1;
    public static final int SUMMATIVE = 2;
    public static final int[][] STATS_STATE = {{FORMATIVE, FORMATIVE, FORMATIVE, SUMMATIVE, SUMMATIVE, SUMMATIVE, SUMMATIVE},
    {FORMATIVE, FORMATIVE, FORMATIVE, SUMMATIVE, FORMATIVE, FORMATIVE, SKIP},
    {FORMATIVE, FORMATIVE, FORMATIVE, SKIP, FORMATIVE, FORMATIVE, SUMMATIVE}};
    public static final Coord SCOREBOARD_SIZE = new Coord(600, 500);
    public static final boolean[] IS_TRUNC = {false, true, false, true, false, false, false};
    public static final boolean[] IS_PERC = {false, false, true, false, false, true, true};
    public static final int STAT_NUM_DECIMAL_PLACES = 1;
    public static final int BATTLE_INFO_SPACING = 35;

    // Winner constants
    public static final int BOTH_LOSE = -1;
    public static final int BOTH_WIN = -2;

    // Omegaman constants
    public static final int EMO_MAN_EDGE_OFFSET = 75;
    public static final Coord HAPPY_MAN_SIZE = new Coord(517, 700);
    public static final Coord SAD_MAN_SIZE = new Coord(326, 500);
    public static final Color[] PLAYER_COLOR = {new Color(0, 112, 237), new Color(255, 22, 22)};

    // 2PVE grade calculation constants
    public static final double[] WEIGHT = {2, 1, -1, 2};
    public static final double TOT_POS_WEIGHT = 5;
    public static final double[] MAX_SCORE = {3, 9, 50, 200};
    public static final double STATS_WEIGHT = 0.5;
    public static final Color[] RARITY_COLORS = {Color.WHITE, Color.GREEN, PLAYER_COLOR[0], OmegaFight3.PURPLE};
    public static final Color GOLD = new Color(255, 186, 3);
    public static final int NUM_RARITIES = 4;
    public static final Coord SCOREBOARD_TO_GRADE_COORD = new Coord(310, 461);
    public static final Font GRADE_FONT = new Font("Consolas", Font.BOLD, 52);
    public static final int HUND_FLASH_HZ = 10;
    public static final Coord SCOREBOARD_TO_BOSS_HEALTH_COORD = new Coord(SCOREBOARD_SIZE.x / 2, 392);

    // PVP winner constatns
    public static final Coord PVP_SCOREBOARD_TO_WINNER_STR_COORD = new Coord(SCOREBOARD_SIZE.x / 2 , 401);
    public static final Font PVP_WINNER_FONT = new Font("Consolas", Font.BOLD, 96);

    // PVPVE constants
    public static final Coord SCOREBOARD_TO_DMG_BAR_COORD = new Coord(15, 317);
    public static final Coord DMG_BAR_SIZE = new Coord(570, 38);
    public static final Coord PVPVE_SCOREBOARD_TO_WINNER_STR_COORD = new Coord(SCOREBOARD_SIZE.x / 2 , 444);

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
    public int difficulty;
    public int lives;
    public double bossHealth;
    public double[][] playerStats = new double[Omegaman.NUM_PLAYERS][Omegaman.NUM_STATS];
    public double grade;
    public String result;

    // Constructor
    public Battle(String stageName, int winner, int gameMode, int lives, int difficulty, double bossHealth, double[][] playerStats) {
        // Initialize variables
        this.stageName = stageName;
        this.winner = winner;
        this.gameMode = gameMode;
        this.lives = lives;
        this.difficulty = difficulty;
        this.bossHealth = bossHealth;
        this.playerStats = playerStats;

        // Calculate grade
        if (gameMode == OmegaFight3.TWOPVE) {
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                for (int j = 0, k = 0; j != Omegaman.NUM_STATS; j++) {
                    if (STATS_STATE[OmegaFight3.TWOPVE][j] == SUMMATIVE) {
                        grade += WEIGHT[k] * Math.min(1, playerStats[i][j] / MAX_SCORE[k]);
                        k++;
                    }
                }
            }
            grade *= STATS_WEIGHT / TOT_POS_WEIGHT / Omegaman.NUM_PLAYERS;
            if (winner != BOTH_LOSE) grade += 1 - STATS_WEIGHT;
            grade = roundStat(grade * 100);
            result = grade + "%";
        }
    }

    // Description:
    // This method draws the scoreboard for the battle.
    public void drawScoreBoard(Coord coord, double progress, Graphics g) {
        // Draw board
        g.drawImage(scoreBoard[gameMode], (int) coord.x, (int) coord.y, null);
        g.setFont(STATS_FONT);

        // Draw each stat (truncate it if needed)
        for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
            Coord firstStatCoord = coord.add(SCOREBOARD_TO_STATS_COORDS[i]);
            int rowNum = 0;
            g.setColor(Color.WHITE);
            for (int j = 0; j != Omegaman.NUM_STATS; j++) {
                if (STATS_STATE[gameMode][j] == FORMATIVE) {
                    drawStat(firstStatCoord, rowNum, i, j, progress, g);
                    rowNum++;
                }
            }
            g.setColor(GOLD);
            for (int j = 0; j != Omegaman.NUM_STATS; j++) {
                if (STATS_STATE[gameMode][j] == SUMMATIVE) {
                    drawStat(firstStatCoord, rowNum, i, j, progress, g);
                    rowNum++;
                }
            }
        }

        if (gameMode == OmegaFight3.TWOPVE) {
            drawBossHealth(coord.add(SCOREBOARD_TO_BOSS_HEALTH_COORD), progress, g);

            // Set grade color
            g.setFont(GRADE_FONT);
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
            Coord gradeCoord = coord.add(SCOREBOARD_TO_GRADE_COORD);
            g.drawString(progress == 1.0? result: (roundStat(grade * progress) + "%"), (int) (gradeCoord.x), (int) (gradeCoord.y));
        }
        else if (gameMode == OmegaFight3.PVP) {
            drawWinnerStr(coord.add(PVP_SCOREBOARD_TO_WINNER_STR_COORD), PVP_WINNER_FONT, g);
        }
        else if (gameMode == OmegaFight3.PVPVE) {
            drawBossHealth(coord.add(SCOREBOARD_TO_BOSS_HEALTH_COORD), progress, g);

            double totBossDmg = 0;
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                totBossDmg += playerStats[i][Omegaman.DMG_TO_BOSS];
            }
            if (totBossDmg != 0) {
                double curBossDmg = 0;
                Coord dmgBarCoord = coord.add(SCOREBOARD_TO_DMG_BAR_COORD);
                for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                    g.setColor(PLAYER_COLOR[i]);
                    g.fillRect((int) (dmgBarCoord.x + curBossDmg / totBossDmg * DMG_BAR_SIZE.x * progress), (int) dmgBarCoord.y,
                    (int) (playerStats[i][Omegaman.DMG_TO_BOSS] / totBossDmg * DMG_BAR_SIZE.x * progress + 1), (int) DMG_BAR_SIZE.y);
                    curBossDmg += playerStats[i][Omegaman.DMG_TO_BOSS];
                }
            }

            drawWinnerStr(coord.add(PVPVE_SCOREBOARD_TO_WINNER_STR_COORD), GRADE_FONT, g);
        }
    }

    private void drawBossHealth(Coord bossHealthStrCoord, double progress, Graphics g) {
        g.setFont(STATS_FONT);
        g.setColor(GOLD); // Change color if didn't kill?
        String bossHealthStr = bossHealth == 0? "BOSS DEFEATED!!": String.format("%.1f BOSS HEALTH REMAINED...", bossHealth * progress);
        g.drawString(bossHealthStr, (int) (bossHealthStrCoord.x - g.getFontMetrics().stringWidth(bossHealthStr) / 2), (int) bossHealthStrCoord.y);
    }

    private void drawStat(Coord firstStatCoord, int rowNum, int playerNo, int statNo, double progress, Graphics g) {
        String stringStat;
        double progresstat = playerStats[playerNo][statNo] * progress;
        if (IS_TRUNC[statNo]) {
            stringStat = (int) (progresstat) + "";
        }
        else {
            stringStat = roundStat(progresstat) + "";
        }
        g.drawString(stringStat, (int) (firstStatCoord.x), (int) (firstStatCoord.y + rowNum * SCORE_SPACING));
    }

    private void drawWinnerStr(Coord winnerTextCoord, Font font, Graphics g) {
        g.setFont(font);
        String winnerStr = "";
        if (winner == BOTH_WIN) {
            winnerStr = "ALL WIN!!";
            g.setColor(GOLD);
        }
        else if (winner == BOTH_LOSE) {
            winnerStr = "NONE WIN..";
            g.setColor(Color.WHITE);
        }
        else {
            for (int i = 0; i != Omegaman.NUM_PLAYERS; i++) {
                if (winner == i) {
                    winnerStr = "P" + i + " WINS!!";
                    g.setColor(PLAYER_COLOR[i]);
                }
            }
        }
        g.drawString(winnerStr, (int) (winnerTextCoord.x - g.getFontMetrics().stringWidth(winnerStr) / 2), (int) (winnerTextCoord.y + Math.pow(font.getSize(), Button.FONT_SIZE_TO_ACC_SIZE) / 2));
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
        Coord[] premoManCoord = {new Coord(EMO_MAN_EDGE_OFFSET, OmegaFight3.SCREEN_SIZE.y), OmegaFight3.SCREEN_SIZE.copy()}; premoManCoord[1].x -= EMO_MAN_EDGE_OFFSET;
        if (winner == BOTH_WIN) {
            g.drawImage(happyMan[0], (int) premoManCoord[0].x, (int) (premoManCoord[0].y - HAPPY_MAN_SIZE.y), null);
            premoManCoord[1] = premoManCoord[1].add(HAPPY_MAN_SIZE.scaledBy(-1));
            g.drawImage(happyMan[1], (int) (premoManCoord[1].x), (int) (premoManCoord[1].y), null);
        }
        
        // Boss win
        else if (winner == BOTH_LOSE) {
            g.drawImage(sadMan[0], (int) premoManCoord[0].x, (int) (premoManCoord[0].y - SAD_MAN_SIZE.y), null);
            premoManCoord[1] = premoManCoord[1].add(SAD_MAN_SIZE.scaledBy(-1));
            g.drawImage(sadMan[1], (int) (premoManCoord[1].x), (int) (premoManCoord[1].y), null);
        }

        // P0 win
        else if (winner == 0) {
            g.drawImage(happyMan[0], (int) premoManCoord[0].x, (int) (premoManCoord[0].y - HAPPY_MAN_SIZE.y), null);
            premoManCoord[1] = premoManCoord[1].add(SAD_MAN_SIZE.scaledBy(-1));
            g.drawImage(sadMan[1], (int) (premoManCoord[1].x), (int) (premoManCoord[1].y), null);
        }

        // P1 win
        else if (winner == 1) {
            g.drawImage(sadMan[0], (int) premoManCoord[0].x, (int) (premoManCoord[0].y - SAD_MAN_SIZE.y), null);
            premoManCoord[1] = premoManCoord[1].add(HAPPY_MAN_SIZE.scaledBy(-1));
            g.drawImage(happyMan[1], (int) (premoManCoord[1].x), (int) (premoManCoord[1].y), null);
        }
    }

    // Description:
    // This method draws the battle information (name and stage) at the specified coordinates using the provided Graphics object.
    public void drawBattleInfo(Coord coord, Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(STATS_FONT);
        g.drawString(name, (int) coord.x, (int) coord.y);
        g.drawString("ON " + stageName.toUpperCase(), (int) coord.x, (int) coord.y + BATTLE_INFO_SPACING);
    }
}
