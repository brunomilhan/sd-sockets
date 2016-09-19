package model;

import java.util.List;

/**
 * Created by Bruno on 12/09/2016.
 */
public class Game {
    public static final int MAXPLAYERS = 2;
    public static final int SCORE_CHAR = 1;
    public static final int SCORE_WORD = 10;
    public static final int MOVES_LIMIT = 1;

    private List<Player> players;
    private List<Player> hosts;
    private int maxFails;
}
