package com.interview.leah.boggle.model;

import android.graphics.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leah on 11/29/15.
 * This is the main model for the board
 */
public class Board {
    public static final int BOARD_SIZE = 4;
    private int score;
    private Map<Integer, Integer> scoreTable;
    private Die [][] dice;
    private Point startPoint;
    private Point endPoint;
    private int wordLength;

    public Board() {
        //Both of these should be configureable by the user, the dice I'd store in an SQL table, the score table I'd put in shared prefs.
        scoreTable = new HashMap<>();
        scoreTable.put(0, 0);
        scoreTable.put(1, 0);
        scoreTable.put(2, 0);
        scoreTable.put(3, 1);
        scoreTable.put(4, 1);
        scoreTable.put(5, 2);
        scoreTable.put(6, 3);
        scoreTable.put(7, 5);
        dice = new Die[4][4];
        dice[0][0] = new Die("A","A","E","E","G","N");
        dice[0][1] = new Die("A","B","B","J","O","O");
        dice[0][2] = new Die("A","C","H","O","P","S");
        dice[0][3] = new Die("A","F","F","K","P","S");
        dice[1][0] = new Die("A","O","O","T","T","W");
        dice[1][1] = new Die("C","I","M","O","T","U");
        dice[1][2] = new Die("D","E","I","L","R","X");
        dice[1][3] = new Die("D","E","L","R","V","Y");
        dice[2][0] = new Die("D","I","S","T","T","Y");
        dice[2][1] = new Die("E","E","G","H","N","W");
        dice[2][2] = new Die("E","E","I","N","S","U");
        dice[2][3] = new Die("E","H","R","T","V","W");
        dice[3][0] = new Die("E","I","O","S","S","T");
        dice[3][1] = new Die("E","L","R","T","T","Y");
        dice[3][2] = new Die("H","I","M","N","U","Qu");
        dice[3][3] = new Die("H","L","N","N","R","Z");
        score = 0;
        startPoint = null;
        endPoint = null;
        wordLength = 0;
    }

    public int getScore() {
        return score;
    }

    public void shake() {
        for (Die[] array : dice) {
            for (Die die : array) {
                die.roll();
            }
        }
        wordLength = 0;
        score = 0;
        startPoint = null;
        endPoint = null;
    }

    public boolean tryBox(Point location) {
        if (startPoint == null) {
            updateScore();
            startPoint = location;
            endPoint = location;
            return true;
        } else if (areAdjascent(location, startPoint)) {
            updateScore();
            startPoint = location;
            return true;
        } else if (areAdjascent(location, endPoint)) {
            updateScore();
            endPoint = location;
            return true;
        } else {
            return false;
        }
    }

    private void updateScore() {
        wordLength++;
        if (score < 8) {
            score = scoreTable.get(wordLength);
        } else {
            score = 11;
        }
    }

    public String getLetter(int x, int y) {
        return dice[x][y].faceShowing;
    }

    private boolean areAdjascent(Point point1, Point point2) {
        if (point1.x == point2.x && Math.abs(point1.y - point2.y) == 1) {
            return true;
        } else if (point1.y == point2.y && Math.abs(point1.x - point2.x) == 1) {
            return true;
        } else {
            return false;
        }
    }
}
