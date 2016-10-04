package model;

import app.App;
import ui.SimpleUI;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bruno on 01/10/2016.
 */
public class PlayerRound {
    private SimpleUI ui;
    private Timer timer;
    private boolean isCanceledTimer;

    private DeltaTimerTask dt1;
    private DeltaTimerTask dt2;

    public PlayerRound(SimpleUI ui){
        this.ui = ui;
        dt1 = new DeltaTimerTask(ui, Game.DELTA_TIME_1);
        dt2 = new DeltaTimerTask(ui, Game.DELTA_TIME_2);
       manage();
    }

    private void manage(){
        ui.round(this);
        ui.nextRound();
        timer = new Timer();

        timer.schedule(dt1, Game.DELTA_TIME_1);
        timer.schedule(dt2, Game.DELTA_TIME_2);
    }

    public void cancel(){
        isCanceledTimer = true;
    }


    private class DeltaTimerTask extends TimerTask {
        private SimpleUI ui;
        private int deltaTime;

        DeltaTimerTask(SimpleUI ui, int deltaTime){
            this.ui = ui;
            this.deltaTime = deltaTime;
        }

        public void run() {
            if (!isCanceledTimer) {
                //System.out.println("CANCELUOU UI");
                if (deltaTime == Game.DELTA_TIME_1)
                    ui.timedOutChar();
                if (deltaTime == Game.DELTA_TIME_2)
                    ui.timedOutWord();
            } else {
                //System.out.println("nao cancelou ui");
            }
            //isCanceledTimer = false;
        }
    }
}
