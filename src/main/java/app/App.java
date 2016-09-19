package app;

import connection.Multicast;
import connection.ResHandlerInterface;
import model.Message;
import model.Player;
import model.WordGenerator;
import ui.SimpleUI;


/**
 * Created by Bruno on 12/09/2016.
 */
public class App {
    private static int PORT = 4000;

    // Connection instances
    private Multicast multicast;
    private ResHandlerInterface handler;
    private KeepAlive keepAlive;

    // Model instances
    private Player player;
    private WordGenerator wordGenerator;

    // UI instances
    private SimpleUI ui = new SimpleUI();


    // EM DESUSOOOO, RETIRAR!!!!
    // Constructors
    public App(ResHandlerInterface handler){
        this.handler = handler;
    }

    public App(){
        this.player = new Player();
        // isso precisa?
        this.ui = new SimpleUI();
        this.handler = new GenericHandler(this, player, ui);
    }

    // EM DESUSOOOO, RETIRAR!!!!
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void connectMulticast(){
        this.multicast = new Multicast(PORT);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void initGame() {
        connectMulticast();

        ui.registerPlayer(player);
        initKeepAlive();
        listen();
    }
}
