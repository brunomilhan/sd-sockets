package tests;

import app.App;
import app.GenericHandler;
import model.Message;
import model.Player;
import ui.SimpleUI;

/**
 * Created by Bruno on 14/09/2016.
 */
public class Test5 {
    public static void main(String[] args){
       /* Player player = new Player();
        SimpleUI ui = new SimpleUI();
        App app = new App();
        GenericHandler handler = new GenericHandler(app, player, ui);
        app.setHandler(handler);

        //handler.setApp(app);
        app.connectMulticast(4000);
        ui.registerPlayer(player);

        app.request(new Message(player, Message.KEEPALIVE, player.getName()));


        app.listen();*/

        App app = new App();

        app.initGame();
    }
}
