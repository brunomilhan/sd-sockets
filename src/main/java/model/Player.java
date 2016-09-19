package model;

import app.App;
import app.WordGenHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno on 12/09/2016.
 */
public class Player {
    private String name;
    private int moves;
    private int score;
    private int fails;
    private int matchesFails; // no maximo 3, ou seja 6 moves
    private String status; // dentro ou fora da partida
    private String publicKey;
    private String privateKey;

    private List<Player> players;
    //private List<WordGenerator> generators;
    private boolean isGenerator;

    public Player() {
        //this.generators = new ArrayList<WordGenerator>();
        this.players = new ArrayList<Player>();
        this.moves = 0;
        this.score = 0;
        this.fails = 0;
    }

    public Player(String playerName) {
        this.name = playerName;
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
        boolean haveGenList = false;
        for (Player p : players) {
            if (p.getName().equals(playerName))
                have = true;
        }

        for (WordGenerator g : generators) {
            if (g.getName().equals(playerName) &&)
                have = true;
        }

        for (WordGenerator w : generators) {
            if (w.getName().equals(playerName))
                haveGenList = true;
        }

        if (!have)
            if (players.size() < Game.MAXPLAYERS)
                players.add(new Player(playerName));

        if (!haveGenList)
            if (generators.size() < Game.MAXPLAYERS)
                generators.add(new WordGenerator(playerName));
    }*/

    public void handlerKeepAlive(App app, Message message) {
        String playerName = message.getPlayer();
        boolean have = false;
        boolean haveGenerator = false;
        for (Player p : players) {
            if (p.getName().equals(playerName))
                have = true;

            if (p.isGenerator)
                haveGenerator = true;
        }

        if (players.size() < Game.MAXPLAYERS) {
            // Add na lista de jogadores desse usuário
            if (!have)
                players.add(new Player(playerName));


        }

        if (players.size() >= Game.MAXPLAYERS){
            // Se não tiver gerador, então o primeiro da lista vira gerador
            if (!haveGenerator){
                Player player = players.get(0);
                player.isGenerator = true;
                app.request(new Message(player, Message.NEW_GEN_REQUEST, "X"));
            }
        }
    }

    public void checkUpdateGenerator(App app, Message message) {
        if (message.getPlayer().equals(this.name)){
            app.setNewHandler();
        }
    }
}
