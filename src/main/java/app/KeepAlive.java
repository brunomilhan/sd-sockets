package app;

import connection.Multicast;
import model.Message;
import model.Player;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bruno on 19/09/2016.
 */
public class KeepAlive {
    private Multicast multicast;
    private Player player;

    public KeepAlive(Multicast multicast, Player player){
        this.multicast = multicast;
        this.player = player;


    }

    public void timerTaskKeepAlive() {
        Timer timer;
        timer = new Timer(true);
        timer.schedule(new TimerTaskKeepAlive(this.multicast, this.player), 0,
                5000);
    }

    public void sendKeepAlive(){
        multicast.request(new Message(player, Message.KEEPALIVE, player.getName()));
    }

    /**
     * TimerTask responsible for keepAlive player on multicast socket.
     *
     * @author bruno
     *
     */
    private class TimerTaskKeepAlive extends TimerTask {
        private Multicast multicast;
        private Message msg;
        private Player player;

        public TimerTaskKeepAlive(Multicast multicast, Player player) {
            this.player = player;
            this.multicast = multicast;
            this.msg = new Message(player, Message.KEEPALIVE, this.player.getName());

            multicast.request(msg);

        }

        @Override
        public void run() {
            System.out.println("Timer task run");
            System.out.println("KeepAlive multicast task schedule");

            this.multicast.request(msg);
        }

    }
}
