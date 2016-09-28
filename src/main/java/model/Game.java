package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno on 12/09/2016.
 */
public class Game {
    public static final int MAXPLAYERS = 3;
    public static final int SCORE_CHAR = 1;
    public static final int SCORE_WORD = 10;
    public static final int MOVES_LIMIT = 1;
    public static final int DELTA_TIME_1 = 10000;
    public static final int DELTA_TIME_2 = 15000;


    private List<Player> players;

    public Game() {
        this.players = new ArrayList<Player>();
    }
}
