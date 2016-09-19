package app;

import connection.ResHandlerInterface;
import model.Message;
import model.Player;
import ui.SimpleUI;

/**
 * Created by Bruno on 14/09/2016.
 */
public class GenericHandler implements ResHandlerInterface {
    private SimpleUI simpleUI;
    private Player player;
    private App app;

    public GenericHandler(App app, Player player, SimpleUI simpleUI){
        this.simpleUI = simpleUI;
        this.player = player;
        this.app = app;
    }

    public void handler(Message message) {
        if (message.getType().equals(Message.KEEPALIVE)) {
            player.handlerKeepAlive(app, message);
        }

        if (message.getType().equals(Message.NEW_GEN_REQUEST)) {
            player.checkUpdateGenerator(app, message);
        }

    }
}
