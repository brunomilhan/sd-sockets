package model;

import app.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bruno on 12/09/2016.
 */
public class WordGenerator extends Player{
    private String finalWord;
    private String lastWord = "";
    private String wrongs;
    //private List<Player> players;
    private String lastPlayer = null;
    private double wordTime;
    private double phraseTime;

    private char[] lastCharWord;

    public WordGenerator() {
        super.setName("generator");
    }

    public WordGenerator(String playerName) {
        super.setName(playerName);
    }

    public void setFinalWord(String finalWord) {
        this.finalWord = finalWord;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Verifica se a letra enviada contem na palavra
     *
     * @param message
     * @param app
     */
    public void receiveChar(Message message, App app) {
        System.out.println("recebeu o char");
        updateScore(message);
        mountWord(message.getBodyString());
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), false);
    }

    private boolean checkWordComplete(App app){
        boolean isComplete = false;
        if (this.lastWord.equals(this.finalWord)){
            app.request(new Message(this, Message.GEN_WORD, "word"));
            isComplete = true;
        }
        return isComplete;
    }

    private void updateScore(Message messsage) {
        boolean correct = false;
        boolean isWord = false;
        /*if (!this.lastWord.contains(messsage.getBodyString()))
            if (this.finalWord.contains(messsage.getBodyString())) {
                correct = true;
            }*/

        if (this.finalWord.contains(messsage.getBodyString())) {
            if (messsage.getType().equals(Message.CHAR)) {
                if (!this.lastWord.contains(messsage.getBodyString())) {
                    correct = true;
                }
            }
            if (messsage.getType().equals(Message.WORD)) {
                isWord = true;
            }
        }

        // Atualiza o placar do jogador no gerador de palavras
        for (Player p : players()) {
            if (p.getName().equals(messsage.getPlayer())) {
                if (correct)
                    p.setScore(Game.SCORE_CHAR);
                else
                    p.setFail();
                if (isWord){
                    p.setScore(Game.SCORE_WORD);
                    this.lastWord = this.finalWord;
                }
            }

        }
    }

    private void countPlayerMoves(App app, String playerName, boolean isLeave) {
        for (Player p : players()) {
            if (p.getName().equals(playerName)) {
                if (p.getMoves() < Game.MOVES_LIMIT && !isLeave) {
                    p.setMoves();
                    app.request(new Message(this, Message.NEXT, p.getName()));
                } else {
                    p.resetMoves();
                    for (Player p2 : players()) {
                        if (!p2.getName().equals(playerName)) {
                            app.request(new Message(this, Message.NEXT, p2.getName()));
                        }
                    }
                }
            }
        }
    }

    private void refreshGameInfo(App app, Message message) {
        String scoreBoard = "";
        String complete = "";
        if (checkWordComplete(app))
            complete = "\n Palavra Completa!!! - Vencedor: " + message.getPlayer();

        for (Player p : players())
            scoreBoard += " Jogador: " + p.getName() + " | Pontos: " + p.getScore()
                    + " | Falhas: " + p.getFails() + "\n";

        String gameInfo = complete + "\nPalavra Atualizada: " + lastWord +
                "\n-------------------------------------\n" +
                "Placar: \n" + scoreBoard;

        app.request(new Message(this, Message.GAME_INFO, gameInfo));
    }

    private void mountWord(String c) {
        if (this.lastWord.length() == 0) {
            this.lastCharWord = new char[this.finalWord.length()];
            for (int i = 0; i < this.lastCharWord.length; i++) {
                this.lastCharWord[i] = '_';
            }
        }
        int fromIndex = 0;
        while (this.finalWord.indexOf(c.charAt(0), fromIndex) != -1) {
            int charPosition = this.finalWord.indexOf(c.charAt(0), fromIndex);
            this.lastCharWord[charPosition] = c.charAt(0);
            fromIndex = charPosition + 1;

            //pontuar
            /*for (Player p : players) {
                if (p.getName().equals(lastPlayer))
                    p.setScore();

            }*/
        }
        this.lastWord = new String(this.lastCharWord);
    }

    //Verificar connexão dos players
    /*public void playerKeepAlive(App app, String playerName) {
        boolean have = false;
        for (Player p : players()) {
            if (p.getName().equals(playerName))
                have = true;
        }
        if (!have)
            if (players().size() < Game.MAXPLAYERS)
                players().add(new Player(playerName));
        if (players().size() == Game.MAXPLAYERS && lastPlayer == null)
            app.request(new Message(this, Message.NEXT, players().get(0).getName()));
    }*/

    public void receiveWord(Message message, App app) {
        updateScore(message);
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), false);
    }


    public void receiveLeave(Message message, App app) {
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), true);
    }

    public void requestFirstPlayer(App app){
        String playerName = app.player().players().get(1).getName();
        app.request(new Message(app.player(), Message.NEXT, playerName));
    }
}
