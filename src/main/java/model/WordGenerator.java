package model;

import app.App;

import java.util.*;

/**
 * Created by Bruno on 12/09/2016.
 */
public class WordGenerator extends Player {
    private String finalWord;
    private String lastWord = "";
    private char[] lastCharWord;

    Timer timer1 = new Timer();
    Timer timer2 = new Timer();
    DeltaTimerTask dt1;
    DeltaTimerTask dt2;
    DeltaTimerTask dt1Aux;
    DeltaTimerTask dt2Aux;


    boolean isCanceledTimer = false;

    public WordGenerator() {
        super.setName("generator");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        purgeTimers();
        updateScore(app, message);
        mountWord(message.getBodyString());
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), false, false);
    }

    public void receiveWord(Message message, App app) {
        purgeTimers();
        updateScore(app, message);
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), false, false);
    }

    public void receiveLeave(Message message, App app) {
        purgeTimers();
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), true, false);
        purgeTimers();
    }

    public void requestFirstPlayer(App app) {
        String playerName = app.player().players().get(1).getName();
        app.request(new Message(app.player(), Message.NEXT, playerName));
        setMovesTimer(app, playerName);
    }

    public void countMatchesFails(Message message, App app){
        System.out.println("countMatchesFails");
        for (Player p : app.player().players()){
            if (p.getName().equals(message.getBodyString())){
                p.setMatchesFails(1);
                if (p.getMatchesFails() <= 3)
                    countPlayerMoves(app, p.getName(), false, true);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setMovesTimer(App app, String playerName){

        //count desistidas
        System.out.println("setMovesTimer");
         dt1 = new DeltaTimerTask(app, new Message(app.player(), Message.EXPIRE_TIME_1, playerName));
         dt2 = new DeltaTimerTask(app, new Message(app.player(), Message.EXPIRE_TIME_2, playerName));

        //timer2 = new Timer();
        //if (dt1.)

        timer1.schedule(dt1, Game.DELTA_TIME_1);
        timer1.schedule(dt2, Game.DELTA_TIME_2);
    }

    private void purgeTimers(){
        System.out.println("cancelou tt");
        isCanceledTimer = true;
        //dt1.cancel();
        //dt2.cancel();
    }

    private class DeltaTimerTask extends TimerTask{
        private App app;
        private Message message;

        public DeltaTimerTask(App app, Message message){
            this.app = app;
            this.message = message;
        }

        public void run() {
            if (!isCanceledTimer) {
                System.out.println("cancelou dentro tt");
                app.request(message);
            } else {
                System.out.println("nao cancelou dentro tt");
            }
            isCanceledTimer = false;
        }
    }

    private boolean checkWordComplete(App app) {
        boolean isComplete = false;
        if (this.lastWord.equals(this.finalWord)) {
            app.request(new Message(app.player(), Message.GEN_WORD, "word"));
            isComplete = true;
        }
        return isComplete;
    }

    private void updateScore(App app, Message messsage) {
        boolean correct = false;
        boolean isWord = false;

        if (this.finalWord.contains(messsage.getBodyString())) {
            if (messsage.getType().equals(Message.CHAR)) {
                if (!this.lastWord.contains(messsage.getBodyString())) {
                    correct = true;
                    // fazer, diz que ja tentou letra, request
                }
            }
            if (messsage.getType().equals(Message.WORD)) {
                isWord = true;
            }
        }

        // Atualiza o placar do jogador no gerador de palavras
        for (Player p : app.player().players()) {
            if (p.getName().equals(messsage.getPlayer())) {
                if (correct)
                    p.setScore(Game.SCORE_CHAR);
                else
                    p.setFail();
                if (isWord) {
                    p.setScore(Game.SCORE_WORD);
                    this.lastWord = this.finalWord;
                }
            }

        }
    }

    /**
     * Método gerencia a quantidade de jogadas (efetuadas e expiradas),
     * chamando o proximo player ou não.
     * @param app
     * @param playerName
     * @param isLeave Se o player enviar a requisição para desistir da rodada
     */
    private void countPlayerMoves(App app, String playerName, boolean isLeave, boolean isTimedOut) {
        for (Player p : app.player().players()) {
            if (p.getName().equals(playerName)) {
                if (!isTimedOut)
                    p.setMatchesFails(0);
                if (p.getMoves() < Game.MOVES_LIMIT && !isLeave) {
                    p.setMoves();
                    app.request(new Message(app.player(), Message.NEXT, p.getName()));
                    setMovesTimer(app, p.getName());
                } else {
                    p.resetMoves();
                    for (Player p2 : app.player().players()) {
                        if (!p2.getName().equals(playerName) && !p2.isGenerator()) {
                            app.request(new Message(app.player(), Message.NEXT, p2.getName()));
                            setMovesTimer(app, p2.getName());
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

        for (Player p : app.player().players())
            scoreBoard += " Jogador: " + p.getName() + " | Pontos: " + p.getScore()
                    + " | Falhas: " + p.getFails() + "\n";

        String gameInfo = complete + "\nPalavra Atualizada: " + lastWord +
                "\n-------------------------------------\n" +
                "Placar: \n" + scoreBoard;

        app.request(new Message(app.player(), Message.GAME_INFO, gameInfo));
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
}
