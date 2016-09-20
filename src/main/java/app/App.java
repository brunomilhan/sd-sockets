package app;

import connection.Multicast;
import connection.ResHandlerInterface;
import model.Game;
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
    private Game game;

    // UI instances
    private SimpleUI ui;

    public App() {
        this.player = new Player();
        this.game = new Game();
        this.ui = new SimpleUI(this);
        this.handler = new GenericHandler(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void connectMulticast() {
        multicast = new Multicast(PORT);
    }

    private void listen() {
        multicast.listenHandler(handler);
    }

    private void initKeepAlive() {
        System.out.println("Init KeepAlive TimerTask");
        keepAlive = new KeepAlive(multicast, player);
        keepAlive.sendKeepAlive();
        keepAlive.timerTaskKeepAlive();
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

    public void updatePlayerKeepAlive() {
        keepAlive.updatePlayer(player);
    }

    public void request(Message message) {
        multicast.request(message);
    }

    public void setNewHandler() {
        wordGenerator = new WordGenerator();
        handler = new WordGenHandler(this);
        multicast.changeListenerHandler(handler);
    }

    public Player player(){
        return player;
    }

    public WordGenerator generator(){
        return wordGenerator;
    }
    public SimpleUI ui(){
        return ui;
    }

}
