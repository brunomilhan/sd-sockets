package app;

import connection.ResHandlerInterface;
import model.Message;

/**
 * Created by Bruno on 14/09/2016.
 */
public class GenericHandler implements ResHandlerInterface {
    private App app;

    public GenericHandler(App app) {
        this.app = app;
    }

    public void handler(Message message) {
        boolean haveCheck = false;
        if (message.getType().equals(Message.KEEPALIVE)) {
            app.player().handlerKeepAlive(app, message);
        } else
            haveCheck = true;

        if (haveCheck) {
            System.out.println(message);
            if (CheckPubKey.check(app, message)) {
                if (message.getType().equals(Message.NEW_GEN_REQUEST)) {
                    app.player().checkNewGenerator(app, message);
                }
                if (message.getType().equals(Message.I_AM_GENERATOR)) {
                    app.player().updateGenerator(message);
                }
                if (message.getType().equals(Message.NEXT)) {
                    app.player().isNext(app, message);
                }
                if (message.getType().equals(Message.GAME_INFO)) {
                    System.out.println(message.getBodyString());
                }

                // WORDGENERATOR METHODS
                if (app.player().isGenerator()){
                    if (message.getType().equals(Message.CHAR)) {
                        app.generator().receiveChar(message, app);
                    }
                    if (message.getType().equals(Message.WORD)) {
                        app.generator().receiveWord(message, app);
                    }
                    if (message.getType().equals(Message.LEAVE)) {
                        app.generator().receiveLeave(message, app);
                    }
                    if (message.getType().equals(Message.GAME_INFO)) {
                        System.out.println(message.getBodyString());
                    }
                    if (message.getType().equals(Message.I_AM_GENERATOR)) {
                        app.player().updateGenerator(message);
                    }
                }
            }
        }
    }
}
