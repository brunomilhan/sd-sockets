package tests;

import app.App;
import app.PlayerHandler;
import model.Message;
import model.Player;
import ui.SimpleUI;

/**
 * Created by Bruno on 12/09/2016.
 */
public class Test2 {
    public static void main(String[] args){
        Player player = new Player();
        SimpleUI ui = new SimpleUI();
        PlayerHandler handler = new PlayerHandler(ui, player);
        App app = new App(handler);

        handler.setApp(app);
        app.connectMulticast(4000);
        ui.registerPlayer(player);

        app.request(new Message(player, Message.KEEPALIVE, player.getName()));


        app.listen();
    }
}
