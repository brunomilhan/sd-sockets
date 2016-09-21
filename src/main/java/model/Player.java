package model;

import app.App;

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
    private String privateKey;

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
    }

    public Player(String playerName, boolean isGenerator) {
        this.name = playerName;
        this.isGenerator = isGenerator;
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


    /*public void handlerKeepAlive(App app, Message message) {
        String playerName = message.getPlayer();
        boolean have = false;
        boolean haveGenerator = false;
        for (Player p : players) {
            if (p.getName().equals(playerName))
                have = true;

            if (p.isGenerator)
                haveGenerator = true;
        }

        if (!have)
            players.add(new Player(playerName, message.getPlayerID()));

        if (players.size() >= Game.MAXPLAYERS){
            // Se não tiver gerador, então o primeiro da lista vira gerador
            if (!haveGenerator){
                Player player = players.get(0);
                player.isGenerator = true;
                app.request(new Message(player, Message.NEW_GEN_REQUEST, "X"));
            }
        }
    }*/

    public void handlerKeepAlive(App app, Message message) {
        String playerName = message.getPlayer();
        boolean have = false;
        boolean haveGenerator = false;
        //System.out.println("handlerKeepAlive" );
        for (Player p : players) {
            if (p.getName().equals(playerName))
                have = true;

            if (p.isGenerator){
                //System.out.println("is" );

                haveGenerator = true;
            }

        }

        if (!have)
            players.add(new Player(playerName, message.getPlayerID()));

        if (!haveGenerator) {
            if (players.size() >= Game.MAXPLAYERS) {
                Collections.sort(players);
                Player player = players.get(0);
                app.request(new Message(player, Message.NEW_GEN_REQUEST, "X"));

            }
        }
    }

    public void checkNewGenerator(App app, Message message) {
        //System.out.println(app.player().getName() + " novo checkNewGenerator " + message.getPlayer());
        if (message.getPlayer().equals(name)) {
            isGenerator = true;
            app.setNewHandler();
            app.updatePlayerKeepAlive();
            app.request(new Message(this, Message.I_AM_GENERATOR, "X"));
            app.ui().registerWordGen();
        }
    }

    public void isNext(App app, Message message){
        if (message.getBodyString().equals(name)) {
            app.ui().nextRound();
            app.ui().round();
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
}


