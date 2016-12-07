package model;

import app.App;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo e regras de negócio de um jogador
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
    private boolean wasGen;

    private PlayerRound playerRound;

    public Player() {
        this.id = (int) ((System.currentTimeMillis() % Integer.MAX_VALUE) % 100000);
        this.players = new ArrayList<Player>();
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

    /**
     * Manipula as mensagens de keepalive recebidas é chamado no handler que é chamado pelo multicast
     * @param app
     * @param message
     */
    public void handlerKeepAlive(App app, Message message) {
        String playerName = message.getPlayer();
        boolean have = false;
        boolean haveGenerator = false;

        // verifica se existe na lista de conhecidos e se é gerador
        for (Player p : players) {
            if (p.getName().equals(playerName))
                have = true;

            if (p.isGenerator)
                haveGenerator = true;

        }

        // se não existe adiciona
        if (!have)
            players.add(new Player(playerName, message.getPlayerID(), message.getBodyString()));

        // se não tiver gerador e todos os players estiverem conectados é eviado uma requisição de novo gerador
        if (!haveGenerator) {
            if (players.size() >= Game.MAXPLAYERS) {
                Collections.sort(players);
                Player player = players.get(0);
                app.request(new Message(this, Message.NEW_GEN_REQUEST, player.getName()));
            }
        }
    }

    /**
     * Quando uma mensagem de novo gerador é recebida ele verefica se é este jogador, se for
     * ele se elege gerador e informa a todos outros jogadores que agora é o gerador
     * @param app
     * @param message
     */
    public void checkNewGenerator(App app, Message message) {
        //System.out.println(app.player().getName() + " novo checkNewGenerator " + message.getPlayer());
        if (message.getBodyString().equals(name) && !isGenerator) {
            isGenerator = true;
            app.setNewGenerator();
            app.updatePlayerKeepAlive();
            app.request(new Message(this, Message.I_AM_GENERATOR, name));
            app.ui().registerWordGen();
        }
    }

    /**
     * chama uma proxima rodada
     * @param app
     * @param message
     */
    public void isNext(App app, Message message) {
        if (message.getBodyString().equals(name)) {

            playerRound = new PlayerRound(app.ui());
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

    /**
     * quando um novo gerador é eleito ele envia uma mensagem informando todos os jogadores
     * e os jogadores atualizam sua lista de conhecidos nesse método
     * @param message
     */
    public void updateGenerator(Message message) {
        String playerName = message.getPlayer();
        for (Player p : players) {
            if (p.isGenerator()){
                p.setGenerator(false);
                p.setWasGen(true);
            }
            if (p.getName().equals(playerName)) {
                p.setGenerator(true);
                p.setWasGen(false);
            }
        }
    }

    public void requestChar(App app, String s) {
        app.request(new Message(this, Message.CHAR, s));
    }

    /**
     * Quando um player é desconectado da partida esse método remove ele da lista de players conectados
     * @param message
     */
    public void playerLeave(Message message) {
        Player player2Remove = null;
        for (Player p : players) {
            if (p.getName().equals(message.getBodyString())) {
                player2Remove = p;
                System.out.println("O jogador: " + p.getName() + "ficou ausente em 3 jogadas" +
                        "seguidas e foi desconectado");
            } else if(!p.isGenerator())
                System.out.println("O jogador: " + p.getName() + " venceu a rodada por desistência do " +
                        "adversário.");
        }
        players.remove(player2Remove);
    }

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

    public void setGenerator(boolean generator) {
        isGenerator = generator;
    }

    public boolean isWasGen() {
        return wasGen;
    }

    public void setWasGen(boolean wasGen) {
        this.wasGen = wasGen;
    }

}


