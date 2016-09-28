package model;

import app.App;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bruno on 12/09/2016.
 */
public class Player implements Comparable<Player> {
    private int id;
    private String name;
    private int moves;
    private int score;
    private int fails;
    private int matchesFails; // no maximo 3, ou seja 6 moves
    private String status; // dentro ou fora da partida
    private String publicKey;
    private PrivateKey privateKey;

    private List<Player> players;
    private boolean isGenerator;

    public Player() {
        this.id = (int) ((System.currentTimeMillis() % Integer.MAX_VALUE) % 100000);
        this.players = new ArrayList<Player>();
        this.moves = 0;
        this.score = 0;
        this.fails = 0;
    }

    public Player(String playerName, int id) {
        this.name = playerName;
        this.id = id;
        this.moves = 0;
        this.score = 0;
        this.fails = 0;
    }

    public Player(String playerName, int playerID, String publicKey) {
        this.name = playerName;
        this.id = playerID;
        this.moves = 0;
        this.score = 0;
        this.fails = 0;
        this.publicKey = publicKey;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score += score;
    }

    public void setFail() {
        this.fails += 1;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves() {
        this.moves += 1;
    }

    public void resetMoves() {
        this.moves = 0;
    }

    public int getScore() {
        return score;
    }

    public int getFails() {
        return fails;
    }

    public void setMatchesFails(int matchesFails) {
        if (matchesFails == 1)
            this.matchesFails += matchesFails;
        else
            this.matchesFails = 0;
    }

    public int getMatchesFails() {
        return matchesFails;
    }

    public boolean isGenerator() {
        return isGenerator;
    }

    public void handlerKeepAlive(App app, Message message) {
        String playerName = message.getPlayer();
        boolean have = false;
        boolean haveGenerator = false;
        for (Player p : players) {
            //System.out.println("player: " + p.getName() + " key: " + p.getPublicKey());
            if (p.getName().equals(playerName))
                have = true;

            if (p.isGenerator)
                haveGenerator = true;

        }

        if (!have)
            players.add(new Player(playerName, message.getPlayerID(), message.getBodyString()));

        if (!haveGenerator) {
            if (players.size() >= Game.MAXPLAYERS) {
                Collections.sort(players);
                Player player = players.get(0);
                app.request(new Message(this, Message.NEW_GEN_REQUEST, player.getName()));
            }
        }
    }

    public void checkNewGenerator(App app, Message message) {
        //System.out.println(app.player().getName() + " novo checkNewGenerator " + message.getPlayer());
        if (message.getBodyString().equals(name) && !isGenerator) {
            isGenerator = true;
            app.setNewHandler();
            app.updatePlayerKeepAlive();
            app.request(new Message(this, Message.I_AM_GENERATOR, name));
            app.ui().registerWordGen();
        }
    }

    public void isNext(App app, Message message) {
        if (message.getBodyString().equals(name)) {
            System.out.println("proximo denovo");
            if (app.ui().isRunning())
                app.ui().cancelInputThread();
            app.ui().nextRound();
            app.ui().round();
        }

    }

    public List<Player> players() {
        return players;
    }

    public int getId() {
        return id;
    }

    public int compareTo(Player p) {
        if (this.id > p.getId())
            return 1;
        if (this.id < p.getId())
            return -1;
        return 0;
    }

    public void updateGenerator(Message message) {
        String playerName = message.getPlayer();
        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                p.isGenerator = true;
            }
        }
    }

    public void requestChar(App app, String s) {
        app.request(new Message(this, Message.CHAR, s));
    }

    public void timedOut(App app, Message message) {
        if (app.player().getName().equals(message.getBodyString())) {
            System.out.println("TIMED OUTTT");
            app.ui().cancelInputThread();
        }

    }
}


