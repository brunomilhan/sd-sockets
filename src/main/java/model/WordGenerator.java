package model;

import app.App;
import generator.GeneratorRound;

/**
 * Classe modelo e regras de negocios de um gerador de palavra
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
        if (!refreshGameInfo(app, message))
            countPlayerMoves(app, message.getPlayer(), false, false);
    }

    /**
     * Verifica se a palavra enviada esta correta
     * @param message
     * @param app
     */
    public void receiveWord(Message message, App app) {
        round.purgeTimers();
        updateScore(app, message);
        if (!refreshGameInfo(app, message))
            countPlayerMoves(app, message.getPlayer(), false, false);
    }

    /**
     * Quando um jogador desiste da rodada
     * @param message
     * @param app
     */
    public void receiveLeave(Message message, App app) {
        round.purgeTimers();
        refreshGameInfo(app, message);
        countPlayerMoves(app, message.getPlayer(), true, false);
        round.purgeTimers();
    }

    /**
     * Inicia a primeira rodada
     */
    public void requestFirstPlayer(App app) {
        countPlayerMoves(app, app.player().getName(), false, false);
    }

    /**
     * conta quantos rodadas o jogador falhou
     * @param message
     * @param app
     */
    public void countMatchesFails(Message message, App app) {
        for (Player p : app.player().players()) {
            if (p.getName().equals(message.getBodyString())) {
                p.setMatchesFails(1);
                if (p.getMatchesFails() <= 2)
                    countPlayerMoves(app, p.getName(), false, true);
                else {
                    //countPlayerMoves(app, p.getName(), false, false);
                    app.request(new Message(app.player(), Message.PLAYER_LEAVE, p.getName()));
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean checkWordComplete(App app) {
        boolean isComplete = false;
        if (lastWord.equals(finalWord)) {

            isComplete = true;
        }
        return isComplete;
    }

    /**
     * quando a palavra é advinhada e a partida termina então é feito uma requisição para o proximo gerador assumir
     * @param app
     */
    private void requestNewGenerator(App app){
        app.player().setGenerator(false);
        app.player().setWasGen(true);
        app.updatePlayerKeepAlive();
        for (Player p : app.player().players()){
            if (!p.getName().equals(app.player().getName()) && !p.isWasGen()) {
                app.request(new Message(app.player(), Message.NEW_GEN_REQUEST, p.getName()));
                break;
            }
        }

    }

    /**
     * atualiza os pontos do jogador
     * @param app
     * @param messsage
     */
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
                if (p.getMoves() < Game.MOVES_LIMIT && !isLeave && !p.isGenerator()) {
                    p.setMoves();
                    app.request(new Message(app.player(), Message.NEXT, p.getName()));
                    round.setMovesTimer(app, p.getName());
                    break;
                } else {
                    p.resetMoves();
                    for (Player p2 : app.player().players()) {
                        if (!p2.getName().equals(playerName) && !p2.isGenerator()) {
                            app.request(new Message(app.player(), Message.NEXT, p2.getName()));
                            round.setMovesTimer(app, p2.getName());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * envia uma requisição mostrando as novas estatisticas
     * @param app
     * @param message
     * @return
     */
    private boolean refreshGameInfo(App app, Message message) {
        String scoreBoard = "";
        String complete = "";
        boolean finish = checkWordComplete(app);
        if (finish)
            complete = "\n Palavra Completa!!! - Vencedor: " + message.getPlayer();

        for (Player p : app.player().players())
            scoreBoard += " Jogador: " + p.getName() + " | Pontos: " + p.getScore()
                    + " | Falhas: " + p.getFails() + "\n";

        String gameInfo = complete + "\nPalavra Atualizada: " + lastWord +
                "\n-------------------------------------\n" +
                "Placar: \n" + scoreBoard;

        app.request(new Message(app.player(), Message.GAME_INFO, gameInfo));

        if (finish)
            requestNewGenerator(app);

        return finish;
    }

    /**
     * monta a palavra adivinhada até o momento e envia na requisição de estatisticas
     * @param c
     */
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
