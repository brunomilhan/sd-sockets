package app;

import connection.Multicast;
import connection.ResHandlerInterface;
import model.Message;
import model.Player;
import model.PlayerInterface;
import model.WordGenerator;

import java.util.TimerTask;

/**
 * Created by Bruno on 12/09/2016.
 */
public class App {
    private Multicast multicast;
    private ResHandlerInterface handler;
    private KeepAlive keepAlive;

    private Player player;
    private WordGenerator wordGenerator;

    public App(ResHandlerInterface handler){
        this.handler = handler;
    }

    public App(){

    }

    public void connectMulticast(int port){
        this.multicast = new Multicast(port);
    }

    public void listen(){
        this.multicast.listenHandler(this.handler);
    }

    public void initKeepAlive(){
        System.out.println("enviando multiast keepalive");
        this.keepAlive = new KeepAlive(this.multicast, this.player);

        this.keepAlive.sendKeepAlive();
        this.keepAlive.timerTaskKeepAlive();
    }

    public void request(Message message){
        multicast.request(message);
    }

    public void setNewHandler(){
        this.wordGenerator = new WordGenerator();
        this.player = this.wordGenerator;
        this.handler = new WordGenHandler(wordGenerator);
        this.multicast.changeListenerHandler(handler);

    }

    public void setHandler(ResHandlerInterface handler) {
        this.handler = handler;
    }
}
