package model;

import app.App;
import generator.GeneratorRound;

/**
 * Created by Bruno on 12/09/2016.
 */
public class WordGenerator extends Player {
    private String finalWord;
    private String lastWord = "";
    private char[] lastCharWord;

    private GeneratorRound round;

    public WordGenerator() {
        round = new GeneratorRound();
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
        round.purgeTimers();
        updateScore(app, message);
        mountWord(message.getBodyString());
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), false, false);
    }

    public void receiveWord(Message message, App app) {
        round.purgeTimers();
        updateScore(app, message);
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), false, false);
    }

    public void receiveLeave(Message message, App app) {
        round.purgeTimers();
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), true, false);
        round.purgeTimers();
    }

    public void requestFirstPlayer(App app) {
        String playerName = app.player().players().get(1).getName();
        app.request(new Message(app.player(), Message.NEXT, playerName));
        round.setMovesTimer(app, playerName);
    }

    public void countMatchesFails(Message message, App app) {
        for (Player p : app.player().players()) {
            if (p.getName().equals(message.getBodyString())) {
                p.setMatchesFails(1);
                if (p.getMatchesFails() <= 3)
                    countPlayerMoves(app, p.getName(), false, true);
                else {
                    countPlayerMoves(app, p.getName(), false, false);
                    System.out.println("O jogador: " + p.getName() + "ficou ausente em 3 jogadas" +
                            "seguidas e foi desconectado");
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
     *
     * @param app
     * @param playerName
     * @param isLeave    Se o player enviar a requisição para desistir da rodada
     */
    private void countPlayerMoves(App app, String playerName, boolean isLeave, boolean isTimedOut) {
        for (Player p : app.player().players()) {
            if (p.getName().equals(playerName)) {
                if (!isTimedOut)
                    p.setMatchesFails(0);
                if (p.getMoves() < Game.MOVES_LIMIT && !isLeave) {
                    p.setMoves();
                    app.request(new Message(app.player(), Message.NEXT, p.getName()));
                    round.setMovesTimer(app, p.getName());
                } else {
                    p.resetMoves();
                    for (Player p2 : app.player().players()) {
                        if (!p2.getName().equals(playerName) && !p2.isGenerator()) {
                            app.request(new Message(app.player(), Message.NEXT, p2.getName()));
                            round.setMovesTimer(app, p2.getName());
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
}
