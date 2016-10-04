package generator;

import app.App;
import model.Game;
import model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Esta classe é responsalvel por gerenciar a partida do lado do servidor,
 * adicionando e removendo os timers de cada jogada.
 * Created by Bruno on 03/10/2016.
 */
public class GeneratorRound {
    private Timer timer;
    private List<DeltaTimerTask> deltaTimerTasks;
    private int roundID;

    public GeneratorRound() {
        timer = new Timer();
        deltaTimerTasks = new ArrayList<DeltaTimerTask>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A cada jogada disponibilizada para o jogador esse metodo é chamada setando um timer para ela,
     * com o objetivo de expirar a jogada caso o jogador estiver ausente.
     *
     * @param app
     * @param playerName
     */
    public void setMovesTimer(App app, String playerName) {
        DeltaTimerTask deltaTimerTask = new DeltaTimerTask(app,
                new Message(app.player(), Message.EXPIRE_TIME_2, playerName));

        deltaTimerTasks.add(roundID, deltaTimerTask);
        roundID += 1;
        timer.schedule(deltaTimerTask, Game.DELTA_TIME_2);
    }

    /**
     * Quando o jogador realiza uma jogada dentro do tempo estabelecido esse metodo irá cancelar o timer
     * já agendado.
     */
    public void purgeTimers() {
        int round = roundID - 1;
        deltaTimerTasks.get(round).cancelTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private InnerClass
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Classe do timertask que irá realizar um request multicast caso o tempo de jogada do usuário expire.
     */
    private class DeltaTimerTask extends TimerTask {
        private App app;
        private Message message;
        private boolean cancel;

        DeltaTimerTask(App app, Message message) {
            this.app = app;
            this.message = message;
        }

        public void run() {
            if (!cancel)
                app.request(message);
        }

        void cancelTask() {
            this.cancel = true;
        }
    }

}
