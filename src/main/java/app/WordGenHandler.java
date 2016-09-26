package app;

import connection.ResHandlerInterface;
import model.Message;
import model.WordGenerator;

/**
 * Created by Bruno on 12/09/2016.
 */
public class WordGenHandler implements ResHandlerInterface {
    private App app;

    public WordGenHandler(App app) {
        this.app = app;
    }

    public void handler(Message message) {
      /*  if (message.getType().equals(Message.CHAR)) {
            app.generator().receiveChar(message, app);
        }
        if (message.getType().equals(Message.WORD)) {
            app.generator().receiveWord(message, app);
        }
        if (message.getType().equals(Message.LEAVE)) {
            app.generator().receiveLeave(message, app);
        }
        *//*if (message.getType().equals(Message.KEEPALIVE)){
            app.generator().playerKeepAlive(app, message.getPlayer());
        }*//*
        if (message.getType().equals(Message.GAME_INFO)) {
            System.out.println(message.getBodyString());
        }
        if (message.getType().equals(Message.I_AM_GENERATOR)) {
            app.player().updateGenerator(message);
        }*/
    }
}
