package app;

import connection.ResHandlerInterface;
import model.Message;
import model.Player;
import ui.SimpleUI;

/**
 * Created by Bruno on 13/09/2016.
 */
public class PlayerHandler implements ResHandlerInterface {
    private SimpleUI simpleUI;
    private Player player;
    private App app;

    public PlayerHandler(SimpleUI ui, Player player) {
        this.simpleUI = ui;
        this.player = player;
    }

    public void handler(Message message) {
        if (message.getType().equals(Message.NEXT)) {
                /*if (message.getBodyString().equals(Message.NULL)){
                    simpleUI.round(player, app);
                }*/
            if (message.getBodyString().equals(this.player.getName())) {
                simpleUI.nextRound();
               // simpleUI.round(player, app);
            }

        }
        if (message.getType().equals(Message.GAME_INFO)) {
            System.out.println(message.getBodyString());
        }
    }

    public void setApp(App app) {
        this.app = app;
    }
}
